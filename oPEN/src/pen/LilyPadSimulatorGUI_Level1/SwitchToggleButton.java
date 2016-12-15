package pen.LilyPadSimulatorGUI_Level1;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SwitchToggleButton extends JToggleButton implements ChangeListener {
	private boolean pinMode = false;
	private boolean pullUp = false;
	
	public SwitchToggleButton(String text) {
		setText(text);
		addChangeListener(this);
	}
	
	public void setPinMode(boolean pinMode) {
		this.pinMode = pinMode;
	}
	
	public void setPinMode(String pinMode) {
		pinMode.toUpperCase();
		
		if(pinMode.equals("INPUT_PULLUP")) {
			setPinMode(true);
			setPullUp(true);
		} else if(pinMode.equals("INPUT")) {
			setPinMode(true);
		} else {
			setPinMode(false);
		}
	}
	
	public void setPullUp(boolean OnOff) {
		if(OnOff) {
			pullUp = true;
		} else {
			pullUp = false;
		}
	}
	
	public int getState() {
		if(pinMode && pullUp && isSelected()) {
			return 0;
		}
		
		return 1;
	}
	
	public void stateChanged(ChangeEvent evt) {
		if(isSelected()) {
			setText("ON");
		} else {
			setText("OFF");
		}
	}
}
