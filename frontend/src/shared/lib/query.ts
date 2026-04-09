import type { PageableQuery } from '@/shared/types/api.ts';

export function buildQueryString(params: Record<string, string | number | undefined | null>) {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.set(key, String(value));
    }
  });

  return searchParams.toString();
}

export function toPageableQuery({ page = 0, size = 10, sort }: PageableQuery) {
  return {
    page,
    size,
    sort,
  };
}
