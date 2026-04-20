import type { FetchBaseQueryError } from '@reduxjs/toolkit/query';
import type { SerializedError } from '@reduxjs/toolkit';

import type { ErrorResponse } from '@/shared/types/api.ts';

const isFetchBaseQueryError = (error: unknown): error is FetchBaseQueryError => {
  return typeof error === 'object' && error !== null && 'status' in error;
};

const isParsingError = (
  error: FetchBaseQueryError,
): error is FetchBaseQueryError & { status: 'PARSING_ERROR'; data?: string } => {
  return error.status === 'PARSING_ERROR';
};

const isHtmlResponse = (value: unknown) => {
  return typeof value === 'string' && /^\s*</.test(value);
};

const isSerializedError = (error: unknown): error is SerializedError => {
  return typeof error === 'object' && error !== null && 'message' in error;
};

export const getErrorResponse = (error: unknown): ErrorResponse | null => {
  if (!isFetchBaseQueryError(error)) {
    return null;
  }

  if (typeof error.data === 'object' && error.data !== null && 'message' in error.data) {
    return error.data as ErrorResponse;
  }

  return null;
};

export const getErrorMessage = (error: unknown) => {
  const response = getErrorResponse(error);

  if (response?.message) {
    return response.message;
  }

  if (isFetchBaseQueryError(error) && isParsingError(error) && isHtmlResponse(error.data)) {
    return 'Сервер вернул некорректный ответ. Попробуй обновить страницу.';
  }

  if (isSerializedError(error) && error.message) {
    return error.message;
  }

  if (isFetchBaseQueryError(error) && 'error' in error && typeof error.error === 'string') {
    return error.error;
  }

  return 'Произошла непредвиденная ошибка';
};

export const getFieldErrors = (error: unknown) => {
  return getErrorResponse(error)?.details ?? {};
};
