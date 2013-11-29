package Language;

import java.util.ArrayList;


public class ToCode {
	
	//改行コード
	private final String br = "\n";
	//タブコード
	private final String tab = "\t";
	
	//プログラム開始のブロック
	private final String firstSearchBlockName = "start";
	private final String[] CONNECTOR_TYPES = {"cmd", "boolean", "number", "io"};
	
	//ブロックのコード情報
	private ArrayList<BlockString> blockCodeStringList;
	
	//workspace上のブロックの情報
	private ArrayList<BlockData> blockDataList;
	
	//出力用プロパティ設定
	private PropertyFile propertyFile;
	
	//コマンド系文字列
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
		
		//プログラム開始のブロック情報を取得
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
		
		//ブロック情報を取得
		BlockString matchBlock = searchBlockCodeString(block.getName());
		
		//該当するブロック情報がない場合は「見つからない」という旨の表記に置き換える
		if(matchBlock == null){
			result += ("//Not found -> "+block.getName()+br());
		}
		
		//見つかった場合はコード変換開始
		else{
			String[] codes = matchBlock.getCode();
			int connectorNum = 0;
			
			for(String code: codes){
				//コネクターに接続されているブロックの値取得
				if(code.equals(VAL)){
					ConnectorInfo ci = block.getConnectorInfo(connectorNum++);
					
					//コネクターが存在しない場合
					if(ci == null){
						System.err.println("not found connector:"+block);
					}
					
					//接続無し
					if(ci.getId() == -1 ){
	
					}
					//接続有り
					else{
						result += iterative(searchBlockData(ci.getId()), indentList);
					}
				}
				
				//コネクターに接続されているブロックの値を、インデントベルを１つ上げて取得
				else if(code.equals(PREVAL)){
					ConnectorInfo ci = block.getConnectorInfo(connectorNum++);
					
					if(ci == null){
						System.err.println("not found connector:"+block);
					}
					
					//接続無し
					if(ci.getId() == -1 ){
						
					}
					//接続有り
					else{
						//インデントの追加処理、および削除処理
						String[] indentCode = matchBlock.getPreCode();
						indentList.add(indentCode);
						result += translationForPreCode(indentCode);
						result += iterative(searchBlockData(ci.getId()), indentList);
						indentList.remove(indentList.size()-1);
					}
				}
					
				//ラベルの値取得	
				else if(code.equals(LABEL)){
					result += block.getLabel();
				}
				
				//改行コマンド処理
				else if(code.equals(BR)){
					result += br();
					result += getIndent(indentList);
				}
				
				//スペース処理
				else if(code.equals(SPACE)){
					result += " ";
				}
				
				//通常のコードなのでそのまま接続
				else{
					result += code;
				}
			}
		}
		
		//次に接続されているブロックの処理へ
		if(block.getNextId() != -1){
			result += br();
			result += getIndent(indentList);
			result += iterative(searchBlockData(block.getNextId()), indentList);
		}
		
		return result;
	}
	
	/**
	 * 各レベルのインデントを繋いで返す
	 * @param indentList 各レベルのインデント情報リスト
	 * @return 各レベルのインデントを繋ぎ合わせたもの
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
	 * コード内の特定の文字列を変換
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
	 * 改行コードが違うと困るのでメソッドから改行コマンド呼び出し
	 * @return 改行コード
	 */
	private String br(){
		return br;
	}
	
	/**
	 * 
	 * @return タブコード
	 */
	private String tab(){
		return tab;
	}
	
	
	/**
	 *  対応するブロックがソケットに挿入されていない場合、可能な限り自動補完する
	 * 	private final String[] connectorTypes = {"cmd", "boolean", "number"}
	 * @param ci ブロックにある１ソケットの情報
	 * @return 補完する文字列
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
