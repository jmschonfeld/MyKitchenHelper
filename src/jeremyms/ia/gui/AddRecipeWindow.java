package jeremyms.ia.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

import jeremyms.ia.Main;
import jeremyms.ia.data.DataManager;
import jeremyms.ia.data.Ingredient;
import jeremyms.ia.data.Recipe;
import jeremyms.ia.util.SpringUtilities;

public class AddRecipeWindow extends JFrame implements WindowListener {
	private static final long serialVersionUID = -5508996629685432939L;
	private static final int WIDTH = 500, HEIGHT = 500;
	
	private boolean submitted = false, editing = false;
	private Recipe editRecipe;
	private JLabel lName, lCategory, lServings, lDirections, lPicture, lIngredients, lIngList;
	private ArrayList<Ingredient> ing = new ArrayList<Ingredient>();
	private JTextField tName, tCategory, tServings, tPicture;
	private JTextArea tDirections;
	private JLabel lError;
	private JButton bPicture, submit, bAddIng, bViewPic;
	private JPanel panelPic;
	private JFileChooser fileChooser;
	
	public AddRecipeWindow(Recipe r) {
		this();
		editRecipe = r;
		tName.setText(r.getName());
		tName.setEnabled(false);
		tCategory.setText(r.getCategory());
		tServings.setText(r.getServings() + "");
		tDirections.setText(r.getDirections());
		ing = r.getIngredients();
		updateIngList();
		tPicture.setText(r.getImageFile());
		bViewPic.setEnabled(true);
		submit.setText("Update");
		editing = true;
	}
	
