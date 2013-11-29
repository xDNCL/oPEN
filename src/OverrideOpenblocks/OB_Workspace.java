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
	    
//	    private OB_FactoryManager factory;
	    
	    protected BlockCanvas blockcanvas = new BlockCanvas(this);
	    
	    
	    private OB_FactoryManager factory;
	    
	    protected OB_Workspace(){
        super();
        
 
        
	    setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 600));

        
        this.factory = new OB_FactoryManager(this);
        super.factory = this.factory;
        
        
        this.addWorkspaceListener(this.factory);
        this.blockCanvas.getHorizontalModel().addChangeListener(this);
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

        blockCanvasLayer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                factory.getJComponent(), blockCanvas.getJComponent());

        
        blockCanvasLayer.setOneTouchExpandable(true);
        blockCanvasLayer.setDividerSize(6);
        add(blockCanvasLayer, BLOCK_LAYER);
        
//        add(blockCanvas.getJComponent(), BLOCK_LAYER);
        
        validate();
        addPageAt(Page.getBlankPage(this), 0, false);

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
//	            PageDrawerLoadingUtils.loadBlockDrawerSets(this, originalLangRoot, factory);
//	            PageDrawerLoadingUtils.loadBlockDrawerSets(this, newRoot, factory);
	            loadWorkspaceSettings(newRoot);
	        } else {
	            //load from original language/workspace root specification
	            blockCanvas.loadSaveString(originalLangRoot);
	            //load block drawers and their content
//	            PageDrawerLoadingUtils.loadBlockDrawerSets(this, originalLangRoot, factory);
	            loadWorkspaceSettings(originalLangRoot);
	        }
	    }
	    
	    protected void loadWorkspaceFrom(Element newRoot, Element originalLangRoot, Element blockDrawerRoot){
	    	loadWorkspaceFrom(newRoot, originalLangRoot);
	    		    	
	    	if(newRoot != null){
	            PageDrawerLoadingUtils.loadBlockDrawerSets(this, blockDrawerRoot, factory);
	            PageDrawerLoadingUtils.loadBlockDrawerSets(this, newRoot, factory);
	    	}
	    	else{
	            PageDrawerLoadingUtils.loadBlockDrawerSets(this, blockDrawerRoot, factory);
	    	}
	    	
	    }
	    
	    
	    OB_FactoryManager debug;
	    public void debug(){
	    	debug.debug();
	    }
	    
	    

	    	    

	    

	    protected void loadBlockEducationModule(){
	    	
	    	
	    	
	    }

	    
	    
}

