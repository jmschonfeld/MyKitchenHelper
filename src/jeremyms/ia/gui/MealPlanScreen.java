package jeremyms.ia.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jeremyms.ia.Main;
import jeremyms.ia.Printer;
import jeremyms.ia.data.MealPlanWeek;

public class MealPlanScreen extends JPanel {
	private static final long serialVersionUID = -8127851817076456265L;
	
	private MealPlanWeek plan;
	
	private JLabel title;
	private MealPlanPanel[] panels;
	private JButton back, print, delete;

	public MealPlanScreen(MealPlanWeek mealPlan) {
		plan = mealPlan;
		
		this.setLayout(new BorderLayout());
		
		title = new JLabel(plan.getTitleString());
		title.setHorizontalAlignment(SwingConstants.CENTER);
		Font f = new Font(Main.FONT_PRIMARY, Font.PLAIN, 48);
		title.setFont(f);
		this.add(title, BorderLayout.NORTH);
		//this.add(Box.createVerticalStrut(50), BorderLayout.NORTH);
		
		panels = new MealPlanPanel[7];
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new MealPlanPanel(plan.getPlanForDay(i));
			pan.add(panels[i], BorderLayout.CENTER);
		}
		this.add(pan, BorderLayout.CENTER);
		JPanel buttons = new JPanel(), south = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
		buttons.add(Box.createHorizontalGlue());
		delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				for (int i = 0; i < Main.getMealPlans().length; i++) {
					if (Main.getMealPlans()[i] != null && Main.getMealPlans()[i].getStartingDate().getTime() == plan.getStartingDate().getTime()) {
						Main.getMealPlans()[i] = null;
					}
				}
				Main.getWindow().setScreen(new MealPlanSelectScreen());
				JOptionPane.showMessageDialog(null, "Meal plan deleted!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttons.add(delete);
		buttons.add(Box.createHorizontalStrut(20));
		print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					Printer.printMealPlan(plan, JOptionPane.showConfirmDialog(null, "Do you want to print ingredients?", "Print Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		buttons.add(print);
		buttons.add(Box.createHorizontalStrut(20));
		back = new JButton("Save and Exit");
		buttons.add(back);
		buttons.add(Box.createHorizontalGlue());
		south.add(buttons);
		south.add(Box.createVerticalStrut(20));
		this.add(south, BorderLayout.SOUTH);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				save();
				Main.getWindow().setScreen(new MealPlanSelectScreen());
			}
		});
		//this.add(Box.createHorizontalGlue(), BorderLayout.SOUTH);
	}
	
	private void save() {
		for (int i = 0; i < panels.length; i++) {
			plan.getMealPlans()[i] = panels[i].getUpdatedMealPlan();
		}
		
		for (int i = 0; i < Main.getMealPlans().length; i++) {
			if (Main.getMealPlans()[i] != null && Main.getMealPlans()[i].getStartingDate().getTime() == plan.getStartingDate().getTime()) {
				Main.getMealPlans()[i] = plan;
			}
		}
	}
}
