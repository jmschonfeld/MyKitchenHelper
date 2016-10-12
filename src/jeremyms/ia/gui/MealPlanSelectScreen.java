package jeremyms.ia.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jeremyms.ia.Main;
import jeremyms.ia.data.MealPlanWeek;

public class MealPlanSelectScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1216226747342786907L;

	private JButton bA, bB, bC, bD, bBack;
	private JLabel title;
	private JPanel buttonPane;
	
	public MealPlanSelectScreen() {
		this.setLayout(new BorderLayout());
		
		title = new JLabel("Select a Week");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		Font f = new Font(Main.FONT_PRIMARY, Font.PLAIN, 56);
		title.setFont(f);
		this.add(title, BorderLayout.CENTER);
		
		buttonPane = new JPanel();
		GridLayout gl = new GridLayout(2, 5);
		gl.setHgap(20);
		gl.setVgap(25);
		buttonPane.setLayout(gl);
		buttonPane.add(Box.createGlue());
		
		bA = new JButton("Week A");
		bA.setName("0");
		bA.addActionListener(this);
		buttonPane.add(bA);
		bB = new JButton("Week B");
		bB.setName("1");
		bB.addActionListener(this);
		buttonPane.add(bB);
		bC = new JButton("Week C");
		bC.setName("2");
		bC.addActionListener(this);
		buttonPane.add(bC);
		bD = new JButton("Week D");
		bD.setName("3");
		bD.addActionListener(this);
		buttonPane.add(bD);
		
		addWeekNames();
		
		buttonPane.add(Box.createGlue());

		buttonPane.add(Box.createGlue());
		buttonPane.add(Box.createGlue());
		buttonPane.add(Box.createVerticalStrut(100));
		buttonPane.add(Box.createGlue());
		buttonPane.add(Box.createGlue());
		this.add(buttonPane, BorderLayout.SOUTH);
		
		bBack = new JButton("Back");
		bBack.addActionListener(this);
		JPanel backPanel = new JPanel();
		backPanel.setLayout(new FlowLayout());
		backPanel.add(bBack);
		this.add(backPanel, BorderLayout.WEST);
		this.validate();
		this.add(Box.createHorizontalStrut(bBack.getPreferredSize().width), BorderLayout.EAST);
	}
	
	private void addWeekNames() {
		for (int i = 0; i < Main.getMealPlans().length; i++) {
			if (Main.getMealPlans()[i] == null)
				continue;
			Date mp = Main.getMealPlans()[i].getStartingDate();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(mp.getTime());
			String txt = getMonthNameFromInt(c.get(Calendar.MONTH)) + " " + c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.DAY_OF_MONTH) + 6);
			switch (i) {
			case 0: bA.setText(txt); break;
			case 1: bB.setText(txt); break;
			case 2: bC.setText(txt); break;
			case 3: bD.setText(txt); break;
			default: break;
			}
		}
	}
	
	private String getMonthNameFromInt(int m) {
		return new DateFormatSymbols().getMonths()[m];
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(bBack)) {
			Main.getWindow().setScreen(new WelcomeScreen());
		} else {
			int index = Integer.parseInt(((JButton)event.getSource()).getName());
			if (Main.getMealPlans()[index] == null) {
				System.out.print("Creating new meal plan...");
				String s = "-";
				s = JOptionPane.showInputDialog(null, "Input the week starting date (mm/dd/yy)", "Create Meal Plan", JOptionPane.QUESTION_MESSAGE);
				if (s == null || s.equals("")) {
					System.out.println(" cancelled");
					return;
				}
				try {
					while (!s.matches("[0-9][0-9]\\/[0-9][0-9]\\/[0-9][0-9]") || s.equals("") || isInvalid(s, new java.sql.Date(new SimpleDateFormat("MM/dd/yy").parse(s).getTime()))) {
						s = JOptionPane.showInputDialog(null, "Invalid input!\n\nInput the week starting date (mm/dd/yy)", "Create Meal Plan", JOptionPane.QUESTION_MESSAGE);
					}
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				try {
					Main.getMealPlans()[index] = new MealPlanWeek(new java.sql.Date(new SimpleDateFormat("MM/dd/yy").parse(s).getTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				System.out.println(" done");
			}
			System.out.println(Main.getMealPlans()[index].getPlanForDay(0).getDescription());
			Main.getWindow().setScreen(new MealPlanScreen(Main.getMealPlans()[index]));
		}
	}
	
	private boolean isInvalid(String s, java.sql.Date date) {
		if (Integer.parseInt(s.split("\\/")[0]) > 31 || Integer.parseInt(s.split("\\/")[1]) > 31) {
			return true;
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		boolean sun =  c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
		boolean tak = false;
		for (MealPlanWeek mp : Main.getMealPlans()) {
			if (mp != null && mp.getStartingDate().getTime() == date.getTime())
				tak = true;
		}
		return !sun || tak;
	}
}
