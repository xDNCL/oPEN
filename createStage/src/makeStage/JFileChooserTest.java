package makeStage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JFileChooserTest extends JFrame implements ActionListener {

	JLabel label;
	JButton cButton;
	JButton xButton;
	File file = null;
	
	public static void main(String[] args) {
		JFileChooserTest frame = new JFileChooserTest();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 300, 200);
		frame.setTitle("createStage");
		frame.setVisible(true);
	}
	
	JFileChooserTest() {
		cButton = new JButton("選択");
		xButton = new JButton("変換");
		cButton.addActionListener(this);
		xButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(cButton);
		buttonPanel.add(xButton);
		label = new JLabel();
		
		JPanel labelPanel = new JPanel();
		labelPanel.add(label);
		
		getContentPane().add(labelPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == cButton) {
			JFileChooser filechooser = new JFileChooser("./");	
			int selected = filechooser.showOpenDialog(this);
			
			if (selected == JFileChooser.APPROVE_OPTION) {
				file = filechooser.getSelectedFile();
				label.setText(file.getAbsolutePath());
			} else if (selected == JFileChooser.CANCEL_OPTION) {
				label.setText("キャンセルされました");
			} else if (selected == JFileChooser.ERROR_OPTION) {
				label.setText("エラーまたは取り消しがありました");
			}
		} 
		
		if (e.getSource() == xButton) {
			if (file != null) {
				ReadXml readXml = new ReadXml(file);
			} else {
				label.setText("ファイルが設定されていません");
			}
		}
	}
}
