package jeremyms.ia.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import jeremyms.ia.Main;
import jeremyms.ia.data.Ingredient;
import jeremyms.ia.util.SpringUtilities;

public class AddIngredientWindow extends JFrame implements WindowListener {
	private static final long serialVersionUID = -5508996629685432939L;
	private static final int WIDTH = 500, HEIGHT = 500;
	
	boolean submitted = false;
	
	private JLabel lName, lMeasurement, lBrand, lStoreSection, lAmount, lProtein, lFat, lCarbohydrates, lCalories, lExpirationDate;
	private JTextField tName, tMeasurement, tBrand, tStoreSection, tAmount, tProtein, tFat, tCarbohydrates, tCalories, tExpirationDate;
	private JLabel lError;
	private JButton submit;
	
	public AddIngredientWindow() {
		super("Add Ingredient");
		this.setResizable(false);
		Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(sz.width / 2 - WIDTH / 2, sz.height / 2 - HEIGHT / 2, WIDTH, HEIGHT);
		this.addWindowListener(this);
		this.getContentPane().setLayout(new SpringLayout());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		lError = new JLabel();
		lError.setForeground(Color.RED);
		this.add(lError);
		this.add(new JLabel());
		
		lName = new JLabel("Name:");
		this.getContentPane().add(lName);
		tName = new JTextField();
		this.getContentPane().add(tName);

		lAmount = new JLabel("Amount:");
		this.getContentPane().add(lAmount);
		tAmount = new JTextField();
		this.getContentPane().add(tAmount);
		
		lMeasurement = new JLabel("Type of Measurement:");
		this.getContentPane().add(lMeasurement);
		tMeasurement = new JTextField();
		tMeasurement.setToolTipText("Example: bag, cup, etc.");
		this.getContentPane().add(tMeasurement);

		lBrand = new JLabel("Brand:");
		this.getContentPane().add(lBrand);
		tBrand = new JTextField();
		this.getContentPane().add(tBrand);

		lStoreSection = new JLabel("Store Section:");
		this.getContentPane().add(lStoreSection);
		tStoreSection = new JTextField();
		this.getContentPane().add(tStoreSection);

		lProtein = new JLabel("Protein:");
		this.getContentPane().add(lProtein);
		tProtein = new JTextField();
		this.getContentPane().add(tProtein);
		
		lFat = new JLabel("Fat:");
		this.getContentPane().add(lFat);
		tFat = new JTextField();
		this.getContentPane().add(tFat);
		
		lCarbohydrates = new JLabel("Carbohydrates:");
		this.getContentPane().add(lCarbohydrates);
		tCarbohydrates = new JTextField();
		this.getContentPane().add(tCarbohydrates);

		lCalories = new JLabel("Calories:");
		this.getContentPane().add(lCalories);
		tCalories = new JTextField();
		this.getContentPane().add(tCalories);
		
		lExpirationDate = new JLabel("Expiration Date (mm/dd/yy):");
		this.getContentPane().add(lExpirationDate);
		tExpirationDate = new JTextField();
		this.getContentPane().add(tExpirationDate);
		
		this.getContentPane().add(new JLabel());

		submit = new JButton("Submit");
		this.getContentPane().add(submit);
		
		SpringUtilities.makeCompactGrid(this.getContentPane(), 12, 2, 10, 10, 10, 10);
		
		final JFrame win = this;
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String error = errorCheck();
				if (error != null) {
					lError.setText(error);
					return;
				}
				((IngredientScreen) Main.getWindow().getScreen()).onAddScreenClosing(getData());
				submitted = true;
				win.setVisible(false);
			}
		});

	}
	
	public String errorCheck() {
		String ret = null;
		if (isBlank(tName.getText()))
			ret = "Name is required";
		if (isNameTaken(tName.getText()))
			ret = "Ingredient is already added";
		if (isBlank(tAmount.getText()))
			ret = "Amount is required";
		if (isBlank(tMeasurement.getText()))
			ret = "Measurement is required";
		if (!isNum(tAmount.getText()))
			ret = "Amount is invalid";
		if (!isNum(tProtein.getText()))
			ret = "Protein is invalid";
		if (!isNum(tFat.getText()))
			ret = "Fat is invalid";
		if (!isNum(tCarbohydrates.getText()))
			ret = "Carbohydrates is invalid";
		if (!isNum(tCalories.getText()))
			ret = "Calories is invalid";
		if (!isDate(tExpirationDate.getText()) || isBlank(tExpirationDate.getText()))
			ret = "Invalid expiration date";
		return ret;
	}
	
	private boolean isNameTaken(String s) {
		for (Ingredient i : Main.getIngredients())
			if (i.getName().equalsIgnoreCase(s))
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
	
	private boolean isDate(String s) {
		return s.matches("[0-9][0-9]\\/[0-9][0-9]\\/[0-9][0-9]") || isBlank(s);
	}
	
	public String[] getData() {
		return new String[]{tName.getText(), tAmount.getText(), tMeasurement.getText(), tBrand.getText(), tStoreSection.getText(), tProtein.getText(), tFat.getText(), tCarbohydrates.getText(), tCalories.getText(), tExpirationDate.getText()};
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
