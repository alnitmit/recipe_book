import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, IconButton, Stack, TextField, Typography } from '@mui/material';
import { useDeferredValue, useMemo, useState } from 'react';

import {
  useCreateIngredientMutation,
  useDeleteIngredientMutation,
  useGetIngredientsQuery,
  useUpdateIngredientMutation,
} from '@/entities/ingredient/api/ingredientApi.ts';
import type { Ingredient, IngredientPayload } from '@/entities/ingredient/model/types.ts';
import { useGetRecipesQuery } from '@/entities/recipe/api/recipeApi.ts';
import { IngredientDialog } from '@/features/ingredient-upsert/IngredientDialog.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarProvider.tsx';
import { EntityTable, type EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

export function IngredientsPage() {
  const { showSnackbar } = useAppSnackbar();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [search, setSearch] = useState('');
  const deferredSearch = useDeferredValue(search);
  const [editingIngredient, setEditingIngredient] = useState<Ingredient | null>(null);
  const [ingredientToDelete, setIngredientToDelete] = useState<Ingredient | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data, isLoading } = useGetIngredientsQuery({ page, size, sort: 'id,desc' });
  const { data: recipesPage } = useGetRecipesQuery({ page: 0, size: 100, sort: 'title,asc' });
  const [createIngredient, { isLoading: isCreating }] = useCreateIngredientMutation();
  const [updateIngredient, { isLoading: isUpdating }] = useUpdateIngredientMutation();
  const [deleteIngredient, { isLoading: isDeleting }] = useDeleteIngredientMutation();

  const recipeNames = useMemo(
    () => new Map((recipesPage?.content ?? []).map((recipe) => [recipe.id, recipe.title])),
    [recipesPage?.content],
  );

  const filteredRows = useMemo(() => {
    const query = deferredSearch.trim().toLowerCase();
    if (!query) {
      return data?.content ?? [];
    }

    return (data?.content ?? []).filter((ingredient) =>
      [ingredient.name, ingredient.quantity, ingredient.unitName ?? '', recipeNames.get(ingredient.recipeId) ?? '']
        .join(' ')
        .toLowerCase()
        .includes(query),
    );
  }, [data?.content, deferredSearch, recipeNames]);

  const columns = useMemo<Array<EntityTableColumn<Ingredient>>>(
    () => [
      { key: 'name', label: 'Название', render: (ingredient) => <Typography fontWeight={700}>{ingredient.name}</Typography> },
      { key: 'quantity', label: 'Количество', render: (ingredient) => ingredient.quantity },
      { key: 'unit', label: 'Единица', render: (ingredient) => ingredient.unitName || '—' },
      {
        key: 'recipe',
        label: 'Рецепт',
        render: (ingredient) => recipeNames.get(ingredient.recipeId) ?? `Recipe #${ingredient.recipeId}`,
      },
    ],
    [recipeNames],
  );

  const handleSubmit = async (payload: IngredientPayload) => {
    try {
      if (editingIngredient?.id) {
        await updateIngredient({ id: editingIngredient.id, body: payload }).unwrap();
        showSnackbar('Ингредиент обновлён', 'success');
      } else {
        await createIngredient(payload).unwrap();
        showSnackbar('Ингредиент создан', 'success');
      }
      setEditingIngredient(null);
      setFieldErrors({});
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDelete = async () => {
    if (!ingredientToDelete) {
      return;
    }

    try {
      await deleteIngredient({ id: ingredientToDelete.id, recipeId: ingredientToDelete.recipeId }).unwrap();
      showSnackbar('Ингредиент удалён', 'success');
      setIngredientToDelete(null);
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <div className={pageStyles.page}>
      <div className={pageStyles.toolbar}>
        <Button
          startIcon={<AddOutlinedIcon />}
          variant="contained"
          onClick={() => {
            setEditingIngredient({
              id: 0,
              name: '',
              quantity: '',
              recipeId: recipesPage?.content[0]?.id ?? 0,
              unitId: null,
              unitName: null,
            });
            setFieldErrors({});
          }}
        >
          Добавить ингредиент
        </Button>
        <TextField label="Поиск по текущей странице" value={search} onChange={(event) => setSearch(event.target.value)} />
      </div>

      {isLoading && !data ? (
        <PageSkeleton />
      ) : data ? (
        <EntityTable
          columns={columns}
          rows={filteredRows}
          getRowId={(ingredient) => ingredient.id}
          page={data.number}
          rowsPerPage={data.size}
          totalElements={data.totalElements}
          onPageChange={setPage}
          onRowsPerPageChange={(nextSize) => {
            setSize(nextSize);
            setPage(0);
          }}
          actions={(ingredient) => (
            <Stack direction="row" justifyContent="flex-end" spacing={1}>
              <IconButton aria-label="Редактировать" onClick={() => setEditingIngredient(ingredient)}>
                <EditOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Удалить" color="error" onClick={() => setIngredientToDelete(ingredient)}>
                <DeleteOutlineIcon />
              </IconButton>
            </Stack>
          )}
        />
      ) : (
        <EmptyState title="Ингредиентов пока нет" description="Создай ингредиент и привяжи его к существующему рецепту." />
      )}

      <IngredientDialog
        open={Boolean(editingIngredient)}
        title={editingIngredient?.id ? 'Редактировать ингредиент' : 'Новый ингредиент'}
        initialValue={editingIngredient ?? undefined}
        loading={isCreating || isUpdating}
        fieldErrors={fieldErrors}
        onSubmit={handleSubmit}
        onClose={() => setEditingIngredient(null)}
      />

      <ConfirmDialog
        open={Boolean(ingredientToDelete)}
        title="Удалить ингредиент?"
        description={ingredientToDelete ? `Ингредиент "${ingredientToDelete.name}" будет удалён.` : ''}
        destructive
        loading={isDeleting}
        confirmLabel="Удалить"
        onConfirm={handleDelete}
        onClose={() => setIngredientToDelete(null)}
      />
    </div>
  );
}
