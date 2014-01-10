package Language;

public class BlockString {
	
	private String name;
	private String[] code;
	private String[] preCode;
	
	public BlockString(String name, String[] code){
		this(name, code, null);
	}
	
	public BlockString(String name, String[] code, String[] preCode){
		this.name = name;
		this.code = code;
		this.preCode = preCode;
	}
	
	protected String getName(){
		return this.name;
	}
	
	protected int getCodeLength(){
		return this.code.length;
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
	
	protected int getConnectorNum(){
		int count = 0;
		for(String str: this.code){
			if(str.equals("_val") || str.equals("_preval")){
				count++;
			}
		}
		return count;
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
