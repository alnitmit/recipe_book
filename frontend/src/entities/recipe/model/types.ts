import type { Ingredient } from '@/entities/ingredient/model/types.ts';
import type { Tag } from '@/entities/tag/model/types.ts';

export interface Recipe {
  id: number;
  title: string;
  description: string;
  instructions: string;
  categoryId?: number | null;
  categoryName?: string | null;
  authorId?: number | null;
  authorUsername?: string | null;
  tags: Tag[];
  ingredients: Ingredient[];
}

export interface RecipePayload {
  title: string;
  description: string;
  instructions: string;
  categoryId?: number | null;
  authorId?: number | null;
  tags: Array<Pick<Tag, 'id'>>;
}

export interface RecipeFilterParams {
  category?: string;
  minIngredients?: number;
  page?: number;
  size?: number;
  sort?: string;
}
