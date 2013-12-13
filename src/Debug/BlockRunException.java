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
			System.out.println("�\�����ʃG���[���������܂����B");
			return;
			
		case NULL_BLOCK:
			System.out.println("�u���b�N�����݂��܂���");
			return;
			
		case BOOLEAN:
			System.out.println("�l��true�܂���false�ł͂���܂���");
			return;
			
		case NO_VARIABLE:
			System.out.println("�ϐ�"+block.getBlockLabel()+"�����݂��܂���");
			return;
			
		case TRANSLATION_MISSING:
			System.out.println("�l�̕ϊ��Ɏ��s���܂����B");
			return;
			
		case NO_NAME:
			System.out.println("�ϐ����A�܂��͒l���w�肵�Ă��������B");
			return;
			
		case DUPLICATION:
			System.out.println("���łɓ����̕ϐ��������݂��܂��B");
			return;
			
		case CAST_ERROR:
			System.out.println("����̌^���قȂ�܂��B");
			return;
			
		case BLOCK_IS_NULL:
			System.out.println("�u���b�N���ڑ�����Ă��܂���B");
			return;		
			
			
		}
	}
	
	
	
}
