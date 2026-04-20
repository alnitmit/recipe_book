import type { Tag, TagPayload } from '@/entities/tag/model/types.ts';
import { baseApi } from '@/app/baseApi.ts';
import { buildQueryString, toPageableQuery } from '@/shared/lib/query.ts';
import type { PageResponse, PageableQuery } from '@/shared/types/api.ts';

export const tagApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getTags: builder.query<PageResponse<Tag>, PageableQuery | void>({
      query: (params) => {
        const query = buildQueryString(toPageableQuery(params ?? {}));
        return `/tags${query ? `?${query}` : ''}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((tag) => ({ type: 'Tag' as const, id: tag.id })),
              { type: 'Tag' as const, id: 'LIST' },
            ]
          : [{ type: 'Tag' as const, id: 'LIST' }],
    }),
    createTag: builder.mutation<Tag, TagPayload>({
      query: (body) => ({
        url: '/tags',
        method: 'POST',
        body,
      }),
      invalidatesTags: [{ type: 'Tag', id: 'LIST' }],
    }),
    updateTag: builder.mutation<Tag, { id: number; body: TagPayload }>({
      query: ({ id, body }) => ({
        url: `/tags/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Tag', id },
        { type: 'Tag', id: 'LIST' },
        { type: 'Recipe', id: 'LIST' },
      ],
    }),
    deleteTag: builder.mutation<void, number>({
      query: (id) => ({
        url: `/tags/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: [{ type: 'Tag', id: 'LIST' }, { type: 'Recipe', id: 'LIST' }],
    }),
  }),
});

export const { useGetTagsQuery, useCreateTagMutation, useUpdateTagMutation, useDeleteTagMutation } = tagApi;
