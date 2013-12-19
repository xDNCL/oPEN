package Language;

public class BlockString {
	
	private String name;
	private String[] code;
	private String[] preCode;
	private int connectorNum;
	
	public BlockString(String name, String[] code, final int connectorNum){
		this(name, code, connectorNum, null);
	}
	
	public BlockString(String name, String[] code, final int connectorNum, String[] preCode){
		this.name = name;
		this.code = code;
		this.preCode = preCode;
		this.connectorNum = connectorNum;
	}
	
	protected String getName(){
		return this.name;
	}
	
	protected int getCodeLength(){
		return this.code.length;
	}
	
	protected int getConnectorNum(){
		return this.connectorNum;
	}
	
	protected String getCode(int index){
		return this.code[index];
	}
	
	protected String[] getCode(){
		return this.code;
	}
	
	protected String[] getPreCode(){
		if(this.preCode != null){
			return this.preCode;
		}
		return null;
	}
	

	@Override
	public String toString(){
		String s = "BlockName:"+this.name+"\n";
		for(int i=0; i<code.length; i++){
			s += "codeLine"+ (i+1) + ":" +code[i] + "\n";
		}
		return s;
	}
		
	
	
}
