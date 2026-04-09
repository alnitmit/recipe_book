import { useMemo } from 'react';

import { useGetRecipesQuery } from '@/entities/recipe/api/recipeApi.ts';
import { useGetUnitsQuery } from '@/entities/unit/api/unitApi.ts';
import type { IngredientPayload } from '@/entities/ingredient/model/types.ts';
import { EntityDialog } from '@/features/common/EntityDialog.tsx';

interface IngredientDialogProps {
  open: boolean;
  title: string;
  initialValue?: Partial<IngredientPayload & { id: number }>;
  loading?: boolean;
  fieldErrors?: Record<string, string>;
  fixedRecipeId?: number;
  onSubmit: (payload: IngredientPayload) => void;
  onClose: () => void;
}

interface IngredientFormValues {
  name: string;
  quantity: string;
  unitId: number | null;
  recipeId: number | null;
}

function toInitialValues(initialValue?: Partial<IngredientPayload>): IngredientFormValues {
  return {
    name: initialValue?.name ?? '',
    quantity: initialValue?.quantity ?? '',
    unitId: initialValue?.unitId ?? null,
    recipeId: initialValue?.recipeId ?? null,
  };
}

export function IngredientDialog({
  open,
  title,
  initialValue,
  loading = false,
  fieldErrors = {},
  fixedRecipeId,
  onSubmit,
  onClose,
}: IngredientDialogProps) {
  const { data: unitsPage } = useGetUnitsQuery({ page: 0, size: 100, sort: 'name,asc' });
  const { data: recipesPage } = useGetRecipesQuery({ page: 0, size: 100, sort: 'title,asc' });

  const values = useMemo(
    () =>
      toInitialValues({
        ...initialValue,
        recipeId: fixedRecipeId ?? initialValue?.recipeId,
      }),
    [fixedRecipeId, initialValue],
  );

  return (
    <EntityDialog<IngredientFormValues>
      open={open}
      title={title}
      loading={loading}
      fieldErrors={fieldErrors}
      initialValues={values}
      fields={[
        { name: 'name', label: 'Название', required: true },
        { name: 'quantity', label: 'Количество', required: true },
        {
          name: 'unitId',
          label: 'Единица измерения',
          type: 'select',
          options: (unitsPage?.content ?? []).map((unit) => ({ label: unit.name, value: unit.id })),
        },
        {
          name: 'recipeId',
          label: 'Рецепт',
          type: 'select',
          required: true,
          disabled: Boolean(fixedRecipeId),
          options: (recipesPage?.content ?? []).map((recipe) => ({ label: recipe.title, value: recipe.id })),
        },
      ]}
      onSubmit={(nextValues) =>
        onSubmit({
          name: nextValues.name,
          quantity: nextValues.quantity,
          unitId: nextValues.unitId,
          recipeId: Number(nextValues.recipeId),
        })
      }
      onClose={onClose}
    />
  );
}
