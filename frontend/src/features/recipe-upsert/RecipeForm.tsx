import { Alert, Autocomplete, Button, Paper, Stack, TextField, Typography } from '@mui/material';
import { useState } from 'react';

import { useGetCategoriesQuery } from '@/entities/category/api/categoryApi.ts';
import type { Category } from '@/entities/category/model/types.ts';
import type { Recipe, RecipePayload } from '@/entities/recipe/model/types.ts';
import type { Tag } from '@/entities/tag/model/types.ts';
import { useGetTagsQuery } from '@/entities/tag/api/tagApi.ts';
import type { User } from '@/entities/user/model/types.ts';
import { useGetUsersQuery } from '@/entities/user/api/userApi.ts';
import styles from '@/features/recipe-upsert/RecipeForm.module.css';

type RecipeFormProps = {
  initialValue?: Recipe;
  loading?: boolean;
  fieldErrors?: Record<string, string>;
  submitLabel?: string;
  onSubmit: (payload: RecipePayload) => void;
  onCancel: () => void;
};

type RecipeFormState = {
  title: string;
  description: string;
  instructions: string;
  categoryId: number | null;
  authorId: number | null;
  tags: Tag[];
};

type RecipeFormContentProps = RecipeFormProps & {
  initialState: RecipeFormState;
};

const EMPTY_CATEGORIES: Category[] = [];
const EMPTY_USERS: User[] = [];
const EMPTY_TAGS: Tag[] = [];

const toFormState = (recipe?: Recipe): RecipeFormState => {
  return {
    title: recipe?.title ?? '',
    description: recipe?.description ?? '',
    instructions: recipe?.instructions ?? '',
    categoryId: recipe?.categoryId ?? null,
    authorId: recipe?.authorId ?? null,
    tags: recipe?.tags ?? [],
  };
};

const RecipeFormContent = ({
  initialValue,
  initialState,
  loading = false,
  fieldErrors = {},
  submitLabel = 'Сохранить',
  onSubmit,
  onCancel,
}: RecipeFormContentProps) => {
  const [values, setValues] = useState(initialState);
  const { data: categoriesPage } = useGetCategoriesQuery({ page: 0, size: 100, sort: 'name,asc' });
  const { data: usersPage } = useGetUsersQuery({ page: 0, size: 100, sort: 'username,asc' });
  const { data: tagsPage } = useGetTagsQuery({ page: 0, size: 100, sort: 'name,asc' });

  const categories = categoriesPage?.content ?? EMPTY_CATEGORIES;
  const users = usersPage?.content ?? EMPTY_USERS;
  const tags = tagsPage?.content ?? EMPTY_TAGS;

  const selectedCategory = categories.find((category) => category.id === values.categoryId) ?? null;
  const selectedAuthor = users.find((user) => user.id === values.authorId) ?? null;

  const submit = () => {
    onSubmit({
      title: values.title,
      description: values.description,
      instructions: values.instructions,
      categoryId: values.categoryId,
      authorId: values.authorId,
      tags: values.tags.map((tag) => ({ id: tag.id })),
    });
  };

  return (
    <Paper className={styles.form} sx={{ p: 3 }}>
      <Stack spacing={0.5}>
        <Typography variant="h5">{initialValue ? 'Редактирование рецепта' : 'Новый рецепт'}</Typography>
        <Typography color="textSecondary">
          Теги связываются напрямую с рецептом. Ингредиенты добавляются отдельно после создания или на странице деталей.
        </Typography>
      </Stack>

      <div className={styles.grid}>
        <TextField
          required
          label="Название"
          value={values.title}
          error={Boolean(fieldErrors.title)}
          helperText={fieldErrors.title}
          onChange={(event) => setValues((previous) => ({ ...previous, title: event.target.value }))}
        />
        <Autocomplete<Category>
          options={categories}
          getOptionLabel={(option) => option.name}
          value={selectedCategory}
          onChange={(_event, value) => setValues((previous) => ({ ...previous, categoryId: value?.id ?? null }))}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Категория"
              error={Boolean(fieldErrors.categoryId)}
              helperText={fieldErrors.categoryId}
            />
          )}
        />
        <Autocomplete<User>
          options={users}
          getOptionLabel={(option) => option.username}
          value={selectedAuthor}
          onChange={(_event, value) => setValues((previous) => ({ ...previous, authorId: value?.id ?? null }))}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Автор"
              error={Boolean(fieldErrors.authorId)}
              helperText={fieldErrors.authorId}
            />
          )}
        />
        <Autocomplete<Tag, true, false, false>
          multiple
          options={tags}
          getOptionLabel={(option) => option.name}
          value={values.tags}
          onChange={(_event, nextValue) => setValues((previous) => ({ ...previous, tags: nextValue }))}
          renderInput={(params) => <TextField {...params} label="Теги" helperText={fieldErrors.tags} />}
        />
      </div>

      <TextField
        label="Описание"
        multiline
        minRows={3}
        value={values.description}
        error={Boolean(fieldErrors.description)}
        helperText={fieldErrors.description}
        onChange={(event) => setValues((previous) => ({ ...previous, description: event.target.value }))}
      />

      <TextField
        required
        label="Инструкции"
        multiline
        minRows={8}
        value={values.instructions}
        error={Boolean(fieldErrors.instructions)}
        helperText={fieldErrors.instructions}
        onChange={(event) => setValues((previous) => ({ ...previous, instructions: event.target.value }))}
      />

      {!initialValue ? (
        <Alert severity="info">После создания рецепта ингредиенты можно будет добавлять отдельно через details или раздел ингредиентов.</Alert>
      ) : null}

      <div className={styles.actions}>
        <Button color="inherit" disabled={loading} onClick={onCancel}>
          Отмена
        </Button>
        <Button disabled={loading} variant="contained" onClick={submit}>
          {submitLabel}
        </Button>
      </div>
    </Paper>
  );
};

export const RecipeForm = (props: RecipeFormProps) => {
  const formKey = props.initialValue?.id ?? 'new-recipe';

  return <RecipeFormContent key={formKey} {...props} initialState={toFormState(props.initialValue)} />;
};
