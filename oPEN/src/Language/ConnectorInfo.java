package Language;

public class ConnectorInfo {
	
	private final int conNum;
	private final String type;
	private final int id;
	
	ConnectorInfo(int conNum, String type, int id){
		this.conNum = conNum;
		this.type = type;
		this.id = id;
	}
	
	protected int getconNum(){
		return this.conNum;
	}

	protected String getType(){
		return this.type;
	}
	
	protected int getId(){
		return this.id;
	}
	

}
