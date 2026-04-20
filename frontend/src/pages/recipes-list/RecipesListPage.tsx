import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import VisibilityOutlinedIcon from '@mui/icons-material/VisibilityOutlined';
import { Button, IconButton, Stack, Typography } from '@mui/material';
import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { useDeleteRecipeMutation, useGetFilteredRecipesQuery, useGetRecipesQuery } from '@/entities/recipe/api/recipeApi.ts';
import type { Recipe, RecipeFilterParams } from '@/entities/recipe/model/types.ts';
import { RecipeFilterBar } from '@/features/recipe-filter/RecipeFilterBar.tsx';
import { getErrorMessage } from '@/shared/lib/error.ts';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';
import { EntityTable, type EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

const defaultFilters: RecipeFilterParams = {
  page: 0,
  size: 10,
  sort: 'id,desc',
};

export const RecipesListPage = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useAppSnackbar();
  const [filters, setFilters] = useState<RecipeFilterParams>(defaultFilters);
  const [recipeToDelete, setRecipeToDelete] = useState<Recipe | null>(null);
  const [deleteRecipe, { isLoading: isDeleting }] = useDeleteRecipeMutation();

  const hasServerFilters = Boolean(filters.category) || Boolean(filters.minIngredients);
  const { data: recipesPage, isFetching: isFetchingRecipes } = useGetRecipesQuery(filters, { skip: hasServerFilters });
  const { data: filteredPage, isFetching: isFetchingFiltered } = useGetFilteredRecipesQuery(filters, {
    skip: !hasServerFilters,
  });

  const page = hasServerFilters ? filteredPage : recipesPage;
  const isLoading = hasServerFilters ? isFetchingFiltered : isFetchingRecipes;

  const columns = useMemo<Array<EntityTableColumn<Recipe>>>(
    () => [
      {
        key: 'title',
        label: 'Название',
        render: (recipe) => (
          <Stack spacing={0.5}>
            <Typography fontWeight={700}>{recipe.title}</Typography>
            <Typography color="textSecondary" variant="body2">
              {recipe.description || 'Без описания'}
            </Typography>
          </Stack>
        ),
      },
      {
        key: 'category',
        label: 'Категория',
        render: (recipe) => recipe.categoryName || 'Не указана',
      },
      {
        key: 'author',
        label: 'Автор',
        render: (recipe) => recipe.authorUsername || 'Не назначен',
      },
      {
        key: 'tags',
        label: 'Теги',
        render: (recipe) => recipe.tags.length,
        align: 'center',
      },
      {
        key: 'ingredients',
        label: 'Ингредиенты',
        render: (recipe) => recipe.ingredients.length,
        align: 'center',
      },
    ],
    [],
  );

  const handleDelete = async () => {
    if (!recipeToDelete) {
      return;
    }

    try {
      await deleteRecipe(recipeToDelete.id).unwrap();
      showSnackbar(`Рецепт "${recipeToDelete.title}" удален`, 'success');
      setRecipeToDelete(null);
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <div className={pageStyles.page}>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.toolbarLeft}>
          <Button startIcon={<AddOutlinedIcon />} variant="contained" onClick={() => navigate('/recipes/new')}>
            Создать рецепт
          </Button>
        </div>
      </div>

      <RecipeFilterBar
        value={filters}
        onApply={(nextValue) => setFilters((previous) => ({ ...previous, ...nextValue, page: 0 }))}
        onReset={() => setFilters(defaultFilters)}
      />

      {isLoading && !page ? (
        <PageSkeleton />
      ) : page?.content.length ? (
        <EntityTable
          columns={columns}
          rows={page.content}
          getRowId={(recipe) => recipe.id}
          page={page.number}
          rowsPerPage={page.size}
          totalElements={page.totalElements}
          onPageChange={(nextPage) => setFilters((previous) => ({ ...previous, page: nextPage }))}
          onRowsPerPageChange={(nextSize) => setFilters((previous) => ({ ...previous, size: nextSize, page: 0 }))}
          actions={(recipe) => (
            <Stack direction="row" justifyContent="flex-end" spacing={1}>
              <IconButton aria-label="Просмотр" onClick={() => navigate(`/recipes/${recipe.id}`)}>
                <VisibilityOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Редактировать" onClick={() => navigate(`/recipes/${recipe.id}/edit`)}>
                <EditOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Удалить" color="error" onClick={() => setRecipeToDelete(recipe)}>
                <DeleteOutlineIcon />
              </IconButton>
            </Stack>
          )}
        />
      ) : (
        <EmptyState
          title="Рецептов пока нет"
          description="Создай первый рецепт или сбрось фильтры, если список пуст из-за условий поиска."
        />
      )}

      <ConfirmDialog
        open={Boolean(recipeToDelete)}
        title="Удалить рецепт?"
        description={recipeToDelete ? `Рецепт "${recipeToDelete.title}" будет удален без возможности восстановления.` : ''}
        destructive
        loading={isDeleting}
        confirmLabel="Удалить"
        onConfirm={handleDelete}
        onClose={() => setRecipeToDelete(null)}
      />
    </div>
  );
};
