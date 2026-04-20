import { Typography } from '@mui/material';
import { useMemo } from 'react';

import { useCreateUserMutation, useDeleteUserMutation, useGetUsersQuery, useUpdateUserMutation } from '@/entities/user/api/userApi.ts';
import type { User, UserPayload } from '@/entities/user/model/types.ts';
import { CrudEntityPage, useCrudPageState } from '@/features/common/index.ts';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';
import type { EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

const emptyUser: User = {
  id: 0,
  username: '',
  email: '',
};

export const UsersPage = () => {
  const { showSnackbar } = useAppSnackbar();
  const [createUser, { isLoading: isCreating }] = useCreateUserMutation();
  const [updateUser, { isLoading: isUpdating }] = useUpdateUserMutation();
  const [deleteUser, { isLoading: isDeleting }] = useDeleteUserMutation();

  const state = useCrudPageState<User>({
    rows: undefined,
    createDraft: () => emptyUser,
    searchMatcher: (user, query) => [user.username, user.email].some((value) => value?.toLowerCase().includes(query)),
  });

  const { data, isLoading } = useGetUsersQuery({ page: state.page, size: state.size, sort: 'createdAt,desc' });

  const columns = useMemo<Array<EntityTableColumn<User>>>(
    () => [
      { key: 'username', label: 'Username', render: (user) => <Typography fontWeight={700}>{user.username}</Typography> },
      { key: 'email', label: 'Email', render: (user) => user.email },
      {
        key: 'createdAt',
        label: 'Создан',
        render: (user) => (user.createdAt ? new Date(user.createdAt).toLocaleString('ru-RU') : '—'),
      },
    ],
    [],
  );

  const filteredRows = useMemo(() => {
    const query = state.search.trim().toLowerCase();
    const rows = data?.content ?? [];

    if (!query) {
      return rows;
    }

    return rows.filter((user) => [user.username, user.email].some((value) => value?.toLowerCase().includes(query)));
  }, [data?.content, state.search]);

  const handleSubmit = async (payload: UserPayload) => {
    try {
      if (state.editingEntity?.id) {
        await updateUser({ id: state.editingEntity.id, body: payload }).unwrap();
        showSnackbar('Пользователь обновлен', 'success');
      } else {
        await createUser(payload).unwrap();
        showSnackbar('Пользователь создан', 'success');
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
      await deleteUser(state.entityToDelete.id).unwrap();
      showSnackbar('Пользователь удален', 'success');
      state.closeDelete();
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <CrudEntityPage<User, UserPayload>
      addLabel="Добавить пользователя"
      searchLabel="Поиск по текущей странице"
      emptyTitle="Пользователей пока нет"
      emptyDescription="Создай автора, чтобы потом назначать его рецептам."
      dialogCreateTitle="Новый пользователь"
      dialogEditTitle="Редактировать пользователя"
      deleteTitle="Удалить пользователя?"
      deleteDescription={(user) => `Пользователь "${user.username}" будет удален.`}
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
        { name: 'username', label: 'Username', required: true },
        { name: 'email', label: 'Email', type: 'email', required: true },
      ]}
      getRowId={(user) => user.id}
      toFormValues={(user) => ({
        username: user?.username ?? '',
        email: user?.email ?? '',
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
