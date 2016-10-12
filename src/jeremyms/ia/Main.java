package jeremyms.ia;

import java.awt.SplashScreen;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jeremyms.ia.data.DataManager;
import jeremyms.ia.data.DatabaseManager;
import jeremyms.ia.data.Ingredient;
import jeremyms.ia.data.MealPlanWeek;
import jeremyms.ia.data.Recipe;
import jeremyms.ia.gui.WelcomeScreen;
import jeremyms.ia.gui.Window;

public class Main {
	private static DatabaseManager dbManager;
	
	private static ArrayList<Ingredient> food;
	private static ArrayList<Recipe> recipes;
	private static MealPlanWeek[] mealPlans;
	
	public static final String FONT_PRIMARY = "Eras Demi ITC", FONT_SECONDARY = "Eras Light ITC";
	
	private static Window window;

	public static void main(String[] args) {
		Map<String, String> arguments = parseArgs(args);
		System.out.println("== My Kitchen Helper v" + SysInfo.FULL_VERSION + " ==");
		System.out.print("Initializing shutdown hook...");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Main.onShutdown();
			}
		});
		System.out.println(" done");
		if (arguments.containsKey("localdb")) {
			System.out.println("Warning: Using local database");
			DatabaseManager.DB_LOC = "";
		} else {
			DatabaseManager.DB_LOC = System.getProperty("user.home") + File.separator + "MyKitchenHelper";
			new File(DatabaseManager.DB_LOC).mkdir();
			DatabaseManager.DB_LOC += File.separator;
			System.out.println(DatabaseManager.DB_LOC);
		}
		if (arguments.containsKey("cleardb")) {
			System.out.print("Deleting database...");
			new File(DatabaseManager.DB_FILE).delete();
			System.out.println(" done");
		}
		dbManager = new DatabaseManager();
		System.out.println("Loading data...");
		food = new ArrayList<Ingredient>();
		recipes = new ArrayList<Recipe>();
		mealPlans = new MealPlanWeek[4];
		try {
			DataManager.loadData(dbManager, food, recipes, mealPlans);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Data loaded!");
		System.out.print("Setting up window...");
		window = new Window();
		System.out.println(" done");
		System.out.print("Launching...");
		if (SplashScreen.getSplashScreen() != null)
			SplashScreen.getSplashScreen().close();
		window.setScreen(new WelcomeScreen());
		window.setVisible(true);
		System.out.println(" done");
	}
	
	public static ArrayList<Ingredient> getIngredients() {
		return food;
	}
	
	public static ArrayList<Recipe> getRecipes() {
		return recipes;
	}
	
	public static MealPlanWeek[] getMealPlans() {
		return mealPlans;
	}
	
	public static Window getWindow() {
		return window;
	}
	
	public static void onShutdown() {
		new File("mkh-print.txt").delete();
		System.out.println("Saving data...");
		try {
			DataManager.saveData(dbManager, food, recipes, mealPlans);
		} catch (SQLException e) {
			System.out.println("Failed to save data!");
			e.printStackTrace();
		}
		System.out.println("Data saved!");
		System.out.print("Disconnecting from database...");
		dbManager.close();
		System.out.println(" done");
	}
	
	public static DatabaseManager getDatabaseManager() {
		return dbManager;
	}
	
	public static Map<String, String> parseArgs(String[] args) {
		Map<String, String> a = new HashMap<String, String>();
		for (String s : args) {
			if (s.split("\\=").length > 1) {
				String var = s.split("\\=")[0].replaceAll("\\-", ""), val = s.split("\\=")[1];
				a.put(var, val);
			} else {
				a.put(s.replaceAll("\\-", ""), null);
			}
		}
		return a;
	}
}
