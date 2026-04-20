import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, IconButton, Stack, TextField } from '@mui/material';

import type { FieldConfig } from '@/features/common/EntityDialog.tsx';
import { EntityDialog } from '@/features/common/EntityDialog.tsx';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import type { PageResponse } from '@/shared/types/api.ts';
import { EntityTable, type EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

type CrudEntityPageProps<TEntity, TPayload extends object> = {
  addLabel: string;
  searchLabel: string;
  emptyTitle: string;
  emptyDescription: string;
  dialogCreateTitle: string;
  dialogEditTitle: string;
  deleteTitle: string;
  deleteDescription: (entity: TEntity) => string;
  data?: PageResponse<TEntity>;
  rows: TEntity[];
  columns: Array<EntityTableColumn<TEntity>>;
  search: string;
  editingEntity: TEntity | null;
  isEditing: boolean;
  entityToDelete: TEntity | null;
  loading: boolean;
  submitting: boolean;
  deleting: boolean;
  fieldErrors: Record<string, string>;
  fields: Array<FieldConfig<TPayload>>;
  getRowId: (entity: TEntity) => number | string;
  toFormValues: (entity: TEntity | null) => TPayload;
  onSearchChange: (value: string) => void;
  onCreateClick: () => void;
  onEditClick: (entity: TEntity) => void;
  onDeleteClick: (entity: TEntity) => void;
  onPageChange: (page: number) => void;
  onRowsPerPageChange: (size: number) => void;
  onSubmit: (payload: TPayload) => void;
  onDialogClose: () => void;
  onDeleteConfirm: () => void;
  onDeleteClose: () => void;
};

export const CrudEntityPage = <TEntity, TPayload extends object>({
  addLabel,
  searchLabel,
  emptyTitle,
  emptyDescription,
  dialogCreateTitle,
  dialogEditTitle,
  deleteTitle,
  deleteDescription,
  data,
  rows,
  columns,
  search,
  editingEntity,
  isEditing,
  entityToDelete,
  loading,
  submitting,
  deleting,
  fieldErrors,
  fields,
  getRowId,
  toFormValues,
  onSearchChange,
  onCreateClick,
  onEditClick,
  onDeleteClick,
  onPageChange,
  onRowsPerPageChange,
  onSubmit,
  onDialogClose,
  onDeleteConfirm,
  onDeleteClose,
}: CrudEntityPageProps<TEntity, TPayload>) => {
  return (
    <div className={pageStyles.page}>
      <div className={pageStyles.toolbar}>
        <Button startIcon={<AddOutlinedIcon />} variant="contained" onClick={onCreateClick}>
          {addLabel}
        </Button>
        <TextField label={searchLabel} value={search} onChange={(event) => onSearchChange(event.target.value)} />
      </div>

      {loading && !data ? (
        <PageSkeleton />
      ) : data ? (
        <EntityTable
          columns={columns}
          rows={rows}
          getRowId={getRowId}
          page={data.number}
          rowsPerPage={data.size}
          totalElements={data.totalElements}
          onPageChange={onPageChange}
          onRowsPerPageChange={onRowsPerPageChange}
          actions={(entity) => (
            <Stack direction="row" justifyContent="flex-end" spacing={1}>
              <IconButton aria-label="Редактировать" onClick={() => onEditClick(entity)}>
                <EditOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Удалить" color="error" onClick={() => onDeleteClick(entity)}>
                <DeleteOutlineIcon />
              </IconButton>
            </Stack>
          )}
        />
      ) : (
        <EmptyState title={emptyTitle} description={emptyDescription} />
      )}

      <EntityDialog<TPayload>
        open={Boolean(editingEntity)}
        title={isEditing ? dialogEditTitle : dialogCreateTitle}
        initialValues={toFormValues(editingEntity)}
        loading={submitting}
        fieldErrors={fieldErrors}
        fields={fields}
        onSubmit={onSubmit}
        onClose={onDialogClose}
      />

      <ConfirmDialog
        open={Boolean(entityToDelete)}
        title={deleteTitle}
        description={entityToDelete ? deleteDescription(entityToDelete) : ''}
        destructive
        loading={deleting}
        confirmLabel="Удалить"
        onConfirm={onDeleteConfirm}
        onClose={onDeleteClose}
      />
    </div>
  );
};
