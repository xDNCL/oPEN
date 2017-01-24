package pen.LilyPadSimulatorGUI_Level1;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

public class LilyPadPanel extends JPanel{
	
	private BufferedImage img;
	private BufferedImage image;
	private Graphics2D imageGraphics;
	
	public LilyPadPanel() {
		try {
			img = ImageIO.read(new File("resources/LilyPad.jpg"));
			image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			imageGraphics = (Graphics2D)image.createGraphics();
			imageGraphics.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent((Graphics2D) g);
		((Graphics2D) g).drawImage(image, 0, 0, this);
	}
}
