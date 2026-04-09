import type { FetchBaseQueryError } from '@reduxjs/toolkit/query';
import type { SerializedError } from '@reduxjs/toolkit';

import type { ErrorResponse } from '@/shared/types/api.ts';

function isFetchBaseQueryError(error: unknown): error is FetchBaseQueryError {
  return typeof error === 'object' && error !== null && 'status' in error;
}

function isSerializedError(error: unknown): error is SerializedError {
  return typeof error === 'object' && error !== null && 'message' in error;
}

export function getErrorResponse(error: unknown): ErrorResponse | null {
  if (!isFetchBaseQueryError(error)) {
    return null;
  }

  if (typeof error.data === 'object' && error.data !== null && 'message' in error.data) {
    return error.data as ErrorResponse;
  }

  return null;
}

export function getErrorMessage(error: unknown) {
  const response = getErrorResponse(error);

  if (response?.message) {
    return response.message;
  }

  if (isSerializedError(error) && error.message) {
    return error.message;
  }

  if (isFetchBaseQueryError(error) && 'error' in error && typeof error.error === 'string') {
    return error.error;
  }

  return 'Произошла непредвиденная ошибка';
}

export function getFieldErrors(error: unknown) {
  return getErrorResponse(error)?.details ?? {};
}
