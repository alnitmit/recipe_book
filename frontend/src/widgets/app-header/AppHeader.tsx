import { Typography } from '@mui/material';
import { useLocation } from 'react-router-dom';

import { Path } from '@/common/routing/index.ts';
import styles from '@/widgets/app-header/AppHeader.module.css';

const titles: Record<string, string> = {
  [Path.Recipes]: 'Рецепты',
  [Path.Ingredients]: 'Ингредиенты',
  [Path.Categories]: 'Категории',
  [Path.Tags]: 'Теги',
  [Path.Units]: 'Единицы измерения',
  [Path.Users]: 'Пользователи',
};

export const AppHeader = () => {
  const location = useLocation();
  const pageTitle = Object.entries(titles).find(([key]) => location.pathname.startsWith(key))?.[1] ?? 'Recipe Book';

  return (
    <header className={styles.header}>
      <Typography variant="h4">{pageTitle}</Typography>
    </header>
  );
};
