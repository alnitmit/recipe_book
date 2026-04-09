import { createBrowserRouter, Navigate } from 'react-router-dom';

import { AppLayout } from '@/app/layout/AppLayout.tsx';
import { CategoriesPage } from '@/pages/categories/CategoriesPage.tsx';
import { IngredientsPage } from '@/pages/ingredients/IngredientsPage.tsx';
import { RecipeCreatePage } from '@/pages/recipe-create/RecipeCreatePage.tsx';
import { RecipeDetailsPage } from '@/pages/recipe-details/RecipeDetailsPage.tsx';
import { RecipeEditPage } from '@/pages/recipe-edit/RecipeEditPage.tsx';
import { RecipesListPage } from '@/pages/recipes-list/RecipesListPage.tsx';
import { TagsPage } from '@/pages/tags/TagsPage.tsx';
import { UnitsPage } from '@/pages/units/UnitsPage.tsx';
import { UsersPage } from '@/pages/users/UsersPage.tsx';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      {
        index: true,
        element: <Navigate replace to="/recipes" />,
      },
      {
        path: 'recipes',
        element: <RecipesListPage />,
      },
      {
        path: 'recipes/new',
        element: <RecipeCreatePage />,
      },
      {
        path: 'recipes/:id',
        element: <RecipeDetailsPage />,
      },
      {
        path: 'recipes/:id/edit',
        element: <RecipeEditPage />,
      },
      {
        path: 'ingredients',
        element: <IngredientsPage />,
      },
      {
        path: 'categories',
        element: <CategoriesPage />,
      },
      {
        path: 'tags',
        element: <TagsPage />,
      },
      {
        path: 'units',
        element: <UnitsPage />,
      },
      {
        path: 'users',
        element: <UsersPage />,
      },
    ],
  },
]);
