package Exe;

import java.awt.Color;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;

public class BlockRunException extends Exception{

	private static final long serialVersionUID = 1L;
	
	//エラーブロックのハイライト点滅用
	private static BlinkBlock brinkBlock;

	
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
	
	
	//エラーの原因となるブロックが見つからないとき(block == null)のコンストラクタ
	public BlockRunException(String comment){
		System.out.println(comment);
	}
	
	public BlockRunException(Block block){
		this(block, UNEXPECTED);
	}
	
	public BlockRunException(Block block, String comment){
		super();
		highlight(block.getWorkspace().getEnv().getRenderableBlock(block.getBlockID()));
		System.out.println(comment);
	}
	
	public BlockRunException(Block block, int error){
		super();
		highlight(block.getWorkspace().getEnv().getRenderableBlock(block.getBlockID()));
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
	
	private void highlight(RenderableBlock block){
		blinkOff();
		brinkBlock = new BlinkBlock(block);
		brinkBlock.start();
	}
	
	public static void blinkOff(){
		if(brinkBlock != null){
			brinkBlock.offBrink();
			brinkBlock = null;
		}
	}
	
	private static class BlinkBlock extends Thread{
		
		private final RenderableBlock block;
		private boolean brinkOn;
		private int SLEEP_TIME = 800;// msec
		
		BlinkBlock(RenderableBlock block){
			this.block = block;
			brinkOn = true;
		}
		
		public void run(){
			try{
			while(brinkOn){
				block.setBlockHighlightColor(Color.RED);
				sleep(SLEEP_TIME);
				block.resetHighlight();
				sleep(SLEEP_TIME);
			}
			}catch(Exception e){
				this.offBrink();
			}
		}
		
		public void offBrink(){
			this.brinkOn = false;
			this.SLEEP_TIME = 0;
		}
		
		
	}
	
	
	
}
