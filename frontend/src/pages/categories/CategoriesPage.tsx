import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, IconButton, Stack, TextField, Typography } from '@mui/material';
import { useDeferredValue, useMemo, useState } from 'react';

import {
  useCreateCategoryMutation,
  useDeleteCategoryMutation,
  useGetCategoriesQuery,
  useUpdateCategoryMutation,
} from '@/entities/category/api/categoryApi.ts';
import type { Category, CategoryPayload } from '@/entities/category/model/types.ts';
import { EntityDialog } from '@/features/common/EntityDialog.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarProvider.tsx';
import { EntityTable, type EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

const emptyCategory: CategoryPayload = { name: '', description: '' };

export function CategoriesPage() {
  const { showSnackbar } = useAppSnackbar();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [search, setSearch] = useState('');
  const deferredSearch = useDeferredValue(search);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [categoryToDelete, setCategoryToDelete] = useState<Category | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data, isLoading } = useGetCategoriesQuery({ page, size, sort: 'name,asc' });
  const [createCategory, { isLoading: isCreating }] = useCreateCategoryMutation();
  const [updateCategory, { isLoading: isUpdating }] = useUpdateCategoryMutation();
  const [deleteCategory, { isLoading: isDeleting }] = useDeleteCategoryMutation();

  const filteredRows = useMemo(() => {
    const query = deferredSearch.trim().toLowerCase();
    if (!query) {
      return data?.content ?? [];
    }

    return (data?.content ?? []).filter((category) =>
      [category.name, category.description].some((value) => value?.toLowerCase().includes(query)),
    );
  }, [data?.content, deferredSearch]);

  const columns = useMemo<Array<EntityTableColumn<Category>>>(
    () => [
      { key: 'name', label: 'Название', render: (category) => <Typography fontWeight={700}>{category.name}</Typography> },
      { key: 'description', label: 'Описание', render: (category) => category.description || '—' },
    ],
    [],
  );

  const handleSubmit = async (payload: CategoryPayload) => {
    try {
      if (editingCategory) {
        await updateCategory({ id: editingCategory.id, body: payload }).unwrap();
        showSnackbar('Категория обновлена', 'success');
      } else {
        await createCategory(payload).unwrap();
        showSnackbar('Категория создана', 'success');
      }
      setEditingCategory(null);
      setFieldErrors({});
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDelete = async () => {
    if (!categoryToDelete) {
      return;
    }

    try {
      await deleteCategory(categoryToDelete.id).unwrap();
      showSnackbar('Категория удалена', 'success');
      setCategoryToDelete(null);
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <div className={pageStyles.page}>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.toolbarLeft}>
          <Button
            startIcon={<AddOutlinedIcon />}
            variant="contained"
            onClick={() => {
              setEditingCategory({ id: 0, ...emptyCategory });
              setFieldErrors({});
            }}
          >
            Добавить категорию
          </Button>
        </div>
        <div className={pageStyles.toolbarRight}>
          <TextField
            label="Поиск по текущей странице"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>
      </div>

      {isLoading && !data ? (
        <PageSkeleton />
      ) : data ? (
        <EntityTable
          columns={columns}
          rows={filteredRows}
          getRowId={(category) => category.id}
          page={data.number}
          rowsPerPage={data.size}
          totalElements={data.totalElements}
          onPageChange={setPage}
          onRowsPerPageChange={(nextSize) => {
            setSize(nextSize);
            setPage(0);
          }}
          actions={(category) => (
            <Stack direction="row" justifyContent="flex-end" spacing={1}>
              <IconButton aria-label="Редактировать" onClick={() => setEditingCategory(category)}>
                <EditOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Удалить" color="error" onClick={() => setCategoryToDelete(category)}>
                <DeleteOutlineIcon />
              </IconButton>
            </Stack>
          )}
        />
      ) : (
        <EmptyState title="Категорий пока нет" description="Создай первую категорию для удобной группировки рецептов." />
      )}

      <EntityDialog<CategoryPayload>
        open={Boolean(editingCategory)}
        title={editingCategory?.id ? 'Редактировать категорию' : 'Новая категория'}
        initialValues={{
          name: editingCategory?.name ?? '',
          description: editingCategory?.description ?? '',
        }}
        loading={isCreating || isUpdating}
        fieldErrors={fieldErrors}
        fields={[
          { name: 'name', label: 'Название', required: true },
          { name: 'description', label: 'Описание', type: 'multiline', rows: 3 },
        ]}
        onSubmit={handleSubmit}
        onClose={() => setEditingCategory(null)}
      />

      <ConfirmDialog
        open={Boolean(categoryToDelete)}
        title="Удалить категорию?"
        description={categoryToDelete ? `Категория "${categoryToDelete.name}" будет удалена.` : ''}
        destructive
        loading={isDeleting}
        confirmLabel="Удалить"
        onConfirm={handleDelete}
        onClose={() => setCategoryToDelete(null)}
      />
    </div>
  );
}
