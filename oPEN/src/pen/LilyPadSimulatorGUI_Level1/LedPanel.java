package pen.LilyPadSimulatorGUI_Level1;

import java.awt.Color;

import javax.swing.JPanel;

public class LedPanel extends JPanel {
	private boolean pinMode = false;
	
	public void setPinMode(boolean pinMode) {
		this.pinMode = pinMode;
	}
	
	public void setPinMode(String pinMode) {
		pinMode.toUpperCase();
		
		if(pinMode.equals("OUTPUT")) {
			setPinMode(true);
		} else {
			setPinMode(false);
		}
	}
	
	public void setLight(boolean OnOff) {
		if(OnOff && pinMode) {
			setLight(255);
		} else {
			setLight(0);
		}
	}
	
	public void setLight(int value) {
		setBackground(new Color(value, value, 0));
	}
	
	public void init() {
		setPinMode(false);
		setBackground(Color.BLACK);
	}
}
