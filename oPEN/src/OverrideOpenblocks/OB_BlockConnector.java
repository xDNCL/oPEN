package OverrideOpenblocks;

import edu.mit.blocks.codeblocks.BlockConnector;

public class OB_BlockConnector extends BlockConnector{
	public OB_BlockConnector(BlockConnector con) {
		super(con);
	}

	public Long linkDefArgument() {
		//checks if connector has a def arg or if connector already has a block
		if (hasDefArg && connBlockID == OB_Block.NULL) {
			OB_Block block = new OB_Block(workspace, arg.getGenusName(), arg.label);
			connBlockID = block.getBlockID();
			return connBlockID;
		}
		return OB_Block.NULL;
	}
}
