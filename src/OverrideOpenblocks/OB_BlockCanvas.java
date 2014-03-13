package OverrideOpenblocks;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
    /** The Swing representation of the page container */
//    private JComponent canvas;
    /** The scrollable JComponent representing the graphical part of this BlockCanvas */
//    private CScrollPane scrollPane;
    
    
//    protected List<Page> pages = new ArrayList<Page>();
    /** ‘‚«Š·‚¦•ª */
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

        //load pages, page drawers, and their blocks from save file
        //PageDrawerManager.loadPagesAndDrawers(root);
        OB_PageDrawerLoadingUtils.loadPagesAndDrawers(workspace, root, workspace.getFactoryManager());

        final NodeList pagesRoot = root.getElementsByTagName("Pages");
        if (pagesRoot != null && pagesRoot.getLength() > 0) {
            final Node pagesNode = pagesRoot.item(0);
            if (pagesNode != null) {
                collapsible = OB_PageDrawerLoadingUtils.getBooleanValue(pagesNode, "collapsible-pages");
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
            return super.getPages().get(position);
        } else {
            return null;
        }
    }
	
    /** @overrides ISupportMomento.loadState() */
    @SuppressWarnings("unchecked")
    @Override
    public void loadState(Object memento) {
        assert (memento instanceof HashMap) : "ISupportMemento contract violated in BlockCanvas";
        if (memento instanceof HashMap) {
            Map<String, Object> pageStates = (HashMap<String, Object>) memento;
            List<String> unloadedPages = new LinkedList<String>();
            List<String> loadedPages = new LinkedList<String>();

            for (String name : pageStates.keySet()) {
                unloadedPages.add(name);
            }

            //First, load all the pages that are in the state to be loaded
            //against all the pages that already exist.
            for (Page existingPage : this.pages) {
                String existingPageName = existingPage.getPageName();

                if (pageStates.containsKey(existingPageName)) {
                    existingPage.loadState(pageStates.get(existingPageName));
                    unloadedPages.remove(existingPageName);
                    loadedPages.add(existingPageName);
                }
            }

            //Now, remove all the pages that don't exist in the save state
            for (Page existingPage : this.pages) {
                String existingPageName = existingPage.getPageName();

                if (!loadedPages.contains(existingPageName)) {
                    this.pages.remove(existingPage);
                }
            }

            //Finally, add all the remaining pages that weren't there before
            for (String newPageName : unloadedPages) {
                Page newPage = new OB_Page(workspace, newPageName);
                newPage.loadState(pageStates.get(newPageName));
                pages.add(newPage);
            }
        }
    }
    
    /**
     * @param page the page to add to the BlockCanvas
     *
     * @requires page != null
     * @modifies this.pages
     * @effects Adds the given page to the rightmost side of the BlockCanvas
     */
    @Override
    public void addPage(Page page) {
        this.addPage(page, pages.size());
    }

    /**
     * @param page - page to be added
     * @param position - the index at which to add the page where 0 is rightmost
     *
     * @requires none
     * @modifies this.pages
     * @effects Inserts the specified page at the specified position.
     * 			Shifts the element currently at that position (if any)
     * 			and any subsequent elements to the right (adds one to
     * 			their current position).
     * @throws RuntimeException if (position < 0 || position > pages.size() || page == null)
     */
    @Override
    public void addPage(Page page, int position) {
        if (page == null) {
            throw new RuntimeException("Invariant Violated: May not add null Pages");
        } else if (position < 0 || position > pages.size()) {
            System.out.println(position + ", " + pages.size());
            throw new RuntimeException("Invariant Violated: Specified position out of bounds");
        }
        pages.add(position, page);
        canvas.add(page.getJComponent(), 0);
        PageDivider pd = new PageDivider(workspace, page);
        dividers.add(pd);
        canvas.add(pd, 0);
        PageChangeEventManager.notifyListeners();
    }

}
