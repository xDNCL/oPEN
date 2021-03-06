package OverrideOpenblocks;

import java.awt.Container;
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

	// 2015/12/16 N.Inaba ADD ブロック群の複製 
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
			
			// 位置関係
			Point myLocation = this.getLocation();
			this.getConnectorTag(socket).setDimension(new Dimension(
					childRB.getBlockWidth() - (int) BlockConnectorShape.NORMAL_DATA_PLUG_WIDTH,
					childRB.getBlockHeight()));

			// sumなどの大きさが変わる部品の位置調整
			Point2D socketPt = getSocketPixelPoint(socket);
			Point2D plugPt = new Point(0, 0);
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				plugPt = childRB.getSocketPixelPoint(childRB.getBlock().getBeforeConnector());
			} else {
				plugPt = childRB.getSocketPixelPoint(childRB.getBlock().getPlug());
			}
			childRB.setLocation((int) (socketPt.getX() + myLocation.x - plugPt.getX()) + POS_LEFT, (int) (socketPt.getY() + myLocation.y - plugPt.getY()));
			
			// 親子を接続
			parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				childRB.getBlock().getBeforeConnector().setConnectorBlockID(parentRB.getBlockID());
			} else {
				childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());
			}
			
			// 孫を複製
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = addBrother(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = addGrandchild(orgChildBlock, childRB);
			}

			// 子ブロックをペースト
			getParentWidget().addBlock(childRB);
			bi++;
		}
	}
	
	// 2015/12/16 N.Inaba ADD ブロック(群)の複製 孫の複製(再帰)
	public OB_RenderableBlock addGrandchild(Block orgParentBlock, OB_RenderableBlock parentRB) {
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

			// 親子を接続
			parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				childRB.getBlock().getBeforeConnector().setConnectorBlockID(parentRB.getBlockID());
			} else {
				childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());
			}
			
			// 孫を複製
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = addBrother(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = addGrandchild(orgChildBlock, childRB);
			}
			
			// 子ブロックをペースト
			getParentWidget().addBlock(childRB);
			bi++;
		}
		return parentRB;
	}
	
	// 2015/12/16 N.Inaba ADD ブロック(群)の複製 孫たちの複製(再帰)
	public OB_RenderableBlock addBrother(Block orgAniBlock, OB_RenderableBlock aniRB) {
		// 子の複製
		addGrandchild(orgAniBlock, aniRB);
		
		// 弟の複製
		Block orgOtotoBlock = workspace.getEnv().getBlock(orgAniBlock.getAfterBlockID());
		OB_RenderableBlock ototoRB = OB_BlockUtilities.cloneBlock(orgOtotoBlock);
		ototoRB.ignoreDefaultArguments();
		addGrandchild(orgOtotoBlock, ototoRB);
		
		// 兄弟接続
		aniRB.getBlock().getAfterConnector().setConnectorBlockID(ototoRB.getBlockID());
		ototoRB.getBlock().getBeforeConnector().setConnectorBlockID(aniRB.getBlockID());
		
		if (orgOtotoBlock.getAfterBlockID() != Block.NULL) {
			ototoRB = addBrother(orgOtotoBlock, ototoRB);
		}
		
		// 弟ブロックをペースト
		getParentWidget().addBlock(ototoRB);
		return aniRB;
	}
	
	// 2015/12/17 N.Inaba ADD ブロック(単品)の複製
	// ほぼduplicateABlockで、ob_ws_shelfに貼り付けている部分が異なる 冗長かも
	public void putOnTheShelf() {
		// 親ブロックをコピペ
		Block orgParentBlock = this.getBlock();
		OB_RenderableBlock parentRB = OB_BlockUtilities.cloneBlockToWorkspace(orgParentBlock, OB_WorkspaceController.ob_ws_shelf);
		parentRB.ignoreDefaultArguments();
		parentRB.setLocation(0, 0);
		OB_WorkspaceController.shelf_page.addBlock(parentRB);
		OB_WorkspaceController.ob_ws_shelf.repaint();
		OB_WorkspaceController.ob_ws_shelf.getBlockCanvas().arrangeAllBlocks();

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
			OB_RenderableBlock childRB = OB_BlockUtilities.cloneBlockToWorkspace(orgChildBlock, OB_WorkspaceController.ob_ws_shelf);
			childRB.ignoreDefaultArguments();
			
			// 位置関係
			Point myLocation = this.getLocation();
			this.getConnectorTag(socket).setDimension(new Dimension(
					childRB.getBlockWidth() - (int) BlockConnectorShape.NORMAL_DATA_PLUG_WIDTH,
					childRB.getBlockHeight()));

			// sumなどの大きさが変わる部品の位置調整
			Point2D socketPt = getSocketPixelPoint(socket);
			Point2D plugPt = new Point(0, 0);
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				plugPt = childRB.getSocketPixelPoint(childRB.getBlock().getBeforeConnector());
			} else {
				plugPt = childRB.getSocketPixelPoint(childRB.getBlock().getPlug());
			}
			childRB.setLocation((int) (socketPt.getX() + myLocation.x - plugPt.getX()) + POS_LEFT, (int) (socketPt.getY() + myLocation.y - plugPt.getY()));
			
			// 親子を接続
			parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				childRB.getBlock().getBeforeConnector().setConnectorBlockID(parentRB.getBlockID());
			} else {
				childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());
			}
			
			// 孫を複製
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = addBrotherToShelf(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = addGrandchildToShelf(orgChildBlock, childRB);
			}

			// 子ブロックをペースト
			OB_WorkspaceController.shelf_page.addBlock(childRB);
			OB_WorkspaceController.ob_ws_shelf.repaint();
			OB_WorkspaceController.ob_ws_shelf.getBlockCanvas().arrangeAllBlocks();
			bi++;
		}
	}
	
	// 2015/12/09 N.Inaba ADD ブロック(群)の複製 孫の複製(再帰)
	public OB_RenderableBlock addGrandchildToShelf(Block orgParentBlock, OB_RenderableBlock parentRB) {
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
			OB_RenderableBlock childRB = OB_BlockUtilities.cloneBlockToWorkspace(orgChildBlock, OB_WorkspaceController.ob_ws_shelf);
			childRB.ignoreDefaultArguments();

			// 親子を接続
			parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				childRB.getBlock().getBeforeConnector().setConnectorBlockID(parentRB.getBlockID());
			} else {
				childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());
			}
			
			// 孫を複製
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = addBrotherToShelf(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = addGrandchildToShelf(orgChildBlock, childRB);
			}
			
			// 子ブロックをペースト
			OB_WorkspaceController.shelf_page.addBlock(childRB);
			OB_WorkspaceController.ob_ws_shelf.repaint();
			OB_WorkspaceController.ob_ws_shelf.getBlockCanvas().arrangeAllBlocks();
			bi++;
		}
		return parentRB;
	}
	
	// 2015/12/16 N.Inaba ADD ブロック(群)の複製 孫たちの複製(再帰)
	public OB_RenderableBlock addBrotherToShelf(Block orgAniBlock, OB_RenderableBlock aniRB) {
		// 子の複製
		addGrandchildToShelf(orgAniBlock, aniRB);
		
		// 弟の複製
		Block orgOtotoBlock = workspace.getEnv().getBlock(orgAniBlock.getAfterBlockID());
		OB_RenderableBlock ototoRB = OB_BlockUtilities.cloneBlockToWorkspace(orgOtotoBlock, OB_WorkspaceController.ob_ws_shelf);
		ototoRB.ignoreDefaultArguments();
		addGrandchildToShelf(orgOtotoBlock, ototoRB);
		
		// 兄弟接続
		aniRB.getBlock().getAfterConnector().setConnectorBlockID(ototoRB.getBlockID());
		ototoRB.getBlock().getBeforeConnector().setConnectorBlockID(aniRB.getBlockID());
		
		if (orgOtotoBlock.getAfterBlockID() != Block.NULL) {
			ototoRB = addBrotherToShelf(orgOtotoBlock, ototoRB);
		}
		
		// 弟ブロックをペースト
		OB_WorkspaceController.shelf_page.addBlock(ototoRB);
		OB_WorkspaceController.ob_ws_shelf.repaint();
		OB_WorkspaceController.ob_ws_shelf.getBlockCanvas().arrangeAllBlocks();
		return aniRB;
	}
	
	// 2015/12/18 N.Inaba ADD Shelfの実装
	// ほぼduplicateABlockで、ob_ws_shelfに貼り付けている部分が異なる 冗長かも
	public void putOnWorkspace() {
		// 親ブロックをコピペ
		Block orgParentBlock = this.getBlock();
		OB_RenderableBlock parentRB = OB_BlockUtilities.cloneBlockToWorkspace(orgParentBlock, OB_WorkspaceController.workspace);
		parentRB.ignoreDefaultArguments();
		
		parentRB.setLocation(POS_LEFT, POS_LEFT);
		
		// 調査
//		System.out.println("x: " + parentRB.getX() + "y: " + parentRB.getY());
		
		OB_WorkspaceController.workspace.getPageNamed("oPEN").addBlock(parentRB);
		OB_WorkspaceController.workspace.repaint();
		
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
			OB_RenderableBlock childRB = OB_BlockUtilities.cloneBlockToWorkspace(orgChildBlock, OB_WorkspaceController.workspace);
			childRB.ignoreDefaultArguments();

			// 2016/02/12 N.Inaba DEL Shelfの実装
//			System.out.println("調整前 x: " + childRB.getX() + "y: " + childRB.getY());
//			// 位置関係
//			Point myLocation = this.getLocation();
//			this.getConnectorTag(socket).setDimension(new Dimension(
//					childRB.getBlockWidth() - (int) BlockConnectorShape.NORMAL_DATA_PLUG_WIDTH,
//					childRB.getBlockHeight()));
//
//			// sumなどの大きさが変わる部品の位置調整
//			Point2D socketPt = getSocketPixelPoint(socket);
//			Point2D plugPt = new Point(0, 0);
//			
//			// 親ブロックが条件分岐などの場合
//			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
//				plugPt = childRB.getSocketPixelPoint(childRB.getBlock().getBeforeConnector());
//			} else {
//				plugPt = childRB.getSocketPixelPoint(childRB.getBlock().getPlug());
//			}
//			childRB.setLocation((int) (socketPt.getX() + myLocation.x - plugPt.getX()) + POS_LEFT, (int) (socketPt.getY() + myLocation.y - plugPt.getY()) + POS_LEFT);

			parentRB.moveConnectedBlocks();
			parentRB.validate();
			parentRB.repaint();
			childRB.moveConnectedBlocks();
			childRB.validate();
			childRB.repaint();
			
			// 親子を接続
			parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				childRB.getBlock().getBeforeConnector().setConnectorBlockID(parentRB.getBlockID());
			} else {
				childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());
			}
			
			// 孫を複製
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = addBrotherToWorkspace(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = addGrandchildToWorkspace(orgChildBlock, childRB);
			}

