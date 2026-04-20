import { Typography } from '@mui/material';
import { useMemo } from 'react';

import { useCreateUnitMutation, useDeleteUnitMutation, useGetUnitsQuery, useUpdateUnitMutation } from '@/entities/unit/api/unitApi.ts';
import type { Unit, UnitPayload } from '@/entities/unit/model/types.ts';
import { CrudEntityPage, useCrudPageState } from '@/features/common/index.ts';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';
import type { EntityTableColumn } from '@/widgets/entity-table/EntityTable.tsx';

const emptyUnit: Unit = {
  id: 0,
  name: '',
  abbreviation: '',
  description: '',
};

export const UnitsPage = () => {
  const { showSnackbar } = useAppSnackbar();
  const [createUnit, { isLoading: isCreating }] = useCreateUnitMutation();
  const [updateUnit, { isLoading: isUpdating }] = useUpdateUnitMutation();
  const [deleteUnit, { isLoading: isDeleting }] = useDeleteUnitMutation();

  const state = useCrudPageState<Unit>({
    rows: undefined,
    createDraft: () => emptyUnit,
    searchMatcher: (unit, query) =>
      [unit.name, unit.abbreviation, unit.description].some((value) => value?.toLowerCase().includes(query)),
  });

  const { data, isLoading } = useGetUnitsQuery({ page: state.page, size: state.size, sort: 'name,asc' });

  const columns = useMemo<Array<EntityTableColumn<Unit>>>(
    () => [
      { key: 'name', label: 'Название', render: (unit) => <Typography fontWeight={700}>{unit.name}</Typography> },
      { key: 'abbreviation', label: 'Сокращение', render: (unit) => unit.abbreviation || '—' },
      { key: 'description', label: 'Описание', render: (unit) => unit.description || '—' },
    ],
    [],
  );

  const filteredRows = useMemo(() => {
    const query = state.search.trim().toLowerCase();
    const rows = data?.content ?? [];

    if (!query) {
      return rows;
    }

    return rows.filter((unit) => [unit.name, unit.abbreviation, unit.description].some((value) => value?.toLowerCase().includes(query)));
  }, [data?.content, state.search]);

  const handleSubmit = async (payload: UnitPayload) => {
    try {
      if (state.editingEntity?.id) {
        await updateUnit({ id: state.editingEntity.id, body: payload }).unwrap();
        showSnackbar('Единица измерения обновлена', 'success');
      } else {
        await createUnit(payload).unwrap();
        showSnackbar('Единица измерения создана', 'success');
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
      await deleteUnit(state.entityToDelete.id).unwrap();
      showSnackbar('Единица измерения удалена', 'success');
      state.closeDelete();
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <CrudEntityPage<Unit, UnitPayload>
      addLabel="Добавить единицу"
      searchLabel="Поиск по текущей странице"
      emptyTitle="Единиц измерения пока нет"
      emptyDescription="Создай базовые единицы, чтобы формы ингредиентов были удобнее."
      dialogCreateTitle="Новая единица"
      dialogEditTitle="Редактировать единицу"
      deleteTitle="Удалить единицу измерения?"
      deleteDescription={(unit) => `Единица "${unit.name}" будет удалена.`}
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
        { name: 'abbreviation', label: 'Сокращение' },
        { name: 'description', label: 'Описание', type: 'multiline', rows: 3 },
      ]}
      getRowId={(unit) => unit.id}
      toFormValues={(unit) => ({
        name: unit?.name ?? '',
        abbreviation: unit?.abbreviation ?? '',
        description: unit?.description ?? '',
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
