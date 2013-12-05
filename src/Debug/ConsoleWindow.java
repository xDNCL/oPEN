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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.mit.blocks.codeblocks.Block;

//import Debug.CodeChecker.ErrorException;
import OverrideOpenblocks.OB_Workspace;


public class ConsoleWindow extends JFrame implements ActionListener{
		
	private static final long serialVersionUID = 1L;

	private OB_Workspace ws;
	
	//�ϐ��e�[�u��
	private DefaultTableModel tableModel;
	
	//�{�^���Q
	private JButton reset;
	private JButton oneStep;
	private JButton allStep;
	
	//�e�L�X�g�o�͐�B�W���o�͂Ƃ��Ĉ����B
	private JTextArea console;
	
	public ConsoleWindow(OB_Workspace workspace){
		super("�R���\�[��");
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
		reset = new JButton("�n�߂ɖ߂�");
		reset.addActionListener(this);
		oneStep = new JButton("1�s���s");
		oneStep.addActionListener(this);
		allStep = new JButton("���s");
		allStep.addActionListener(this);
		north.add(reset);
		north.add(oneStep);
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
		String[] columnNames = {"�ϐ���", "�l"};
		tableModel = new DefaultTableModel(columnNames, 0);
		JTable valiableTable = new JTable(tableModel);
		valiableTable.setPreferredSize(new Dimension(300, 150));
		south.add(valiableTable);
		body.add(south, BorderLayout.SOUTH);
		
		//�W���o�͐��ύX
		JTextAreaStream stream = new JTextAreaStream(console);
		System.setOut(new PrintStream(stream, true));
		
		return body;
	}
	
	public void consoleClear(){
		this.console.setText("");
	}

	public void runDebug(){
		for(Block block: this.ws.getBlocks()){
			System.out.println(block);
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

	        // Swing �̃C�x���g�X���b�h�ɂ̂���
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                _area.append(_buf.toString());
	                _buf.reset();
	            }
	        });
	    }
	
	}



}
