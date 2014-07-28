package Language;

public class DecideVariable {

	String[] VARIABLE = {}; 
	private final int MAX_NUMBER = 26;
	private int now;
	private String upper;
	private int over;
	
	DecideVariable(){
		resetVariable();
		
		VARIABLE = new String[MAX_NUMBER];
		for(int i=0; i<MAX_NUMBER; i++){
			VARIABLE[i] = String.valueOf('a'+i);
		}
		
	}
	
	
	protected void resetVariable(){
		now = 0;
		upper = "";
		over = 0;
	}

	
	protected String getVariable(){
		if(over > MAX_NUMBER){
			return "value"+ Integer.toString((int)(Math.random()*1000000));
		}
		if(now > MAX_NUMBER){
			now = 0;
			upper = VARIABLE[over++];
		}
		return (upper + VARIABLE[now++]);
	}
}
