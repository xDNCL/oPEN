package OverrideOpenblocks;

import java.util.ArrayList;

import Debug.BlockRunException;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.workspace.Workspace;

public class OB_Block extends Block{
	public static enum ValType {INT, FLOAT, BOOLEAN, STRING, }
	private enum CAL{SUM, DIF, PRO, QUO, SUR, }
	
	private OB_Workspace workspace;
	
	protected static ArrayList<Variable> variableList = new ArrayList<Variable>();
	
	public OB_Block(Workspace workspace, String genusName, String label) {
		super(workspace, genusName, label);
	}

    public OB_Block(Workspace workspace, String genusName, boolean linkToStubs) { 	
        super(workspace, genusName, linkToStubs);
     }

    protected OB_Block(Block block){
    	super(block.getWorkspace(), block.getGenusName(), block.getBlockLabel());
    }
    
    ///////
    //exe//
    ///////
    
    /**
     * コマンドブロックの処理を記述
     */
    public void runBlock(){
    	
    	if(this.getGenusName().equals("start")){
    		
    	}
    	
    }
    
	/**
	 * ブロックの持つ値を返すメソッド。整数型、文字列型問わず、文字列として返す。
	 * @return Returns the value as a String
	 * @throws BlockRunException 
	 */
    public String getValue() throws BlockRunException{
    	//if this block is data
    	if(this.getGenusName().equals("number")){
    		return this.getBlockLabel();
    	}
    	if(this.getGenusName().equals("string")){
    		return this.getBlockLabel();
    	}
    	if(this.getGenusName().equals("variable-Number")){
    		return this.getVariable(getBlockLabel()).getValue();
    	}
    	if(this.getGenusName().equals("variable-String")){
    		return this.getVariable(getBlockLabel()).getValue();
    	}
    
    	//if this block is calculation
    	if(this.getGenusName().equals("sum")){
    		this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).getValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).getValue(), 
    					CAL.SUM);
    	}
    	if(this.getGenusName().equals("difference")){
    		this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).getValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).getValue(), 
    					CAL.DIF);
    	}
    	if(this.getGenusName().equals("product")){
    		this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).getValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).getValue(), 
    					CAL.PRO);
    	}
    	if(this.getGenusName().equals("surplus")){
    		this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).getValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).getValue(), 
    					CAL.SUR);
    	}
    	if(this.getGenusName().equals("quotient")){
    		this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).getValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).getValue(), 
    					CAL.QUO);
    	}
    	
    	//other ...
    	
    	//
    	
    	
    	throw new BlockRunException(this);
    }
    
    /**
     * boolean connector用
     * @return boolean
     */
    public boolean evaluateBoolean(){
    	//boolean block
    	if(this.getGenusName().equals("true")){
    		return true;
    	}
    	if(this.getGenusName().equals("false")){
    		return false;
    	}
    	if(this.getGenusName().equals("or")){
    		return getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean() || 
    				getBlock(this.getSocketAt(1).getBlockID()).evaluateBoolean();
    	}
    	if(this.getGenusName().equals("and")){
    		return getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean() &&
    				getBlock(this.getSocketAt(1).getBlockID()).evaluateBoolean();
    	}
    	if(this.getGenusName().equals("not")){
    		return !(getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean());
    	}
    	
    	//caluculation block
    	if(this.getGenusName().equals("equals")){

    	}
    	if(this.getGenusName().equals("not-equals")){
    		
    	}
    	if(this.getGenusName().equals("lessthan")){
    		
    	}
    	if(this.getGenusName().equals("lessthanoreequalt")){
    		
    	}
    	if(this.getGenusName().equals("greaterthan")){
    		
    	}
    	if(this.getGenusName().equals("greaterthanorequalt")){
    		
    	}
    	
    	for(int i=0; i < this.getNumSockets(); i++){
			BlockConnector blockConnector = this.getSocketAt(i);
			
    	}
    	
    	//to do
    	return false;
    }

    private OB_Block getBlock(long id){
    	Block block = this.workspace.getEnv().getBlock(id);
    	
    	if(block == null){
    		return null;
    	}
    	
    	if(this.workspace.getEnv().getBlock(id) instanceof OB_Block){
    		return (OB_Block)this.workspace.getEnv().getBlock(id);
    	}
    	else{
    		OB_Block newBlock = new OB_Block(block);
    		
    		return newBlock;
    	}
    }
    

    
    private Variable getVariable(String name) throws BlockRunException{
    	for(Variable val: variableList){
    		if(val.getName().equals(name)){
    			return val;
    		}
    	}
    	throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    }
    
    private String caluculation(String a, String b, CAL order)throws BlockRunException{
    	float fa;
    	float fb;
    	float result = 0;
    	try{
    		fa = Float.parseFloat(a);
    		fb = Float.parseFloat(b);
    	}catch(Exception e){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	switch(order){
    	case SUM:
    		result = fa + fb;
    		break;
    	case DIF:
    		result = fa - fb;
    		break;
    	case PRO:
    		result = fa * fb;
    		break;
    	case QUO:
    		result = fa / fb;
    		break;
    	case SUR:
    		result = fa % fb;
    		break;
    	}
    	
    	return Float.toString(result);
    }

    @Override
    public String toString(){
    	return "OB_Block:: name is -"+this.getGenusName();
    }
    private class Variable{
    	
    	private String name;
    	private String value;
    	private ValType type;
    	
    	Variable(String name, String value, ValType type){
    		this.name = name;
    		this.value = value;
    		this.type = type;
    	}
    	
    	void setValue(String val){
    		this.value = val;
    	}
    	
    	String getName(){
    		return this.name;
    	}
    	
    	ValType getType(){
    		return this.type;
    	}
    	
    	String getValue(){
    		return this.value;
    	}
    	
    }
}
