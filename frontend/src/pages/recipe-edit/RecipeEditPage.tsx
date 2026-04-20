import { Typography } from '@mui/material';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { useGetRecipeByIdQuery, useUpdateRecipeMutation } from '@/entities/recipe/api/recipeApi.ts';
import type { RecipePayload } from '@/entities/recipe/model/types.ts';
import { RecipeForm } from '@/features/recipe-upsert/RecipeForm.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';

export const RecipeEditPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const recipeId = Number(id);
  const { showSnackbar } = useAppSnackbar();
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const { data: recipe, isLoading: isLoadingRecipe } = useGetRecipeByIdQuery(recipeId, {
    skip: Number.isNaN(recipeId),
  });
  const [updateRecipe, { isLoading: isUpdating }] = useUpdateRecipeMutation();

  const handleSubmit = async (payload: RecipePayload) => {
    try {
      await updateRecipe({ id: recipeId, body: payload }).unwrap();
      showSnackbar('Рецепт обновлен', 'success');
      navigate(`/recipes/${recipeId}`);
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  if (isLoadingRecipe) {
    return <PageSkeleton />;
  }

  if (!recipe) {
    return <EmptyState title="Рецепт не найден" description="Проверь идентификатор или вернись к списку рецептов." />;
  }

  return (
    <div className={pageStyles.page}>
      <div className={pageStyles.hero}>
        <Typography variant="h4">Редактирование рецепта</Typography>
      </div>
      <RecipeForm
        initialValue={recipe}
        loading={isUpdating}
        fieldErrors={fieldErrors}
        submitLabel="Сохранить изменения"
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/recipes/${recipeId}`)}
      />
    </div>
  );
};
