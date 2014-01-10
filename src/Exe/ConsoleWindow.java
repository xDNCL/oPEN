package Exe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;

import OverrideOpenblocks.OB_Block;
import OverrideOpenblocks.OB_Workspace;


public class ConsoleWindow implements ActionListener{
		
	private OB_Workspace ws;
	
	//変数テーブル
	private static final String[] columnNames = {"変数名", "値"};
	private static final String[][] dummy = {{"", ""}};
	private static DefaultTableModel tableModel = new DefaultTableModel(dummy, columnNames);
	private static JTable valiableTable = new JTable(tableModel);
	
	public static void setVariableTable(Hashtable<String, Object> list){
		tableModel = new DefaultTableModel(dummy, columnNames);
		valiableTable.setModel(tableModel);
		
		
		for (Enumeration<String> e = list.keys(); e.hasMoreElements();){
			 Object key = e.nextElement();
			 Object value = list.get(key.toString());
			 String[] val = {key.toString(), value.toString()};
			 tableModel.addRow(val);
		}

		valiableTable.revalidate();
		valiableTable.repaint();
	}
	
	//ボタン群
	private JButton reset;
	private JButton oneStep;
	private JButton allStep;
	
	//
	private static JTextField inputForm;
	private static boolean inputFlag = false;
	
	public static String getInputformText(){
		return inputForm.getText();
	}
	
	//ウィンドウモード用
	private JFrame frame;
	
	//中身
	private JComponent body;
	
	//テキスト出力先。標準出力として扱う。
	private JTextArea console;
		
	public ConsoleWindow(OB_Workspace workspace, boolean isWindow){
		this.ws = workspace;
		
		if(isWindow){
			frame = new JFrame("コンソール");
		    frame.setBounds(200, 200, 300, 400);
		    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		    body = init();
		    frame.add(body);
		    frame.setVisible(true);
		}
		else{
			frame = null;
			body = init();
		}
	}
	
	public JComponent getBody(){
		return body;
	}

	
	public void reload(OB_Workspace workspace){
		this.ws = workspace;
		consoleClear();
		
		if(frame != null){
			frame.setVisible(true);
		}
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
		JLabel consoleLabel = new JLabel("実行結果");
		center.setLayout(new BorderLayout());
		console = new JTextArea();
		console.setEditable(false);
//		console.setPreferredSize(new Dimension(300, 200));
		JScrollPane scroll= new JScrollPane(console);
		scroll.setPreferredSize(new Dimension(300, 200));
		center.add(consoleLabel, BorderLayout.NORTH);
		center.add(scroll, BorderLayout.CENTER);
		body.add(center, BorderLayout.CENTER);
		
		//SouthPane
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		JLabel variableLabel = new JLabel("変数名とその値");
		valiableTable.setPreferredSize(new Dimension(300, 150));
		JLabel inputFormLabel = new JLabel("入力フォーム");
		inputForm = new JTextField(20);
		inputForm.addActionListener(this);
		JPanel inputFormBase = new JPanel();
		
		inputFormBase.add(inputFormLabel);
		inputFormBase.add(inputForm);
		south.add(variableLabel, BorderLayout.NORTH);
		south.add(valiableTable, BorderLayout.CENTER);
		south.add(inputFormBase, BorderLayout.SOUTH);
		
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
	
	private BlockRun blockRun = null;
	private boolean runNow = false;

	public void runBlock() throws BlockRunException{	
		if(!runNow){
			OB_Block start = null;
			for(Block block: this.ws.getBlocks()){
				if(block.getGenusName().equals("start")){
					if(block instanceof OB_Block){
						if(start == null){
							start = (OB_Block)block;
						}
						else{
							throw new BlockRunException(block, "プログラム開始ブロックが2つ以上存在しています。");
						}
					}
				}
			}
//			if(start == null) {System.out.println("out"); return;};
			blockRun = new BlockRun(start);
			runNow = true;
			blockRun.start();
		}
	}
	
	public static void getTextFieldForcus(){
		inputForm.requestFocus();
		inputFlag = true;
	}
	
	//to do
	//コード吐く前にエラーがないかチェックするべき？
	public static boolean checkCode(){
		return true;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.allStep){
			consoleClear();
			try{
				runBlock();
			}catch(Exception ex){
				//実行がうまく行かなかったときの動作。特になし。
			}
			//To Do
		}
		
		if(e.getSource() == this.reset){
			consoleClear();
			blockRun = null;
			//reset Highlight
			BlockRunException.blinkOff();
		}
		
		if(e.getSource() == inputForm && inputFlag){
			inputFlag = false;
			blockRun.notify();
		}
	}
	
	/**
	 * ブロックの実行を並列処理
	 * @author shuhara
	 *
	 */
	public class BlockRun extends Thread{
		
		final OB_Block start;
		
		BlockRun(OB_Block block){
			this.start = block;
		}
		
		public void run(){
			try {
				start.runBlock();
			}catch(BlockRunException e1) {
				//実行時エラー
			}catch(Exception e2){
				e2.printStackTrace();
			}finally{
				//実行終了
				runNow = false;
			}
			
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
