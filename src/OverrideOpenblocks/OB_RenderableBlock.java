package OverrideOpenblocks;

import java.awt.Point;
import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.Comment;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceWidget;

public class OB_RenderableBlock extends RenderableBlock{

	private static final long serialVersionUID = 1L;

	
	protected OB_RenderableBlock(Workspace workspace, WorkspaceWidget parent, Long blockID, boolean isLoading) {
		super(workspace, parent, blockID, isLoading);
	}
	
    /**
     * Loads a RenderableBlock and its related Block instance from the specified blockNode;
     * returns null if no RenderableBlock was loaded.
     * @param workspace The workspace to use
     * @param blockNode Node containing information to load into a RenderableBlock instance
     * @param parent WorkspaceWidget to contain the block to load
     * @return RenderableBlock instance holding the information in blockNode; null if no RenderableBlock loaded
     */
    public static RenderableBlock loadBlockNode(Workspace workspace, Node blockNode, WorkspaceWidget parent, HashMap<Long, Long> idMapping) {
        boolean isBlock = blockNode.getNodeName().equals("Block");
        boolean isBlockStub = blockNode.getNodeName().equals("BlockStub");
        
        System.out.println("OB_Renderable ’Ê‰ß");
        
        if (isBlock || isBlockStub) {
            RenderableBlock rb = new OB_RenderableBlock(workspace, parent, OB_Block.loadBlockFrom(workspace, blockNode, idMapping).getBlockID(), true);

            if (isBlockStub) {
                //need to get actual block node
                NodeList stubchildren = blockNode.getChildNodes();
                for (int j = 0; j < stubchildren.getLength(); j++) {
                    Node node = stubchildren.item(j);
                    if (node.getNodeName().equals("Block")) {
                        blockNode = node;
                        break;
                    }
                }
            }


            if (rb.getBlock().labelMustBeUnique()) {
                //TODO check the instance number of this block
                //and update instance checker
            }

            Point blockLoc = new Point(0, 0);
            NodeList children = blockNode.getChildNodes();
            Node child;

            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (child.getNodeName().equals("Location")) {
                    //extract location information
                    extractLocationInfo(child, blockLoc);
                } else if (child.getNodeName().equals("Comment")) {
                    rb.setComment(Comment.loadComment(workspace, child.getChildNodes(), rb));
                    if (rb.getComment() != null) {
                        rb.getComment().setParent(rb.getParentWidget().getJComponent());
                    }
                } else if (child.getNodeName().equals("Collapsed")) {
                    rb.setCollapsed(true);
                }
            }
            //set location from info
            rb.setLocation(blockLoc.x, blockLoc.y);

            if (rb.getComment() != null) {
                rb.getComment().getArrow().updateArrow();
            }


            return rb;
        }
        return null;
    }
}
