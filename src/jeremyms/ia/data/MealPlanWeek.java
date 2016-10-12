package jeremyms.ia.data;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.util.Calendar;

public class MealPlanWeek {
	Date startingDate; // (Sunday)
	MealPlan[] plans;
	
	public MealPlanWeek(Date date) {
		this(date, genEmptyPlans(date));
	}
	
	private static MealPlan[] genEmptyPlans(Date date) {
		MealPlan[] plan = new MealPlan[7];
		for (int i = 0; i < plan.length; i++) {
			plan[i] = new MealPlan(new Date(date.getTime() + (86400000 * i)));
		}
		return plan;
	}
	
	public MealPlanWeek(Date date, MealPlan[] ps) {
		startingDate = date;
		plans = ps;		
	}
	
	public Date getStartingDate() {
		return startingDate;
	}
	
	public MealPlan getPlanForDay(int day) {
		return plans[day];
	}
	
	public MealPlan[] getMealPlans() {
		return plans;
	}
	
	public void updateMealPlans(MealPlan[] ps) {
		plans = ps;
	}
	

	
	public String getTitleString() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startingDate.getTime());
		return new DateFormatSymbols().getMonths()[c.get(Calendar.MONTH)] + " " + c.get(Calendar.DAY_OF_MONTH) + " - " + (c.get(Calendar.DAY_OF_MONTH) + 6);
	}
}
