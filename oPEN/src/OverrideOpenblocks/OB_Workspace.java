package OverrideOpenblocks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mit.blocks.renderable.BlockUtilities;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.typeblocking.FocusTraversalManager;
import edu.mit.blocks.workspace.typeblocking.TypeBlockManager;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblockutil.Explorer;
import edu.mit.blocks.codeblockutil.ExplorerEvent;
import edu.mit.blocks.codeblockutil.ExplorerListener;
import edu.mit.blocks.workspace.*;

//import edu.mit.blocks.workspace.BlockCanvas;
//import edu.mit.blocks.workspace.FactoryManager;


/**
 * The Workspace is the main block area, where blocks are manipulated and assembled.
 * This class governs the blocks, the world, the view, drawing, dragging, animating.
 */
public class OB_Workspace extends Workspace {
	
		private boolean CUSTOM = true;
	
		private static final long serialVersionUID = 328149080422L;
		
		// the environment wrapps all the components of a workspace (Blocks, RenderableBlocks, BlockStubs, BlockGenus)
		protected static final OB_WorkspaceEnvironment env = new OB_WorkspaceEnvironment();
		
		// 2015/11/11 N.Inaba ADD Shelfの実装
		private boolean is_shelf = false;
		
		// 2016/02/05 N.Inaba ADD drawerUI改善
		JSplitPane blockListLayer;
		
		public OB_Workspace getWorkspace() {
			return this;
		}
		
		@Override
		public OB_WorkspaceEnvironment getEnv() {
			return this.env;
		}
		
		// 2015/11/11 N.Inaba ADD Shelfの実装 protectedに
		protected OB_FactoryManager factory;
		
		protected OB_Workspace(){
		super();
		super.blockCanvas = new OB_BlockCanvas(this);
		
		setLayout(null);
		setBackground(Color.yellow);
		setPreferredSize(new Dimension(1000, 600));

		
		this.factory = new OB_FactoryManager(this);
		super.factory = this.factory;
		
		
		this.addWorkspaceListener(this.factory);
		blockCanvas.getHorizontalModel().addChangeListener(this);
		for (final Explorer exp : factory.getNavigator().getExplorers()) {
			exp.addListener(this);
		}

		this.miniMap = new MiniMap(this);
		this.addWidget(this.miniMap, true, true);
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				miniMap.repositionMiniMap();
				blockCanvas.reformBlockCanvas();
				blockCanvasLayer.setSize(getSize());
				blockCanvasLayer.validate();
			}
		});

		// 2016/02/23 N.Inaba DEL 配布用コメントアウト
		// 2016/02/05 N.Inaba MOD drawerUI改善
		blockCanvasLayer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, factory.getJComponent(), blockCanvas.getJComponent());
//		blockListLayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		// drawer一覧
//		blockListLayer.setLeftComponent(factory.getJComponent());
//		blockListLayer.setRightComponent(new JPanel());
//		blockListLayer.setOneTouchExpandable(false);
//		blockListLayer.setDividerSize(6);
//		// キャンバスと合体
//		blockCanvasLayer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, blockListLayer, blockCanvas.getJComponent());
//		blockCanvasLayer.setOneTouchExpandable(true);
//		blockCanvasLayer.setDividerSize(6);
		
		add(blockCanvasLayer, BLOCK_LAYER);
		
		validate();
		addPageAt(Page.getBlankPage(this), 0, false);

		this.workspaceWidgets.add(factory);
		

		
	}
		
		// 2015/11/11 N.Inaba ADD Shelfの実装
		public OB_Workspace(boolean is_shelf) {
			super();
			super.blockCanvas = new OB_BlockCanvas(this);
			this.is_shelf = is_shelf;
			
			setLayout(null);
			setBackground(Color.yellow);
			setPreferredSize(new Dimension(300, 600));

			this.factory = new OB_FactoryManager(this);
			super.factory = this.factory;
			
			
			this.addWorkspaceListener(this.factory);
			blockCanvas.getHorizontalModel().addChangeListener(this);
			for (final Explorer exp : factory.getNavigator().getExplorers()) {
				exp.addListener(this);
			}

			this.miniMap = new MiniMap(this);
//			this.addWidget(this.miniMap, true, true);
			this.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
//					miniMap.repositionMiniMap();
					blockCanvas.reformBlockCanvas();
					blockCanvasLayer.setSize(getSize());
//					blockCanvasLayer.setSize(300, 500);
					blockCanvasLayer.validate();
				}
			});

			// 2015/11/25 N.Inaba MOD Shelfの実装 不要なDrawer群用パネルの不可視化
			// 2016/12/17 N.Inaba MOD Shelfの実装 ワークスペースに書き出すために復活
			blockCanvasLayer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, factory.getJComponent(), blockCanvas.getJComponent());
