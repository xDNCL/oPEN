package OverrideOpenblocks;

import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockLink;
import edu.mit.blocks.renderable.BlockUtilities;
import edu.mit.blocks.renderable.Comment;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.ContextMenu;
import edu.mit.blocks.workspace.MiniMap;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
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
    
    // 2015/10/28 N.Inaba ADD begin コピーブロック
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            dragHandler.mouseClicked(e);
            if (e.getClickCount() == 2 && !dragging) {
                workspace.notifyListeners(new WorkspaceEvent(workspace, this.getParentWidget(), this.getBlockID(), WorkspaceEvent.BLOCK_STACK_COMPILED));
            }
        }
    }

    // 2015/10/28 N.Inaba ADD begin コピーブロック
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (pickedUp) {
                dragHandler.mouseReleased(e);

                //if the block was dragged before...then
                if (dragging) {
                    BlockLink link = getNearbyLink(); //look for nearby link opportunities
                    WorkspaceWidget widget = null;

                    // if a suitable link wasn't found, just drop the block
                    if (link == null) {
                        widget = lastDragWidget;
                        stopDragging(this, widget);
                    } // otherwise, if a link WAS found...
                    else {

                        /* Make sure that no matter who's connecting to whom, the block
                        * that's being dragged gets dropped on the parent widget of the
                        * block that's already on the canvas.
                        */
                        if (blockID.equals(link.getSocketBlockID())) {
                            // dragged block is the socket block, so take plug's parent.
                            widget = workspace.getEnv().getRenderableBlock(link.getPlugBlockID()).getParentWidget();
                        } else {
                            // dragged block is the plug block, so take the socket block's parent.
                            widget = workspace.getEnv().getRenderableBlock(link.getSocketBlockID()).getParentWidget();
                        }

                        // drop the block and connect its link
                        stopDragging(this, widget);
                        link.connect();
                        workspace.notifyListeners(new WorkspaceEvent(workspace, widget, link, WorkspaceEvent.BLOCKS_CONNECTED));
                        workspace.getEnv().getRenderableBlock(link.getSocketBlockID()).moveConnectedBlocks();
                    }

                    //set the locations for X and Y based on zoom at 1.0
                    this.unzoomedX = this.calculateUnzoomedX(this.getX());
                    this.unzoomedY = this.calculateUnzoomedY(this.getY());

                    workspace.notifyListeners(new WorkspaceEvent(workspace, widget, link, WorkspaceEvent.BLOCK_MOVED, true));
                    if (widget instanceof MiniMap) {
                        workspace.getMiniMap().animateAutoCenter(this);
                    }
                }
            }            
        }
        pickedUp = false;
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e) || e.isControlDown()) {
            //add context menu at right click location to provide functionality
            //for adding new comments and removing comments
            PopupMenu popup = OB_ContextMenu.getContextMenuFor(this);
            add(popup);
            popup.show(this, e.getX(), e.getY());
        }
        workspace.getMiniMap().repaint();
    }
    
    
    // 2015/10/13 N.Inaba ADD begin コピーブロック
    public void copyBlock() {
    	OB_RenderableBlock newRB = OB_BlockUtilities.cloneBlock(workspace.getEnv().getBlock(this.getBlockID()));
    	newRB.ignoreDefaultArguments();
    	newRB.setLocation(this.getX() + 200, this.getY());
    	this.getParent().add(newRB);
    }
    // 2015/10/13 N.Inaba ADD end
}
