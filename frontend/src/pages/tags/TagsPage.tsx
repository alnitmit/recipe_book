import { Typography } from '@mui/material';
import { useMemo } from 'react';

import { useCreateTagMutation, useDeleteTagMutation, useGetTagsQuery, useUpdateTagMutation } from '@/entities/tag/api/tagApi.ts';
import type { Tag, TagPayload } from '@/entities/tag/model/types.ts';
import { CrudEntityPage, useCrudPageState } from '@/features/common/index.ts';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';
import type { EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

const emptyTag: Tag = {
  id: 0,
  name: '',
};

export const TagsPage = () => {
  const { showSnackbar } = useAppSnackbar();
  const [createTag, { isLoading: isCreating }] = useCreateTagMutation();
  const [updateTag, { isLoading: isUpdating }] = useUpdateTagMutation();
  const [deleteTag, { isLoading: isDeleting }] = useDeleteTagMutation();

  const state = useCrudPageState<Tag>({
    rows: undefined,
    createDraft: () => emptyTag,
    searchMatcher: (tag, query) => tag.name.toLowerCase().includes(query),
  });

  const { data, isLoading } = useGetTagsQuery({ page: state.page, size: state.size, sort: 'name,asc' });

  const columns = useMemo<Array<EntityTableColumn<Tag>>>(
    () => [{ key: 'name', label: 'Название', render: (tag) => <Typography fontWeight={700}>{tag.name}</Typography> }],
    [],
  );

  const filteredRows = useMemo(() => {
    const query = state.search.trim().toLowerCase();
    const rows = data?.content ?? [];

    if (!query) {
      return rows;
    }

    return rows.filter((tag) => tag.name.toLowerCase().includes(query));
  }, [data?.content, state.search]);

  const handleSubmit = async (payload: TagPayload) => {
    try {
      if (state.editingEntity?.id) {
        await updateTag({ id: state.editingEntity.id, body: payload }).unwrap();
        showSnackbar('Тег обновлен', 'success');
      } else {
        await createTag(payload).unwrap();
        showSnackbar('Тег создан', 'success');
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
      await deleteTag(state.entityToDelete.id).unwrap();
      showSnackbar('Тег удален', 'success');
      state.closeDelete();
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <CrudEntityPage<Tag, TagPayload>
      addLabel="Добавить тег"
      searchLabel="Поиск по текущей странице"
      emptyTitle="Тегов пока нет"
      emptyDescription="Создай теги, чтобы размечать рецепты по признакам и темам."
      dialogCreateTitle="Новый тег"
      dialogEditTitle="Редактировать тег"
      deleteTitle="Удалить тег?"
      deleteDescription={(tag) => `Тег "${tag.name}" будет удален.`}
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
      fields={[{ name: 'name', label: 'Название', required: true }]}
      getRowId={(tag) => tag.id}
      toFormValues={(tag) => ({ name: tag?.name ?? '' })}
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
