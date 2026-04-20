import { Navigate, Route, Routes } from 'react-router-dom';

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

import { Path } from './paths.ts';

export const Routing = () => {
  return (
    <Routes>
      <Route path={Path.Root} element={<AppLayout />}>
        <Route index element={<Navigate replace to={Path.Recipes} />} />
        <Route path={Path.Recipes.slice(1)} element={<RecipesListPage />} />
        <Route path={Path.RecipeCreate.slice(1)} element={<RecipeCreatePage />} />
        <Route path={Path.RecipeDetails.slice(1)} element={<RecipeDetailsPage />} />
        <Route path={Path.RecipeEdit.slice(1)} element={<RecipeEditPage />} />
        <Route path={Path.Ingredients.slice(1)} element={<IngredientsPage />} />
        <Route path={Path.Categories.slice(1)} element={<CategoriesPage />} />
        <Route path={Path.Tags.slice(1)} element={<TagsPage />} />
        <Route path={Path.Units.slice(1)} element={<UnitsPage />} />
        <Route path={Path.Users.slice(1)} element={<UsersPage />} />
      </Route>
    </Routes>
  );
};
