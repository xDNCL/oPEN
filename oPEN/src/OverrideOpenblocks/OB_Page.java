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
import edu.mit.blocks.workspace.ISupportMemento;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.SearchableContainer;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
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
	
	// 2015/10/29 N.Inaba ADD defaultArgをOB_Block型に
	@Override
	public void addBlock(RenderableBlock block) {
//		OB_RenderableBlock block = new OB_RenderableBlock(workspace, null, pre_block.getBlockID(), false);
		
		//update parent widget if dropped block
		WorkspaceWidget oldParent = block.getParentWidget();
		if (oldParent != this) {
			if (oldParent != null) {
				oldParent.removeBlock(block);
				if (block.hasComment()) {
					block.getComment().getParent().remove(block.getComment());
				}
			}
			block.setParentWidget(this);
			if (block.hasComment()) {
				block.getComment().setParent(block.getParentWidget().getJComponent());
			}
		}

		this.getRBParent().addToBlockLayer(block);
		block.setHighlightParent(this.getRBParent());

		//if block has page labels enabled, in other words, if it can, then set page label to this
		if (workspace.getEnv().getBlock(block.getBlockID()).isPageLabelSetByPage()) {
			workspace.getEnv().getBlock(block.getBlockID()).setPageLabel(this.getPageName());
		}

		//notify block to link default args if it has any
		block.linkDefArgs();

		//fire to workspace that block was added to canvas if oldParent != this
		if (oldParent != this) {
			workspace.notifyListeners(new WorkspaceEvent(workspace, oldParent, block.getBlockID(), WorkspaceEvent.BLOCK_MOVED));
			workspace.notifyListeners(new WorkspaceEvent(workspace, this, block.getBlockID(), WorkspaceEvent.BLOCK_ADDED, true));
		}

		// if the block is off the edge, shift everything or grow as needed to fully show it
		this.reformBlockPosition(block);

		this.pageJComponent.setComponentZOrder(block, 0);
	}
	
	// 2015/12/17 N.Inaba ADD Shelfの実装 調査
	@Override
	public void blockEntered(RenderableBlock block) {
		if (workspace.toString().equals("Shelf")) {
//			System.out.println("Shelf!!");
		} else {
//			System.out.println("Canvas!");
		}
//		System.out.println("blorckEntered is called.");
		if (mouseIsInPage == false) {
			mouseIsInPage = true;
			this.pageJComponent.repaint();
		}
	}

	// 2015/12/24 N.Inaba MOD Shelfの実装 調査
	/** @overrides WorkspaceWidget.blockExited() */
	@Override
	public void blockExited(RenderableBlock block) {
		if (workspace.toString().equals("Shelf")) {
//			System.out.println("Shelf!!");
			
		} else {
//			System.out.println("Canvas!");
		}
//		System.out.println("blockExited is called.");
		mouseIsInPage = false;
		this.pageJComponent.repaint();
	}
}
