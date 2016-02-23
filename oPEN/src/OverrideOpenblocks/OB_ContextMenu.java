package OverrideOpenblocks;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.ContextMenu;

/**
 * ContextMenu handles all the right-click menus within the Workspace.
 * TODO ria enable customization of what menu items appear, fire events depending
 * on what items are clicked (if we enabled the first feature)
 * 
 * TODO ria still haven't enabled the right click menu for blocks
 */
public class OB_ContextMenu extends ContextMenu implements ActionListener {

    private static final long serialVersionUID = 328149080421L;
    //context menu renderableblocks plus
    //menu items for renderableblock context menu
    private static OB_ContextMenu rndBlockMenu = new OB_ContextMenu();
    private static OB_ContextMenu addCommentMenu = new OB_ContextMenu();
    private static MenuItem addCommentItem;
    private final static String ADD_COMMENT_BLOCK = "ADDCOMMENT";
    private static boolean addCommentMenuInit = false;
    private static OB_ContextMenu removeCommentMenu = new OB_ContextMenu();
    private static MenuItem removeCommentItem;
    private final static String REMOVE_COMMENT_BLOCK = "REMOVECOMMENT";
    private static boolean removeCommentMenuInit = false;
    //context menu for canvas plus
    //menu items for canvas context menu
    private static OB_ContextMenu canvasMenu = new OB_ContextMenu();
    private static MenuItem arrangeAllBlocks;
    private final static String ARRANGE_ALL_BLOCKS = "ARRANGE_ALL_BLOCKS";
    private static boolean canvasMenuInit = false;
    /** The JComponent that launched the context menu in the first place */
    private static Object activeComponent = null;

    // 2015/02/26 N.Inaba ADD ブロック(単品)の複製 コピーブロックメニュー
    private static MenuItem duplicateABlockItem;
    private final static String DUPLICATE_A_BLOCK = "DUPLICATEABLOCK";

    // 2015/11/11 N.Inaba ADD Shelfの実装 Shelfメニュー
    private static MenuItem  putOnTheShelfItem;
    private final static String PUT_ON_THE_SHELF = "PUTONTHESHELF";
    
    // 2015/12/25 N.Inaba ADD Shelfの実装 Shelfから削除
    private static MenuItem deleteABlockItem;
    private final static String DELETE_A_BLOCK = "DELETEABLOCK";
    
    //privatize the constructor
    OB_ContextMenu() {
//    	super();
    }

    /**
     * Initializes the context menu for adding Comments.
     */
    private static void initAddCommentMenu() {
//    	addCommentItem = new MenuItem("Add Comment");
    	addCommentItem = new MenuItem("コメントを挿入");
        addCommentItem.setActionCommand(ADD_COMMENT_BLOCK);
        addCommentItem.addActionListener(rndBlockMenu);
        addCommentMenu.add(addCommentItem);

        // 2015/02/26 N.Inaba ADD ブロック(単品)の複製 コピーブロックメニュー
//        duplicateABlockItem = new MenuItem("Duplicate a Block");
        duplicateABlockItem = new MenuItem("複製");
        duplicateABlockItem.setActionCommand(DUPLICATE_A_BLOCK);
        duplicateABlockItem.addActionListener(rndBlockMenu);
    	addCommentMenu.add(duplicateABlockItem);
    	
    	// 2016/02/23 N.Inaba DEL 配布用コメントアウト
//    	// 2015/11/12 N.Inaba ADD Shelfの実装 Shelfメニュー
//    	putOnTheShelfItem = new MenuItem("Put on the Shelf");
//    	putOnTheShelfItem.setActionCommand(PUT_ON_THE_SHELF);
//    	putOnTheShelfItem.addActionListener(rndBlockMenu);
//    	addCommentMenu.add(putOnTheShelfItem);
//    	// 2015/12/25 N.Inaba ADD Shelfの実装 Shelfから削除
//        deleteABlockItem = new MenuItem("Delete a Block");
//        deleteABlockItem.setActionCommand(DELETE_A_BLOCK);
//        deleteABlockItem.addActionListener(rndBlockMenu);
//    	addCommentMenu.add(deleteABlockItem);
    	
        addCommentMenuInit = true;
    }

