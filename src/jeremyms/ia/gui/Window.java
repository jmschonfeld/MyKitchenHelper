package jeremyms.ia.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame {
	private static final long serialVersionUID = 765451771202239495L;

	static final int WIDTH = 800, HEIGHT = 550;
	
	private JPanel currentScreen;
	
	public Window() {
		super("My Kitchen Helper");
		Dimension screen = getScreenSize();
		this.setBounds(screen.width / 2 - WIDTH / 2, screen.height / 2 - HEIGHT / 2, WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
	}
	
	public void setScreen(JPanel p) {
		currentScreen = p;
		this.setContentPane(p);
		this.validate();
	}
	
	public JPanel getScreen() {
		return currentScreen;
	}
	
	private static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
}
