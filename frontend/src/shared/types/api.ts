export type PageResponse<T> = {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};

export type ErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: Record<string, string>;
};

export type SortDirection = 'asc' | 'desc';

export type PageableQuery = {
  page?: number;
  size?: number;
  sort?: string;
};
