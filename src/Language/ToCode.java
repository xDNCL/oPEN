package Language;

import java.util.ArrayList;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;

import Exe.BlockRunException;
import OverrideOpenblocks.OB_Workspace;


public class ToCode {
	
	//���s�R�[�h
	private final String br = "\n";
	//�^�u�R�[�h
	private final String tab = "\t";
	
	//�v���O�����J�n�̃u���b�N
	private final String firstSearchBlockName = "start";
	private final String[] CONNECTOR_TYPES = {"cmd", "boolean", "number", "io"};
	
	//�u���b�N�̃R�[�h���
	private ArrayList<BlockString> blockCodeStringList;
	
	//workspace��̃u���b�N�̏��
	private OB_Workspace workspace;
		
	//�R�}���h�n������
	private final String VAL = "_val";
	private final String PREVAL = "_preval";
	private final String LABEL = "_label";
	private final String BR = "_br";
	private final String SPACE = "_space";
	private final String TAB = "_t";
	
	public ToCode(ArrayList<BlockString> blockCodeStringList, OB_Workspace workspace){
		this.blockCodeStringList = blockCodeStringList;
		this.workspace = workspace;
	}
	
	public String getCode() throws BlockRunException{
		String result = "";
		Block first = null;
		for(Block block: this.workspace.getBlocks()){
			if(block.getGenusName().equals(this.firstSearchBlockName)){
				if(first == null){
					first = block;
				}
				else{
					throw new BlockRunException(block, "�v���O�����J�n�̃u���b�N���d�����Ă��܂��B");
				}	
			}
		}
		
		if(first == null){
			throw new BlockRunException("�v���O�����J�n�̃u���b�N��������܂���B");
		}
		
		for(String code: iterative(first)){
			result += code;
			result += br();
		}
		
		return result;
	}
	
	
	private ArrayList<String> iterative(Block block){
		String result = "";
		
		//�R�l�N�^��̃u���b�N�擾
		ArrayList<ArrayList<String>> connectorCodeList = new ArrayList<ArrayList<String>>();
		for(int i=0; i<block.getNumSockets(); i++){
			BlockConnector bc = block.getSocketAt(i);
			connectorCodeList.add(iterative(workspace.getEnv().getBlock(bc.getBlockID())));
		}
		
		//�u���b�N������u���b�N�R�[�h���擾
		BlockString matchBlock = searchBlockCodeString(block.getGenusName(), connectorCodeList);
		
		//�R�[�h�����@�P�v�f���P�s�̃R�[�h
		ArrayList<String> codeLine = new ArrayList<String>();
		int connectorNum  = 0;
		
		for(String code: matchBlock.getCode()){
			//�R�l�N�^�[��̒l�擾����
			if(code.equals(VAL)){
				for(String line: connectorCodeList.get(connectorNum)){
					result += line;
				}
				connectorNum++;
			}
			
			else if(code.equals(PREVAL)){
				if(connectorCodeList.size() > 0){
					for(String line: connectorCodeList.get(connectorNum)){
						String indent = translationForPreCode(matchBlock.getPreCode());
						codeLine.add(indent + line);
					}
				}
				connectorNum++;
			}
			//���x���̒l�擾	
			else if(code.equals(LABEL)){
				result += block.getBlockLabel();
			}
			
			//���s�R�}���h����
			else if(code.equals(BR)){
				codeLine.add(result);
				result = "";
			}
			
			//�X�y�[�X����
			else if(code.equals(SPACE)){
				result += " ";
			}
			//�ʏ�R�[�h�Ƃ��ď���
			else{
				result += code;
			}
		}
		
		if(!result.equals("")){
			codeLine.add(result);
		}
		
		if(block.getAfterBlockID() != Block.NULL){
			for(String code: iterative(workspace.getEnv().getBlock(block.getAfterBlockID()))){
				codeLine.add(code);
			}
		}
		
		return codeLine;
	}
	
	
	/**
	 * �C���f���g�R�[�h���̓���̕������ϊ�
	 */
	private String translationForPreCode(String[] indents){
		String result = "";
		for(String indent: indents){
			if(indent.equals(SPACE)){
				result += " ";
			}
			else if(indent.equals(TAB)){
				result += tab();
			}
			else{
				result += indent;
			}
		}
		return result;
	}
						
	/**
	 * ���s�R�[�h���Ⴄ�ƍ���̂Ń��\�b�h������s�R�}���h�Ăяo��
	 * @return ���s�R�[�h
	 */
	private String br(){
		return br;
	}
	
	/**
	 * 
	 * @return �^�u�R�[�h
	 */
	private String tab(){
		return tab;
	}
	
	/**
	 * �u���b�N�f�[�^�x�[�X����T�[�`
	 * @param name�@�u���b�N���O�i��{�I�ɂ����炾���ŃT�[�`�j
	 * 
	 * @param connecotorList�@�����ڑ�����Ă���u���b�N�ɂ���ďo�͂���R�[�h���ς��ꍇ�A
	 * ����connectorList����T���āA�K�؂ȃR�[�h��f���u���b�N�f�[�^�x�[�X��T������
	 * 
	 * connectorList.get(0) ��ԏ�̃R�l�N�^�[�̃u���b�N���X�g�擾
	 * connectorList.get(2) ��Ԗڂ̃R�l�N�^�[�̃u���b�N���X�g�擾
	 * �E�E�E�ȉ����E�E�E
	 * @return
	 */
	private BlockString searchBlockCodeString(String name, ArrayList<ArrayList<String>> connecotorList){
		
		/** �����ɓ��ꏈ������u���b�N���ƁA���̏������e���L�q���� **/
//		if(name.equals("")){
//			String advancedName = "hoge";
//			return searchBlockCodeString(advancedName);
//		}
		
		return searchBlockCodeString(name);		
	}
		
	private BlockString searchBlockCodeString(String name){
		for(BlockString bs:this.blockCodeStringList){
			if(name.equals(bs.getName())){
				return bs;
			}
		}
		return null;
	}
	

}
