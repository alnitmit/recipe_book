import type { Recipe, RecipeFilterParams, RecipePayload } from '@/entities/recipe/model/types.ts';
import { baseApi } from '@/app/baseApi.ts';
import { buildQueryString, toPageableQuery } from '@/shared/lib/query.ts';
import type { PageResponse } from '@/shared/types/api.ts';

export const recipeApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getRecipes: builder.query<PageResponse<Recipe>, RecipeFilterParams | void>({
      query: (params) => {
        const query = buildQueryString(toPageableQuery({ page: 0, size: 10, ...params }));
        return `/recipes${query ? `?${query}` : ''}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((recipe) => ({ type: 'Recipe' as const, id: recipe.id })),
              { type: 'Recipe' as const, id: 'LIST' },
            ]
          : [{ type: 'Recipe' as const, id: 'LIST' }],
    }),
    getFilteredRecipes: builder.query<PageResponse<Recipe>, RecipeFilterParams>({
      query: (params) => {
        const query = buildQueryString({
          category: params.category,
          minIngredients: params.minIngredients,
          ...toPageableQuery({ page: 0, size: 10, sort: params.sort, ...params }),
        });
        return `/recipes/filter/jpql?${query}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((recipe) => ({ type: 'Recipe' as const, id: recipe.id })),
              { type: 'Recipe' as const, id: 'LIST' },
            ]
          : [{ type: 'Recipe' as const, id: 'LIST' }],
    }),
    getRecipeById: builder.query<Recipe, number>({
      query: (id) => `/recipes/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Recipe', id }],
    }),
    createRecipe: builder.mutation<Recipe, RecipePayload>({
      query: (body) => ({
        url: '/recipes',
        method: 'POST',
        body,
      }),
      invalidatesTags: [{ type: 'Recipe', id: 'LIST' }],
    }),
    updateRecipe: builder.mutation<Recipe, { id: number; body: RecipePayload }>({
      query: ({ id, body }) => ({
        url: `/recipes/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Recipe', id },
        { type: 'Recipe', id: 'LIST' },
      ],
    }),
    deleteRecipe: builder.mutation<void, number>({
      query: (id) => ({
        url: `/recipes/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: [{ type: 'Recipe', id: 'LIST' }, { type: 'Ingredient', id: 'LIST' }],
    }),
  }),
});

export const {
  useGetRecipesQuery,
  useGetFilteredRecipesQuery,
  useGetRecipeByIdQuery,
  useCreateRecipeMutation,
  useUpdateRecipeMutation,
  useDeleteRecipeMutation,
} = recipeApi;
