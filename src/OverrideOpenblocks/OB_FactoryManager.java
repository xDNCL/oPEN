package OverrideOpenblocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.*;
import edu.mit.blocks.codeblockutil.CGraphite;
import edu.mit.blocks.codeblockutil.Canvas;
import edu.mit.blocks.codeblockutil.Navigator;

public class OB_FactoryManager extends FactoryManager{

	
	private OB_Workspace workspace;
	private ArrayList<FactoryCanvas> customCanvas;
	
	
	private Navigator navigator;
	
	public OB_FactoryManager(OB_Workspace workspace){
		super(workspace);
		this.workspace = workspace;
		
		customCanvas = new ArrayList<FactoryCanvas>();
		this.navigator = super.getNavigator();
		
	}
	
	
	@Override
	public JComponent getJComponent(){
//		System.out.println("debug::"+this.navigator.getJComponent());
		return this.navigator.getJComponent();
	}
	
	
//	//ボタン配置
//	@Override
//    public void addStaticDrawer(String name, Color color) {
//    	System.out.println(name);
//    }
	
	/* SuperClass Method
    public void addStaticDrawer(String name, int position, Color color) {
        if (isValidDrawer(true, false, name, position)) {
            FactoryCanvas canvas = new FactoryCanvas(name, color);
            this.staticCanvases.add(position, canvas);
            this.navigator.setCanvas(staticCanvases, STATIC_NAME);
        } else {
            this.printError("Invalid Drawer: trying to add a drawer that already exists: " + name);
        }
    }
	*/
	
	//ブロック表示メソッド
//	@Override
//	public void addStaticBlocks(Collection<RenderableBlock> blocks, String drawer){
////		for(RenderableBlock rb:blocks){
////			System.out.println(rb);
////		}
//		
//		debug(blocks);
//	}
	/* superClasws Method
    public void addStaticBlocks(Collection<RenderableBlock> blocks, String drawer) {
        //find canvas
        for (FactoryCanvas canvas : this.staticCanvases) {
            if (canvas.getName().equals(drawer)) {
                for (RenderableBlock block : blocks) {
                    if (block == null || Block.NULL.equals(block.getBlockID())) {
                        continue;
                    }
                    canvas.addBlock(block);
                    workspace.notifyListeners(new WorkspaceEvent(workspace, this, block.getBlockID(), WorkspaceEvent.BLOCK_ADDED));

                }
                canvas.layoutBlocks();
                return;
            }
        }
        this.printError("Drawer not found: " + drawer);
        return;
    }
	*/
	
//	@Override
//	public void addStaticDrawer(String name, Color color){
//		System.out.println("name:"+name+ " Color:"+color);
//	}
//	@Override
//	public JComponent getJComponent(){
//		super.reset();
//		return super.getJComponent();
//	}
	
	
	public void debug(){
		Collection<RenderableBlock> collection;
		System.err.println("OB_Manager debug::");
		collection = getBlocks();
			
		System.out.println("size::"+collection.size());
		for(RenderableBlock rb:collection){
			System.out.println(rb);
		}
		
	}
	
	public void debug(Collection<RenderableBlock> rb){
		System.err.println("OB_Manager debug::");
		for(RenderableBlock block:rb){
			System.out.println(block.getName());
		}
	}
	
	@Override
	public String toString(){
		return "OB_Manager is OK";
	}
	
	
	
	
}
