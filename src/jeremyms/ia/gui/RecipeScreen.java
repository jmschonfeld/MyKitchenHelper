package jeremyms.ia.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import jeremyms.ia.Main;
import jeremyms.ia.Printer;
import jeremyms.ia.data.DataSorter;
import jeremyms.ia.data.Ingredient;
import jeremyms.ia.data.Recipe;

public class RecipeScreen extends JPanel implements ActionListener{
	private static final long serialVersionUID = 4765758502013798134L;

	private JPanel panelList, panelOptions, panelButtons, panelEast;
	private JTable table;
	private JLabel title;
	private JButton bNew, bEdit, bDelete, bPrint, bSearch, bBack, bClearS, bRecommendR;
	private JTextField tSearch;
	
	
	public RecipeScreen() {
		this.setLayout(new BorderLayout());
		
		title = new JLabel("Recipes");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		Font f = new Font(Main.FONT_PRIMARY, Font.BOLD, 48);
		title.setFont(f);
		this.add(title, BorderLayout.NORTH);
		
		panelList = new JPanel();
		JScrollPane scroll = null;
		Object[][] data = genData(Main.getRecipes());
		table = new JTable(data, new String[]{"Name", "Servings", "Category"}) {
			private static final long serialVersionUID = -8597309529694252145L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				bEdit.setEnabled(true);
				bDelete.setEnabled(true);
				bPrint.setEnabled(true);
			}
		});
		table.getTableHeader().addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int c = table.getTableHeader().columnAtPoint(arg0.getPoint());
				try {
					switch (c) {
					case 0: DataSorter.sortRecipesByString("getName"); break;
					case 1: DataSorter.sortRecipesByInt("getServings"); break;
					case 2: DataSorter.sortRecipesByString("getCategory"); break;
					default: break;
					}
					updateDataModel();
					} catch(Exception e) {
						System.out.println("Unable to sort data (" + e.getMessage() + ")");
					}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scroll = new JScrollPane(table);
		panelList.add(scroll);
		this.add(panelList, BorderLayout.CENTER);
		
		panelEast = new JPanel();
		panelEast.setLayout(new BoxLayout(panelEast, BoxLayout.Y_AXIS));
		
		panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
		panelButtons.setBorder(BorderFactory.createTitledBorder("Actions"));
		tSearch = new JTextField(15);
		tSearch.setMaximumSize(new Dimension(400, tSearch.getPreferredSize().height));
		if (Main.getIngredients().size() == 0) tSearch.setEnabled(false);
		panelButtons.add(tSearch);
		panelButtons.add(Box.createVerticalStrut(5));
		bSearch = new JButton("Search");
		if (Main.getIngredients().size() == 0) bSearch.setEnabled(false);
		bSearch.addActionListener(this);
		panelButtons.add(bSearch);		
		bClearS = new JButton("Clear");
		bClearS.setEnabled(false);
		bClearS.addActionListener(this);
		panelButtons.add(Box.createVerticalStrut(5));
		panelButtons.add(bClearS);
		bRecommendR = new JButton("Recommend Recipes");
		bRecommendR.addActionListener(this);
		if (Main.getIngredients().size() == 0) bRecommendR.setEnabled(false);
		panelButtons.add(Box.createVerticalStrut(5));
		panelButtons.add(bRecommendR);
		panelButtons.add(Box.createVerticalStrut(20));
		bBack = new JButton("Back to Home");
		bBack.addActionListener(this);
		panelButtons.add(bBack);
		
		panelEast.add(panelButtons);
		panelEast.add(Box.createVerticalStrut(5));
		
		panelOptions = new JPanel();
		panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.Y_AXIS));
		panelOptions.setBorder(BorderFactory.createTitledBorder("Options"));
		
		bNew = new JButton("New");
		bNew.addActionListener(this);
		panelOptions.add(bNew);
		panelOptions.add(Box.createVerticalStrut(5));
		bEdit = new JButton("View/Edit");
		bEdit.addActionListener(this);
		bEdit.setEnabled(false);
		panelOptions.add(bEdit);
		panelOptions.add(Box.createVerticalStrut(5));
		bDelete = new JButton("Delete");
		bDelete.addActionListener(this);
		bDelete.setEnabled(false);
		panelOptions.add(bDelete);
		panelOptions.add(Box.createVerticalStrut(5));
		bPrint = new JButton("Print");
		bPrint.addActionListener(this);
		bPrint.setEnabled(false);
		panelOptions.add(bPrint);
		panelOptions.add(Box.createHorizontalGlue());
		panelEast.add(panelOptions);
		panelEast.add(Box.createVerticalGlue());
		
		this.add(panelEast, BorderLayout.EAST);
	}
	
	@SuppressWarnings("unchecked")
	public void onAddScreenClosing(Object[] data) {
		Recipe r = new Recipe((String)data[0], (ArrayList<Ingredient>)data[1], (String)data[2], (String)data[3], (Integer)data[4], (String)data[5]);
		Main.getRecipes().add(r);
		updateDataModel();
	}
	
	public void updateDataModel() {
		updateDataModel(Main.getRecipes());
	}
	
	private void updateDataModel(ArrayList<Recipe> recipes) {
		table.setModel(new DefaultTableModel(genData(recipes), new String[]{"Name", "Servings", "Category"}));
		bSearch.setEnabled(Main.getRecipes().size() != 0);
		tSearch.setEnabled(Main.getRecipes().size() != 0);
		bRecommendR.setEnabled(Main.getRecipes().size() != 0);
		bEdit.setEnabled(false);
		bPrint.setEnabled(false);
		bDelete.setEnabled(false);
	}
	
	private Object[][] genData(ArrayList<Recipe> recipes) {
		int row = 0;
		Object[][] data = new Object[recipes.size()][3];
		for (Recipe r : recipes) {
			data[row][0] = r.getName();
			data[row][1] = r.getServings();
			data[row][2] = r.getCategory();
			row++;
		}
		return data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(bBack)) {
			Main.getWindow().setScreen(new WelcomeScreen());
		} else if (e.getSource().equals(bSearch)) {
			String s = tSearch.getText();
			if (s.equals("")) {
				updateDataModel();
				return;
			}
			ArrayList<Recipe> rs = new ArrayList<Recipe>();
			for (Recipe r : Main.getRecipes()) {
				if (r.getName().contains(s))
					rs.add(r);
			}
			updateDataModel(rs);
			bClearS.setEnabled(true);
		} else if (e.getSource().equals(bClearS)) {
			tSearch.setText("");
			updateDataModel();
			bClearS.setEnabled(false);
		} else if (e.getSource().equals(bRecommendR)) {
			ArrayList<Recipe> recom = new ArrayList<Recipe>();
			for (Recipe r : Main.getRecipes()) {
				int have = 0;
				for (Ingredient i : r.getIngredients()) {
					for (Ingredient mI : Main.getIngredients()) {
						if (mI.getName().equals(i.getName())) {
							if (mI.getAmount() >= i.getAmount())
								have++;
						}
					}
				}
				if (have == r.getIngredients().size())
					recom.add(r);
			}
			updateDataModel(recom);
			bClearS.setEnabled(true);
		} else if (e.getSource().equals(bPrint)) {
			try {
				Printer.printRecipe(getRecipeByName((String)table.getModel().getValueAt(table.getSelectedRow(), 0)), JOptionPane.showConfirmDialog(null, "Do you want to print the image?", "Print Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(bNew)) {
			new AddRecipeWindow().setVisible(true);
		} else if (e.getSource().equals(bDelete)) {
			int ans = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ans == 0) {
				Main.getRecipes().remove(getRecipeByName((String)table.getModel().getValueAt(table.getSelectedRow(), 0)));
				updateDataModel();
				JOptionPane.showMessageDialog(null, "Complete", "Recipe Deleted!", JOptionPane.INFORMATION_MESSAGE);
				bDelete.setEnabled(false);
				bEdit.setEnabled(false);
				bPrint.setEnabled(false);
			}
		} else if (e.getSource().equals(bEdit)) {
			new AddRecipeWindow(getRecipeByName((String)table.getModel().getValueAt(table.getSelectedRow(), 0))).setVisible(true);
		}
	}
	
	private Recipe getRecipeByName(String n) {
		for (Recipe r : Main.getRecipes()) {
			if (r.getName().equals(n))
				return r;
		}
		return null;
	}
}
