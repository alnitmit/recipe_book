import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, IconButton, Stack, TextField, Typography } from '@mui/material';
import { useDeferredValue, useMemo, useState } from 'react';

import { useCreateUserMutation, useDeleteUserMutation, useGetUsersQuery, useUpdateUserMutation } from '@/entities/user/api/userApi.ts';
import type { User, UserPayload } from '@/entities/user/model/types.ts';
import { EntityDialog } from '@/features/common/EntityDialog.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';
import { EntityTable, type EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

export const UsersPage = () => {
  const { showSnackbar } = useAppSnackbar();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [search, setSearch] = useState('');
  const deferredSearch = useDeferredValue(search);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [userToDelete, setUserToDelete] = useState<User | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data, isLoading } = useGetUsersQuery({ page, size, sort: 'createdAt,desc' });
  const [createUser, { isLoading: isCreating }] = useCreateUserMutation();
  const [updateUser, { isLoading: isUpdating }] = useUpdateUserMutation();
  const [deleteUser, { isLoading: isDeleting }] = useDeleteUserMutation();

  const filteredRows = useMemo(() => {
    const query = deferredSearch.trim().toLowerCase();
    if (!query) {
      return data?.content ?? [];
    }

    return (data?.content ?? []).filter((user) =>
      [user.username, user.email].some((value) => value?.toLowerCase().includes(query)),
    );
  }, [data?.content, deferredSearch]);

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

  const handleSubmit = async (payload: UserPayload) => {
    try {
      if (editingUser?.id) {
        await updateUser({ id: editingUser.id, body: payload }).unwrap();
        showSnackbar('Пользователь обновлён', 'success');
      } else {
        await createUser(payload).unwrap();
        showSnackbar('Пользователь создан', 'success');
      }
      setEditingUser(null);
      setFieldErrors({});
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDelete = async () => {
    if (!userToDelete) {
      return;
    }

    try {
      await deleteUser(userToDelete.id).unwrap();
      showSnackbar('Пользователь удалён', 'success');
      setUserToDelete(null);
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
            setEditingUser({ id: 0, username: '', email: '' });
            setFieldErrors({});
          }}
        >
          Добавить пользователя
        </Button>
        <TextField label="Поиск по текущей странице" value={search} onChange={(event) => setSearch(event.target.value)} />
      </div>

      {isLoading && !data ? (
        <PageSkeleton />
      ) : data ? (
        <EntityTable
          columns={columns}
          rows={filteredRows}
          getRowId={(user) => user.id}
          page={data.number}
          rowsPerPage={data.size}
          totalElements={data.totalElements}
          onPageChange={setPage}
          onRowsPerPageChange={(nextSize) => {
            setSize(nextSize);
            setPage(0);
          }}
          actions={(user) => (
            <Stack direction="row" justifyContent="flex-end" spacing={1}>
              <IconButton aria-label="Редактировать" onClick={() => setEditingUser(user)}>
                <EditOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Удалить" color="error" onClick={() => setUserToDelete(user)}>
                <DeleteOutlineIcon />
              </IconButton>
            </Stack>
          )}
        />
      ) : (
        <EmptyState title="Пользователей пока нет" description="Создай автора, чтобы потом назначать его рецептам." />
      )}

      <EntityDialog<UserPayload>
        open={Boolean(editingUser)}
        title={editingUser?.id ? 'Редактировать пользователя' : 'Новый пользователь'}
        initialValues={{ username: editingUser?.username ?? '', email: editingUser?.email ?? '' }}
        loading={isCreating || isUpdating}
        fieldErrors={fieldErrors}
        fields={[
          { name: 'username', label: 'Username', required: true },
          { name: 'email', label: 'Email', type: 'email', required: true },
        ]}
        onSubmit={handleSubmit}
        onClose={() => setEditingUser(null)}
      />

      <ConfirmDialog
        open={Boolean(userToDelete)}
        title="Удалить пользователя?"
        description={userToDelete ? `Пользователь "${userToDelete.username}" будет удалён.` : ''}
        destructive
        loading={isDeleting}
        confirmLabel="Удалить"
        onConfirm={handleDelete}
        onClose={() => setUserToDelete(null)}
      />
    </div>
  );
};
