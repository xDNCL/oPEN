package pen.LilyPadSimulatorGUI_Level1;

import java.awt.Color;


public class FullLedPanel extends LedPanel {
	private boolean pinModeRED = false;
	private boolean pinModeGREEN = false;
	private boolean pinModeBLUE = false;

	public void setPinMode(int pin, boolean pinMode) {
		switch( pin ){
			case 9:
				pinModeRED = pinMode;
				break;
			case 10:
				pinModeBLUE = pinMode;
				break;
			case 11:
				pinModeGREEN = pinMode;
				break;
		}
		
		setLight(pin, false);
	}
	
	public void setPinMode(int pin, String pinMode) {
		pinMode.toUpperCase();
		
		if(pinMode.equals("OUTPUT")) {
			setPinMode(pin, true);
		} else {
			setPinMode(pin, false);
		}
	}
	
	public void setLight(int pin, boolean OnOff) {
		boolean flag = false;
		switch( pin ){
			case 9:
				flag = pinModeRED;
				break;
			case 10:
				flag = pinModeBLUE;
				break;
			case 11:
				flag = pinModeGREEN;
				break;
		}
		
		if(OnOff && flag) {
			setLight(pin, 255);
		} else {
			setLight(pin, 0);
		}
	}
	
	public void setLight(int pin, int value) {
		Color fullColor =  getBackground();
		int fullColorRed = fullColor.getRed();
		int fullColorGreen = fullColor.getGreen();
		int fullColorBlue = fullColor.getBlue();
		
		value = 255 - value;
		
		switch( pin ){
			case 9:
				setBackground(new Color(value, fullColorGreen, fullColorBlue));
				break;
			case 10:
				setBackground(new Color(fullColorRed, fullColorGreen, value));
				break;
			case 11:
				setBackground(new Color(fullColorRed, value, fullColorBlue));
				break;
		}
	}
}
