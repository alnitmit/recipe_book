import { useDeferredValue, useMemo, useState } from 'react';

type CrudPageStateParams<TEntity> = {
  rows?: TEntity[];
  createDraft: () => TEntity;
  searchMatcher: (entity: TEntity, query: string) => boolean;
  initialPage?: number;
  initialSize?: number;
};

export const useCrudPageState = <TEntity,>({
  rows,
  createDraft,
  searchMatcher,
  initialPage = 0,
  initialSize = 10,
}: CrudPageStateParams<TEntity>) => {
  const [page, setPage] = useState(initialPage);
  const [size, setSize] = useState(initialSize);
  const [search, setSearch] = useState('');
  const deferredSearch = useDeferredValue(search);
  const [editingEntity, setEditingEntity] = useState<TEntity | null>(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const [entityToDelete, setEntityToDelete] = useState<TEntity | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const filteredRows = useMemo(() => {
    const query = deferredSearch.trim().toLowerCase();
    const sourceRows = rows ?? [];

    if (!query) {
      return sourceRows;
    }

    return sourceRows.filter((entity) => searchMatcher(entity, query));
  }, [deferredSearch, rows, searchMatcher]);

  return {
    page,
    size,
    search,
    filteredRows,
    editingEntity,
    isEditMode,
    entityToDelete,
    fieldErrors,
    setPage,
    setSearch,
    setFieldErrors,
    setSize: (nextSize: number) => {
      setSize(nextSize);
      setPage(0);
    },
    startCreate: () => {
      setIsEditMode(false);
      setEditingEntity(createDraft());
      setFieldErrors({});
    },
    startEdit: (entity: TEntity) => {
      setIsEditMode(true);
      setEditingEntity(entity);
      setFieldErrors({});
    },
    requestDelete: (entity: TEntity) => {
      setEntityToDelete(entity);
    },
    closeEditor: () => {
      setIsEditMode(false);
      setEditingEntity(null);
    },
    closeDelete: () => {
      setEntityToDelete(null);
    },
    resetFormState: () => {
      setIsEditMode(false);
      setEditingEntity(null);
      setFieldErrors({});
    },
  };
};
