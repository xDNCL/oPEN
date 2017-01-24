package Exe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import pen.IntVgOutputWindow.IntVgOutputWindow;
import pen.LilyPadSimulatorGUI_Level1.LilyPadSimulatorGUI_Level1;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;
import OverrideOpenblocks.OB_Block;
import OverrideOpenblocks.OB_Workspace;


public class ConsoleWindow implements ActionListener{
	private IntVgOutputWindow ivw = new IntVgOutputWindow();
	private LilyPadSimulatorGUI_Level1 lps = new LilyPadSimulatorGUI_Level1();
	private OB_Workspace ws;
	
	private final Color actionButtonBGC = new Color(255, 80, 0);
	private final Color consoleBGC = new Color(242, 238, 231);
	
	//変数テーブル
	private static final String[] columnNames = {"変数名", "値"};
	private static final String[][] dummy = {{"", ""}};
	private static DefaultTableModel tableModel = new DefaultTableModel(dummy, columnNames);
	private static JTable valiableTable = new JTable(tableModel);
	
	public static void setVariableTable(Hashtable<String, Object> list){
		tableModel = new DefaultTableModel(dummy, columnNames);
		valiableTable.setModel(tableModel);
		valiableTable.setFont(new Font("SansSerif",Font.PLAIN, 18));
		
		
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
	private static boolean nowInput = false;
	
	public static String getInputformText(){
		String s = inputForm.getText();
//		inputForm.setText("");
		return s;
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
		}else{
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
		north.setLayout(new BorderLayout());
		reset = new JButton("始めに戻る");
		reset.addActionListener(this);
		oneStep = new JButton("ステップ実行");
		oneStep.addActionListener(this);
		allStep = new JButton("実行");
		allStep.addActionListener(this);
		JLabel inputFormLabel = new JLabel("入力");
		inputForm = new JTextField(20);
		inputForm.addActionListener(this);
		inputForm.setEditable(false);
		JPanel f = new JPanel();
		f.setLayout(new BorderLayout());
		f.add(inputFormLabel, BorderLayout.WEST);
		f.add(inputForm, BorderLayout.CENTER);
		north.add(reset, BorderLayout.WEST);
		north.add(oneStep, BorderLayout.CENTER);
		north.add(allStep, BorderLayout.EAST);
		north.add(f, BorderLayout.SOUTH);
		north.setBackground(this.actionButtonBGC);
		body.add(north, BorderLayout.NORTH);
		
		//CenterPane
		JPanel center = new JPanel();
		JLabel consoleLabel = new JLabel("実行結果");
		center.setLayout(new BorderLayout());
		console = new JTextArea();
		console.setEditable(false);
		console.setFont(new Font("SansSerif",Font.PLAIN, 18));
		console.setBackground(this.consoleBGC);
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
		valiableTable.setBackground(this.consoleBGC);
		JPanel inputFormBase = new JPanel();
		
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
	
//	private BlockRun blockRun = null;
//	private boolean runNow = false;
//
//	public void runBlock() throws BlockRunException{	
//		if(!runNow){
//			OB_Block start = null;
//			for(Block block: this.ws.getBlocks()){
//				if(block.getGenusName().equals("start")){
//					if(block instanceof OB_Block){
//						if(start == null){
//							start = (OB_Block)block;
//						}else{
//							throw new BlockRunException(block, "プログラム開始ブロックが2つ以上存在しています。");
//						}
//					}
//				}
//			}
////			if(start == null) {System.out.println("out"); return;};
//			blockRun = new BlockRun(start);
//			runNow = true;
//			blockRun.start();
//		}
//	}
	
	private EventStack event = null;
	
	private OB_Block getStartBlock() throws BlockRunException{
		OB_Block start = null;
		// 2015/10/27 N.Inaba ADD ブロック(単品)の複製 型の調査
		// コピー元: OB_Block型 コピー:Block型になっているのが原因
//		int block_cnt = 0;
//		for(Block block: this.ws.getBlocks()) {
//			System.out.println("" + block_cnt + block);
//			block_cnt++;
//		}
		
		for(Block block: this.ws.getBlocks()){
			if(block.getGenusName().equals("start")){
				if(block instanceof OB_Block){
					if(start == null){
						start = (OB_Block)block;
					}else{
						throw new BlockRunException(block, "プログラム開始ブロックが2つ以上存在しています。");
					}
				}
			}
		}
		if(start != null){
			return start;
		}else{
			throw new BlockRunException("プログラム開始ブロックが見つかりません。");
		}
	}
	
	private void runAllBlock() throws BlockRunException{
		if(event == null){
			event = new EventStack(getStartBlock());
			event.start();
		}
		event.actionAll();
	}
	
	private void runBlock() throws BlockRunException{
		if(event == null){
			event = new EventStack(getStartBlock());
			event.start();
		}
		event.action();
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.allStep){
//			consoleClear();
			try{
				runAllBlock();
				this.allStep.setEnabled(false);
				this.oneStep.setEnabled(false);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//To Do
		}
		
		if(e.getSource() == this.oneStep){
//			consoleClear();
			if(nowInput) {
				setForcus();
				return;
			}
			try{
				runBlock();
				if(event.isEmpty()){
					this.oneStep.setEnabled(false);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		if(e.getSource() == this.reset){
			consoleClear();
			inputForm.setText("");
			ivw.gCloseWindow();
			this.allStep.setEnabled(true);
			this.oneStep.setEnabled(true);
			if(event != null){
				event.resetHighLight();
				event = null;
			}
			//reset Highlight
			BlockRunException.blinkOff();
			nowInput = false;
		}
		
		if(e.getSource() == inputForm){
			nowInput = false;
			inputFlag = true;
			inputForm.setEditable(false);
		}
	}
	
	public static void setForcus(){
		inputForm.requestFocus(true);
		inputForm.setEditable(true);
		inputForm.setText("");
		nowInput = true;
		inputFlag = false;
	}
	
	public static boolean isEntered(){
		return inputFlag;
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
	
	private class EventStack extends Thread{
		
		private ArrayList<OB_Block> eventQueue;
		private int stackCount;
		private OB_Block highLightBlock = null;
		private boolean go;
		private boolean allGo;
		
		EventStack(OB_Block firstEvent){
			this.eventQueue = new ArrayList<OB_Block>();
			this.stackCount = -1;
			this.go = false;
			this.allGo = false;
			this.addEvent(firstEvent);
		}
		
		@Override
		public void run(){
			while(this.stackCount > -1){
				
					while(!(go || allGo)){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if(highLightBlock != null){
						//ハイライト消す
						this.resetHighLight();
					}
					
					OB_Block run = this.getEvent();
					if(run == null){
						this.resetHighLight();
						return;
					}
					
					//ハイライト点灯
					highLightBlock = run;
					// 2015/10/26 N.Inaba ADD ブロック(単品)の複製 型の調査 NullPointerエラー
					(highLightBlock.getWorkspace().getEnv().getRenderableBlock(highLightBlock.getBlockID())).setBlockHighlightColor(Color.YELLOW);
					
					ArrayList<OB_Block> results = null;
					try {
						results = run.runBlock(ivw,lps);
					} catch (BlockRunException e) {
						//エラー処理はBlock側で表示されるのでここではしない。
						return;
					}
					
					for(int i=results.size()-1; i>=0; i--){
						this.addEvent(results.get(i));
					}
					go = false;
	//				this.debug();
				}
			if(allGo){
				allGo = false;
				resetHighLight();
				System.out.println("＝＝＝＝プログラム終了＝＝＝＝");
				return;
			}
		}
		
		protected void action(){
			if(this.stackCount <= -1){
				resetHighLight();
				System.out.println("＝＝＝＝プログラム終了＝＝＝＝");
				return;
			}
			if(!this.isEmpty()){
				go = true;
			}
		}
		
		protected void actionAll(){
			allGo = true;
		}
		
		protected boolean isEmpty(){
			return this.stackCount <= -1 ? true: false;
		}
		
		protected void resetHighLight(){
			if(highLightBlock != null){
				// 2015/10/26 N.Inaba ADD ブロック(単品)の複製 型の調査 NullPointerエラー
				(highLightBlock.getWorkspace().getEnv().getRenderableBlock(highLightBlock.getBlockID())).resetHighlight();
			}
		}
		
		private void addEvent(OB_Block block){
			eventQueue.add(block);
			stackCount++;
		}
		
		private OB_Block getEvent(){
			if(stackCount < 0){
				System.out.println("no events");
				return null;
			}
			OB_Block next = this.eventQueue.get(stackCount);
			this.eventQueue.remove(stackCount--);
			return next;
		}
		
		private void debug(){
			for(int i=0; i<this.stackCount+1; i++){
				System.out.println("No"+i+":"+this.eventQueue.get(i).getGenusName());
			}
		}
		
		
	}



}
