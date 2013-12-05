package OverrideOpenblocks;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.workspace.Workspace;

public class OB_Block extends edu.mit.blocks.codeblocks.OB_Block{

	private OB_Workspace workspace;
	
	public OB_Block(Workspace workspace, String genusName, String label) {
		super(workspace, genusName, label);
	}

    public OB_Block(Workspace workspace, String genusName, boolean linkToStubs) { 	
        super(workspace, genusName, linkToStubs);
     }

    protected OB_Block(Block block){
    	super(block.getWorkspace(), block.getGenusName(), block.getBlockLabel());
    }

    
    /**
     * boolean connector—p
     * @return boolean
     */
    public boolean evaluateBoolean(){
    	//boolean block
    	if(this.getGenusName().equals("true")){
    		return true;
    	}
    	if(this.getGenusName().equals("false")){
    		return false;
    	}
    	
    	//caluculation block
    	if(this.getGenusName().equals("equals")){
    		return this.getBlock(this.getSocketAt(0).getBlockID()) == 
    				this.getBlock(this.getSocketAt(1).getBlockID()) ?
    						true: false;
    	}
    	if(this.getGenusName().equals("not-equals")){
    		
    	}
    	if(this.getGenusName().equals("lessthan")){
    		
    	}
    	if(this.getGenusName().equals("lessthanoreequalt")){
    		
    	}
    	if(this.getGenusName().equals("greaterthan")){
    		
    	}
    	if(this.getGenusName().equals("greaterthanorequalt")){
    		
    	}
    	
    	for(int i=0; i < this.getNumSockets(); i++){
			BlockConnector blockConnector = this.getSocketAt(i);
			
    	}
    	
    	//to do
    	return false;
    }

    private OB_Block getBlock(long id){
    	Block block = this.workspace.getEnv().getBlock(id);
    	
    	if(block == null){
    		return null;
    	}
    	
    	if(this.workspace.getEnv().getBlock(id) instanceof OB_Block){
    		return (OB_Block)this.workspace.getEnv().getBlock(id);
    	}
    	else{
    		OB_Block newBlock = new OB_Block(block);
    		
    		return newBlock;
    	}
    }
    
    

    @Override
    public String toString(){
    	return "OB_Block:: name is -"+this.getGenusName();
    }
}
