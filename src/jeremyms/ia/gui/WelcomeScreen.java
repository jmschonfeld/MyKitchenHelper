package jeremyms.ia.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jeremyms.ia.Main;

public class WelcomeScreen extends JPanel {
	private static final long serialVersionUID = -5179697944976222302L;
	
	private JLabel title;
	private JButton buttonFood, buttonRecipes, buttonMealPlans;
	private JPanel buttonPane;
	
	public WelcomeScreen() {
		this.setLayout(new BorderLayout());
		
		
		title = new JLabel("My Kitchen Helper");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		Font f = new Font(Main.FONT_PRIMARY, Font.PLAIN, 56);
		title.setFont(f);
		this.add(title, BorderLayout.CENTER);
		
		buttonPane = new JPanel();
		GridLayout gl = new GridLayout(2, 5);
		gl.setHgap(50);
		gl.setVgap(25);
		buttonPane.setLayout(gl);
		buttonPane.add(Box.createGlue());
		
		buttonFood = new JButton("Ingredients");
		buttonPane.add(buttonFood);
		
		buttonRecipes = new JButton("Recipes");
		buttonPane.add(buttonRecipes);
		
		buttonMealPlans = new JButton("Meal Plans");
		buttonPane.add(buttonMealPlans);
		buttonPane.add(Box.createGlue());

		buttonPane.add(Box.createGlue());
		buttonPane.add(Box.createGlue());
		buttonPane.add(Box.createVerticalStrut(100));
		buttonPane.add(Box.createGlue());
		buttonPane.add(Box.createGlue());
		this.add(buttonPane, BorderLayout.SOUTH);
		
		setupActions();
	}
	
	private void setupActions() {
		buttonFood.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main.getWindow().setScreen(new IngredientScreen());
			}
		});
		buttonRecipes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main.getWindow().setScreen(new RecipeScreen());
			}
		});
		buttonMealPlans.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main.getWindow().setScreen(new MealPlanSelectScreen());
			}
		});
	}

}
