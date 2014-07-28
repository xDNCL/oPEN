package edu.mit.blocks.codeblocks;

import edu.mit.blocks.workspace.Workspace;


public class OB_Block extends Block{
		
	public OB_Block(Workspace workspace, String genusName, String label) {
		super(workspace, genusName, label);
	}

    public OB_Block(Workspace workspace, String genusName, boolean linkToStubs) { 	
        super(workspace, genusName, linkToStubs);
     }

}
