package OverrideOpenblocks;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.Comment;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceWidget;

public class OB_Page extends Page{
	
	private Workspace workspace;

	public OB_Page(Workspace workspace, String name, int pageWidth, int pageHeight, String pageDrawer, boolean inFullview, Color defaultColor, boolean isCollapsible) {
		super(workspace, name, pageWidth, 0, pageDrawer, inFullview, defaultColor, isCollapsible);
		this.workspace = workspace;
	}
	
    public OB_Page(Workspace workspace, String name, int pageWidth, int pageHeight, String pageDrawer) {
        this(workspace, name, pageWidth, pageHeight, pageDrawer, true, null, true);
    }
	
    public OB_Page(Workspace workspace, String name) {
        this(workspace, name, -1, -1, name);
    }

	
    //////////////////////////
    //SAVING AND LOADING	//
    //////////////////////////
	@Override
    public ArrayList<RenderableBlock> loadPageFrom(Node pageNode, boolean importingPage) {
        //note: this code is duplicated in BlockCanvas.loadSaveString().
        NodeList pageChildren = pageNode.getChildNodes();
        Node pageChild;
        ArrayList<RenderableBlock> loadedBlocks = new ArrayList<RenderableBlock>();
        HashMap<Long, Long> idMapping = importingPage ? new HashMap<Long, Long>() : null;
        if (importingPage) {
            reset();
        }
        for (int i = 0; i < pageChildren.getLength(); i++) {
            pageChild = pageChildren.item(i);
            if (pageChild.getNodeName().equals("PageBlocks")) {
                NodeList blocks = pageChild.getChildNodes();
                Node blockNode;
                for (int j = 0; j < blocks.getLength(); j++) {
                    blockNode = blocks.item(j);
                    RenderableBlock rb = OB_RenderableBlock.loadBlockNode(workspace, blockNode, this, idMapping);
                    // save the loaded blocks to add later
                    loadedBlocks.add(rb);
                }
                break;  //should only have one set of page blocks
            }
        }
        return loadedBlocks;
    }
	


}