	public AddRecipeWindow() {
		super("Add Recipe");
		this.setResizable(false);
		Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(sz.width / 2 - WIDTH / 2, sz.height / 2 - HEIGHT / 2, WIDTH, HEIGHT);
		this.addWindowListener(this);
		this.getContentPane().setLayout(new SpringLayout());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		lError = new JLabel();
		lError.setForeground(Color.RED);
		this.getContentPane().add(lError);
		this.getContentPane().add(new JLabel());
		
		lName = new JLabel("Name:");
		this.getContentPane().add(lName);
		tName = new JTextField();
		this.getContentPane().add(tName);

		lCategory = new JLabel("Category:");
		this.getContentPane().add(lCategory);
		tCategory = new JTextField();
		this.getContentPane().add(tCategory);

		lServings = new JLabel("Servings:");
		this.getContentPane().add(lServings);
		tServings = new JTextField();
		this.getContentPane().add(tServings);
		
		lIngredients = new JLabel("Ingredients:");
		this.getContentPane().add(lIngredients);
		JPanel panel = new JPanel();
		lIngList = new JLabel("(none)");
		panel.add(lIngList);
		bAddIng = new JButton("Add Ingredient");
		if (Main.getIngredients().size() == 0)
			bAddIng.setEnabled(false);
		panel.add(bAddIng);
		this.getContentPane().add(panel);

		lDirections = new JLabel("Directions:");
		this.getContentPane().add(lDirections);
		tDirections = new JTextArea(12, 30);
		tDirections.setLineWrap(true);
		this.getContentPane().add(new JScrollPane(tDirections));

		lPicture = new JLabel("Picture:");
		this.getContentPane().add(lPicture);
		panelPic = new JPanel();
		bPicture = new JButton("Choose File");
		tPicture = new JTextField(15);
		panelPic.add(tPicture);
		panelPic.add(bPicture);
		bViewPic = new JButton("View");
		bViewPic.setEnabled(false);
		bViewPic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null, new ImageIcon(new ImageIcon(tPicture.getText()).getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT)), "Recipe Image", JOptionPane.PLAIN_MESSAGE);
			}
		});
		panelPic.add(bViewPic);
		this.getContentPane().add(panelPic);
		panelPic.validate();
		bPicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select Recipe Picture");
				if (new File(tPicture.getText()).exists()) {
					//fileChooser.setCurrentDirectory(new File(tPicture.getText()).getParentFile());
					fileChooser.setSelectedFile(new File(tPicture.getText()));
				}
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setFileFilter(new FileNameExtensionFilter("Pictures", "png", "jpg", "jpeg", "bmp"));
				fileChooser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						tPicture.setText((fileChooser.getSelectedFile() == null ? tPicture.getText() : fileChooser.getSelectedFile().getPath()));
						fileChooser.setVisible(false); 
						if (!tPicture.equals("")) {
							bViewPic.setEnabled(true);
						}
					}
				});
				fileChooser.showDialog(null, "Select Picture");
			}
		});

		this.getContentPane().add(Box.createVerticalStrut(50));
		this.getContentPane().add(Box.createVerticalStrut(50));
		
		this.getContentPane().add(new JLabel());

		submit = new JButton("Submit");
		this.getContentPane().add(submit);
		
		SpringUtilities.makeCompactGrid(this.getContentPane(), 9, 2, 10, 10, 10, 10);
		
		final JFrame win = this;
		submit.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String error = errorCheck();
				if (error != null) {
					lError.setText(error);
					return;
				}
				if (!editing) {
					((RecipeScreen) Main.getWindow().getScreen()).onAddScreenClosing(getData());
				} else {
					Object[] data = getData();
					for (int i = 0; i < Main.getRecipes().size(); i++) {
						if (Main.getRecipes().get(i).equals(editRecipe)) {
							Main.getRecipes().set(i, new Recipe((String)data[0], (ArrayList<Ingredient>)data[1], (String)data[2], (String)data[3], (Integer)data[4], (String)data[5]));
							break;
						}
					}
					((RecipeScreen) Main.getWindow().getScreen()).updateDataModel();
				}
				submitted = true;
				win.setVisible(false);
			}
		});
		
		bAddIng.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] names = DataManager.getIngredientNames();
				String name = (String) JOptionPane.showInputDialog(null, "Select the ingredient:", "Add Ingredient", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
				Ingredient i = DataManager.getIngredientByName(name).clone();
				String s = "-";
				s = JOptionPane.showInputDialog(null, "How many " + i.getMeasurement() + "(s)?", "Add Ingredient", JOptionPane.QUESTION_MESSAGE);
				while (!s.matches("[0-9\\.]*") || s.equals("")) {
					s = JOptionPane.showInputDialog(null, "Invalid Input!\n\nHow many " + i.getMeasurement() + "(s)?", "Add Ingredient", JOptionPane.QUESTION_MESSAGE);
				}
				i.setAmount(Double.parseDouble(s));
				ing.add(i);
				updateIngList();
			}
		});

	}
	
	private void updateIngList() {
		String l = "<html>";
		for (Ingredient i : ing) {
			l += i.getAmount() + " " + i.getMeasurement() + "(s) of " + i.getName() + "<br>";
		}
		l += "</html>";
		lIngList.setText(l);
	}
	
	public String errorCheck() {
		String ret = null;
		if (isBlank(tName.getText()))
			ret = "Name is required";
		if (isNameTaken(tName.getText()) && !editing)
			ret = "Recipe is already added";
		if (isBlank(tServings.getText()) || !isNum(tServings.getText()))
			ret = "Invalid number of servings";
		if (isBlank(tDirections.getText()))
			ret = "Directions are required";
		return ret;
	}
	
	private boolean isNameTaken(String s) {
		for (Recipe r : Main.getRecipes())
			if (r.getName().equalsIgnoreCase(s))
				return true;
		return false;
	}
	
	private boolean isBlank(String s) {
		return s.equals("");
	}
	
	private boolean isNum(String s) {
		if (isBlank(s)) {
			return true;
		}
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Object[] getData() {
		return new Object[]{tName.getText(), ing, tDirections.getText(), (tCategory.getText().equals("") ? "Uncategorized" : tCategory.getText()), Integer.parseInt(tServings.getText()), tPicture.getText()};
	}


	@Override
	public void windowClosing(WindowEvent arg0) {
		if (!submitted) {
			int ans = JOptionPane.showConfirmDialog(null, "Are you sure you want to close? All unsaved data will be lost", "Confirm Close", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (ans == 0)
				arg0.getWindow().dispose();
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}
