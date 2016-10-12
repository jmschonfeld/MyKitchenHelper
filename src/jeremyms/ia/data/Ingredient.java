package jeremyms.ia.data;

import java.sql.Date;


public class Ingredient {
	private String name, measurement, brand, storeSection;
	private double amount, protein, fat, carbohydrates;
	private int calories;
	private Date expirationDate;
	private boolean used;

	
	public Ingredient(String name, String measurement, double amount) {
		this.name = name;
		this.measurement = measurement;
		this.amount = amount;
		this.used = amount == 0;
	}
	
	public Ingredient clone() {
		Ingredient i = new Ingredient(name, measurement, amount);
		i.setBrand(brand);
		i.setStoreSection(storeSection);
		i.setProtein(protein);
		i.setFat(fat);
		i.setCarbohydrates(carbohydrates);
		i.setCalories(calories);
		i.setExpirationDate(expirationDate);
		return i;
	}
	
	public void setBrand(String b) {
		brand = b;
	}
	
	public void setStoreSection(String s) {
		storeSection = s;
	}
	
	public void setProtein(double p) {
		protein = p;
	}
	
	public void setFat(double f) {
		fat = f;
	}
	
	public void setCarbohydrates(double c) {
		carbohydrates = c;
	}
	
	public void setCalories(int c) {
		calories = c;
	}
	
	public void setExpirationDate(Date d) {
		expirationDate = d;
	}
	
	public void setAmount(double a) {
		amount = a;
	}
	
	public void setUsed(boolean u) {
		used = u;
		if (u) {
			amount = 0.0;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getMeasurement() {
		return measurement;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public String getStoreSection() {
		return storeSection;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public double getProtein() {
		return protein;
	}
	
	public double getCarbohydrates() {
		return carbohydrates;
	}
	
	public double getFat() {
		return fat;
	}
	
	public int getCalories() {
		return calories;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public boolean isExpired() {
		return expirationDate.before(new Date(System.currentTimeMillis()));
	}
}
