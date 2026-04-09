import type { Category, CategoryPayload } from '@/entities/category/model/types.ts';
import { baseApi } from '@/shared/api/baseApi.ts';
import { buildQueryString, toPageableQuery } from '@/shared/lib/query.ts';
import type { PageResponse, PageableQuery } from '@/shared/types/api.ts';

export const categoryApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getCategories: builder.query<PageResponse<Category>, PageableQuery | void>({
      query: (params) => {
        const query = buildQueryString(toPageableQuery(params ?? {}));
        return `/categories${query ? `?${query}` : ''}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((category) => ({ type: 'Category' as const, id: category.id })),
              { type: 'Category' as const, id: 'LIST' },
            ]
          : [{ type: 'Category' as const, id: 'LIST' }],
    }),
    getCategoryById: builder.query<Category, number>({
      query: (id) => `/categories/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Category', id }],
    }),
    createCategory: builder.mutation<Category, CategoryPayload>({
      query: (body) => ({
        url: '/categories',
        method: 'POST',
        body,
      }),
      invalidatesTags: [{ type: 'Category', id: 'LIST' }],
    }),
    updateCategory: builder.mutation<Category, { id: number; body: CategoryPayload }>({
      query: ({ id, body }) => ({
        url: `/categories/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Category', id },
        { type: 'Category', id: 'LIST' },
        { type: 'Recipe', id: 'LIST' },
      ],
    }),
    deleteCategory: builder.mutation<void, number>({
      query: (id) => ({
        url: `/categories/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: [{ type: 'Category', id: 'LIST' }, { type: 'Recipe', id: 'LIST' }],
    }),
  }),
});

export const {
  useGetCategoriesQuery,
  useGetCategoryByIdQuery,
  useCreateCategoryMutation,
  useUpdateCategoryMutation,
  useDeleteCategoryMutation,
} = categoryApi;
