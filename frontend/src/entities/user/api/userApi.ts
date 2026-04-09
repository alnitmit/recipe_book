import type { User, UserPayload } from '@/entities/user/model/types.ts';
import { baseApi } from '@/shared/api/baseApi.ts';
import { buildQueryString, toPageableQuery } from '@/shared/lib/query.ts';
import type { PageResponse, PageableQuery } from '@/shared/types/api.ts';

export const userApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getUsers: builder.query<PageResponse<User>, PageableQuery | void>({
      query: (params) => {
        const query = buildQueryString(toPageableQuery(params ?? {}));
        return `/users${query ? `?${query}` : ''}`;
      },
      providesTags: (result) =>
        result
          ? [
              ...result.content.map((user) => ({ type: 'User' as const, id: user.id })),
              { type: 'User' as const, id: 'LIST' },
            ]
          : [{ type: 'User' as const, id: 'LIST' }],
    }),
    createUser: builder.mutation<User, UserPayload>({
      query: (body) => ({
        url: '/users',
        method: 'POST',
        body,
      }),
      invalidatesTags: [{ type: 'User', id: 'LIST' }],
    }),
    updateUser: builder.mutation<User, { id: number; body: UserPayload }>({
      query: ({ id, body }) => ({
        url: `/users/${id}`,
        method: 'PUT',
        body,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'User', id },
        { type: 'User', id: 'LIST' },
        { type: 'Recipe', id: 'LIST' },
      ],
    }),
    deleteUser: builder.mutation<void, number>({
      query: (id) => ({
        url: `/users/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: [{ type: 'User', id: 'LIST' }, { type: 'Recipe', id: 'LIST' }],
    }),
  }),
});

export const { useGetUsersQuery, useCreateUserMutation, useUpdateUserMutation, useDeleteUserMutation } = userApi;
