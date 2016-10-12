package jeremyms.ia.data;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class MealPlan {
	Date day;
	ArrayList<Recipe> recipes;
	String description;
	
	public MealPlan(Date day) {
		this.day = day;
		recipes = new ArrayList<Recipe>();
	}
	
	public MealPlan(String storage) {
		String[] parts = storage.split("\\*");
		day = new Date(Long.parseLong(parts[0]));
		description = (parts[1].equals("NULL") ? "" : parts[1]);
		recipes = new ArrayList<Recipe>();
		if (!parts[2].equals("NULL")) {
			String[] rec = parts[2].split("\\,");
			for (String r : rec) {
				this.recipes.add(DataManager.getRecipeByName(r));
			}
		}
	}
	
	public void addRecipe(Recipe r) {
		recipes.add(r);
	}
	
	public void removeRecipe(Recipe r) {
		recipes.remove(r);
	}
	
	public ArrayList<Recipe> getRecipes() {
		return recipes;
	}
	
	public void setRecipes(ArrayList<Recipe> r) {
		recipes = r;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getStorageString() {
		return day.getTime() + "*" + (description.equals("") ? "NULL" : description) + "*" + (getRecipeString(recipes, false).equals("") ? "NULL" : getRecipeString(recipes, false));
	}
	
	public String getRecipeString(ArrayList<Recipe> rs, boolean spaces) {
		String s = "";
		for (Recipe r : rs) {
			s += r.getName() + "," + (spaces ? " " : "");
		}
		return s;
	}
	
	public String getDayName() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(day.getTime());
		return new DateFormatSymbols().getWeekdays()[c.get(Calendar.DAY_OF_WEEK)];
	}
}
