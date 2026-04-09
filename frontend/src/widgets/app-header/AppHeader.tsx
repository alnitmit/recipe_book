import { Typography } from '@mui/material';
import { useLocation } from 'react-router-dom';

import styles from '@/widgets/app-header/AppHeader.module.css';

const titles: Record<string, string> = {
  '/recipes': 'Рецепты',
  '/ingredients': 'Ингредиенты',
  '/categories': 'Категории',
  '/tags': 'Теги',
  '/units': 'Единицы измерения',
  '/users': 'Пользователи',
};

export function AppHeader() {
  const location = useLocation();
  const pageTitle = Object.entries(titles).find(([key]) => location.pathname.startsWith(key))?.[1] ?? 'Recipe Book';

  return (
    <header className={styles.header}>
      <Typography variant="h4">{pageTitle}</Typography>
    </header>
  );
}
