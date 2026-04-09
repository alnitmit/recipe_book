export interface Ingredient {
  id: number;
  name: string;
  quantity: string;
  unitId?: number | null;
  unitName?: string | null;
  recipeId: number;
}

export interface IngredientPayload {
  name: string;
  quantity: string;
  unitId?: number | null;
  recipeId: number;
}