//			System.out.println("調整後 x: " + childRB.getX() + "y: " + childRB.getY());
			
			// 子ブロックをペースト
			OB_WorkspaceController.workspace.getPageNamed("oPEN").addBlock(childRB);
			OB_WorkspaceController.workspace.repaint();
			
			// 整数<x>の親子の表示がずれる
			Container parent = parentRB.getParent();
			if (parent != null) {
				parentRB.moveConnectedBlocks(); // これで直った
				parent.validate();
				parent.repaint();
			}
			bi++;
		}
	}
	
	// 2015/12/09 N.Inaba ADD ブロック(群)の複製 孫の複製(再帰)
	public OB_RenderableBlock addGrandchildToWorkspace(Block orgParentBlock, OB_RenderableBlock parentRB) {
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
			OB_RenderableBlock childRB = OB_BlockUtilities.cloneBlockToWorkspace(orgChildBlock, OB_WorkspaceController.workspace);
			childRB.ignoreDefaultArguments();

			// 親子を接続
			parentRB.getBlock().getSocketAt(bi).setConnectorBlockID(childRB.getBlockID());
			// 親ブロックが条件分岐などの場合
			if (orgParentBlock.getSocketAt(bi).getKind().equals("cmd")) {
				childRB.getBlock().getBeforeConnector().setConnectorBlockID(parentRB.getBlockID());
			} else {
				childRB.getBlock().getPlug().setConnectorBlockID(parentRB.getBlockID());
			}
			
			// 孫を複製
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = addBrotherToWorkspace(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = addGrandchildToWorkspace(orgChildBlock, childRB);
			}
			
			// 子ブロックをペースト
			OB_WorkspaceController.workspace.getPageNamed("oPEN").addBlock(childRB);
			OB_WorkspaceController.workspace.repaint();
			bi++;
		}
		return parentRB;
	}
	
	// 2015/12/16 N.Inaba ADD ブロック(群)の複製 孫たちの複製(再帰)
	public OB_RenderableBlock addBrotherToWorkspace(Block orgAniBlock, OB_RenderableBlock aniRB) {
		// 子の複製
		addGrandchildToWorkspace(orgAniBlock, aniRB);
		
		// 弟の複製
		Block orgOtotoBlock = workspace.getEnv().getBlock(orgAniBlock.getAfterBlockID());
		OB_RenderableBlock ototoRB = OB_BlockUtilities.cloneBlockToWorkspace(orgOtotoBlock, OB_WorkspaceController.workspace);
		ototoRB.ignoreDefaultArguments();
		addGrandchildToWorkspace(orgOtotoBlock, ototoRB);
		
		// 兄弟接続
		aniRB.getBlock().getAfterConnector().setConnectorBlockID(ototoRB.getBlockID());
		ototoRB.getBlock().getBeforeConnector().setConnectorBlockID(aniRB.getBlockID());
		
		if (orgOtotoBlock.getAfterBlockID() != Block.NULL) {
			ototoRB = addBrotherToWorkspace(orgOtotoBlock, ototoRB);
		}
		
		// 弟ブロックをペースト
		OB_WorkspaceController.workspace.getPageNamed("oPEN").addBlock(ototoRB);
		OB_WorkspaceController.workspace.repaint();
		return aniRB;
	}
	
	// 2015/12/28 N.Inaba ADD ブロック群の削除
	public void deleteABlock() {
   	 	// Pageの設定
		Container parent = this.getParent();
		
		// 親ブロックを設定
		OB_RenderableBlock parentRB = this;
		Block orgParentBlock = parentRB.getBlock();
		
		// 親ブロックのソケットを設定
		BlockConnector socket;
		Iterator<BlockConnector> sockets = orgParentBlock.getSockets().iterator();
		// ソケットの数だけループ
		while (sockets.hasNext()) {
			socket = sockets.next();
			
			// ソケットに子ブロックがない
			if (socket.connBlockID == Block.NULL) {
				continue;
			}

			// 子ブロックを設定
			RenderableBlock childRB = workspace.getEnv().getRenderableBlock(socket.connBlockID);
			Block orgChildBlock = childRB.getBlock();
			
			// 孫を削除
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = deleteBrother(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = deleteGrandchild(orgChildBlock, childRB);
			}
			if (parent != null) {
				parent.remove(childRB);
				parent.validate();
				parent.repaint();
				this.setParentWidget(null);
			}
		}
		
		// TODO 親に親がいる場合
		if (parentRB.getBlock().getPlugBlockID() != Block.NULL) {
			System.out.println("親の親がいる！");
		}
		
		
		WorkspaceWidget oldParent = parentRB.getParentWidget();
		if (oldParent != null) {
			oldParent.removeBlock(parentRB);
		}
		if (parent != null) {
			parent.remove(this);
			parent.validate();
			parent.repaint();
			this.setParentWidget(null);
		}
	}
	
	// 2015/12/28 N.Inaba ADD ブロック群の削除 孫の削除(再帰)
	public RenderableBlock deleteGrandchild(Block orgParentBlock, RenderableBlock parentRB) {
		// 親ブロックのソケットを設定
		BlockConnector socket;
		Iterator<BlockConnector> sockets = orgParentBlock.getSockets().iterator();
		
		// ソケットの数だけループ
		while (sockets.hasNext()) {
			socket = sockets.next();
			
			// ソケットに子ブロックがない
			if (socket.connBlockID == Block.NULL) {
				continue;
			}

			// 子ブロックを設定
			RenderableBlock childRB = workspace.getEnv().getRenderableBlock(socket.connBlockID);
			Block orgChildBlock = childRB.getBlock();
			
			// 孫を削除
			if(orgChildBlock.getAfterBlockID() != Block.NULL) {
				childRB = deleteBrother(orgChildBlock, childRB);
			}else if (orgChildBlock.getNumSockets() > 0) {
				childRB = deleteGrandchild(orgChildBlock, childRB);
			}
			
	   	 	//remove block
			WorkspaceWidget oldParent = childRB.getParentWidget();
			if (oldParent != null) {
				oldParent.removeBlock(childRB);
			}
			Container parent = this.getParent();
			if (parent != null) {
				parent.remove(childRB);
				parent.validate();
				parent.repaint();
				this.setParentWidget(null);
			}
		}
		return parentRB;
	}
	
	// 2015/12/28 N.Inaba ADD ブロック群の削除 孫たちの削除(再帰)
	public RenderableBlock deleteBrother(Block orgAniBlock, RenderableBlock aniRB) {
		// 子の削除
		deleteGrandchild(orgAniBlock, aniRB);
		
		// 弟の設定
		RenderableBlock ototoRB = workspace.getEnv().getRenderableBlock(orgAniBlock.getAfterBlockID());
		Block orgOtotoBlock = ototoRB.getBlock();
		deleteGrandchild(orgOtotoBlock, ototoRB);
		
		if (orgOtotoBlock.getAfterBlockID() != Block.NULL) {
			ototoRB = deleteBrother(orgOtotoBlock, ototoRB);
		}
		
		//remove block
		WorkspaceWidget oldParent = ototoRB.getParentWidget();
		if (oldParent != null) {
			oldParent.removeBlock(ototoRB);
		}
		Container parent = this.getParent();
		if (parent != null) {
			parent.remove(aniRB);
			parent.remove(ototoRB);
			parent.validate();
			parent.repaint();
			this.setParentWidget(null);
		}
		return aniRB;
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
					
//					this.getParent().add(arg);

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
		
		// 2015/12/17 N.Inaba ADD Shelfの実装 調査
		if (renderable.getWorkspace().toString().equals("Shelf")) {
			renderable.putOnTheShelf();
			renderable.putOnWorkspace();
		}
		
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
}
