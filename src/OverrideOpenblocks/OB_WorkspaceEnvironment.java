package OverrideOpenblocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockGenus;
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

    // BlockGenuses
    
    private Map<String, BlockGenus> nameToGenus = new HashMap<String, BlockGenus>();
    
    /**
     * Returns the BlockGenus with the specified name; null if this name does not exist
     * @param name the name of the desired BlockGenus  
     * @return the BlockGenus with the specified name; null if this name does not exist
     */
    @Override
    public BlockGenus getGenusWithName(String name) {
//    	System.out.println("Debug:WorkspaceEnv Line145\nString name=::"+name+"\nnameToGenus::"+nameToGenus.get(name));
    	
        return nameToGenus.get(name);
    }
    
    @Override
    public void addBlockGenus(BlockGenus genus) {
    	nameToGenus.put(genus.getGenusName(), genus);
    }
    
    /**
     * Resets all the Block Genuses of current language.
     */
    @Override
    public void resetAllGenuses() {
        nameToGenus.clear();
    }


}
