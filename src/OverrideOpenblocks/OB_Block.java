package OverrideOpenblocks;

import java.util.ArrayList;

import Debug.BlockRunException;
import Debug.ConsoleWindow;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.workspace.Workspace;

public class OB_Block extends Block{
	public static enum ValType {INT, FLOAT, BOOLEAN, STRING, }
	private enum CAL{SUM, DIF, PRO, QUO, SUR, }
	private enum CON{EQU, LESS, GREATER, EQU_LESS, EQU_GREATER, }
		
	protected static ArrayList<Variable> variableList = new ArrayList<Variable>();
	
	//変数テーブルの初期化用
	void resetAll(){
		variableList = new ArrayList<Variable>();
	}
		
	public static ArrayList<Variable> getVariableList(){
		return variableList;
	}
	
	
	/**
	 * コンストラクタ
	 * @param workspace
	 * @param genusName
	 * @param label
	 */
	
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
    public void runBlock() throws BlockRunException{
//    	System.out.println("now:"+this.getGenusName());
    	    	
    	if(this.getGenusName().equals("start")){
    		resetAll();
    	}
    	
    	//変数宣言
    	if(this.getGenusName().equals("setInt")){
    		String name = this.getBlock(this.getSocketAt(0).getBlockID()).getBlockLabel();
    		createVariable(name, "0", ValType.INT);
    	}
    	if(this.getGenusName().equals("setDouble")){
    		String name = this.getBlock(this.getSocketAt(0).getBlockID()).getBlockLabel();
    		createVariable(name, "0.0", ValType.FLOAT);
    	}
    	if(this.getGenusName().equals("setString")){
    		String name = this.getBlock(this.getSocketAt(0).getBlockID()).getBlockLabel();
    		createVariable(name, "",  ValType.STRING);
    	}
    	
    	//標準出力系
    	if(this.getGenusName().equals("print-number")){
    		String value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.print(value);
    	}
    	if(this.getGenusName().equals("println-number")){
    		String value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.println(value);
    	}
    	if(this.getGenusName().equals("print-string")){
    		String value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.print(value);
    	}
    	if(this.getGenusName().equals("println-string")){
    		String value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.println(value);
    	}
    	
    	//代入系
    	if(this.getGenusName().equals("substitution-number")){
    		String value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		this.setVariavle(this.getBlockLabel(), value);
    	}
    	if(this.getGenusName().equals("substitution-string")){
    		String value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		this.setVariavle(this.getBlockLabel(), value);
    	}
    	
    	if(next() == null){
    		return; 
    	}
//    	System.out.println(this.getGenusName()+" is clear.");
		this.next().runBlock();
    }
    
