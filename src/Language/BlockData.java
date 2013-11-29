package Language;

import java.util.ArrayList;


public class BlockData {
		
	/**
	 * @param nextId :: 次のブロックのID
	 * @param beforeId ::親ブロックのID
	 * 
	 */
	private int nextId;
	private int beforeId;
	private String name, label;
	private int id;
	private ArrayList<ConnectorInfo> connectionList;

	
	BlockData(String name, int id, ArrayList<ConnectorInfo> connectionList, int nextId, int beforeId, String label){
		this.name = name;
		this.id = id;
		this.connectionList = connectionList;
		this.nextId = nextId;
		this.beforeId = beforeId;
		this.label = label;
	}
	
	protected String getName(){
		return name;
	}
	
	protected String getLabel(){
		if(label.equals("")){
			return "1";
		}
		return label;
	}
	
	protected int getId(){
		return id;
	}
	
	protected ArrayList<ConnectorInfo> getConnectionList(){
		return connectionList;
	}
	
	protected ConnectorInfo getConnectorInfo(int index){
		if(index > connectorNum()){
			return null;
		}
		return this.connectionList.get(index);
	}
	
	protected int connectorNum(){
		return this.connectionList.size();
	}
	
	protected int getNextId(){
		return nextId;
	}
	
	protected int getBeforeId(){
		return beforeId;
	}
	
	protected boolean isConnected(){
		for(ConnectorInfo ci:this.connectionList){
			if(ci.getId() != -1){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString(){
		String s="";
		s += "BlockName::"+getName()+"\n";
		s += "BlockID::"+getId()+"\n";
		for(ConnectorInfo ci: getConnectionList()){
			s += "BlockSocketConnectionID::"+ci.getId()+"  ";
			s += "ConnectionNumber::"+ci.getconNum()+"  ";
			s += "ConnectorType::"+ci.getType()+"  ";
			s += "ConnectionID::"+Integer.toString(ci.getId())+"\n";
		}
		s += "NextBlockID::"+getNextId();
		s += "BeforeBlockID::"+getBeforeId();
		s += "\n\n";
		return s;
	}


}
