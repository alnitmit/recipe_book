import type { Unit, UnitPayload } from '@/entities/unit/model/types.ts';
import { baseApi } from '@/shared/api/baseApi.ts';
import { buildQueryString, toPageableQuery } from '@/shared/lib/query.ts';
import type { PageResponse, PageableQuery } from '@/shared/types/api.ts';

export const unitApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getUnits: builder.query<PageResponse<Unit>, PageableQuery | void>({
      query: (params) => {
        const query = buildQueryString(toPageableQuery(params ?? {}));
        return `/units${query ? `?${query}` : ''}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((unit) => ({ type: 'Unit' as const, id: unit.id })),
              { type: 'Unit' as const, id: 'LIST' },
            ]
          : [{ type: 'Unit' as const, id: 'LIST' }],
    }),
    createUnit: builder.mutation<Unit, UnitPayload>({
      query: (body) => ({
        url: '/units',
        method: 'POST',
        body,
      }),
      invalidatesTags: [{ type: 'Unit', id: 'LIST' }, { type: 'Ingredient', id: 'LIST' }],
    }),
    updateUnit: builder.mutation<Unit, { id: number; body: UnitPayload }>({
      query: ({ id, body }) => ({
        url: `/units/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Unit', id },
        { type: 'Unit', id: 'LIST' },
        { type: 'Ingredient', id: 'LIST' },
        { type: 'Recipe', id: 'LIST' },
      ],
    }),
    deleteUnit: builder.mutation<void, number>({
      query: (id) => ({
        url: `/units/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: [{ type: 'Unit', id: 'LIST' }, { type: 'Ingredient', id: 'LIST' }],
    }),
  }),
});

export const { useGetUnitsQuery, useCreateUnitMutation, useUpdateUnitMutation, useDeleteUnitMutation } = unitApi;
