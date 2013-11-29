package Debug;

import java.util.ArrayList;

import edu.mit.blocks.renderable.RenderableBlock;


public class EventStack {
		
	private ArrayList<RenderableBlock> runBlockList;
	
	EventStack(ArrayList<RenderableBlock> runList){
		this.runBlockList = new ArrayList<RenderableBlock>();
		//ƒXƒ^ƒbƒN‚É‹t‡‚É‹l‚ßž‚Þ
		for(int i=runList.size()-1; i >= 0; i--){
			this.setEventBlock(runList.get(i));
		}
	}
	
	public RenderableBlock getEventBlock(){
		int pointer = runBlockList.size()-1;
		RenderableBlock event = this.runBlockList.get(pointer);
		runBlockList.remove(pointer);
		return event;
	}
	
	public RenderableBlock showNextEventBlock(){
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
		return this.runBlockList.size() == 0 ? true : false;
	}
	
	
	@Override
	public String toString(){
		String result = "";
		for(int i=this.runBlockList.size()-1; i>=0 ; i--){
			RenderableBlock rb = runBlockList.get(i);
			result += "BlockID:"+rb.getBlockID()
					+"\nGenusName:"+rb.getGenus()
					+"\n";
		}
		return result;
	}
	
}
