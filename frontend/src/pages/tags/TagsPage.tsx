import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, IconButton, Stack, TextField, Typography } from '@mui/material';
import { useDeferredValue, useMemo, useState } from 'react';

import { useCreateTagMutation, useDeleteTagMutation, useGetTagsQuery, useUpdateTagMutation } from '@/entities/tag/api/tagApi.ts';
import type { Tag, TagPayload } from '@/entities/tag/model/types.ts';
import { EntityDialog } from '@/features/common/EntityDialog.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarProvider.tsx';
import { EntityTable, type EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

export function TagsPage() {
  const { showSnackbar } = useAppSnackbar();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [search, setSearch] = useState('');
  const deferredSearch = useDeferredValue(search);
  const [editingTag, setEditingTag] = useState<Tag | null>(null);
  const [tagToDelete, setTagToDelete] = useState<Tag | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data, isLoading } = useGetTagsQuery({ page, size, sort: 'name,asc' });
  const [createTag, { isLoading: isCreating }] = useCreateTagMutation();
  const [updateTag, { isLoading: isUpdating }] = useUpdateTagMutation();
  const [deleteTag, { isLoading: isDeleting }] = useDeleteTagMutation();

  const filteredRows = useMemo(() => {
    const query = deferredSearch.trim().toLowerCase();
    if (!query) {
      return data?.content ?? [];
    }

    return (data?.content ?? []).filter((tag) => tag.name.toLowerCase().includes(query));
  }, [data?.content, deferredSearch]);

  const columns = useMemo<Array<EntityTableColumn<Tag>>>(
    () => [{ key: 'name', label: 'Название', render: (tag) => <Typography fontWeight={700}>{tag.name}</Typography> }],
    [],
  );

  const handleSubmit = async (payload: TagPayload) => {
    try {
      if (editingTag?.id) {
        await updateTag({ id: editingTag.id, body: payload }).unwrap();
        showSnackbar('Тег обновлён', 'success');
      } else {
        await createTag(payload).unwrap();
        showSnackbar('Тег создан', 'success');
      }
      setEditingTag(null);
      setFieldErrors({});
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDelete = async () => {
    if (!tagToDelete) {
      return;
    }

    try {
      await deleteTag(tagToDelete.id).unwrap();
      showSnackbar('Тег удалён', 'success');
      setTagToDelete(null);
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
            setEditingTag({ id: 0, name: '' });
            setFieldErrors({});
          }}
        >
          Добавить тег
        </Button>
        <TextField label="Поиск по текущей странице" value={search} onChange={(event) => setSearch(event.target.value)} />
      </div>

      {isLoading && !data ? (
        <PageSkeleton />
      ) : data ? (
        <EntityTable
          columns={columns}
          rows={filteredRows}
          getRowId={(tag) => tag.id}
          page={data.number}
          rowsPerPage={data.size}
          totalElements={data.totalElements}
          onPageChange={setPage}
          onRowsPerPageChange={(nextSize) => {
            setSize(nextSize);
            setPage(0);
          }}
          actions={(tag) => (
            <Stack direction="row" justifyContent="flex-end" spacing={1}>
              <IconButton aria-label="Редактировать" onClick={() => setEditingTag(tag)}>
                <EditOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Удалить" color="error" onClick={() => setTagToDelete(tag)}>
                <DeleteOutlineIcon />
              </IconButton>
            </Stack>
          )}
        />
      ) : (
        <EmptyState title="Тегов пока нет" description="Создай теги, чтобы размечать рецепты по признакам и темам." />
      )}

      <EntityDialog<TagPayload>
        open={Boolean(editingTag)}
        title={editingTag?.id ? 'Редактировать тег' : 'Новый тег'}
        initialValues={{ name: editingTag?.name ?? '' }}
        loading={isCreating || isUpdating}
        fieldErrors={fieldErrors}
        fields={[{ name: 'name', label: 'Название', required: true }]}
        onSubmit={handleSubmit}
        onClose={() => setEditingTag(null)}
      />

      <ConfirmDialog
        open={Boolean(tagToDelete)}
        title="Удалить тег?"
        description={tagToDelete ? `Тег "${tagToDelete.name}" будет удалён.` : ''}
        destructive
        loading={isDeleting}
        confirmLabel="Удалить"
        onConfirm={handleDelete}
        onClose={() => setTagToDelete(null)}
      />
    </div>
  );
}
