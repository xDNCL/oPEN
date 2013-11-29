package Debug;

import java.util.ArrayList;

import edu.mit.blocks.renderable.RenderableBlock;


public class EventStack {
	
	private int pointer = 0;
	
	private ArrayList<RenderableBlock> runBlockList;
	
	EventStack(ArrayList<RenderableBlock> runBlockList){
		
	}
	
	public RenderableBlock getNextEvent(){
		return runBlockList.get(pointer--);
	}
	
	public void setEvent
	
	

}
