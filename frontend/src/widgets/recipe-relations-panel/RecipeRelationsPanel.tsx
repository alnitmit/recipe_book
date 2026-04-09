import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, Chip, Divider, IconButton, Paper, Stack, Typography } from '@mui/material';

import type { Ingredient } from '@/entities/ingredient/model/types.ts';
import type { Tag } from '@/entities/tag/model/types.ts';
import { EmptyState } from '@/shared/ui/empty-state/EmptyState.tsx';
import styles from '@/widgets/recipe-relations-panel/RecipeRelationsPanel.module.css';

interface RecipeRelationsPanelProps {
  tags: Tag[];
  ingredients: Ingredient[];
  onAddIngredient: () => void;
  onEditIngredient: (ingredient: Ingredient) => void;
  onDeleteIngredient: (ingredient: Ingredient) => void;
}

export function RecipeRelationsPanel({
  tags,
  ingredients,
  onAddIngredient,
  onEditIngredient,
  onDeleteIngredient,
}: RecipeRelationsPanelProps) {
  return (
    <Stack spacing={2}>
      <Paper className={styles.panel}>
        <Typography variant="h6">Many-to-Many: теги рецепта</Typography>
        {tags.length > 0 ? (
          <div className={styles.tagList}>
            {tags.map((tag) => (
              <Chip key={tag.id} color="secondary" label={tag.name} />
            ))}
          </div>
        ) : (
          <EmptyState title="Тегов пока нет" description="Добавьте теги при редактировании рецепта." />
        )}
      </Paper>

      <Paper className={styles.panel}>
        <Stack direction="row" justifyContent="space-between" spacing={2}>
          <div>
            <Typography variant="h6">One-to-Many: ингредиенты</Typography>
            <Typography color="textSecondary">Ингредиенты редактируются отдельным API, но показаны в карточке рецепта.</Typography>
          </div>
          <Button variant="contained" onClick={onAddIngredient}>
            Добавить ингредиент
          </Button>
        </Stack>
        {ingredients.length > 0 ? (
          <Stack divider={<Divider flexItem />} spacing={1}>
            {ingredients.map((ingredient) => (
              <div key={ingredient.id} className={styles.ingredientRow}>
                <div>
                  <Typography fontWeight={600}>{ingredient.name}</Typography>
                  <Typography color="textSecondary">
                    {ingredient.quantity}
                    {ingredient.unitName ? ` • ${ingredient.unitName}` : ''}
                  </Typography>
                </div>
                <IconButton aria-label="Редактировать ингредиент" onClick={() => onEditIngredient(ingredient)}>
                  <EditOutlinedIcon />
                </IconButton>
                <IconButton aria-label="Удалить ингредиент" color="error" onClick={() => onDeleteIngredient(ingredient)}>
                  <DeleteOutlineIcon />
                </IconButton>
              </div>
            ))}
          </Stack>
        ) : (
          <EmptyState title="Список ингредиентов пуст" description="Создайте ингредиент для этого рецепта, чтобы связь появилась в интерфейсе." />
        )}
      </Paper>
    </Stack>
  );
}
