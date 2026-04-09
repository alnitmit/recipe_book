import { Typography } from '@mui/material';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { useCreateRecipeMutation } from '@/entities/recipe/api/recipeApi.ts';
import type { RecipePayload } from '@/entities/recipe/model/types.ts';
import { RecipeForm } from '@/features/recipe-upsert/RecipeForm.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarProvider.tsx';

export function RecipeCreatePage() {
  const navigate = useNavigate();
  const { showSnackbar } = useAppSnackbar();
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [createRecipe, { isLoading }] = useCreateRecipeMutation();

  const handleSubmit = async (payload: RecipePayload) => {
    try {
      const created = await createRecipe(payload).unwrap();
      showSnackbar('Рецепт успешно создан', 'success');
      navigate(`/recipes/${created.id}`);
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  return (
    <div className={pageStyles.page}>
      <div className={pageStyles.hero}>
        <Typography variant="h4">Создание рецепта</Typography>
      </div>
      <RecipeForm
        loading={isLoading}
        fieldErrors={fieldErrors}
        submitLabel="Создать рецепт"
        onSubmit={handleSubmit}
        onCancel={() => navigate('/recipes')}
      />
    </div>
  );
}