//			blockCanvasLayer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, blockCanvas.getJComponent(), null);
			
			blockCanvasLayer.setOneTouchExpandable(false);
			blockCanvasLayer.setDividerSize(0);
			
			// スライダー位置調整 loc = 0だとブロックが消えてくれない
			int loc = blockCanvasLayer.getMaximumDividerLocation();
			blockCanvasLayer.setDividerLocation(loc);
			
			add(blockCanvasLayer, BLOCK_LAYER);
//			add(blockCanvas.getJComponent(), BLOCK_LAYER);
			validate();

			// 2015/11/25 N.Inaba MOD Shelfの実装 Shelfにpageをadd
//			addPageAt(Page.getBlankPage(this), 0, false);
//			OB_WorkspaceController.shelf_page = new OB_Page(this, "Shelf");
			OB_WorkspaceController.shelf_page = new OB_Page(this, "Shelf", 600, 0, null, true, new Color(255,255,204), true);

			addPageAt(OB_WorkspaceController.shelf_page, 0, false);
			
			this.workspaceWidgets.add(factory);
		}

		// 2016/02/05 N.Inaba ADD drawerUIの改善
		public OB_Workspace(int a) {
			super();
			super.blockCanvas = new OB_BlockCanvas(this);
			
			setLayout(null);
			setBackground(Color.yellow);
			setPreferredSize(new Dimension(300, 600));

			this.factory = new OB_FactoryManager(this);
			super.factory = this.factory;
			
			this.addWorkspaceListener(this.factory);
			blockCanvas.getHorizontalModel().addChangeListener(this);
			for (final Explorer exp : factory.getNavigator().getExplorers()) {
				exp.addListener(this);
			}

			this.miniMap = new MiniMap(this);
//			this.addWidget(this.miniMap, true, true);
			this.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
//					miniMap.repositionMiniMap();
					blockCanvas.reformBlockCanvas();
					blockCanvasLayer.setSize(getSize());
//					blockCanvasLayer.setSize(300, 500);
					blockCanvasLayer.validate();
				}
			});

			blockCanvasLayer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, blockCanvas.getJComponent(), factory.getJComponent());
			blockCanvasLayer.setOneTouchExpandable(false);
			blockCanvasLayer.setDividerSize(0);
			
			// スライダー位置調整 loc = 0だとブロックが消えてくれない
			int loc = blockCanvasLayer.getMaximumDividerLocation();
			blockCanvasLayer.setDividerLocation(loc);
			
			add(blockCanvasLayer, BLOCK_LAYER);
			validate();

			OB_WorkspaceController.blocklist_page = new OB_Page(this, "Blocklist", 600, 0, null, true, new Color(255,255,204), true);

			addPageAt(OB_WorkspaceController.blocklist_page, 0, false);
			
			this.workspaceWidgets.add(factory);
		}
		
		/**
		 * Loads the workspace with the following content:
		 * - RenderableBlocks and their associated Block instances that reside
		 *   within the BlockCanvas
		 * @param newRoot the XML Element containing the new desired content.  Some of the
		 * content in newRoot may override the content in originalLangRoot.  (For now,
		 * pages are automatically overwritten.  In the future, will allow drawers
		 * to be optionally overriden or new drawers to be inserted.)
		 * @param originalLangRoot the original language/workspace specification content
		 * @requires originalLangRoot != null
		 */
		@Override
		public void loadWorkspaceFrom(Element newRoot, Element originalLangRoot) {
			if (newRoot != null) {
				//load pages, page drawers, and their blocks from save file
				blockCanvas.loadSaveString(newRoot);
				//load the block drawers specified in the file (may contain
				//custom drawers) and/or the lang def file if the contents specify
//				OB_PageDrawerLoadingUtils.loadBlockDrawerSets(this, originalLangRoot, factory);
				OB_PageDrawerLoadingUtils.loadBlockDrawerSets(this, newRoot, factory);
				loadWorkspaceSettings(newRoot);
			} else {
				//load from original language/workspace root specification
				blockCanvas.loadSaveString(originalLangRoot);
				//load block drawers and their content
//				OB_PageDrawerLoadingUtils.loadBlockDrawerSets(this, originalLangRoot, factory);
				loadWorkspaceSettings(originalLangRoot);
			}
		}
		
		protected void loadWorkspaceFrom(Element newRoot, Element originalLangRoot, Element blockDrawerRoot){
			loadWorkspaceFrom(newRoot, originalLangRoot);
						
			if(newRoot != null){
				OB_PageDrawerLoadingUtils.loadBlockDrawerSets(this, blockDrawerRoot, factory);
				OB_PageDrawerLoadingUtils.loadBlockDrawerSets(this, newRoot, factory);
			}else{
				OB_PageDrawerLoadingUtils.loadBlockDrawerSets(this, blockDrawerRoot, factory);
			}
			
		}
		
		

		
		OB_FactoryManager debug;
		public void debug(){
			debug.debug();
		}
		
		

