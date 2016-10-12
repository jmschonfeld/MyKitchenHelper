package jeremyms.ia.data;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import jeremyms.ia.Main;

public class Recipe {
	private String name, picture;
	private ArrayList<Ingredient> ingredients;
	private String directions;
	private String category;
	private int servings;
	
	public Recipe(String name, ArrayList<Ingredient> ingredients, String directions, String category, int servings, String picture) {
		this.name = name;
		this.ingredients = ingredients;
		this.directions = directions;
		this.category = category;
		this.servings = servings;
		this.picture = picture;
		if (!new File(picture).exists() && !picture.equals("")) {
			picture = "";
		}
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Ingredient> getIngredients() {
		return ingredients;
	}
	
	public boolean needsIngredient(Ingredient ing) {
		return ingredients.contains(ing);
	}
	
	public String getDirections() {
		return directions;
	}
	
	public String getCategory() {
		return category;
	}
	
	public int getServings() {
		return servings;
	}
	
	public Image getImage() {
		return new ImageIcon(picture).getImage();
	}
	
	public String getImageFile() {
		return picture;
	}
	
	public String getIngStorageString() {
		String s = "";
		for (Ingredient i : ingredients) {
			s += i.getAmount() + "-" + i.getName() + ",";
		}
		if (!s.equals(""))
			s = s.substring(0, s.length() - 1);
		return s;
	}
	
	public static ArrayList<Ingredient> convertIngStorageString(String s) {
		ArrayList<Ingredient> is = new ArrayList<Ingredient>();
		if (s.equals(""))
			return is;
		String[] split = s.split("\\,");
		for (String i : split) {
			String name = i.split("\\-")[1];
			double amount = Double.parseDouble(i.split("\\-")[0]);
			for (Ingredient ing : Main.getIngredients()) {
				if (ing.getName().equals(name)) {
					Ingredient ingredient = ing.clone();
					ingredient.setAmount(amount);
					is.add(ingredient);
					break;
				}
			}
		}
		return is;
	}
	
	public Recipe clone() {
		Recipe r = new Recipe(name, ingredients, directions, category, servings, picture);
		return r;
	}
}
