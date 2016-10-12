package jeremyms.ia.gui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.ImageIcon;

public class ImgComp extends Component {
	private static final long serialVersionUID = -6774253552918985835L;

	private ImageIcon img;
	
	public ImgComp() {
		img = null;
	}
	
	public ImgComp(String file) {
		img = new ImageIcon(file);
	}
	
	public void updateImage(String file) {
		img = new ImageIcon(file);
	}
	
	@Override
	public void paint(Graphics g) {
		if (img != null)
			g.drawImage(img.getImage(), this.getX(), this.getY(), null);
	}
}
