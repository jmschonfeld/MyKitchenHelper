package jeremyms.ia.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManager {
	private Connection conn; //connection object
	
	public static final String DB_FILE = "kitchen.sqlite"; //file name
	public static String DB_LOC = ""; //location (user folder, local, etc.)
	
	public DatabaseManager() {
		//determine if a DB has to be generated
		boolean newdb = !new File(DB_LOC + DB_FILE).exists();
		connect();
		
		if (newdb) {
			//generate new DB if needed
			setupDatabase();
		}
	}
	
	public void backup() {
		File f = new File(DB_LOC + DB_FILE + ".bak");
		if (f.exists())
			f.delete();
		//create new backup file (empty)
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//copy contents of current db to backup db
			FileInputStream in = new FileInputStream(new File(DB_LOC + DB_FILE));
			FileOutputStream out = new FileOutputStream(f);
			while (in.available() > 0) {
				out.write(in.read());
			}
			out.flush();
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		try {
			return !conn.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void connect() {
		System.out.print("Connecting to database... " + "jdbc:sqlite:" + DB_LOC + DB_FILE);
		try {
			//Ensure driver is installed
			Class.forName("org.sqlite.JDBC");
			//open connection
			conn = DriverManager.getConnection("jdbc:sqlite:" + DB_LOC + DB_FILE);
			System.out.println(" done");
		} catch (SQLException e) {
			System.out.println("Unable to connect to database!");
			e.printStackTrace();
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to register JDBC SQLite driver");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public ResultSet query(String q) {
		ResultSet res = null;
		try {
			//create a statement
			Statement stmt = conn.createStatement();
			//execute it
			stmt.execute(q);
			//save results
			res = stmt.getResultSet();
			return res;
		} catch(SQLException e) {
			System.out.println("Error in executing query: " + q);
			e.printStackTrace();
		}
		//return results
		return res;
	}
	
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Unable to close connection to database!");
			e.printStackTrace();
		}
	}
	
	public void clearDatabase() {
		System.out.println("Clearing database...");
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Unable to close connection to database!");
			e.printStackTrace();
			return;
		}
		//delete file
		new File(DB_LOC + DB_FILE).delete();
		//re-setup database
		connect();
		setupDatabase();
	}
	
	public ArrayList<ResultSet> query(String ... queries) {
		if (queries.length == 0)
			return null;
		//query multiple things and return a list of results
		ArrayList<ResultSet> res = new ArrayList<ResultSet>();
		for (String q : queries) {
			res.add(query(q));
		}
		return res;
	}
	
	private void setupDatabase() {
		System.out.print("Creating and initializing database...");
		//create tables with columns
		query("CREATE TABLE recipes (name VARCHAR, directions VARCHAR, category VARCHAR, servings INTEGER, ingredients VARCHAR, picture VARCHAR)");
		query("CREATE TABLE ingredients (name VARCHAR, amount DOUBLE, amount_suffix VARCHAR, calories INTEGER, protein DOUBLE, fat DOUBLE, carbohydrates DOUBLE, brand VARCHAR, store_sec VARCHAR, exp_date VARCHAR)");
		query("CREATE TABLE mealplans (id VARCHAR, isempty BOOLEAN, date_start VARCHAR, day1 VARCHAR, day2 VARCHAR, day3 VARCHAR, day4 VARCHAR, day5 VARCHAR, day6 VARCHAR, day7 VARCHAR)");
		System.out.println(" done");
	}
}
