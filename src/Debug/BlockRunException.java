package Debug;

import java.awt.Color;

import edu.mit.blocks.codeblocks.Block;

public class BlockRunException extends Exception{

	private static final long serialVersionUID = 1L;

	//Error type
	public static final int UNEXPECTED = -1;
	public static final int NULL_BLOCK = 0;
	public static final int BOOLEAN = 1;
	public static final int NO_VARIABLE = 2;
	public static final int TRANSLATION_MISSING = 3;
	public static final int NO_NAME = 4;
	public static final int DUPLICATION = 5;
	public static final int CAST_ERROR = 6;
	public static final int BLOCK_IS_NULL = 7;
	
	public BlockRunException(Block block){
		this(block, UNEXPECTED);
	}
	
	public BlockRunException(Block block, String comment){
		super();
		System.out.println(comment);
	}
	
	public BlockRunException(Block block, int error){
		super();
		
		//highlight
		block.getWorkspace().getEnv().getRenderableBlock(block.getBlockID()).setBlockHighlightColor(Color.RED);
		//debug
//		System.out.println("Error block is "+block.getGenusName() + ":: Error Number is" + error);
		errorComment(block, error);
	}
	
	private void errorComment(Block block, int error){
		switch(error){
		case UNEXPECTED:
			System.out.println("予期せぬエラーが発生しました。");
			return;
			
		case NULL_BLOCK:
			System.out.println("ブロックが存在しません");
			return;
			
		case BOOLEAN:
			System.out.println("値がtrueまたはfalseではありません");
			return;
			
		case NO_VARIABLE:
			System.out.println("変数"+block.getBlockLabel()+"が存在しません");
			return;
			
		case TRANSLATION_MISSING:
			System.out.println("値の変換に失敗しました。");
			return;
			
		case NO_NAME:
			System.out.println("変数名、または値を指定してください。");
			return;
			
		case DUPLICATION:
			System.out.println("すでに同名の変数名が存在します。");
			return;
			
		case CAST_ERROR:
			System.out.println("代入の型が異なります。");
			return;
			
		case BLOCK_IS_NULL:
			System.out.println("ブロックが接続されていません。");
			return;		
			
			
		}
	}
	
	
	
}