	/**
	 * ブロックの持つ値を返すメソッド。整数型、浮動小数点型、文字列型問わず、文字列として返す（扱う）。
	 * @return Returns the value as a String
	 * @throws BlockRunException 
	 */
    public String evaluateValue() throws BlockRunException{
    	//if this block is data
    	if(this.getGenusName().equals("number")){
    		return this.getBlockLabel();
    	}
    	if(this.getGenusName().equals("string")){
    		System.err.println(this.getBlockLabel());
    		return this.getBlockLabel();
    	}
    	if(this.getGenusName().equals("variable-Number")){
    		if(this.getVariable(this.getBlockLabel()).getValue() == null){
    			throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    		}
    		return this.getVariable(this.getBlockLabel()).getValue();
    	}
    	if(this.getGenusName().equals("variable-String")){
    		if(this.getVariable(this.getBlockLabel()).getValue() == null){
    			throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    		}
    		return this.getVariable(this.getBlockLabel()).getValue();
    	}
    
    	//if this block is calculation
    	if(this.getGenusName().equals("sum")){
    		return this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue(), 
    					CAL.SUM);
    	}
    	if(this.getGenusName().equals("difference")){
    		return this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue(), 
    					CAL.DIF);
    	}
    	if(this.getGenusName().equals("product")){
    		return this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue(), 
    					CAL.PRO);
    	}
    	if(this.getGenusName().equals("surplus")){
    		return this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue(), 
    					CAL.SUR);
    	}
    	if(this.getGenusName().equals("quotient")){
    		return this.caluculation(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue(), 
    					CAL.QUO);
    	}
    	
    	//other ...
    	
    	//
    	
    	
    	throw new BlockRunException(this);
    }
    
    /**
     * Booleanメソッド用
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
    
    ///////////////////
    //Block Operation//
    ///////////////////
    private OB_Block getBlock(long id){
    	Block block = workspace.getEnv().getBlock(id);
    	
    	if(block == null){
    		return null;
    	}
    	
    	if(block instanceof OB_Block){
    		return (OB_Block)workspace.getEnv().getBlock(id);
    	}
    	else{
    		OB_Block newBlock = new OB_Block(block);
    		return newBlock;
    	}
    }
    
    private OB_Block next(){
//    	System.out.println("next ID------"+this.getAfterBlockID());
    	return getBlock(this.getAfterBlockID());
    }
    
    /////////////
    //variables//
    /////////////
    
    private void createVariable(String name, String value, ValType type)throws BlockRunException{
    	if(name == null || value == null){
    		throw new BlockRunException(this, BlockRunException.NO_NAME);
    	}
    	if(checkVariable(name)){
    		throw new BlockRunException(this, BlockRunException.DUPLICATION);
    	}
    	Variable newVariable = new Variable(name, value, type);
    	variableList.add(newVariable);
    	this.variableRenewal();
    }
    
    private void setVariavle(String name, String value)throws BlockRunException{
    	if(checkVariable(name)){
        	this.getVariable(name).setValue(value); 
        	this.variableRenewal();
    	}
    	else{
    		throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    	}
    }
    
    private boolean checkVariable(String name){
    	for(Variable val: variableList){
    		if(val.getName().equals(name)){
    			return true;
    		}
    	}
    	return false;
    }
    
    private Variable getVariable(String name) throws BlockRunException{
    	for(Variable val: variableList){
    		if(val.getName().equals(name)){
    			return val;
    		}
    	}
		throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    }
    
    private void variableRenewal(){
    	ConsoleWindow.setVariableTable(variableList);
    }
    
    
    ////////////////
    //calculation//
    ///////////////
    private boolean judgeNumber(String a, String b, CON order)throws BlockRunException{
    	float fa;
    	float fb;
    	try{
    		fa = Float.parseFloat(a);
    		fb = Float.parseFloat(b);
    	}catch(Exception e){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	
    	switch(order){
    	case EQU:
    		return fa == fb;
    		
    	case LESS:
    		return fa < fb;
    	
    	case GREATER:
    		return fa > fb;
    		
    	case EQU_LESS:
    		return fa <= fb;
    		
    	case EQU_GREATER:
    		return fa >= fb;
    	}
    	
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
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

    
    
    //////////
    //other//
    /////////
    @Override
    public String toString(){
    	return "OB_Block:: name is -"+this.getGenusName();
    }
    
    /**
     * 変数名(name)とその値(value)を保持する構造体
     * @author shuhara
     */
    public class Variable{
    	
    	/**
    	 * @param name 変数名
    	 * @param value　値
    	 * 値は数値の場合、全てfloat型で扱う。値の型（type)に応じて小数点以下をカットすることで、整数型を表現する。
    	 * @param type　この変数の型(int, float, string, boolean);
    	 */
    	private String name;
    	private String value;
    	private ValType type;
    	
    	public Variable(String name, String value, ValType type){
    		this.name = name;
    		this.value = value;
    		this.type = type;
    	}
    	
    	public void setValue(String val){
    		this.value = val;
    	}
    	
    	public String getName(){
    		return this.name;
    	}
    	
    	public ValType getType(){
    		return this.type;
    	}
    	
    	public String getValue(){
    		//小数点を削除
    		if(this.type == ValType.INT){
    			return Integer.toString(Integer.parseInt(this.value));
    		}
    		
    		return this.value;
    	}
    	
    }
}
