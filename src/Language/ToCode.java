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
	
	public String connectionAllBlockCode() throws BlockRunException{
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
		//�v���O�����J�n�̃u���b�N�����擾
		BlockString matchBlock = searchBlockCodeString(first.getGenusName());
		if(matchBlock == null){
			return ("//Not found -> "+first.getGenusName());
		}
		
		ArrayList<String[]> indentList = new ArrayList<String[]>();
		if(matchBlock.getPreCode() != null){
			indentList.add(matchBlock.getPreCode());
		}
		
		return iterative(first, indentList);
	}
	
		
	private String iterative(Block block, ArrayList<String[]> indentList)throws BlockRunException{
		String result = "";
		
		//�u���b�N�����擾
		BlockString matchBlock = searchBlockCodeString(block.getGenusName());
		
		//�Y������u���b�N��񂪂Ȃ��ꍇ�́u������Ȃ��v�Ƃ����|�̕\�L�ɒu��������
		if(matchBlock == null){
			result += ("//Not found -> "+block.getGenusName()+br());
		}
		
		//���������ꍇ�̓R�[�h�ϊ��J�n
		else{
			String[] codes = matchBlock.getCode();
			int connectorNum = 0;
			
			for(String code: codes){
				//�R�l�N�^�[�ɐڑ�����Ă���u���b�N�̒l�擾
				if(code.equals(VAL)){
					BlockConnector bc = null;
					try{
						bc = block.getSocketAt(connectorNum++);
					}catch(Exception e){
						throw new BlockRunException("BlockConnector���̎擾�Ɏ��s���܂����B");
					}
					
					//�R�l�N�^�[�����݂��Ȃ��ꍇ
					if(bc == null){
						System.err.println("not found connector:"+block);
					}
					
					//�ڑ�����
					if(bc.getBlockID() == Block.NULL){
	
					}
					//�ڑ��L��
					else{
						result += iterative(this.workspace.getEnv().getBlock(bc.getBlockID()), indentList);
					}
				}
				
				//�R�l�N�^�[�ɐڑ�����Ă���u���b�N�̒l���A�C���f���g�x�����P�グ�Ď擾
				else if(code.equals(PREVAL)){
					BlockConnector bc = null;
					try{
						bc = block.getSocketAt(connectorNum++);
					}catch(Exception e){
						throw new BlockRunException("BlockConnector���̎擾�Ɏ��s���܂����B");
					}
					
					if(bc == null){
						System.err.println("not found connector:"+block);
					}
					
					//�ڑ�����
					if(bc.getBlockID() == Block.NULL){
						
					}
					//�ڑ��L��
					else{
						//�C���f���g�̒ǉ������A����э폜����
						String[] indentCode = matchBlock.getPreCode();
						indentList.add(indentCode);
						result += translationForPreCode(indentCode);
						result += iterative(this.workspace.getEnv().getBlock(bc.getBlockID()), indentList);
						indentList.remove(indentList.size()-1);
					}
				}
					
				//���x���̒l�擾	
				else if(code.equals(LABEL)){
					result += block.getBlockLabel();
				}
				
				//���s�R�}���h����
				else if(code.equals(BR)){
					result += br();
					result += getIndent(indentList);
				}
				
				//�X�y�[�X����
				else if(code.equals(SPACE)){
					result += " ";
				}
				
				//�ʏ�̃R�[�h�Ȃ̂ł��̂܂ܐڑ�
				else{
					result += code;
				}
			}
		}
		
		//���ɐڑ�����Ă���u���b�N�̏�����
		if(block.getAfterBlockID() != Block.NULL){
			result += br();
			result += getIndent(indentList);
			result += iterative(this.workspace.getEnv().getBlock(block.getAfterBlockID()), indentList);
		}
		
		return result;
	}
	
	/**
	 * �e���x���̃C���f���g���q���ŕԂ�
	 * @param indentList �e���x���̃C���f���g��񃊃X�g
	 * @return �e���x���̃C���f���g���q�����킹������
	 */
	private String getIndent(ArrayList<String[]> indentList){
		String result = "";
		for(String[] indents: indentList){
			assert indents != null;
			result += translationForPreCode(indents);
		}
		return result;
	}
	
	/**
	 * �R�[�h���̓���̕������ϊ�
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
	
	
		
	private BlockString searchBlockCodeString(String name){
		for(BlockString bs:this.blockCodeStringList){
			if(name.equals(bs.getName())){
				return bs;
			}
		}
		return null;
	}
	

}
