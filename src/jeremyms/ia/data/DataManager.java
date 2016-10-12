package jeremyms.ia.data;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import jeremyms.ia.Main;

public class DataManager {
	
	public static String[] getIngredientNames() {
		String[] names = new String[Main.getIngredients().size()];
		for (int i = 0; i < Main.getIngredients().size(); i++) {
			names[i] = Main.getIngredients().get(i).getName();
		}
		return names;
	}
	
	public static String[] getRecipeNames() {
		String[] names = new String[Main.getRecipes().size()];
		for (int i = 0; i < Main.getRecipes().size(); i++) {
			names[i] = Main.getRecipes().get(i).getName();
		}
		return names;
	}
	
	public static Ingredient getIngredientByName(String name) {
		for (Ingredient ing : Main.getIngredients()) {
			if (ing.getName().equals(name))
				return ing;
		}
		return null;
	}
	
	public static Recipe getRecipeByName(String name) {
		for (Recipe r : Main.getRecipes()) {
			if (r.getName().equals(name))
				return r;
		}
		return null;
	}
	
	
	public static void loadData(DatabaseManager db, ArrayList<Ingredient> food, ArrayList<Recipe> recipes, MealPlanWeek[] plans) throws SQLException {
		if (!verifyConn(db))
			return;
		System.out.print("Loading ingredients...");
		ResultSet res = db.query("SELECT * FROM ingredients");
		while (res.next()) {
			Ingredient i = new Ingredient(res.getString("name"), res.getString("amount_suffix"), res.getDouble("amount"));
			if (res.getInt("calories") != 0)
				i.setCalories(res.getInt("calories"));
			if (res.getDouble("protein") != 0)
				i.setProtein(res.getDouble("protein"));
			if (res.getDouble("fat") != 0)
				i.setFat(res.getDouble("fat"));
			if (res.getDouble("carbohydrates") != 0)
				i.setCarbohydrates(res.getDouble("carbohydrates"));
			if (!res.getString("brand").equals("null"))
				i.setBrand(res.getString("brand"));
			if (!res.getString("store_sec").equals("null"))
				i.setStoreSection(res.getString("store_sec"));
			if (!res.getString("exp_date").equals("null"))
				i.setExpirationDate(Date.valueOf(res.getString("exp_date")));
			food.add(i);
		}
		System.out.println(" loaded " + food.size() + " ingredients");
		
		System.out.print("Loading recipes...");
		res = db.query("SELECT * FROM recipes");
		while (res.next()) {
			recipes.add(new Recipe(res.getString("name"), Recipe.convertIngStorageString(res.getString("ingredients")), res.getString("directions"), res.getString("category"), res.getInt("servings"), res.getString("picture")));
		}
		System.out.println(" loaded " + recipes.size() + " recipes");
		
		System.out.print("Loading meal plans...");
		res = db.query("SELECT * FROM mealplans");
		int counter = 0;
		while (res.next()) {
			if (!res.getString("isempty").equals("false")) {
				plans[counter] = null;
				continue;
			}
			MealPlan[] days = new MealPlan[7];
			for (int i = 0; i < days.length; i++) {
				days[i] = new MealPlan(res.getString("day" + (i+1)));
			}
			plans[counter] = new MealPlanWeek(new java.sql.Date(res.getLong("date_start")), days);
			counter++;
		}
		System.out.println(" loaded " + counter + " meal plans");
	}
	
	public static void saveData(DatabaseManager db, ArrayList<Ingredient> food, ArrayList<Recipe> recipes, MealPlanWeek[] plans) throws SQLException {
		db.backup();
		if (!verifyConn(db))
			return;
		System.out.print("Saving ingredients...");
		ResultSet rs = db.query("SELECT * FROM ingredients");
		while (rs.next()) {
			boolean found = false;
			for (Ingredient i : food) {
				if (i.getName().equals(rs.getString("name")))
					found = true;
			}
			if (!found) {
				db.query("DELETE FROM ingredients WHERE name='" + rs.getString("name") + "'");
			}
		}
		for (Ingredient ing : food) {
			ResultSet res = db.query("SELECT * FROM ingredients WHERE name='" + ing.getName() + "'");
			if (res.next()) {
				db.query("UPDATE ingredients SET amount=" + ing.getAmount() + ", amount_suffix='" + ing.getMeasurement() + "', calories=" + ing.getCalories() + ", protein=" + ing.getProtein() + ", fat=" + ing.getFat() + ", carbohydrates=" + ing.getCarbohydrates() + ", brand='" + ing.getBrand() + "', store_sec='" + ing.getStoreSection() + "', exp_date='" + ing.getExpirationDate().toString() + "' WHERE name='" + ing.getName() + "'");
			} else {
				db.query("INSERT INTO ingredients VALUES ('" + ing.getName() + "', " + ing.getAmount() + ", '" + ing.getMeasurement() + "', " + ing.getCalories() + ", " + ing.getProtein() + ", " + ing.getFat() + ", " + ing.getCarbohydrates() + ", '" + ing.getBrand() + "', '" + ing.getStoreSection() + "', '" + ing.getExpirationDate().toString() + "')");
			}
		}
		System.out.println(" saved " + food.size() + " ingredients");
		
		System.out.print("Saving recipes...");
		rs = db.query("SELECT * FROM recipes");
		while (rs.next()) {
			boolean found = false;
			for (Recipe r : recipes) {
				if (r.getName().equals(rs.getString("name")))
					found = true;
			}
			if (!found) {
				db.query("DELETE FROM recipes WHERE name='" + rs.getString("name") + "'");
			}
		}
		for (Recipe r : recipes) {
			ResultSet res = db.query("SELECT * FROM recipes WHERE name='" + r.getName() + "'");
			if (res.next()) {
				db.query("UPDATE recipes SET directions='" + r.getDirections() + "', category='" + r.getCategory() + "', servings=" + r.getServings() + ", ingredients='" + r.getIngStorageString() + "', picture='" + r.getImageFile() + "' WHERE name='" + r.getName() + "'");
			} else {
				db.query("INSERT INTO recipes VALUES ('" + r.getName() + "', '" + r.getDirections() + "', '" + r.getCategory() + "', " + r.getServings() + ", '" + r.getIngStorageString() + "', '" + r.getImageFile() + "')");
			}
		}
		System.out.println(" saved " + recipes.size() + " recipes");
		
		System.out.print("Saving meal plans...");
		int counter = 0, i = 0;

		db.query("DELETE FROM mealplans");
		for (MealPlanWeek plan : plans) {
			if (plan == null) {
				db.query("INSERT INTO mealplans VALUES ('" + i + "', 'true', '', '', '', '', '', '', '', '')");
				i++;
				continue;
			}
			MealPlan[] days = plan.getMealPlans();
			db.query("INSERT INTO mealplans VALUES ('" + i + "', 'false', '" + plan.getStartingDate().getTime() + "', '" + days[0].getStorageString() + "', '" + days[1].getStorageString() + "', '" + days[2].getStorageString() + "', '" + days[3].getStorageString() + "', '" + days[4].getStorageString() + "', '" + days[5].getStorageString() + "', '" + days[6].getStorageString() + "')");
			i++;
			counter++;
		}
		
		System.out.println(" saved " + counter + " meal plans");
	}
	
	private static boolean verifyConn(DatabaseManager db) {
		System.out.print("Verifying database connection...");
		if (!db.isConnected()) {
			System.out.println(" Unable to access database!");
			return false;
		} else {
			System.out.println(" done");
			return true;
		}
	}
}
