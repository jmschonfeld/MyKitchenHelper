package jeremyms.ia.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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

public class IngredientScreen extends JPanel implements ActionListener{
	private static final long serialVersionUID = 4765758502013798134L;

	private JPanel panelList, panelOptions, panelButtons, panelEast;
	private JTable table;
	private JLabel title;
	private JButton bNew, bDelete, bPrint, bUsed, bSearch, bBack, bClearS;
	private JTextField tSearch;
	
	
	public IngredientScreen() {
		this.setLayout(new BorderLayout());
		
		title = new JLabel("Ingredients");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		Font f = new Font(Main.FONT_PRIMARY, Font.BOLD, 48);
		title.setFont(f);
		this.add(title, BorderLayout.NORTH);
		
		panelList = new JPanel();
		JScrollPane scroll = null;
		Object[][] data = genData(Main.getIngredients());
		table = new JTable(data, new String[]{"Name", "Amount", "Expiration"}) {
			private static final long serialVersionUID = -8597309529694252145L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				bDelete.setEnabled(true);
				bUsed.setEnabled(true);
				if (table.getSelectedRow() != -1) {
					Ingredient i = getIngredientByName((String)table.getModel().getValueAt(table.getSelectedRow(), 0));
					bUsed.setText(i.isUsed() ? "Mark as Restocked" : "Mark as Used");
				}
				
			}
		});
		table.getTableHeader().addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int c = table.getTableHeader().columnAtPoint(arg0.getPoint());
				try {
				switch (c) {
				case 0: DataSorter.sortIngredientsByString("getName"); break;
				case 1: DataSorter.sortIngredientsByInt("getAmount"); break;
				case 2: DataSorter.sortIngredientsByDate(); break;
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
		panelButtons.add(Box.createVerticalStrut(5));
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setMaximumSize(new Dimension(panelButtons.getWidth() - 50, 3));
		panelButtons.add(sep);
		panelButtons.add(Box.createVerticalStrut(5));
		bPrint = new JButton("Print Shopping List");
		bPrint.addActionListener(this);
		if (Main.getIngredients().size() == 0) bPrint.setEnabled(false);
		panelButtons.add(bPrint);
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
		bDelete = new JButton("Delete");
		bDelete.addActionListener(this);
		bDelete.setEnabled(false);
		panelOptions.add(bDelete);
		panelOptions.add(Box.createVerticalStrut(5));
		bUsed = new JButton("Mark as Used");
		bUsed.addActionListener(this);
		bUsed.setEnabled(false);
		panelOptions.add(bUsed);
		panelOptions.add(Box.createHorizontalGlue());
		panelEast.add(panelOptions);
		panelEast.add(Box.createVerticalGlue());
		
		this.add(panelEast, BorderLayout.EAST);
	}
	
	public void onAddScreenClosing(String[] data) {
		Ingredient ing = new Ingredient(data[0], data[2], Double.parseDouble(data[1]));
		System.out.println("Adding ingredient: " + Arrays.toString(data));
		if (!data[3].equals(""))
			ing.setBrand(data[3]);
		if (!data[4].equals(""))
			ing.setStoreSection(data[4]);
		if (!data[5].equals(""))
			ing.setProtein(Double.parseDouble(data[5]));
		if (!data[6].equals(""))
			ing.setFat(Double.parseDouble(data[6]));
		if (!data[7].equals(""))
			ing.setCarbohydrates(Double.parseDouble(data[7]));
		if (!data[8].equals(""))
			ing.setCalories(Integer.parseInt(data[8]));
		if (!data[9].equals("")) {
			try {
				ing.setExpirationDate(new java.sql.Date(new SimpleDateFormat("MM/dd/yy").parse(data[9]).getTime()));
			} catch (ParseException e) {
				System.out.println("Unable to convert java.util.Date to java.sql.Date");
			}
		}
		Main.getIngredients().add(ing);
		updateDataModel();
	}
	
	private void updateDataModel() {
		updateDataModel(Main.getIngredients());
	}
	
	private void updateDataModel(ArrayList<Ingredient>ingredients) {
		table.setModel(new DefaultTableModel(genData(ingredients), new String[]{"Name", "Amount", "Expiration"}));
		bPrint.setEnabled(Main.getIngredients().size() != 0);
		bSearch.setEnabled(Main.getIngredients().size() != 0);
		tSearch.setEnabled(Main.getIngredients().size() != 0);
		bDelete.setEnabled(false);
		bUsed.setEnabled(false);
		bUsed.setText("Mark as Used");
	}
	
	private Object[][] genData(ArrayList<Ingredient> ingredients) {
		int row = 0;
		Object[][] data = new Object[ingredients.size()][3];
		for (Ingredient i : ingredients) {
			data[row][0] = i.getName();
			data[row][1] = i.getAmount() + " " + i.getMeasurement() + "(s)";
			Calendar d = Calendar.getInstance();
			d.setTime(new java.util.Date(i.getExpirationDate().getTime()));
			data[row][2] = d.get(Calendar.MONTH) + "/" + d.get(Calendar.DAY_OF_MONTH) + "/" + d.get(Calendar.YEAR);
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
			ArrayList<Ingredient> rs = new ArrayList<Ingredient>();
			for (Ingredient r : Main.getIngredients()) {
				if (r.getName().contains(s))
					rs.add(r);
			}
			updateDataModel(rs);
			bClearS.setEnabled(true);
		} else if (e.getSource().equals(bClearS)) {
			tSearch.setText("");
			updateDataModel();
			bClearS.setEnabled(false);
		} else if (e.getSource().equals(bPrint)) {
			try {
				Printer.printShoppingList();
			} catch (IOException e1) {
				System.out.println("Unable to print shopping list");
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(bNew)) {
			new AddIngredientWindow().setVisible(true);
		} else if (e.getSource().equals(bDelete)) {
			int ans = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ans == 0) {
				Main.getIngredients().remove(getIngredientByName((String)table.getModel().getValueAt(table.getSelectedRow(), 0)));
				updateDataModel();
				JOptionPane.showMessageDialog(null, "Complete", "Ingredient Deleted!", JOptionPane.INFORMATION_MESSAGE);
				bDelete.setEnabled(false);
				bUsed.setEnabled(false);
				bUsed.setText("Mark as Used");
			}
		} else if (e.getSource().equals(bUsed)) {
			Ingredient i = getIngredientByName((String)table.getModel().getValueAt(table.getSelectedRow(), 0));
			if (i.isUsed()) {
				i.setUsed(false);
				String s = "-";
				s = JOptionPane.showInputDialog(null, "How many " + i.getMeasurement() + "(s) do you have now?", "Restock", JOptionPane.QUESTION_MESSAGE);
				while (!s.matches("[0-9\\.]*") || s.equals("")) {
					s = JOptionPane.showInputDialog(null, "Invalid input!\n\nHow many " + i.getMeasurement() + "(s) do you have now?", "Restock", JOptionPane.QUESTION_MESSAGE);
				}
				i.setAmount(Double.parseDouble(s));
			} else {
				i.setUsed(true);
			}
			bUsed.setText(i.isUsed() ? "Mark as Restocked" : "Mark as Used");
			updateDataModel();
		}
	}
	
	private Ingredient getIngredientByName(String n) {
		for (Ingredient i : Main.getIngredients()) {
			if (i.getName().equals(n))
				return i;
		}
		return null;
	}
}
