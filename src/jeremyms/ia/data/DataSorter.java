package jeremyms.ia.data;

import jeremyms.ia.Main;

public class DataSorter {
	public static void sortIngredientsByString(String method) throws Exception {
		for (int pass = 0; pass < Main.getIngredients().size(); pass++) {
			for (int i = 0; i < Main.getIngredients().size() - 1; i++) {
				Ingredient c = Main.getIngredients().get(i);
				Ingredient n = Main.getIngredients().get(i + 1);
				if (((String)Ingredient.class.getMethod(method).invoke(c)).compareTo((String)Ingredient.class.getMethod(method).invoke(n)) > 0) {
					Ingredient temp = n;
					Main.getIngredients().set(i + 1, c);
					Main.getIngredients().set(i, temp);
				}
			}
		}
	}
	
	public static void sortIngredientsByDate() throws Exception {
		for (int pass = 0; pass < Main.getIngredients().size(); pass++) {
			for (int i = 0; i < Main.getIngredients().size() - 1; i++) {
				Ingredient c = Main.getIngredients().get(i);
				Ingredient n = Main.getIngredients().get(i + 1);
				if (c.getExpirationDate().after(n.getExpirationDate())) {
					Ingredient temp = n;
					Main.getIngredients().set(i + 1, c);
					Main.getIngredients().set(i, temp);
				}
			}
		}
	}
	
	public static void sortIngredientsByInt(String method) throws Exception {
		for (int pass = 0; pass < Main.getIngredients().size(); pass++) {
			for (int i = 0; i < Main.getIngredients().size() - 1; i++) {
				Ingredient c = Main.getIngredients().get(i);
				Ingredient n = Main.getIngredients().get(i + 1);
				if ((Double)Ingredient.class.getMethod(method).invoke(c) > (Double)Ingredient.class.getMethod(method).invoke(n)) {
					Ingredient temp = n;
					Main.getIngredients().set(i + 1, c);
					Main.getIngredients().set(i, temp);
				}
			}
		}
	}
	
	public static void sortRecipesByString(String method) throws Exception {
		for (int pass = 0; pass < Main.getRecipes().size(); pass++) {
			for (int i = 0; i < Main.getRecipes().size() - 1; i++) {
				Recipe c = Main.getRecipes().get(i);
				Recipe n = Main.getRecipes().get(i + 1);
				if (((String)Recipe.class.getMethod(method).invoke(c)).compareTo((String)Recipe.class.getMethod(method).invoke(n)) > 0) {
					Recipe temp = n;
					Main.getRecipes().set(i + 1, c);
					Main.getRecipes().set(i, temp);
				}
			}
		}
	}
	
	public static void sortRecipesByInt(String method) throws Exception {
		for (int pass = 0; pass < Main.getRecipes().size(); pass++) {
			for (int i = 0; i < Main.getRecipes().size() - 1; i++) {
				Recipe c = Main.getRecipes().get(i);
				Recipe n = Main.getRecipes().get(i + 1);
				if ((Integer)Recipe.class.getMethod(method).invoke(c) > (Integer)Recipe.class.getMethod(method).invoke(n)) {
					Recipe temp = n;
					Main.getRecipes().set(i + 1, c);
					Main.getRecipes().set(i, temp);
				}
			}
		}
	}
}
