package Exe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;

import OverrideOpenblocks.OB_Block;
import OverrideOpenblocks.OB_Workspace;


public class ConsoleWindow implements ActionListener{
		
	private OB_Workspace ws;
	
	//�ϐ��e�[�u��
	private static final String[] columnNames = {"�ϐ���", "�l"};
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
	
	//�{�^���Q
	private JButton reset;
	private JButton oneStep;
	private JButton allStep;
	
	//
	private static JTextField inputForm;
	private static boolean inputFlag = false;
	
	public static String getInputformText(){
		return inputForm.getText();
	}
	
	//�E�B���h�E���[�h�p
	private JFrame frame;
	
	//���g
	private JComponent body;
	
	//�e�L�X�g�o�͐�B�W���o�͂Ƃ��Ĉ����B
	private JTextArea console;
		
	public ConsoleWindow(OB_Workspace workspace, boolean isWindow){
		this.ws = workspace;
		
		if(isWindow){
			frame = new JFrame("�R���\�[��");
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
		reset = new JButton("�n�߂ɖ߂�");
		reset.addActionListener(this);
		oneStep = new JButton("�X�e�b�v���s");
		oneStep.addActionListener(this);
		allStep = new JButton("���s");
		allStep.addActionListener(this);
		north.add(reset);
		north.add(oneStep);
		north.add(allStep);
		body.add(north, BorderLayout.NORTH);
		
		//CenterPane
		JPanel center = new JPanel();
		JLabel consoleLabel = new JLabel("���s����");
		center.setLayout(new BorderLayout());
		console = new JTextArea();
		console.setEditable(false);
		console.setBackground(new Color(245, 245, 245));
//		console.setPreferredSize(new Dimension(300, 200));
		JScrollPane scroll= new JScrollPane(console);
		scroll.setPreferredSize(new Dimension(300, 200));
		center.add(consoleLabel, BorderLayout.NORTH);
		center.add(scroll, BorderLayout.CENTER);
		body.add(center, BorderLayout.CENTER);
		
		//SouthPane
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		JLabel variableLabel = new JLabel("�ϐ����Ƃ��̒l");
		valiableTable.setPreferredSize(new Dimension(300, 150));
		valiableTable.setBackground(new Color(249, 249, 249));
		JLabel inputFormLabel = new JLabel("���̓t�H�[��");
		inputForm = new JTextField(20);
		inputForm.addActionListener(this);
		JPanel inputFormBase = new JPanel();
		
		inputFormBase.add(inputFormLabel);
		inputFormBase.add(inputForm);
		south.add(variableLabel, BorderLayout.NORTH);
		south.add(valiableTable, BorderLayout.CENTER);
		south.add(inputFormBase, BorderLayout.SOUTH);
		
		body.add(south, BorderLayout.SOUTH);
		
		//�W���o�͐��ύX
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
//						}
//						else{
//							throw new BlockRunException(block, "�v���O�����J�n�u���b�N��2�ȏ㑶�݂��Ă��܂��B");
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
		for(Block block: this.ws.getBlocks()){
			if(block.getGenusName().equals("start")){
				if(block instanceof OB_Block){
					if(start == null){
						start = (OB_Block)block;
					}
					else{
						throw new BlockRunException(block, "�v���O�����J�n�u���b�N��2�ȏ㑶�݂��Ă��܂��B");
					}
				}
			}
		}
		if(start != null){
			return start;
		}
		else{
			throw new BlockRunException("�v���O�����J�n�u���b�N��������܂���B");
		}
	}
	
	private void runAllBlock() throws BlockRunException{
		int stack = runBlock();
		while(stack != -1){
			stack = runBlock();
		}
	}
	
	private int runBlock() throws BlockRunException{
		if(event == null){
			event = new EventStack(getStartBlock());
		}
		int result = event.action();
		if(result == -1){
			event.resetHighLight();
			System.out.println("���������v���O�����I����������");
			event = null;
		}
		return result;
	}
	
//	public static void getTextFieldForcus(){
//		inputForm.requestFocus();
//		inputFlag = true;
//	}
//	
//	//to do
//	//�R�[�h�f���O�ɃG���[���Ȃ����`�F�b�N����ׂ��H
//	public static boolean checkCode(){
//		return true;
//	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.allStep){
//			consoleClear();
			try{
				runAllBlock();
				this.allStep.setEnabled(false);
				this.oneStep.setEnabled(false);
			}catch(Exception ex){
				//���s�����܂��s���Ȃ������Ƃ��̓���B���ɂȂ��B
			}
			//To Do
		}
		
		if(e.getSource() == this.oneStep){
//			consoleClear();
			try{
				if(runBlock() == -1){
					this.oneStep.setEnabled(false);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		if(e.getSource() == this.reset){
			consoleClear();
			this.allStep.setEnabled(true);
			this.oneStep.setEnabled(true);
			if(event != null){
				event.resetHighLight();
				event = null;
			}
			//reset Highlight
			BlockRunException.blinkOff();
		}
		
		if(e.getSource() == inputForm){
			System.setIn(new ByteArrayInputStream(inputForm.getText().getBytes()));
		}
	}
	
	public static void setForcus(){
		inputForm.requestFocus(true);
	}
	
//	/**
//	 * �u���b�N�̎��s����񏈗�
//	 * @author shuhara
//	 *
//	 */
//	public class BlockRun extends Thread{
//		
//		final OB_Block start;
//		
//		BlockRun(OB_Block block){
//			this.start = block;
//		}
//		
//		public void run(){
//			try {
//				start.runBlock();
//			}catch(BlockRunException e1) {
//				//���s���G���[
//			}catch(Exception e2){
//				e2.printStackTrace();
//			}finally{
//				//���s�I��
//				runNow = false;
//			}
//			
//		}
//		
//	}
	
	
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

	        // Swing �̃C�x���g�X���b�h�ɂ̂���
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
		
		EventStack(OB_Block firstEvent){
			this.eventQueue = new ArrayList<OB_Block>();
			this.stackCount = -1;
			this.addEvent(firstEvent);
		}
		
		@Override
		public void run(){
			
		}
		
		protected int action() throws BlockRunException{
			if(highLightBlock != null){
				//�n�C���C�g����
				this.resetHighLight();
			}
			
			OB_Block run = this.getEvent();
			if(run == null){
				this.resetHighLight();
				return -1;
			}
			
			//�n�C���C�g�_��
			highLightBlock = run;
			(highLightBlock.getWorkspace().getEnv().getRenderableBlock(highLightBlock.getBlockID())).setBlockHighlightColor(Color.YELLOW);
			
			ArrayList<OB_Block> results = run.runBlock();
			for(int i=results.size()-1; i>=0; i--){
				this.addEvent(results.get(i));
			}
//			this.debug();
			return this.stackCount;
		}
		
		protected void resetHighLight(){
			if(highLightBlock != null){
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
