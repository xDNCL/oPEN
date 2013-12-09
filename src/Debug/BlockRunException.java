package Debug;

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
	
	
	public BlockRunException(Block block){
		this(block, UNEXPECTED);
	}
	
	public BlockRunException(String comment){
		super();
		System.out.println(comment);
	}
	
	public BlockRunException(Block block, int error){
		super();
		System.out.println("Error block is "+block.getGenusName() + ":: Error Number is" + error);
		errorComment(error);
	}
	
	private void errorComment(int error){
		switch(error){
		case UNEXPECTED:
			System.out.println("");
			return;
			
		case NULL_BLOCK:
			System.out.println("");
			return;
			
		case BOOLEAN:
			System.out.println("");
			return;
			
		case NO_VARIABLE:
			System.out.println("");
			return;
			
		case TRANSLATION_MISSING:
			System.out.println("");
			return;
			
		case NO_NAME:
			System.out.println("");
			return;
			
		case DUPLICATION:
			System.out.println("");
			return;
			
			
			
		}
	}
	
	
	
}
