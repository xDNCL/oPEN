package OverrideOpenblocks;

import java.util.HashMap;
import java.util.Map;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockStub;
import edu.mit.blocks.renderable.BlockUtilities;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;

public class OB_BlockUtilities extends BlockUtilities{

	   private static final Map<String, Integer> instanceCounter = new HashMap<String, Integer>();
	   private static double zoom = 1.0;
		
	public static OB_RenderableBlock cloneBlock(Block myblock) {
		String mygenusname = myblock.getGenusName();
		String label = myblock.getBlockLabel();
		Workspace workspace = myblock.getWorkspace();
		
		//sometimes the factory block will have an assigned label different
		//from its genus label.
		if (!myblock.getInitialLabel().equals(myblock.getBlockLabel())) {
			//acquire prefix and suffix length from myblock label
			int prefixLength = myblock.getLabelPrefix().length();
			int suffixLength = myblock.getLabelSuffix().length();
			//we need to set the block label without the prefix and suffix attached because those
			//values are automatically concatenated to the string specified in setBlockLabel.  I know its
			//weird, but its the way block labels were designed.
			if (prefixLength > 0 || suffixLength > 0) //TODO we could do this outside of this method, even in constructor
			{
				label = myblock.getBlockLabel().substring(prefixLength, myblock.getBlockLabel().length() - suffixLength);
			}
		}

		//check genus instance counter and if label unique - change label accordingly
		//also check if label already has a value at the end, if so update counter to have the max value
		//TODO ria need to make this smarter
		//some issues to think about:
		// - what if they throw out an instance, such as setup2? should the next time they take out
		//   a setup block, should it have setup2 on it?  but wouldn't that be confusing?
		// - when we load up a new project with some instances with numbered labels, how do we keep
		//   track of new instances relative to these old ones?
		// - the old implementation just iterated through all the instances of a particular genus in the
		//   workspace and compared a possible label to the current labels of that genus.  if there wasn't
		//   any current label that matched the possible label, it returned that label.  do we want to do this?
		//   is there something more efficient?

		String labelWithIndex = label;  //labelWithIndex will have the instance value

		int value;
		//initialize value that will be appended to the end of the label
		if (instanceCounter.containsKey(mygenusname)) {
			value = instanceCounter.get(mygenusname).intValue();
		} else {
			value = 0;
		}
		//begin check for validation of label
		//iterate until label is valid
		while (!isLabelValid(myblock, labelWithIndex)) {
			value++;
			labelWithIndex = labelWithIndex + value;
		}

		//set valid label and save current instance number
		instanceCounter.put(mygenusname, new Integer(value));
		if (!labelWithIndex.equals(label)) //only set it if the label actually changed...
		{
			label = labelWithIndex;
		}

		Block block;
		if (myblock instanceof BlockStub) {
			Block parent = ((BlockStub) myblock).getParent();
			block = new BlockStub(workspace, parent.getBlockID(), parent.getGenusName(), parent.getBlockLabel(), myblock.getGenusName());
		} else {
			block = new OB_Block(workspace, myblock.getGenusName(), label);
		}

		// TODO - djwendel - create a copy of the RB properties too, using an RB copy constructor.  Don't just use the genus.
		//RenderableBlock renderable = new RenderableBlock(this.getParentWidget(), block.getBlockID());
		OB_RenderableBlock renderable = new OB_RenderableBlock(workspace, null, block.getBlockID(), false);
		renderable.setZoomLevel(OB_BlockUtilities.zoom);
		renderable.redrawFromTop();
		renderable.repaint();
		return renderable;
	}
	
	// 2015/12/01 N.Inaba MOD Shelfの実装 ob_ws_shelfに複製
	public static OB_RenderableBlock cloneBlockToWorkspace(Block myblock, Workspace workspace) {
//		System.out.println("cloneBlockToWokspace is called. workspace : " + workspace.toString());
		String mygenusname = myblock.getGenusName();
		String label = myblock.getBlockLabel();

		//sometimes the factory block will have an assigned label different
		//from its genus label.
		if (!myblock.getInitialLabel().equals(myblock.getBlockLabel())) {
			//acquire prefix and suffix length from myblock label
			int prefixLength = myblock.getLabelPrefix().length();
			int suffixLength = myblock.getLabelSuffix().length();
			//we need to set the block label without the prefix and suffix attached because those
			//values are automatically concatenated to the string specified in setBlockLabel.  I know its
			//weird, but its the way block labels were designed.
			if (prefixLength > 0 || suffixLength > 0) //TODO we could do this outside of this method, even in constructor
			{
				label = myblock.getBlockLabel().substring(prefixLength, myblock.getBlockLabel().length() - suffixLength);
			}
		}

		//check genus instance counter and if label unique - change label accordingly
		//also check if label already has a value at the end, if so update counter to have the max value
		//TODO ria need to make this smarter
		//some issues to think about:
		// - what if they throw out an instance, such as setup2? should the next time they take out
		//   a setup block, should it have setup2 on it?  but wouldn't that be confusing?
		// - when we load up a new project with some instances with numbered labels, how do we keep
		//   track of new instances relative to these old ones?
		// - the old implementation just iterated through all the instances of a particular genus in the
		//   workspace and compared a possible label to the current labels of that genus.  if there wasn't
		//   any current label that matched the possible label, it returned that label.  do we want to do this?
		//   is there something more efficient?

		String labelWithIndex = label;  //labelWithIndex will have the instance value

		int value;
		//initialize value that will be appended to the end of the label
		if (instanceCounter.containsKey(mygenusname)) {
			value = instanceCounter.get(mygenusname).intValue();
		} else {
			value = 0;
		}
		//begin check for validation of label
		//iterate until label is valid
		while (!isLabelValid(myblock, labelWithIndex)) {
			value++;
			labelWithIndex = labelWithIndex + value;
		}

		//set valid label and save current instance number
		instanceCounter.put(mygenusname, new Integer(value));
		if (!labelWithIndex.equals(label)) //only set it if the label actually changed...
		{
			label = labelWithIndex;
		}

		Block block;
		if (myblock instanceof BlockStub) {
			Block parent = ((BlockStub) myblock).getParent();
			block = new BlockStub(workspace, parent.getBlockID(), parent.getGenusName(), parent.getBlockLabel(), myblock.getGenusName());
		} else {
			block = new OB_Block(workspace, myblock.getGenusName(), label);
		}

		// TODO - djwendel - create a copy of the RB properties too, using an RB copy constructor.  Don't just use the genus.
		//RenderableBlock renderable = new RenderableBlock(this.getParentWidget(), block.getBlockID());
		OB_RenderableBlock renderable = new OB_RenderableBlock(workspace, null, block.getBlockID(), false);
		renderable.setZoomLevel(OB_BlockUtilities.zoom);
		renderable.redrawFromTop();
		renderable.repaint();
		return renderable;
	}
}
