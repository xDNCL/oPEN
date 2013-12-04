package OverrideOpenblocks;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.mit.blocks.codeblockutil.CScrollPane;
import edu.mit.blocks.workspace.BlockCanvas;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.PageChangeEventManager;
import edu.mit.blocks.workspace.PageDivider;
import edu.mit.blocks.workspace.PageDrawerLoadingUtils;
import edu.mit.blocks.workspace.Workspace;

public class OB_BlockCanvas extends BlockCanvas{
	
    /** serial version ID */
    private static final long serialVersionUID = 7458721329L;
    /** the collection of pages that this BlockCanvas stores */
    private List<Page> pages = new ArrayList<Page>();
    /** the collection of PageDivideres that this BlockCanvas stores */
    private List<PageDivider> dividers = new ArrayList<PageDivider>();
    /** The Swing representation of the page container */
    private JComponent canvas;
    /** The scrollable JComponent representing the graphical part of this BlockCanvas */
    private CScrollPane scrollPane;
    
    /** ‘‚«Š·‚¦•ª */
	private final OB_Workspace workspace;
	
	
    private boolean collapsible = false;
	
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

        //load pages, page drawers, and their blocks from save file
        //PageDrawerManager.loadPagesAndDrawers(root);
        OB_PageDrawerLoadingUtils.loadPagesAndDrawers(workspace, root, workspace.getFactoryManager());

        final NodeList pagesRoot = root.getElementsByTagName("Pages");
        if (pagesRoot != null && pagesRoot.getLength() > 0) {
            final Node pagesNode = pagesRoot.item(0);
            if (pagesNode != null) {
                collapsible = PageDrawerLoadingUtils.getBooleanValue(pagesNode, "collapsible-pages");
            }
        }

        // FIXME: this UI code should not be here, fails unit tests that run in headless mode
        // As a workaround, only execute if we have a UI
        if (!GraphicsEnvironment.isHeadless()) {
            int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
            int canvasWidth = canvas.getPreferredSize().width;
            if (canvasWidth < screenWidth) {
                Page p = pages.get(pages.size() - 1);
                p.addPixelWidth(screenWidth - canvasWidth);
                PageChangeEventManager.notifyListeners();
            }
        }
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
            return pages.get(position);
        } else {
            return null;
        }
    }
	
}
