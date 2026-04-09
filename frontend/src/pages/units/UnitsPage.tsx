import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, IconButton, Stack, TextField, Typography } from '@mui/material';
import { useDeferredValue, useMemo, useState } from 'react';

import { useCreateUnitMutation, useDeleteUnitMutation, useGetUnitsQuery, useUpdateUnitMutation } from '@/entities/unit/api/unitApi.ts';
import type { Unit, UnitPayload } from '@/entities/unit/model/types.ts';
import { EntityDialog } from '@/features/common/EntityDialog.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarProvider.tsx';
import { EntityTable, type EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

const emptyUnit: UnitPayload = { name: '', abbreviation: '', description: '' };

export function UnitsPage() {
  const { showSnackbar } = useAppSnackbar();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [search, setSearch] = useState('');
  const deferredSearch = useDeferredValue(search);
  const [editingUnit, setEditingUnit] = useState<Unit | null>(null);
  const [unitToDelete, setUnitToDelete] = useState<Unit | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data, isLoading } = useGetUnitsQuery({ page, size, sort: 'name,asc' });
  const [createUnit, { isLoading: isCreating }] = useCreateUnitMutation();
  const [updateUnit, { isLoading: isUpdating }] = useUpdateUnitMutation();
  const [deleteUnit, { isLoading: isDeleting }] = useDeleteUnitMutation();

  const filteredRows = useMemo(() => {
    const query = deferredSearch.trim().toLowerCase();
    if (!query) {
      return data?.content ?? [];
    }

    return (data?.content ?? []).filter((unit) =>
      [unit.name, unit.abbreviation, unit.description].some((value) => value?.toLowerCase().includes(query)),
    );
  }, [data?.content, deferredSearch]);

  const columns = useMemo<Array<EntityTableColumn<Unit>>>(
    () => [
      { key: 'name', label: 'Название', render: (unit) => <Typography fontWeight={700}>{unit.name}</Typography> },
      { key: 'abbreviation', label: 'Сокращение', render: (unit) => unit.abbreviation || '—' },
      { key: 'description', label: 'Описание', render: (unit) => unit.description || '—' },
    ],
    [],
  );

  const handleSubmit = async (payload: UnitPayload) => {
    try {
      if (editingUnit?.id) {
        await updateUnit({ id: editingUnit.id, body: payload }).unwrap();
        showSnackbar('Единица измерения обновлена', 'success');
      } else {
        await createUnit(payload).unwrap();
        showSnackbar('Единица измерения создана', 'success');
      }
      setEditingUnit(null);
      setFieldErrors({});
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDelete = async () => {
    if (!unitToDelete) {
      return;
    }

    try {
      await deleteUnit(unitToDelete.id).unwrap();
      showSnackbar('Единица измерения удалена', 'success');
      setUnitToDelete(null);
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
            setEditingUnit({ id: 0, ...emptyUnit });
            setFieldErrors({});
          }}
        >
          Добавить единицу
        </Button>
        <TextField label="Поиск по текущей странице" value={search} onChange={(event) => setSearch(event.target.value)} />
      </div>

      {isLoading && !data ? (
        <PageSkeleton />
      ) : data ? (
        <EntityTable
          columns={columns}
          rows={filteredRows}
          getRowId={(unit) => unit.id}
          page={data.number}
          rowsPerPage={data.size}
          totalElements={data.totalElements}
          onPageChange={setPage}
          onRowsPerPageChange={(nextSize) => {
            setSize(nextSize);
            setPage(0);
          }}
          actions={(unit) => (
            <Stack direction="row" justifyContent="flex-end" spacing={1}>
              <IconButton aria-label="Редактировать" onClick={() => setEditingUnit(unit)}>
                <EditOutlinedIcon />
              </IconButton>
              <IconButton aria-label="Удалить" color="error" onClick={() => setUnitToDelete(unit)}>
                <DeleteOutlineIcon />
              </IconButton>
            </Stack>
          )}
        />
      ) : (
        <EmptyState title="Единиц измерения пока нет" description="Создай базовые единицы, чтобы формы ингредиентов были удобнее." />
      )}

      <EntityDialog<UnitPayload>
        open={Boolean(editingUnit)}
        title={editingUnit?.id ? 'Редактировать единицу' : 'Новая единица'}
        initialValues={{
          name: editingUnit?.name ?? '',
          abbreviation: editingUnit?.abbreviation ?? '',
          description: editingUnit?.description ?? '',
        }}
        loading={isCreating || isUpdating}
        fieldErrors={fieldErrors}
        fields={[
          { name: 'name', label: 'Название', required: true },
          { name: 'abbreviation', label: 'Сокращение' },
          { name: 'description', label: 'Описание', type: 'multiline', rows: 3 },
        ]}
        onSubmit={handleSubmit}
        onClose={() => setEditingUnit(null)}
      />

      <ConfirmDialog
        open={Boolean(unitToDelete)}
        title="Удалить единицу измерения?"
        description={unitToDelete ? `Единица "${unitToDelete.name}" будет удалена.` : ''}
        destructive
        loading={isDeleting}
        confirmLabel="Удалить"
        onConfirm={handleDelete}
        onClose={() => setUnitToDelete(null)}
      />
    </div>
  );
}