//		////////////////
//		//PAGE METHODS (note: these may change)
//		////////////////
//		/**
//		 * Adds the specified page to the Workspace at the right end of the canvas
//		 * @param page the desired page to add
//		 */
//		@Override
//		public void addPage(Page page) {
//			addPage(page, blockCanvas.numOfPages());
//		}
//
//		/**
//		 * Adds the specified page to the Workspace at the specified position on the canvas
//		 * @param page the desired page to add
//		 */
//		@Override
//		public void addPage(Page page, int position) {
//			//this method assumes that this addPage was a user or file loading
//			//event in which case a page added event should be thrown
//			addPageAt(page, position, true);
//		}
//
//		/**
//		 * Places the specified page at the specified index.
//		 * If a page already exists at that index,
//		 * this method will replace it.
//		 * @param page the Page to place
//		 * @param position - the position to place the specified page,
//		 * 		  where 0 is the leftmost page
//		 */
//		@Override
//		public void putPage(Page page, int position) {
//			if (blockCanvas.hasPageAt(position)) {
//				removePageAt(position);
//			}
//			addPageAt(page, blockCanvas.numOfPages(), true);
//		}
//
//		/**
//		 * Adds a Page in the specified position, where position 0 is the leftmost page
//		 * @param page - the desired Page to add
//		 * @param index - the desired position of the page
//		 * @param fireWorkspaceEvent if set to true, will fire a WorkspaceEvent that a
//		 * Page was added
//		 */
//		@Override
//		protected void addPageAt(Page page, int index, boolean fireWorkspaceEvent) {
//			blockCanvas.addPage(page, index);
//			workspaceWidgets.add(page);
//			if (fireWorkspaceEvent) {
//				notifyListeners(new WorkspaceEvent(this, page, WorkspaceEvent.PAGE_ADDED));
//			}
//		}
//
//
//		/**
//		 * Removes the specified page from the Workspace
//		 * @param page the desired page to remove
//		 */
//		@Override
//		public void removePage(Page page) {
//			boolean success = workspaceWidgets.remove(page);
//			if (!success) {
//				System.out.println("Page: " + page + ", was NOT removed successfully");
//			}
//			notifyListeners(new WorkspaceEvent(this, page, WorkspaceEvent.PAGE_REMOVED));
//			blockCanvas.removePage(page);
//		}
//
//		/**
//		 * Renames the page with the specified oldName to the specified newName.
//		 * @param oldName the oldName of the page to rename
//		 * @param newName the String name to change the page name to
//		 */
//		@Override
//		public void renamePage(String oldName, String newName) {
//			Page renamedPage = blockCanvas.renamePage(oldName, newName);
//			//TODO ria HACK TO GET DRAWERS AND PAGE IN SYNC
//			//as a rule, all relevant data like pages and drawers should be updated before
//			//an event is released because the listeners make assumptions on the state
//			//of the data.  in the future, have the page rename its drawer
//			factory.renameDynamicDrawer(oldName, newName);
//			notifyListeners(new WorkspaceEvent(this, renamedPage, oldName, WorkspaceEvent.PAGE_RENAMED));
//		}
//
//		/**
//		 * Returns the number of pages contained within this.  By default
//		 * will always have a page even if a page was not specified.  The page
//		 * will just be blank.
//		 * @return the number of pages contained within this
//		 */
//		@Override
//		public int getNumPages() {
//			return blockCanvas.numOfPages();
//		}
//
//		/**
//		 * Find the page that lies underneath this block
//		 * CAN RETURN NULL
//		 * @param block
//		 */
//		@Override
//		public Page getCurrentPage(RenderableBlock block) {
//			for (Page page : getBlockCanvas().getPages()) {
//				if (page.contains(SwingUtilities.convertPoint(block.getParent(), block.getLocation(), page.getJComponent()))) {
//					return page;
//				}
//			}
//			return null;
//		}
//
//		/**
//		 * Marks the page of the specified name as being selected.  The workspace
//		 * view may shift to that page.
//		 * @param page the Page selected
//		 * @param byUser true if Page was selected by the User
//		 */
//		@Override
//		public void pageSelected(Page page, boolean byUser) {
//			blockCanvas.switchViewToPage(page);
//		}
// 
		

		protected void loadBlockEducationModule(){
			
			
			
		}
		
		
		@Override
		public String toString(){
//			return "m9(^Д^)";
			if (is_shelf) {
				return "Shelf";
			} else {
				return "oPEN";
			}
		}

		
		
}

