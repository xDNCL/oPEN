package Language;

import java.util.ArrayList;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;

import Exe.BlockRunException;
import OverrideOpenblocks.OB_Workspace;


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
	private OB_Workspace workspace;
		
	//コマンド系文字列
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
					throw new BlockRunException(block, "プログラム開始のブロックが重複しています。");
				}	
			}
		}
		
		if(first == null){
			throw new BlockRunException("プログラム開始のブロックが見つかりません。");
		}
		//プログラム開始のブロック情報を取得
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
		
		//ブロック情報を取得
		BlockString matchBlock = searchBlockCodeString(block.getGenusName());
		
		//該当するブロック情報がない場合は「見つからない」という旨の表記に置き換える
		if(matchBlock == null){
			result += ("//Not found -> "+block.getGenusName()+br());
		}
		
		//見つかった場合はコード変換開始
		else{
			String[] codes = matchBlock.getCode();
			int connectorNum = 0;
			
			for(String code: codes){
				//コネクターに接続されているブロックの値取得
				if(code.equals(VAL)){
					BlockConnector bc = null;
					try{
						bc = block.getSocketAt(connectorNum++);
					}catch(Exception e){
						throw new BlockRunException("BlockConnector情報の取得に失敗しました。");
					}
					
					//コネクターが存在しない場合
					if(bc == null){
						System.err.println("not found connector:"+block);
					}
					
					//接続無し
					if(bc.getBlockID() == Block.NULL){
	
					}
					//接続有り
					else{
						result += iterative(this.workspace.getEnv().getBlock(bc.getBlockID()), indentList);
					}
				}
				
				//コネクターに接続されているブロックの値を、インデントベルを１つ上げて取得
				else if(code.equals(PREVAL)){
					BlockConnector bc = null;
					try{
						bc = block.getSocketAt(connectorNum++);
					}catch(Exception e){
						throw new BlockRunException("BlockConnector情報の取得に失敗しました。");
					}
					
					if(bc == null){
						System.err.println("not found connector:"+block);
					}
					
					//接続無し
					if(bc.getBlockID() == Block.NULL){
						
					}
					//接続有り
					else{
						//インデントの追加処理、および削除処理
						String[] indentCode = matchBlock.getPreCode();
						indentList.add(indentCode);
						result += translationForPreCode(indentCode);
						result += iterative(this.workspace.getEnv().getBlock(bc.getBlockID()), indentList);
						indentList.remove(indentList.size()-1);
					}
				}
					
				//ラベルの値取得	
				else if(code.equals(LABEL)){
					result += block.getBlockLabel();
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
		if(block.getAfterBlockID() != Block.NULL){
			result += br();
			result += getIndent(indentList);
			result += iterative(this.workspace.getEnv().getBlock(block.getAfterBlockID()), indentList);
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
	
	
		
	private BlockString searchBlockCodeString(String name){
		for(BlockString bs:this.blockCodeStringList){
			if(name.equals(bs.getName())){
				return bs;
			}
		}
		return null;
	}
	

}
