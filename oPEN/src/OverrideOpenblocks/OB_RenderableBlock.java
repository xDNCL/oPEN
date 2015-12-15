package OverrideOpenblocks;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.codeblocks.BlockConnectorShape;
import edu.mit.blocks.codeblocks.BlockLink;
import edu.mit.blocks.codeblocks.BlockLinkChecker;
import edu.mit.blocks.renderable.BlockUtilities;
import edu.mit.blocks.renderable.Comment;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.BlockCanvas;
import edu.mit.blocks.workspace.ContextMenu;
import edu.mit.blocks.workspace.MiniMap;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceWidget;

public class OB_RenderableBlock extends RenderableBlock{

	private static final int POS_LEFT = 200; // 複製元ブロックを基準とした、複製先ブロックの相対位置
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
    
    // 2015/10/28 N.Inaba ADD ブロック(単品)の複製 
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            dragHandler.mouseClicked(e);
            if (e.getClickCount() == 2 && !dragging) {
                workspace.notifyListeners(new WorkspaceEvent(workspace, this.getParentWidget(), this.getBlockID(), WorkspaceEvent.BLOCK_STACK_COMPILED));
            }
        }
    }

    // 2015/10/28 N.Inaba ADD ブロック(単品)の複製
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

    // 2015/10/13 N.Inaba ADD ブロック(単品)の複製 
    public void duplicateABlock() {
    	// 親ブロックをコピペ
    	Block orgParentBlock = this.getBlock();
    	OB_RenderableBlock parentRB = OB_BlockUtilities.cloneBlock(orgParentBlock);
    	parentRB.ignoreDefaultArguments();
    	parentRB.setLocation(this.getX() + POS_LEFT, this.getY());
    	this.getParent().add(parentRB);

    	// 親ブロックのソケットを設定
    	BlockConnector socket;
    	Iterator<BlockConnector> sockets = orgParentBlock.getSockets().iterator();
    	int bi = 0;
    	
    	// ソケットの数だけループ
    	while (sockets.hasNext()) {
    		socket = sockets.next();
    		
    		// ソケットに子ブロックがない
    		if (socket.connBlockID == Block.NULL) {
    			bi++;
    			continue;
    		}

    		// 子ブロックをコピー
    		Block orgChildBlock = workspace.getEnv().getBlock(socket.connBlockID);
    		OB_RenderableBlock childRB = OB_BlockUtilities.cloneBlock(orgChildBlock);
    		childRB.ignoreDefaultArguments();
    		Point myLocation = this.getLocation();
    		this.getConnectorTag(socket).setDimension(new Dimension(
    				childRB.getBlockWidth() - (int) BlockConnectorShape.NORMAL_DATA_PLUG_WIDTH,
    				childRB.getBlockHeight()));

    		// sumなどの大きさが変化するブロック用
    		Point2D socketPt = getSocketPixelPoint(socket);
    		Point2D plugPt = childRB.getSocketPixelPoint(childRB.getBlock().getPlug());
			childRB.setLocation((int) (socketPt.getX() + myLocation.x - plugPt.getX()) + 200, (int) (socketPt.getY() + myLocation.y - plugPt.getY()));
			
    		// 親子を接続
    		parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
    		bi++;
    		childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());

    		if (childRB.getBlock().hasPlug()) {
    			childRB = duplicateABlock(orgChildBlock, childRB);
    		}

    		// 子ブロックをペースト
    		getParentWidget().addBlock(childRB);
    	}
    }
    
    // 2015/12/09 N.Inaba ADD ブロック(群)の複製 親子接続(再帰)
    public OB_RenderableBlock duplicateABlock(Block orgParentBlock, OB_RenderableBlock parentRB) {
    	// 親ブロックのソケットを設定
    	BlockConnector socket;
    	Iterator<BlockConnector> sockets = orgParentBlock.getSockets().iterator();
    	int bi = 0;
    	
    	// ソケットの数だけループ
    	while (sockets.hasNext()) {
    		socket = sockets.next();
    		
    		// ソケットに子ブロックがない
    		if (socket.connBlockID == Block.NULL) {
    			bi++;
    			continue;
    		}

    		// 子ブロックをコピー
    		Block orgChildBlock = workspace.getEnv().getBlock(socket.connBlockID);
    		OB_RenderableBlock childRB = OB_BlockUtilities.cloneBlock(orgChildBlock);
    		childRB.ignoreDefaultArguments();

    		workspace.getEnv().getRenderableBlock(orgParentBlock.getBlockID()).getConnectorTag(socket).setDimension(new Dimension(
    				childRB.getBlockWidth() - (int) BlockConnectorShape.NORMAL_DATA_PLUG_WIDTH,
    				childRB.getBlockHeight()));

    		// 親子を接続
    		parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
    		bi++;
    		childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());
    		
    		if (childRB.getBlock().hasPlug()) {
    			childRB = duplicateABlock(orgChildBlock, childRB);
    		}

    		// 子ブロックをペースト
    		getParentWidget().addBlock(childRB);
    	}
    	return parentRB;
    }
    
    // 2015/10/13 N.Inaba ADD ブロック(単品)の複製 
    public void putOnTheShelf() {
    	// ここでShelfのFrameに対象ブロックをコピーする (workspace = ob_ws_shelf)
    	OB_RenderableBlock newRB = OB_BlockUtilities.cloneBlockToShelf(workspace.getEnv().getBlock(this.getBlockID()));
    	newRB.ignoreDefaultArguments();
    	newRB.setLocation(0, 0);
    	
//    	OB_WorkspaceController.ob_ws_shelf.addToBlockLayer(newRB);
    	OB_WorkspaceController.shelf_page.addBlock(newRB);
    	OB_WorkspaceController.ob_ws_shelf.repaint();
    	OB_WorkspaceController.ob_ws_shelf.getBlockCanvas().arrangeAllBlocks();
    }
    
    // 2015/10/29 N.Inaba ADD defaultArgをOB_Block型に
    @Override
    public Long getBlockID() {
        return blockID;
    }
    
    // 2015/10/29 N.Inaba ADD defaultArgをOB_Block型に
    public void linkDefArgs() {
        if (!linkedDefArgsBefore && getBlock().hasDefaultArgs()) {
            Iterator<Long> ids = getBlock().linkAllDefaultArgs().iterator();
            Iterator<BlockConnector> sockets = getBlock().getSockets().iterator();
            Long id;
            BlockConnector socket;

            // Store the ids, sockets, and blocks we need to update.
            List<Long> idList = new ArrayList<Long>();
            List<BlockConnector> socketList = new ArrayList<BlockConnector>();
           
            // 2015/10/29 N.Inaba MOD defaultArgをOB_Block型に
            List<OB_RenderableBlock> argList = new ArrayList<OB_RenderableBlock>();
            
            while (ids.hasNext() && sockets.hasNext()) {
                id = ids.next();
                socket = sockets.next();
                if (id != OB_Block.NULL) {
                    //for each block id, create a new RenderableBlock
                    OB_RenderableBlock arg = new OB_RenderableBlock(workspace, this.getParentWidget(), id, false);
                    arg.setZoomLevel(this.zoom);
                    //getParentWidget().addBlock(arg);
                    //arg.repaint();
                    //this.getParent().add(arg);
                    //set the location of the def arg at
                    Point myLocation = getLocation();
                    Point2D socketPt = getSocketPixelPoint(socket);
                    Point2D plugPt = arg.getSocketPixelPoint(arg.getBlock().getPlug());
                    arg.setLocation((int) (socketPt.getX() + myLocation.x - plugPt.getX()), (int) (socketPt.getY() + myLocation.y - plugPt.getY()));
                    //update the socket space of at this socket
                    this.getConnectorTag(socket).setDimension(new Dimension(
                            arg.getBlockWidth() - (int) BlockConnectorShape.NORMAL_DATA_PLUG_WIDTH,
                            arg.getBlockHeight()));
                    //drop each block to this parent's widget/component
                    //getParentWidget().blockDropped(arg);
                    
                    // 2015/10/29 N.Inaba MOD begin defaultArgをOB_Block型に
                    getParentWidget().addBlock(arg);
                    
//                    this.getParent().add(arg);

                    idList.add(id);
                    socketList.add(socket);
                    argList.add(arg);
                }
            }

            int size = idList.size();
            for (int i = 0; i < size; i++) {
                workspace.notifyListeners(
                        new WorkspaceEvent(workspace, this.getParentWidget(),
                        argList.get(i).getBlockID(),
                        WorkspaceEvent.BLOCK_ADDED, true));

                //must call this method to update the dimensions of this
                //TODO ria in the future would be good to just link the default args
                //but first creating a block link object and then connecting
                //something like notifying the renderableblock to update its dimensions will be
                //take care of
                this.blockConnected(socketList.get(i), idList.get(i));
                argList.get(i).repaint();
            }
            this.redrawFromTop();
            linkedDefArgsBefore = true;
        }
    }
    
    // 2015/11/02 N.Inaba ADD defaultArgをOB_Block型に
    public static void stopDragging(OB_RenderableBlock renderable, WorkspaceWidget widget) {
        if (!renderable.dragging) {
            throw new RuntimeException("dropping without prior dragging?");
        }
        //notify children
        for (BlockConnector socket : BlockLinkChecker.getSocketEquivalents(renderable.getBlock())) {
            if (socket.hasBlock()) {
                stopDragging(renderable.getWorkspace().getEnv().getRenderableBlock(socket.getBlockID()), widget);
            }
        }
        // drop this block on its widget (if w is null it'll throw an exception)
        widget.blockDropped(renderable);
        // stop rendering as transparent
        renderable.dragging = false;
        //move comment
        if (renderable.hasComment()) {
            if (renderable.getParentWidget() != null) {
                renderable.comment.setParent(renderable.getParentWidget().getJComponent(), 0);
            } else {
                renderable.comment.setParent(null, renderable.getBounds());
            }

            renderable.comment.setConstrainComment(true);
            renderable.comment.setLocation(renderable.comment.getLocation());
            renderable.comment.getArrow().updateArrow();
        }
    }

    // 2015/11/02 N.Inaba ADD defaultArgをOB_Block型に
    private void drag(OB_RenderableBlock renderable, int dx, int dy, WorkspaceWidget widget, boolean isTopLevelBlock) {
        if (!renderable.pickedUp) {
            throw new RuntimeException("dragging without prior pickup");
        }
        //mark this as being dragged
        renderable.dragging = true;
        // move the block by drag amount
        if (!isTopLevelBlock) {
            renderable.setLocation(renderable.getX() + dx, renderable.getY() + dy);
        }
        // send blockEntered/blockExited/blogDragged as appropriate
        if (widget != null) {
            if (!widget.equals(renderable.lastDragWidget)) {
                widget.blockEntered(renderable);
                if (renderable.lastDragWidget != null) {
                    renderable.lastDragWidget.blockExited(renderable);
                }
            }
            widget.blockDragged(renderable);
            renderable.lastDragWidget = widget;
        }

        // translate highlight along with the block - this would happen automatically,
        // but putting the call here takes out any lag.
        renderable.highlighter.repaint();
        // Propagate the drag event to anything plugged into this block
        for (BlockConnector socket : BlockLinkChecker.getSocketEquivalents(renderable.getBlock())) {
            if (socket.hasBlock()) {
                drag(workspace.getEnv().getRenderableBlock(socket.getBlockID()), dx, dy, widget, false);
            }
        }
    }

}
