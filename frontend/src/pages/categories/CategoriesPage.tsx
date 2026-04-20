import { Typography } from '@mui/material';
import { useMemo } from 'react';

import {
  useCreateCategoryMutation,
  useDeleteCategoryMutation,
  useGetCategoriesQuery,
  useUpdateCategoryMutation,
} from '@/entities/category/api/categoryApi.ts';
import type { Category, CategoryPayload } from '@/entities/category/model/types.ts';
import { CrudEntityPage, useCrudPageState } from '@/features/common/index.ts';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';
import type { EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

const emptyCategory: Category = {
  id: 0,
  name: '',
  description: '',
};

export const CategoriesPage = () => {
  const { showSnackbar } = useAppSnackbar();
  const [createCategory, { isLoading: isCreating }] = useCreateCategoryMutation();
  const [updateCategory, { isLoading: isUpdating }] = useUpdateCategoryMutation();
  const [deleteCategory, { isLoading: isDeleting }] = useDeleteCategoryMutation();

  const state = useCrudPageState<Category>({
    rows: undefined,
    createDraft: () => emptyCategory,
    searchMatcher: (category, query) =>
      [category.name, category.description].some((value) => value?.toLowerCase().includes(query)),
  });

  const { data, isLoading } = useGetCategoriesQuery({ page: state.page, size: state.size, sort: 'name,asc' });

  const columns = useMemo<Array<EntityTableColumn<Category>>>(
    () => [
      { key: 'name', label: 'Название', render: (category) => <Typography fontWeight={700}>{category.name}</Typography> },
      { key: 'description', label: 'Описание', render: (category) => category.description || '—' },
    ],
    [],
  );

  const filteredRows = useMemo(() => {
    const query = state.search.trim().toLowerCase();
    const rows = data?.content ?? [];

    if (!query) {
      return rows;
    }

    return rows.filter((category) => [category.name, category.description].some((value) => value?.toLowerCase().includes(query)));
  }, [data?.content, state.search]);

  const handleSubmit = async (payload: CategoryPayload) => {
    try {
      if (state.editingEntity?.id) {
        await updateCategory({ id: state.editingEntity.id, body: payload }).unwrap();
        showSnackbar('Категория обновлена', 'success');
      } else {
        await createCategory(payload).unwrap();
        showSnackbar('Категория создана', 'success');
      }

      state.resetFormState();
    } catch (error) {
      state.setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDelete = async () => {
    if (!state.entityToDelete) {
      return;
    }

    try {
      await deleteCategory(state.entityToDelete.id).unwrap();
      showSnackbar('Категория удалена', 'success');
      state.closeDelete();
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <CrudEntityPage<Category, CategoryPayload>
      addLabel="Добавить категорию"
      searchLabel="Поиск по текущей странице"
      emptyTitle="Категорий пока нет"
      emptyDescription="Создай первую категорию для удобной группировки рецептов."
      dialogCreateTitle="Новая категория"
      dialogEditTitle="Редактировать категорию"
      deleteTitle="Удалить категорию?"
      deleteDescription={(category) => `Категория "${category.name}" будет удалена.`}
      data={data}
      rows={filteredRows}
      columns={columns}
      search={state.search}
      editingEntity={state.editingEntity}
      isEditing={state.isEditMode}
      entityToDelete={state.entityToDelete}
      loading={isLoading}
      submitting={isCreating || isUpdating}
      deleting={isDeleting}
      fieldErrors={state.fieldErrors}
      fields={[
        { name: 'name', label: 'Название', required: true },
        { name: 'description', label: 'Описание', type: 'multiline', rows: 3 },
      ]}
      getRowId={(category) => category.id}
      toFormValues={(category) => ({
        name: category?.name ?? '',
        description: category?.description ?? '',
      })}
      onSearchChange={state.setSearch}
      onCreateClick={state.startCreate}
      onEditClick={state.startEdit}
      onDeleteClick={state.requestDelete}
      onPageChange={state.setPage}
      onRowsPerPageChange={state.setSize}
      onSubmit={handleSubmit}
      onDialogClose={state.closeEditor}
      onDeleteConfirm={handleDelete}
      onDeleteClose={state.closeDelete}
    />
  );
};
