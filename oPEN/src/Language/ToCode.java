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
	
	public String getCode() throws BlockRunException{
		String result = "";
		Block first = null;
		for(Block block: this.workspace.getBlocks()){
			if(block.getGenusName().equals(this.firstSearchBlockName)){
				if(first == null){
					first = block;
				}else{
					throw new BlockRunException(block, "プログラム開始のブロックが重複しています。");
				}	
			}
		}
		
		if(first == null){
			throw new BlockRunException("プログラム開始のブロックが見つかりません。");
		}
		
		for(String code: iterative(first)){
			result += code;
			result += br();
		}
		
		return result;
	}
	
	
	private ArrayList<String> iterative(Block block)throws BlockRunException{
		String result = "";
		
		if(block == null) return null;
		
		//コネクタ先のブロック取得
		ArrayList<ArrayList<String>> connectorCodeList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<Block>> connectionBlocks = new ArrayList<ArrayList<Block>>();
		
		for(int i=0; i<block.getNumSockets(); i++){
			BlockConnector bc = block.getSocketAt(i);
//			System.out.println(block.getGenusName() +", socketNum:"+ block.getNumSockets() +", connected:"+workspace.getEnv().getBlock(b			ArrayList<String> tmp = iterative(workspace.getEnv().getBlock(bc.getBlockID())); 
			ArrayList<String> tmp = iterative(workspace.getEnv().getBlock(bc.getBlockID()));
			if(tmp != null){
				connectorCodeList.add(tmp);
			}
			connectionBlocks.add(getBlockList(bc));
		}
		
		//ブロック名からブロックコードを取得
		BlockString matchBlock = searchBlockCodeString(block.getGenusName(), connectionBlocks);
		
		if(matchBlock == null){
			throw new BlockRunException("ブロック名："+block.getGenusName()+"の変換ルールが見つかりません。");
		}
		
		//コード生成　１要素＝１行のコード
		ArrayList<String> codeLine = new ArrayList<String>();
		int connectorNum  = 0;
		
		for(String code: matchBlock.getCode()){
			//コネクター先の値取得処理
			if(code.equals(VAL)){
				for(String line: connectorCodeList.get(connectorNum)){
					result += line;
				}
				connectorNum++;
			}else if(code.equals(PREVAL)){
				String indent = translationForPreCode(matchBlock.getPreCode());
				//スタートブロック専用。後で消す
				if(matchBlock.getName().equals("start")){
					for(String Scode: iterative(workspace.getEnv().getBlock(block.getAfterBlockID()))){
						codeLine.add(indent + Scode);
					}
				}
				//正規ルート
				if(connectorCodeList.size() > 0){
					for(String line: connectorCodeList.get(connectorNum)){
						codeLine.add(indent + line);
					}
				}
				connectorNum++;
			}else if(code.equals(LABEL)){
				//ラベルの値取得
				result += block.getBlockLabel();
			}else if(code.equals(BR)){
				//改行コマンド処理
				codeLine.add(result);
				result = "";
			}else if(code.equals(SPACE)){
				//スペース処理
				result += " ";
			}else{
				//通常コードとして処理
				result += code;
			}
		}
		
		if(!result.equals("")){
			codeLine.add(result);
		}
		
		if(block.getGenusName().equals("start")){
			return codeLine;
		}
		
		if(block.getAfterBlockID() != Block.NULL){
			for(String code: iterative(workspace.getEnv().getBlock(block.getAfterBlockID()))){
				codeLine.add(code);
			}
		}
		
		return codeLine;
	}
	
	
	/**
	 * インデントコード内の特定の文字列を変換
	 */
	private String translationForPreCode(String[] indents){
		String result = "";
		for(String indent: indents){
			if(indent.equals(SPACE)){
				result += " ";
			}else if(indent.equals(TAB)){
				result += tab();
			}else{
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
	 * ブロックデータベースからサーチ
	 * @param name　ブロック名前（基本的にこちらだけでサーチ）
	 * 
	 * @param connecotorList　もし接続されているブロックによって出力するコードが変わる場合、
	 * このconnectorList内を探して、適切なコードを吐くブロックデータベースを探すこと
	 * 
	 * connectorList.get(0) 一番上のコネクターのブロックリスト取得
	 * connectorList.get(1) 二番目のコネクターのブロックリスト取得
	 * ・・・以下略・・・
	 * @return
	 */
	private BlockString searchBlockCodeString(String name, ArrayList<ArrayList<Block>> connectionBlocks) throws BlockRunException{
		
		/** ここに特殊処理するブロック名と、その処理内容を記述する **/
//		if(name.equals("")){
//			String advancedName = "hoge";
//			return searchBlockCodeString(advancedName);
//		}
//		debug(connectionBlocks);
		
		
		if(name.equals("motor-on-fwd")){
			BlockString bs = searchBlockCodeString("motor-on-fwd");
			for(Block block: connectionBlocks.get(2)){
				if(block.getGenusName().equals("rotate")){
					bs = searchBlockCodeString("rotate-motor");
					int i = 0;
					int j = 0;
					String[] code = new String[bs.getCode().length];
					for(String str: bs.getCode()){
						if(str.equals("_val")){
							i++;
							if(i == 3){
								str = iterative(block).get(0);
							}
						}
						code[j++] = str;
					}
					bs = new BlockString(bs.getName(), code);
					break;
				}
			}
			for(Block block: connectionBlocks.get(2)){
				if(block.getGenusName().equals("wait")){
					ArrayList<String> codes = new ArrayList<String>();
					for(String c: bs.getCode()){
						codes.add(c);
					}
					//waitCAERA[EhCACOC?
					codes.add(iterative(block).get(0));
					String[] strs = new String[codes.size()];
					int i=0;
					for(String s: codes){
						
						strs[i++] = new String(s);
					}
					
					return new BlockString(bs.getName(), strs);
				}
			}
			return bs;
		}
		
		if(name.equals("motor-on-rev")){
			BlockString bs = searchBlockCodeString("motor-on-rev");
			if(connectionBlocks.get(2).equals(null)){
				return bs;
			}
			for(Block block: connectionBlocks.get(2)){
				if(block.getGenusName().equals("rotate")){
					bs = searchBlockCodeString("rotate-motor-rev");
					int i = 0;
					int j = 0;
					String[] code = new String[bs.getCode().length];
					for(String str: bs.getCode()){
						if(str.equals("_val")){
							i++;
							if(i == 3){
								str = iterative(block).get(0);
							}
						}
						code[j++] = str;
					}
					bs = new BlockString(bs.getName(), code);
					break;
				}
			}
			for(Block block: connectionBlocks.get(2)){
				if(block.getGenusName().equals("wait")){
					ArrayList<String> codes = new ArrayList<String>();
					for(String c: bs.getCode()){
						codes.add(c);
					}
					//waitCAERA[EhCACOC?
					codes.add(iterative(block).get(0));
					String[] strs = new String[codes.size()];
					int i=0;
					for(String s: codes){
						
						strs[i++] = new String(s);
					}
					
					return new BlockString(bs.getName(), strs);
				}
			}
			return bs;
		}
		
		return searchBlockCodeString(name);		
	}
		
	/**
	 * @param name ブロック名前
	 * @return
	 */
	private BlockString searchBlockCodeString(String name){
		for(BlockString bs:this.blockCodeStringList){
			if(name.equals(bs.getName())){
				return bs;
			}
		}
		return null;
	}
	
	private ArrayList<Block> getBlockList(BlockConnector connector){
		ArrayList<Block> blockList = new ArrayList<Block>();
		Block connectedBlock = workspace.getEnv().getBlock(connector.getBlockID());
//		System.out.println(connectedBlock.getGenusName() + " ID:"+ connectedBlock.getBlockID());
		while(connectedBlock != null){
			blockList.add(connectedBlock);
			for(int i=0; i<connectedBlock.getNumSockets(); i++){
				for(Block block: getBlockList(connectedBlock.getSocketAt(i))){
					blockList.add(block);
				}
			}
			connectedBlock = workspace.getEnv().getBlock(connectedBlock.getAfterBlockID());
		}
		return blockList;
	}
	
	private void debug(ArrayList<ArrayList<Block>> connectionBlocks){
		int i=0;
		for(ArrayList<Block> conList: connectionBlocks){
			System.out.println("connectionList("+ i++ + ")");
			for(Block block: conList){
				System.out.println("genusName:"+block.getGenusName());
			}
		}
		
	}

}
