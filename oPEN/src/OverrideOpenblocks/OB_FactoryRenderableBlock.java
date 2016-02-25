package OverrideOpenblocks;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockStub;
import edu.mit.blocks.codeblocks.JComponentDragHandler;
import edu.mit.blocks.renderable.BlockUtilities;
import edu.mit.blocks.renderable.FactoryRenderableBlock;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceWidget;

//public class OB_FactoryRenderableBlock extends FactoryRenderableBlock{
public class OB_FactoryRenderableBlock extends RenderableBlock{	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 2015/10/28 N.Inaba MOD ブロック(単品)の複製
	private OB_RenderableBlock createdRB = null;
	
	private boolean createdRB_dragged = false;
	private JComponentDragHandler dragHandler;

	/**
	 * Returns a new RenderableBlock instance (and creates its associated Block) instance of the same genus as this.
	 * @return a new RenderableBlock instance with a new associated Block instance of the same genus as this.
	 */
	public OB_FactoryRenderableBlock(OB_Workspace workspace, WorkspaceWidget widget, Long blockID) {
		super(workspace, widget, blockID);
		this.setBlockLabelUneditable();
		dragHandler = new JComponentDragHandler(workspace, this);
	}
	
	public OB_FactoryRenderableBlock(Workspace workspace, WorkspaceWidget widget, Long blockID) {
		super(workspace, widget, blockID);
		this.setBlockLabelUneditable();
		dragHandler = new JComponentDragHandler(workspace, this);
	}
	
//	@Override
 // 2015/10/28 N.Inaba MOD ブロック(単品)の複製
	public OB_RenderableBlock createNewInstance() {
//		RenderableBlock rb = BlockUtilities.cloneBlock(workspace.getEnv().getBlock(super.getBlockID()));
//		Block original = rb.getBlock();
//		System.out.println("hoge");
//		Block original = workspace.getEnv().getBlock(super.getBlockID());
//		Block newBlock = new OB_Block(original.getWorkspace(), original.getGenusName(), original.getBlockLabel());
//		RenderableBlock newRenderableBlock = new RenderableBlock(workspace, null, newBlock.getBlockID());
//		return newRenderableBlock;
		
		// 2015/09/29 N.Inaba ADD NormalizeIDs BlockIDの調査
		Long testBlock = super.getBlockID();
//		System.out.println(testBlock);
   	 	 return OB_BlockUtilities.cloneBlock(workspace.getEnv().getBlock(testBlock));
//		 return OB_BlockUtilities.cloneBlock(workspace.getEnv().getBlock(super.getBlockID()));
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		this.requestFocus();
		//create new renderable block and associated block
		createdRB = createNewInstance();
		//add this new rb to parent component of this
		this.getParent().add(createdRB, 0);
		//set the parent widget of createdRB to parent widget of this
		//createdRB not really "added" to widget (not necessary to since it will be removed)
		createdRB.setParentWidget(this.getParentWidget());
		//set the location of new rb from this 
		createdRB.setLocation(this.getX(), this.getY());
		//send the event to the mousedragged() of new block
		MouseEvent newE = SwingUtilities.convertMouseEvent(this, e, createdRB);
		createdRB.mousePressed(newE);
		mouseDragged(e); // immediately make the RB appear under the mouse cursor
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (createdRB != null) {
			//translate this e to a MouseEvent for createdRB
			MouseEvent newE = SwingUtilities.convertMouseEvent(this, e, createdRB);
			createdRB.mouseDragged(newE);
			createdRB_dragged = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (createdRB != null) {
			if (!createdRB_dragged) {
				Container parent = createdRB.getParent();
				parent.remove(createdRB);
				parent.validate();
				parent.repaint();
			} else {
				//translate this e to a MouseEvent for createdRB
				MouseEvent newE = SwingUtilities.convertMouseEvent(this, e, createdRB);
				createdRB.mouseReleased(newE);
			}
			createdRB_dragged = false;
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		dragHandler.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		dragHandler.mouseExited(e);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void startDragging(MouseEvent e) {
	}

	public void stopDragging(MouseEvent e, WorkspaceWidget w) {
	}

	public void setZoomLevel(double newZoom) {
	}

}
