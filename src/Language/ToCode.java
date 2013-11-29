package Language;

import java.util.ArrayList;


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
	private ArrayList<BlockData> blockDataList;
	
	//�o�͗p�v���p�e�B�ݒ�
	private PropertyFile propertyFile;
	
	//�R�}���h�n������
	private final String VAL = "_val";
	private final String PREVAL = "_preval";
	private final String LABEL = "_label";
	private final String BR = "_br";
	private final String SPACE = "_space";
	private final String TAB = "_t";
	
	public ToCode(ArrayList<BlockString> blockCodeStringList, ArrayList<BlockData> blockDataList){
		this.blockCodeStringList = blockCodeStringList;
		this.blockDataList = blockDataList;
		this.propertyFile = new PropertyFile();
	}
	
	public String connectionAllBlockCode(){
//		System.out.println(blockData);
		BlockData block = searchBlockData(firstSearchBlockName);
		
		//�v���O�����J�n�̃u���b�N�����擾
		BlockString matchBlock = searchBlockCodeString(block.getName());
		if(matchBlock == null){
			return ("//Not found -> "+block.getName());
		}
		
		ArrayList<String[]> indentList = new ArrayList<String[]>();
		if(matchBlock.getPreCode() != null){
			indentList.add(matchBlock.getPreCode());
		}
		
		return iterative(block, indentList);
	}
	
		
	private String iterative(BlockData block, ArrayList<String[]> indentList){
		String result = "";
		
		//�u���b�N�����擾
		BlockString matchBlock = searchBlockCodeString(block.getName());
		
		//�Y������u���b�N��񂪂Ȃ��ꍇ�́u������Ȃ��v�Ƃ����|�̕\�L�ɒu��������
		if(matchBlock == null){
			result += ("//Not found -> "+block.getName()+br());
		}
		
		//���������ꍇ�̓R�[�h�ϊ��J�n
		else{
			String[] codes = matchBlock.getCode();
			int connectorNum = 0;
			
			for(String code: codes){
				//�R�l�N�^�[�ɐڑ�����Ă���u���b�N�̒l�擾
				if(code.equals(VAL)){
					ConnectorInfo ci = block.getConnectorInfo(connectorNum++);
					
					//�R�l�N�^�[�����݂��Ȃ��ꍇ
					if(ci == null){
						System.err.println("not found connector:"+block);
					}
					
					//�ڑ�����
					if(ci.getId() == -1 ){
	
					}
					//�ڑ��L��
					else{
						result += iterative(searchBlockData(ci.getId()), indentList);
					}
				}
				
				//�R�l�N�^�[�ɐڑ�����Ă���u���b�N�̒l���A�C���f���g�x�����P�グ�Ď擾
				else if(code.equals(PREVAL)){
					ConnectorInfo ci = block.getConnectorInfo(connectorNum++);
					
					if(ci == null){
						System.err.println("not found connector:"+block);
					}
					
					//�ڑ�����
					if(ci.getId() == -1 ){
						
					}
					//�ڑ��L��
					else{
						//�C���f���g�̒ǉ������A����э폜����
						String[] indentCode = matchBlock.getPreCode();
						indentList.add(indentCode);
						result += translationForPreCode(indentCode);
						result += iterative(searchBlockData(ci.getId()), indentList);
						indentList.remove(indentList.size()-1);
					}
				}
					
				//���x���̒l�擾	
				else if(code.equals(LABEL)){
					result += block.getLabel();
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
		if(block.getNextId() != -1){
			result += br();
			result += getIndent(indentList);
			result += iterative(searchBlockData(block.getNextId()), indentList);
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
	
	
	/**
	 *  �Ή�����u���b�N���\�P�b�g�ɑ}������Ă��Ȃ��ꍇ�A�\�Ȍ��莩���⊮����
	 * 	private final String[] connectorTypes = {"cmd", "boolean", "number"}
	 * @param ci �u���b�N�ɂ���P�\�P�b�g�̏��
	 * @return �⊮���镶����
	 */
	private String codeComplement(ConnectorInfo ci){		
		String connectorType = ci.getType();
	
		if(!this.propertyFile.complementFlag()){
			return "";
		}
		
		if(connectorType.equals(CONNECTOR_TYPES[0])){
			return "/**non command**/";
		}
		if(connectorType.equals(CONNECTOR_TYPES[1])){
			return "true";
		}
		if(connectorType.equals(CONNECTOR_TYPES[2])){
			return "1";
		}
		
		return "";
	}
	
	//Search
	private BlockData searchBlockData(String name){
		for(BlockData bd: this.blockDataList){
			if(name.equals(bd.getName())){
				return bd;
			}
		}
		return null;
	}
	
	private BlockData searchBlockData(int id){
		if(id == -1) return null;
		
		for(BlockData bd: this.blockDataList){
			if(id == bd.getId()){
				return bd;
			}
		}
		return null;
	}
	
	private BlockString searchBlockCodeString(String name){
		for(BlockString bs:this.blockCodeStringList){
			if(name.equals(bs.getName())){
				return bs;
			}
		}
		return null;
	}
	
	private void connectorNotFoundError(){
		System.err.println("Connector is Not Found");
	}

}
