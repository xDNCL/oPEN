package OverrideOpenblocks;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mit.blocks.codeblockutil.CGraphite;
import edu.mit.blocks.codeblockutil.CHoverScrollPane;
import edu.mit.blocks.codeblockutil.CScrollPane;
import edu.mit.blocks.codeblockutil.CScrollPane.ScrollPolicy;
import edu.mit.blocks.workspace.BlockCanvas;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.PageChangeEventManager;
import edu.mit.blocks.workspace.PageDivider;
import edu.mit.blocks.workspace.PageDrawerLoadingUtils;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.BlockCanvas.Canvas;

public class OB_BlockCanvas extends BlockCanvas{
	
    /** serial version ID */
    private static final long serialVersionUID = 7458721329L;
    /** The Swing representation of the page container */
//    private JComponent canvas;
    /** The scrollable JComponent representing the graphical part of this BlockCanvas */
//    private CScrollPane scrollPane;
    
    /** èëÇ´ä∑Ç¶ï™ */
	private final OB_Workspace workspace;
		
	public OB_BlockCanvas(OB_Workspace ob_ws){
		super(ob_ws);
		this.workspace = ob_ws;
	}
	
    /**
     * Loads all the RenderableBlocks and their associated Blocks that
     * reside within the block canvas.  All blocks will have their nessary
     * data populated including connection information, stubs, etc.
     * Note: This method should only be called if this language only uses the
     * BlockCanvas to work with blocks and no pages. Otherwise, workspace live blocks
     * are loaded from Pages.
     * @param root the Document Element containing the desired information
     */
	@Override
    public void loadSaveString(Element root) {
        //Extract canvas blocks and load
        super.loadSaveString(root);
        //load pages, page drawers, and their blocks from save file
        //PageDrawerManager.loadPagesAndDrawers(root);
        OB_PageDrawerLoadingUtils.loadPagesAndDrawers(workspace, root, workspace.getFactoryManager());

    }

    /**
     * @param position - 0 is the left most position
     *
     * @requires none
     * @return page at position or null if non exists at position
     */
	@Override
    protected Page getPageAt(int position) {
        if (hasPageAt(position)) {
            return super.getPages().get(position);
        } else {
            return null;
        }
    }
	
}
