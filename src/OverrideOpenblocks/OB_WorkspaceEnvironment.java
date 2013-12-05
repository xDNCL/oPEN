package OverrideOpenblocks;

import java.util.HashMap;
import java.util.Map;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.workspace.WorkspaceEnvironment;

public class OB_WorkspaceEnvironment extends WorkspaceEnvironment{
	

	OB_WorkspaceEnvironment(){
		super();
	}


    // advanced Block

    private final Map<Long, Block> allBlocks = new HashMap<Long, Block>();

    @Override
    public Block getBlock(Long blockID) {
        return this.allBlocks.get(blockID);
    }

    @Override
    public void addBlock(Block block) {

    	long id = block.getBlockID();

        if (this.allBlocks.containsKey(id)) {
            Block dup = this.allBlocks.get(id);
            System.out.println("pre-existing block is: " + dup + " with genus " + dup.getGenusName() + " and label " + dup.getBlockLabel());
            assert !this.allBlocks.containsKey(id) : "Block id: " + id + " already exists!  BlockGenus " + block.getGenusName() + " label: " + block.getBlockLabel();
        }

    	this.allBlocks.put(id, block);
    }



}
