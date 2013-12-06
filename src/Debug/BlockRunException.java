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
	
	
	public BlockRunException(Block block){
		this(block, UNEXPECTED);
	}
	
	public BlockRunException(Block block, int error){
		super();
		System.out.println("Error block is "+block.getGenusName() + ":: Error Number is" + error);
		
	}
	
	
	
}
