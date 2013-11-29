package Debug;

import java.util.ArrayList;
import java.util.Iterator;

import OverrideOpenblocks.OB_Workspace;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.codeblocks.BlockGenus;
import edu.mit.blocks.renderable.RenderableBlock;

public class CodeChecker {

	private final boolean DEBUG = true;
	/**
	 * startBlock�̓u���b�N���Ō���
	 */
	private final String START = "start";
	
	protected enum ConnectorType{Boolean, Number, String, Command};
	
	/**
	 * �ϐ��̈�
	 */
	private ArrayList<Variable> variableList;
	
	private Iterable<RenderableBlock> blockList;
	
	//�C�x���g�����X�^�b�N
	private EventStack eventStack;
	
	private OB_Workspace ws;
		
	CodeChecker(OB_Workspace workspace){
		//init
		this.ws = workspace;
		this.blockList = ws.getRenderableBlocks();
		this.variableList = new ArrayList<Variable>();
	}
	
	public void runTheCode() throws ErrorException{
		RenderableBlock runRenderableBlock = findStartBlock();
		ArrayList<RenderableBlock> runBlockList = new ArrayList<RenderableBlock>();
		
		//�ċA����
		runBlockList = this.getAllRunBlocks(runRenderableBlock, runBlockList);
		
		eventStack = new EventStack(runBlockList);
		if(DEBUG) System.out.println(eventStack);
		
	}
	
	private ArrayList<RenderableBlock> getAllRunBlocks(RenderableBlock rb, ArrayList<RenderableBlock> list){		
		Block block = rb.getBlock();
		if(DEBUG) System.out.println("genus name:"+block.getGenusName());
		
		//��ɃR�l�N�^�[����
		if(block.getNumSockets() > 0){
			for(int i=0; i<block.getNumSockets(); i++){
				BlockConnector blockConnector = block.getSocketAt(i);
				RenderableBlock nextBlock = getRenderableBlock(blockConnector.getBlockID());
				
				//�R�l�N�^�[�ɐڑ�����Ă���u���b�N��BlockGenus���擾
				BlockGenus genus = ws.getEnv().getGenusWithName(nextBlock.getGenus());
				
				//BlockGenus���̃f�[�^����<command, function, data, variable, procedure, param>�̑I��
				if(genus.isDataBlock()){
					continue;
				}
				if(genus.isFunctionBlock()){
					list = getAllRunBlocks(nextBlock, list);
				}
				if(genus.isCommandBlock()){
					list = getAllRunBlocks(nextBlock, list);
				}
				
			}
		}
		
		//�R�l�N�^�[�������I������玩�g������
		list.add(rb);
		
		//���̃u���b�N������Ύ��ցA�Ȃ���ΏI��
		if(block.getAfterBlockID() != Block.NULL){
			list = getAllRunBlocks(getRenderableBlock(block.getAfterBlockID()), list);
		}
		return list;
	}
	
	private RenderableBlock getRenderableBlock(long id){
		for(RenderableBlock rb: this.blockList){
			if(rb.getBlockID() == id){
				return rb;
			}
		}
		return null;
	}
	
	/**
	 * �v���O�����J�n�u���b�N��T���B
	 * @return �u�v���O�����J�n�v��RenderableBlock
	 * @throws ErrorException �Y������u���b�N�������A�܂��͕������݂���ꍇ
	 */
	private RenderableBlock findStartBlock() throws ErrorException{
		ArrayList<RenderableBlock> startBlocks = new ArrayList<RenderableBlock>();
		for(RenderableBlock rb: this.blockList){
			if(rb.getBlock().getGenusName().equals(START)){
				startBlocks.add(rb);
			}
		}
		//�X�^�[�g�u���b�N���Ȃ��A�܂��͕�������ꍇ�̃G���[����
		if(startBlocks.size() == 0){
			throw new ErrorException(ErrorException.NO_START);
		}
		else if(startBlocks.size() != 1){
			for(RenderableBlock rb :startBlocks){
				throw new ErrorException(rb.getBlockID(), ErrorException.DOUBLE_START);				
			}
		}

		return startBlocks.get(0);
	}
	
	//////////
	// �o�� //
	/////////
	public void print(String text){
		System.out.print(text);
	}
	
	public void println(String text){
		System.out.println(text);
	}

	
	
	protected class ErrorException extends Exception{

		private static final long serialVersionUID = 1L;
		
		protected static final int ZERO_DIVIDE = 0;
		protected static final int NO_NUMBER = 1;
		protected static final int BOOLEAN = 2;
		protected static final int DOUBLE_START = 3;
		protected static final int NO_START = 4;
		
		ErrorException(long blockID, int errorCode){
			super();
			String blockid = "BlockID:" +Long.toString(blockID) + "�ɂ����ăG���[\n";
			println(blockid + createErrorMessage(errorCode));
		}
		
		ErrorException(int errorCode){
			super();
			println(createErrorMessage(errorCode));
		}
		
		private String createErrorMessage (int errorCode){
			String result = "";
			switch(errorCode){
				case ZERO_DIVIDE:
					
					break;
				case NO_NUMBER:
					
					break;
					
				case BOOLEAN:
					
					break;
					
				case DOUBLE_START:
					
					break;
					
				case NO_START:
					
					break;
					
			}
			return result;
		}
	}
	
	private class Variable{
	
		private String name;
		private Object value;
		private ConnectorType type;
		
		Variable(String name, Object value, ConnectorType ct){
			this.name = name;
			this.value = value;
			this.type = ct;
		}
		
		public void setValue(Object value){
			this.value = value;
		}

		public String getName(){
			return this.name == null ? null : this.name;
		}
		
		public Object getValue(){
			return this.value == null ? null : this.value;
		}
		
		public ConnectorType getConnectorType(){
			return this.type;
		}
		
		
	}
}
