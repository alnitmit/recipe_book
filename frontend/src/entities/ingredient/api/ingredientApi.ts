import type { Ingredient, IngredientPayload } from '@/entities/ingredient/model/types.ts';
import { baseApi } from '@/app/baseApi.ts';
import { buildQueryString, toPageableQuery } from '@/shared/lib/query.ts';
import type { PageResponse, PageableQuery } from '@/shared/types/api.ts';

export const ingredientApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getIngredients: builder.query<PageResponse<Ingredient>, PageableQuery | void>({
      query: (params) => {
        const query = buildQueryString(toPageableQuery({ page: 0, size: 20, ...params }));
        return `/ingredients${query ? `?${query}` : ''}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((ingredient) => ({ type: 'Ingredient' as const, id: ingredient.id })),
              { type: 'Ingredient' as const, id: 'LIST' },
            ]
          : [{ type: 'Ingredient' as const, id: 'LIST' }],
    }),
    getIngredientsByRecipe: builder.query<PageResponse<Ingredient>, { recipeId: number } & PageableQuery>({
      query: ({ recipeId, ...params }) => {
        const query = buildQueryString({
          recipeId,
          ...toPageableQuery({ page: 0, size: 20, ...params }),
        });
        return `/ingredients/by-recipe?${query}`;
      },
      providesTags: (result, _error, { recipeId }) =>
        result
          ? [
              ...result.content.map((ingredient) => ({ type: 'Ingredient' as const, id: ingredient.id })),
              { type: 'Ingredient' as const, id: `RECIPE-${recipeId}` },
            ]
          : [{ type: 'Ingredient' as const, id: `RECIPE-${recipeId}` }],
    }),
    createIngredient: builder.mutation<Ingredient, IngredientPayload>({
      query: (body) => ({
        url: '/ingredients',
        method: 'POST',
        body,
      }),
      invalidatesTags: (_result, _error, body) => [
        { type: 'Ingredient', id: 'LIST' },
        { type: 'Ingredient', id: `RECIPE-${body.recipeId}` },
        { type: 'Recipe', id: 'LIST' },
        { type: 'Recipe', id: body.recipeId },
      ],
    }),
    updateIngredient: builder.mutation<Ingredient, { id: number; body: IngredientPayload }>({
      query: ({ id, body }) => ({
        url: `/ingredients/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: (_result, _error, { id, body }) => [
        { type: 'Ingredient', id },
        { type: 'Ingredient', id: 'LIST' },
        { type: 'Ingredient', id: `RECIPE-${body.recipeId}` },
        { type: 'Recipe', id: 'LIST' },
        { type: 'Recipe', id: body.recipeId },
      ],
    }),
    deleteIngredient: builder.mutation<void, { id: number; recipeId?: number }>({
      query: ({ id }) => ({
        url: `/ingredients/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: (_result, _error, { id, recipeId }) => [
        { type: 'Ingredient', id },
        { type: 'Ingredient', id: 'LIST' },
        { type: 'Recipe', id: 'LIST' },
        ...(recipeId
          ? [
              { type: 'Ingredient' as const, id: `RECIPE-${recipeId}` },
              { type: 'Recipe' as const, id: recipeId },
            ]
          : []),
      ],
    }),
  }),
});

export const {
  useGetIngredientsQuery,
  useGetIngredientsByRecipeQuery,
  useCreateIngredientMutation,
  useUpdateIngredientMutation,
  useDeleteIngredientMutation,
} = ingredientApi;
