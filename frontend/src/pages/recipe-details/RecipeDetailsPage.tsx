import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, Chip, Paper, Stack, Typography } from '@mui/material';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import {
  useCreateIngredientMutation,
  useDeleteIngredientMutation,
  useGetIngredientsByRecipeQuery,
  useUpdateIngredientMutation,
} from '@/entities/ingredient/api/ingredientApi.ts';
import type { Ingredient, IngredientPayload } from '@/entities/ingredient/model/types.ts';
import { useDeleteRecipeMutation, useGetRecipeByIdQuery } from '@/entities/recipe/api/recipeApi.ts';
import { IngredientDialog } from '@/features/ingredient-upsert/IngredientDialog.tsx';
import { getErrorMessage, getFieldErrors } from '@/shared/lib/error.ts';
import { ConfirmDialog } from '@/shared/ui/confirm-dialog/ConfirmDialog.tsx';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import pageStyles from '@/shared/ui/page-layout/PageLayout.module.css';
import { PageSkeleton } from '@/shared/ui/page-skeleton/PageSkeleton.tsx';
import { useAppSnackbar } from '@/shared/ui/app-snackbar/AppSnackbarContext.ts';
import { RecipeRelationsPanel } from '@/widgets/recipe-relations-panel/RecipeRelationsPanel.tsx';

export const RecipeDetailsPage = () => {
  const { id } = useParams();
  const recipeId = Number(id);
  const navigate = useNavigate();
  const { showSnackbar } = useAppSnackbar();

  const { data: recipe, isLoading: isLoadingRecipe } = useGetRecipeByIdQuery(recipeId, {
    skip: Number.isNaN(recipeId),
  });
  const { data: ingredientsPage } = useGetIngredientsByRecipeQuery(
    { recipeId, page: 0, size: 100, sort: 'id,desc' },
    { skip: Number.isNaN(recipeId) },
  );

  const [deleteRecipe, { isLoading: isDeletingRecipe }] = useDeleteRecipeMutation();
  const [createIngredient, { isLoading: isCreatingIngredient }] = useCreateIngredientMutation();
  const [updateIngredient, { isLoading: isUpdatingIngredient }] = useUpdateIngredientMutation();
  const [deleteIngredient, { isLoading: isDeletingIngredient }] = useDeleteIngredientMutation();

  const [isIngredientDialogOpen, setIngredientDialogOpen] = useState(false);
  const [ingredientToEdit, setIngredientToEdit] = useState<Ingredient | null>(null);
  const [ingredientToDelete, setIngredientToDelete] = useState<Ingredient | null>(null);
  const [isRecipeDeleteOpen, setRecipeDeleteOpen] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const ingredientLoading = isCreatingIngredient || isUpdatingIngredient;

  const handleSaveIngredient = async (payload: IngredientPayload) => {
    try {
      if (ingredientToEdit) {
        await updateIngredient({ id: ingredientToEdit.id, body: payload }).unwrap();
        showSnackbar('Ингредиент обновлён', 'success');
      } else {
        await createIngredient(payload).unwrap();
        showSnackbar('Ингредиент создан', 'success');
      }

      setIngredientDialogOpen(false);
      setIngredientToEdit(null);
      setFieldErrors({});
    } catch (error) {
      setFieldErrors(getFieldErrors(error));
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDeleteIngredient = async () => {
    if (!ingredientToDelete) {
      return;
    }

    try {
      await deleteIngredient({ id: ingredientToDelete.id, recipeId }).unwrap();
      showSnackbar('Ингредиент удалён', 'success');
      setIngredientToDelete(null);
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  const handleDeleteRecipe = async () => {
    if (!recipe) {
      return;
    }

    try {
      await deleteRecipe(recipe.id).unwrap();
      showSnackbar('Рецепт удалён', 'success');
      navigate('/recipes');
    } catch (error) {
      showSnackbar(getErrorMessage(error), 'error');
    }
  };

  if (isLoadingRecipe) {
    return <PageSkeleton />;
  }

  if (!recipe) {
    return <EmptyState title="Рецепт не найден" description="Возможно, он уже удалён или ссылка некорректна." />;
  }

  return (
    <div className={pageStyles.page}>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.hero}>
          <Typography variant="h4">{recipe.title}</Typography>
        </div>
        <div className={pageStyles.toolbarRight}>
          <Button startIcon={<EditOutlinedIcon />} variant="outlined" onClick={() => navigate(`/recipes/${recipe.id}/edit`)}>
            Редактировать
          </Button>
          <Button
            color="error"
            startIcon={<DeleteOutlineIcon />}
            variant="contained"
            onClick={() => setRecipeDeleteOpen(true)}
          >
            Удалить
          </Button>
        </div>
      </div>

      <div className={pageStyles.twoColumns}>
        <Paper className={pageStyles.section}>
          <Typography variant="h6">Основная информация</Typography>
          <Stack direction="row" flexWrap="wrap" gap={1}>
            <Chip label={`Категория: ${recipe.categoryName || 'не указана'}`} />
            <Chip label={`Автор: ${recipe.authorUsername || 'не назначен'}`} />
            <Chip label={`ID: ${recipe.id}`} variant="outlined" />
          </Stack>
          <Typography variant="subtitle1" fontWeight={700}>
            Инструкции
          </Typography>
          <Typography sx={{ whiteSpace: 'pre-wrap' }}>{recipe.instructions}</Typography>
        </Paper>

        <RecipeRelationsPanel
          tags={recipe.tags}
          ingredients={ingredientsPage?.content ?? recipe.ingredients}
          onAddIngredient={() => {
            setIngredientToEdit(null);
            setFieldErrors({});
            setIngredientDialogOpen(true);
          }}
          onEditIngredient={(ingredient) => {
            setIngredientToEdit(ingredient);
            setFieldErrors({});
            setIngredientDialogOpen(true);
          }}
          onDeleteIngredient={(ingredient) => setIngredientToDelete(ingredient)}
        />
      </div>

      <IngredientDialog
        open={isIngredientDialogOpen}
        title={ingredientToEdit ? 'Редактировать ингредиент' : 'Добавить ингредиент'}
        initialValue={ingredientToEdit ?? { recipeId }}
        fixedRecipeId={recipeId}
        loading={ingredientLoading}
        fieldErrors={fieldErrors}
        onSubmit={handleSaveIngredient}
        onClose={() => {
          setIngredientDialogOpen(false);
          setIngredientToEdit(null);
        }}
      />

      <ConfirmDialog
        open={Boolean(ingredientToDelete)}
        title="Удалить ингредиент?"
        description={ingredientToDelete ? `Ингредиент "${ingredientToDelete.name}" будет удалён.` : ''}
        destructive
        loading={isDeletingIngredient}
        confirmLabel="Удалить"
        onConfirm={handleDeleteIngredient}
        onClose={() => setIngredientToDelete(null)}
      />

      <ConfirmDialog
        open={isRecipeDeleteOpen}
        title="Удалить рецепт?"
        description={`Рецепт "${recipe.title}" будет удалён вместе со связями на клиенте.`}
        destructive
        loading={isDeletingRecipe}
        confirmLabel="Удалить рецепт"
        onConfirm={handleDeleteRecipe}
        onClose={() => setRecipeDeleteOpen(false)}
      />
    </div>
  );
};