    /**
     * Initializes the context menu for deleting Comments.
     */
    private static void initRemoveCommentMenu() {

    	// 2016/02/23 N.Inaba MOD メニューを日本語化
//      removeCommentItem = new MenuItem("Delete Comment");
        removeCommentItem = new MenuItem("コメントを削除");
        removeCommentItem.setActionCommand(REMOVE_COMMENT_BLOCK);
        removeCommentItem.addActionListener(rndBlockMenu);
        removeCommentMenu.add(removeCommentItem);
        //rndBlockMenu.add(runBlockItem);

        // 2015/02/26 N.Inaba ADD ブロック(単品)の複製 コピーブロックメニュー
//      duplicateABlockItem = new MenuItem("Duplicate a Block");
        duplicateABlockItem = new MenuItem("複製");
        duplicateABlockItem.setActionCommand(DUPLICATE_A_BLOCK);
        duplicateABlockItem.addActionListener(rndBlockMenu);
        removeCommentMenu.add(duplicateABlockItem);
    	
        // 2016/02/23 N.Inaba DEL 配布用コメントアウト
//    	// 2015/11/12 N.Inaba ADD Shelfの実装 Shelfメニュー
//    	putOnTheShelfItem = new MenuItem("Put on the Shelf");
//    	putOnTheShelfItem.setActionCommand(PUT_ON_THE_SHELF);
//    	putOnTheShelfItem.addActionListener(rndBlockMenu);
//    	removeCommentMenu.add(putOnTheShelfItem);
//    	// 2015/12/25 N.Inaba ADD Shelfの実装 Shelfから削除
//      deleteABlockItem = new MenuItem("Delete a Block");
//      deleteABlockItem.setActionCommand(DELETE_A_BLOCK);
//      deleteABlockItem.addActionListener(rndBlockMenu);
//      removeCommentMenu.add(deleteABlockItem);
    	
        removeCommentMenuInit = true;
    }

    /**
     * Initializes the context menu for the BlockCanvas
     *
     */
    private static void initCanvasMenu() {
        arrangeAllBlocks = new MenuItem("Organize all blocks");  //TODO some workspaces don't have pages
        arrangeAllBlocks.setActionCommand(ARRANGE_ALL_BLOCKS);
        arrangeAllBlocks.addActionListener(canvasMenu);
        canvasMenu.add(arrangeAllBlocks);

        canvasMenuInit = true;
    }
    
    /**
     * Returns the right click context menu for the specified JComponent.  If there is 
     * none, returns null.
     * @param o JComponent object seeking context menu
     * @return the right click context menu for the specified JComponent.  If there is 
     * none, returns null.
     */
    public static PopupMenu getContextMenuFor(Object o) {
        // 2015/10/28 N.Inaba MOD ブロック(単品)の複製 OB_RenderableBlockに修正
        if (o instanceof OB_RenderableBlock) {
            if (((OB_RenderableBlock) o).hasComment()) {
                if (!removeCommentMenuInit) {
                    initRemoveCommentMenu();
                }
                activeComponent = o;
                return removeCommentMenu;
            } else {
                if (!addCommentMenuInit) {
                    initAddCommentMenu();
                }
                activeComponent = o;
                return addCommentMenu;
            }
        } else if (o instanceof OB_BlockCanvas) {
            if (!canvasMenuInit) {
                initCanvasMenu();
            }
            activeComponent = o;
            return canvasMenu;
        }
        return null;
    }

    public void actionPerformed(ActionEvent a) {
        if (a.getActionCommand() == ARRANGE_ALL_BLOCKS) {
            //notify the component that launched the context menu in the first place
            if (activeComponent != null && activeComponent instanceof OB_BlockCanvas) {
                ((OB_BlockCanvas) activeComponent).arrangeAllBlocks();
            }
        } else if (a.getActionCommand() == ADD_COMMENT_BLOCK) {
            //notify the renderableblock componenet that lauched the conetxt menu
            if (activeComponent != null && activeComponent instanceof OB_RenderableBlock) {
                ((OB_RenderableBlock) activeComponent).addComment();
            }
        } else if (a.getActionCommand() == REMOVE_COMMENT_BLOCK) {
            //notify the renderableblock componenet that lauched the conetxt menu
            if (activeComponent != null && activeComponent instanceof OB_RenderableBlock) {
                ((OB_RenderableBlock) activeComponent).removeComment();
            }

        // 2015/10/13 N.Inaba ADD ブロック(単品)の複製
        } else if (a.getActionCommand() == DUPLICATE_A_BLOCK) {
            //notify the renderableblock componenet that lauched the conetxt menu
            if (activeComponent != null && activeComponent instanceof OB_RenderableBlock) {
            	((OB_RenderableBlock) activeComponent).duplicateABlock();
            }
        
        // 2015/11/11 N.Inaba ADD Shelfの実装
        } else if (a.getActionCommand() == PUT_ON_THE_SHELF) {
            //notify the renderableblock componenet that lauched the conetxt menu
        	if (activeComponent != null && activeComponent instanceof OB_RenderableBlock) {
        		((OB_RenderableBlock) activeComponent).putOnTheShelf();
        	}
        	
        // 2015/12/25 N.Inaba ADD Shelfの実装 Shelfから削除
        } else if (a.getActionCommand() == DELETE_A_BLOCK) {
        	//notify the renderableblock componenet that lauched the conetxt menu
        	if (activeComponent != null && activeComponent instanceof OB_RenderableBlock) {
        		((OB_RenderableBlock) activeComponent).deleteABlock();
        	}
        }
    }
    
}