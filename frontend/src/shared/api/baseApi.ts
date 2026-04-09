import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

import { API_URL } from '@/shared/config/api.ts';

export const tagTypes = ['Recipe', 'Ingredient', 'Category', 'Tag', 'Unit', 'User'] as const;

export const baseApi = createApi({
  reducerPath: 'recipeBookApi',
  baseQuery: fetchBaseQuery({
    baseUrl: API_URL,
  }),
  tagTypes,
  endpoints: () => ({}),
});
