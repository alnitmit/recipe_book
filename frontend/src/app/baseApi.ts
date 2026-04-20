import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query';

import { setAppErrorAC, setAppStatusAC } from '@/app/app-slice.ts';
import { API_URL } from '@/shared/config/api.ts';

const rawBaseQuery = fetchBaseQuery({
  baseUrl: API_URL,
});

const isParsingError = (
  error: FetchBaseQueryError,
): error is FetchBaseQueryError & { status: 'PARSING_ERROR'; data?: string } => {
  return error.status === 'PARSING_ERROR';
};

const isHtmlResponse = (value: unknown) => {
  return typeof value === 'string' && /^\s*</.test(value);
};

const getBaseQueryErrorMessage = (error: FetchBaseQueryError) => {
  if (isParsingError(error) && isHtmlResponse(error.data)) {
    return 'Сервер вернул некорректный ответ. Обнови страницу или попробуй снова через несколько секунд.';
  }

  if (typeof error.data === 'object' && error.data !== null && 'message' in error.data) {
    const message = error.data.message;

    if (typeof message === 'string' && message.trim()) {
      return message;
    }
  }

  if ('error' in error && typeof error.error === 'string' && error.error.trim()) {
    return error.error;
  }

  return 'Не удалось загрузить данные с сервера';
};

const shouldShowGlobalError = (error: FetchBaseQueryError) => {
  if (isParsingError(error) && isHtmlResponse(error.data)) {
    return false;
  }

  return true;
};

const baseQueryWithAppHandling: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (
  args,
  api,
  extraOptions,
) => {
  api.dispatch(setAppStatusAC({ status: 'loading' }));

  const result = await rawBaseQuery(args, api, extraOptions);
  const method = typeof args === 'string' ? 'GET' : (args.method ?? 'GET').toUpperCase();

  if (result.error) {
    api.dispatch(setAppStatusAC({ status: 'failed' }));

    if (method === 'GET' && shouldShowGlobalError(result.error)) {
      api.dispatch(setAppErrorAC({ error: getBaseQueryErrorMessage(result.error) }));
    }

    return result;
  }

  api.dispatch(setAppStatusAC({ status: 'succeeded' }));
  return result;
};

export const tagTypes = ['Recipe', 'Ingredient', 'Category', 'Tag', 'Unit', 'User'] as const;

export const baseApi = createApi({
  reducerPath: 'recipeBookApi',
  baseQuery: baseQueryWithAppHandling,
  tagTypes,
  endpoints: () => ({}),
});
