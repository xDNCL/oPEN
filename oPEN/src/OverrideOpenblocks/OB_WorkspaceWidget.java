package OverrideOpenblocks;

import java.util.Collection;

import javax.swing.JComponent;

import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.WorkspaceWidget;
// 使ってないかも
public interface OB_WorkspaceWidget extends WorkspaceWidget{
    public void blockDropped(OB_RenderableBlock block);
    public void blockDragged(OB_RenderableBlock block);
    public void blockEntered(OB_RenderableBlock block);
    public void blockExited(OB_RenderableBlock block);
    public void removeBlock(OB_RenderableBlock block);
    public void addBlock(OB_RenderableBlock block);  //TODO ria maybe rename this to putBlock?
    public void addBlocks(Collection<RenderableBlock> blocks);
    public boolean contains(int x, int y);
    public JComponent getJComponent();
    public Iterable<RenderableBlock> getBlocks();
}
