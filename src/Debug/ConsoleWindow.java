package Debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.mit.blocks.codeblocks.Block;

import OverrideOpenblocks.OB_Block;
import OverrideOpenblocks.OB_Workspace;


public class ConsoleWindow extends JFrame implements ActionListener{
		
	private static final long serialVersionUID = 1L;

	private OB_Workspace ws;
	
	//変数テーブル
	private static final String[] columnNames = {"変数名", "値"};
	private static DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
	private static JTable valiableTable = new JTable(tableModel);
	
	public static void setVariableTable(Hashtable<String, Object> list){
		tableModel = new DefaultTableModel(columnNames, 0);
		valiableTable.setModel(tableModel);
		
		for (Enumeration<String> e = list.keys(); e.hasMoreElements();){
			 String key = e.nextElement();
			 String[] val = {key, list.get(key).toString()};
			 tableModel.addRow(val);
		}

		valiableTable.revalidate();
		valiableTable.repaint();
	}
	
	//ボタン群
	private JButton reset;
	private JButton oneStep;
	private JButton allStep;
	
	//テキスト出力先。標準出力として扱う。
	private JTextArea console;
	
	public ConsoleWindow(OB_Workspace workspace){
		super("コンソール");
		this.ws = workspace;
	    this.setBounds(200, 200, 300, 400);
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
//		north.add(oneStep);
		north.add(allStep);
		body.add(north, BorderLayout.NORTH);
		
		//CenterPane
		JPanel center = new JPanel();
		console = new JTextArea();
		console.setEditable(false);
//		console.setPreferredSize(new Dimension(300, 200));
		JScrollPane scroll= new JScrollPane(console);
		scroll.setPreferredSize(new Dimension(300, 200));
		center.add(scroll);
		body.add(center, BorderLayout.CENTER);
		
		//SouthPane
		JPanel south = new JPanel();
		valiableTable.setPreferredSize(new Dimension(300, 150));
		south.add(valiableTable);
		body.add(south, BorderLayout.SOUTH);
		
		//標準出力先を変更
		JTextAreaStream stream = new JTextAreaStream(console);
		System.setOut(new PrintStream(stream, true));
		
		return body;
	}
	
	public void consoleClear(){
		this.console.setText("");
		setVariableTable(new Hashtable<String, Object>());
	}

	public void runDebug(){
		for(Block block: this.ws.getBlocks()){
			if(block.getGenusName().equals("start")){
				if(block instanceof OB_Block){
					try{
					OB_Block tmp = (OB_Block)block;
					tmp.runBlock();
					return;
					}
					catch(Exception e){
						e.printStackTrace();
						System.err.println("--program error--");
					}
				}
			}
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.allStep){
			consoleClear();
			runDebug();
			//To Do
		}
		
		if(e.getSource() == this.reset){
			consoleClear();
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
