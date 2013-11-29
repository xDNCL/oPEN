package Debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Debug.CodeChecker.ErrorException;
import OverrideOpenblocks.OB_Workspace;


public class ConsoleWindow extends JFrame implements ActionListener{
		
	private OB_Workspace ws;
	
	private DefaultTableModel tableModel;
	
	private JButton reset;
	private JButton oneStep;
	private JButton allStep;
	
	private JTextArea console;
	
	public ConsoleWindow(OB_Workspace workspace){
		super("コンソール");
		this.ws = workspace;
	    this.setBounds(200, 200, 200, 400);
	    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	    this.add(init());
	    this.setVisible(true);
	}
	
	public void reload(OB_Workspace workspace){
		this.ws = workspace;
		consoleClear();
		this.setVisible(true);
		//To Do
	}
	
	
	private JComponent init(){
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());
		
		//NorthPane
		JPanel north = new JPanel();
		reset = new JButton("始めに戻る");
		reset.addActionListener(this);
		oneStep = new JButton("1行実行");
		oneStep.addActionListener(this);
		allStep = new JButton("実行");
		allStep.addActionListener(this);
		north.add(reset);
		north.add(oneStep);
		north.add(allStep);
		body.add(north, BorderLayout.NORTH);
		
		//CenterPane
		JPanel center = new JPanel();
		console = new JTextArea();
		console.setEditable(false);
		console.setPreferredSize(new Dimension(200, 200));
		center.add(console);
		body.add(center, BorderLayout.CENTER);
		
		//SouthPane
		JPanel south = new JPanel();
		String[] columnNames = {"変数名", "値"};
		tableModel = new DefaultTableModel(columnNames, 0);
		JTable valiableTable = new JTable(tableModel);
		valiableTable.setPreferredSize(new Dimension(100, 150));
		south.add(valiableTable);
		body.add(south, BorderLayout.SOUTH);
		
		//標準出力先を変更
		JTextAreaStream stream = new JTextAreaStream(console);
		System.setOut(new PrintStream(stream, true));
		
		return body;
	}
	
	public void consoleClear(){
		this.console.setText("");
	}

	public void runDebug(){
		CodeChecker codeChecker = new CodeChecker(this.ws.getRenderableBlocks());
		try {
			consoleClear();
			codeChecker.runTheCode();
		} catch (ErrorException e) {

		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == allStep){
			runDebug();
			//To Do
		}
		
	}
	
	
	
	
	public class JTextAreaStream extends OutputStream {

	    private JTextArea _area;
	    private ByteArrayOutputStream _buf;

	    public JTextAreaStream(JTextArea area) {
	        _area = area;
	        _buf = new ByteArrayOutputStream();
	    }
	    
	    @Override
	    public void write(int b) throws IOException {
	        _buf.write(b);
	    }
	    
	    @Override
	    public void flush() throws IOException {

	        // Swing のイベントスレッドにのせる
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                _area.append(_buf.toString());
	                _buf.reset();
	            }
	        });
	    }
	
	}



}
