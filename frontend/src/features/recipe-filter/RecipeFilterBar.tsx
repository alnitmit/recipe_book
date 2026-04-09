import { Button, MenuItem, Paper, Stack, TextField } from '@mui/material';
import { useEffect, useState } from 'react';

import type { RecipeFilterParams } from '@/entities/recipe/model/types.ts';

type RecipeFilterBarProps = {
  value: RecipeFilterParams;
  onApply: (nextValue: RecipeFilterParams) => void;
  onReset: () => void;
};

export const RecipeFilterBar = ({ value, onApply, onReset }: RecipeFilterBarProps) => {
  const [filters, setFilters] = useState(value);

  useEffect(() => {
    setFilters(value);
  }, [value]);

  return (
    <Paper sx={{ p: 2.5 }}>
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
        <TextField
          fullWidth
          label="Фильтр по категории"
          placeholder="Например, супы"
          value={filters.category ?? ''}
          onChange={(event) => setFilters((previous) => ({ ...previous, category: event.target.value, page: 0 }))}
        />
        <TextField
          fullWidth
          label="Мин. ингредиентов"
          type="number"
          value={filters.minIngredients ?? ''}
          onChange={(event) =>
            setFilters((previous) => ({
              ...previous,
              minIngredients: event.target.value === '' ? undefined : Number(event.target.value),
              page: 0,
            }))
          }
        />
        <TextField
          select
          fullWidth
          label="Сортировка"
          value={filters.sort ?? 'id,desc'}
          onChange={(event) => setFilters((previous) => ({ ...previous, sort: event.target.value, page: 0 }))}
        >
          <MenuItem value="id,desc">Сначала новые</MenuItem>
          <MenuItem value="id,asc">Сначала старые</MenuItem>
          <MenuItem value="title,asc">Название А-Я</MenuItem>
          <MenuItem value="title,desc">Название Я-А</MenuItem>
        </TextField>
        <Button
          variant="contained"
          sx={{
            fontSize: '0.82rem',
            px: 1.5,
            minWidth: 0,
            flexShrink: 0,
            whiteSpace: 'nowrap',
          }}
          onClick={() => onApply(filters)}
        >
          Применить
        </Button>
        <Button color="inherit" onClick={onReset}>
          Сбросить
        </Button>
      </Stack>
    </Paper>
  );
};
