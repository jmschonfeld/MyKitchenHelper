package jeremyms.ia;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import jeremyms.ia.data.Ingredient;
import jeremyms.ia.data.MealPlan;
import jeremyms.ia.data.MealPlanWeek;
import jeremyms.ia.data.Recipe;

public class Printer {
	
	public static void printShoppingList() throws IOException {
		File f = new File("mkh-print.txt");
		PrintWriter writer = setupFile(f);
		writer.println("Shopping List - " + new SimpleDateFormat("M/d/y").format(new Date()));
		writer.println("============================");
		
		int total = 0;
		for (Ingredient i : Main.getIngredients()) {
			if (i.isUsed() || i.isExpired()) {
				writer.println(i.getName() + (i.isExpired() ? " (expired)" : ""));
				total++;
			}
		}
		
		writer.println();
		writer.println("Total Items: " + total);
		
		writer.flush();
		writer.close();
		Desktop.getDesktop().print(f);
		//f.delete();
	}
	
	public static void printMealPlan(MealPlanWeek plan, int ing) throws IOException {
		File f = new File("mkh-print.txt");
		PrintWriter writer = setupFile(f);
		writer.println("Meal Plan - " + plan.getTitleString());
		writer.println("+----------------------------------+");
		writer.println();
		for (int i = 0; i < 7; i++) {
			MealPlan p = plan.getPlanForDay(i);
			writer.println(p.getDayName());
			writer.println("===============");
			writer.println("Recipes: " + p.getRecipeString(p.getRecipes(), true));
			if (ing == 0) {
				writer.print("Ingredients: ");
				for (Recipe r : p.getRecipes()) {
					for (Ingredient ingredient : r.getIngredients()) {
						writer.print(ingredient.getName() + " (" + ingredient.getAmount() + "), ");
					}
				}
				writer.println();
			}
			writer.println("Notes: " + p.getDescription());
			writer.println();
		}
		writer.flush();
		writer.close();
		Desktop.getDesktop().print(f);
		//f.delete()
	}
	
	public static void printRecipe(Recipe r, int image) throws IOException {
		File f = new File("mkh-print.rtf");
		PrintWriter writer = setupFile(f);
		if (new File(r.getImageFile()).exists() && image == 0) {
			ImageIcon img = new ImageIcon(r.getImageFile());
			ImageInputStream s = ImageIO.createImageInputStream(new File(r.getImageFile()));
			String data = ""; int i;
			while ((i = s.read()) != -1) {
				data += i;
			}
			writer.println("{\\pict\\" +
					(r.getImageFile().endsWith(".png") ? "png" : "jpeg") + "blip" +
					"\\picw" + img.getIconWidth() +
					"\\pich" + img.getIconHeight() +
					"\\bin binary " + data + " }");
			writer.println();
		}
		writer.println(r.getName());
		writer.println("===========================");
		writer.println("Category: " + r.getCategory());
		writer.println("Servings: " + r.getServings());
		String s = "";
		for (Ingredient i : r.getIngredients()) {
			s += i.getAmount() + " " + i.getMeasurement() + "(s) of " + i.getName();
		}
		writer.println("Ingredients: " + s);
		writer.println();
		writer.println("Directions: " + r.getDirections());
		writer.flush();
		writer.close();
		Desktop.getDesktop().print(f);
		//f.delete()
	}
	
	private static PrintWriter setupFile(File f) throws IOException {
		if (f.exists())
			f.delete();
		f.createNewFile();
		return new PrintWriter(f);
	}
}
