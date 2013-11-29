package Debug;

import java.util.ArrayList;

import edu.mit.blocks.renderable.RenderableBlock;


public class EventStack {
		
	private ArrayList<RenderableBlock> runBlockList;
	
	EventStack(ArrayList<RenderableBlock> runBlockList){
		for(int i=runBlockList.size()-1; i > 0; i--){
			this.setEventBlock(runBlockList.get(i));
		}
	}
	
	public RenderableBlock getEventBlock(){
		return this.runBlockList.get(runBlockList.size()-1);
	}
	
	public void setEventBlock(RenderableBlock rb){
		this.runBlockList.add(rb);
	}
	
	/**
	 * empty: true
	 * @return
	 */
	public boolean isEmpty(){
		return runBlockList.size() == 0 ? true : false;
	}
	
	
	@Override
	public String toString(){
		String result = "";
		for(RenderableBlock rb: this.runBlockList){
			result += "BlockID:"+rb.getBlockID()
					+"\nGenusName:"+rb.getGenus()
					+"\n";
		}
		return result;
	}
	
}
