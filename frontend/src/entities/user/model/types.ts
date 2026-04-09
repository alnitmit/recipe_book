export interface User {
  id: number;
  username: string;
  email: string;
  createdAt?: string;
}

export interface UserPayload {
  username: string;
  email: string;
}
