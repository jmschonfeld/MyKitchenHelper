package jeremyms.ia.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jeremyms.ia.data.DataManager;
import jeremyms.ia.data.MealPlan;
import jeremyms.ia.data.Recipe;

public class MealPlanPanel extends JPanel {
	private static final long serialVersionUID = 6143777905243144881L;
	
	private JLabel lRecipes;
	private JButton bAddRecipe;
	private JTextArea textArea;
	
	private MealPlan plan;	
	
	
	public MealPlanPanel(Date d) {
		this(new MealPlan(d));
	}
	
	public MealPlanPanel(MealPlan p) {
		this.plan = p;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setPreferredSize(new Dimension(110, 305));
		this.setMinimumSize(new Dimension(110, 305));
		this.setMaximumSize(new Dimension(110, 305));
		this.setBorder(BorderFactory.createTitledBorder(plan.getDayName()));
		
		lRecipes = new JLabel("<html>Recipes:<br>(none)</html>");
		this.add(lRecipes);
		
		bAddRecipe = new JButton("Add");
		this.add(bAddRecipe);
		
		if (plan.getRecipes().size() > 0)
			updateRecipeList();
		
		this.add(Box.createVerticalStrut(10));
		
		textArea = new JTextArea(5, 5);
		textArea.setText(plan.getDescription());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		this.add(new JScrollPane(textArea));
		
		bAddRecipe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] names = DataManager.getRecipeNames();
				if (names.length == 0) {
					JOptionPane.showMessageDialog(null, "No recipes to add", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String name = (String) JOptionPane.showInputDialog(null, "Select the recipe:", "Add Recipe", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
				plan.addRecipe(DataManager.getRecipeByName(name).clone());
				updateRecipeList();
			}
		});
		
		
	}
	
	private void updateRecipeList() {
		String l = "<html>Recipes:<br>";
		for (Recipe r : plan.getRecipes()) {
			l += r.getName() + "<br>";
		}
		l += "</html>";
		lRecipes.setText(l);
	}
	
	public MealPlan getUpdatedMealPlan() {
		plan.setDescription(textArea.getText());
		return plan;
	}
}
