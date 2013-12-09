package OverrideOpenblocks;

import java.util.Hashtable;

import Debug.BlockRunException;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.workspace.Workspace;

public class OB_Block extends Block{
			
	protected static Hashtable<String, Object> variableTable = new Hashtable<String, Object>();
	
	//変数テーブルの初期化用
	void resetAll(){
		variableTable .clear();
	}
		
	public static Hashtable<String, Object> getVariableList(){
		return variableTable;
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
    		createVariable(name, new Integer(0));
    	}
    	if(this.getGenusName().equals("setDouble")){
    		String name = this.getBlock(this.getSocketAt(0).getBlockID()).getBlockLabel();
    		createVariable(name, new Double(0));
    	}
    	if(this.getGenusName().equals("setString")){
    		String name = this.getBlock(this.getSocketAt(0).getBlockID()).getBlockLabel();
    		createVariable(name, new String(""));
    	}
    	if(this.getGenusName().equals("setBoolean")){
    		String name = this.getBlock(this.getSocketAt(0).getBlockID()).getBlockLabel();
    		createVariable(name, new Boolean(true));
    	}
    	
    	//標準出力系
    	if(this.getGenusName().equals("print-number")){
    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.print(value);
    	}
    	if(this.getGenusName().equals("println-number")){
    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.println(value);
    	}
    	if(this.getGenusName().equals("print-string")){
    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.print(value.toString());
    	}
    	if(this.getGenusName().equals("println-string")){
    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		System.out.println(value.toString());
    	}
    	
    	//代入系
    	if(this.getGenusName().equals("substitution-number")){
    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		this.setVariavle(this.getBlockLabel(), value);
    	}
    	if(this.getGenusName().equals("substitution-string")){
    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
    		this.setVariavle(this.getBlockLabel(), value);
    	}
    	
    	//制御系
    	if(this.getGenusName().equals("if")){
    		boolean value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean();
    		if(value == true){
    			this.getBlock(this.getSocketAt(1).getBlockID()).runBlock();
    		}
    		else{
    			//nothing
    		}
    	}
    	if(this.getGenusName().equals("ifelse")){
    		boolean value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean();
    		if(value){
    			this.getBlock(this.getSocketAt(1).getBlockID()).runBlock();
    		}
    		else{
    			this.getBlock(this.getSocketAt(2).getBlockID()).runBlock();
    		}
    	}
    	if(this.getGenusName().equals("repeat-if")){
    		boolean value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean();
    		
    		while(value){
	   			this.getBlock(this.getSocketAt(1).getBlockID()).runBlock();
	   			value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean();
    		}
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
    public Object evaluateValue() throws BlockRunException{
    	//if this block is data
    	if(this.getGenusName().equals("number")){
    		String value = this.getBlockLabel();
    		for(int i=0; i<value.length(); i++){
    			if(value.charAt(i) == '.'){
    				try{
    					return new Double(Double.parseDouble(value));
    				}catch(Exception e){
    					throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    				}
    			}
    		}
    		return new Integer(Integer.parseInt(value));
    	}
    	if(this.getGenusName().equals("string")){
    		return this.getBlockLabel();
    	}
    	if(this.getGenusName().equals("variable-Number")){
    		if(variableTable.get(this.getBlockLabel()) == null){
    			throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    		}
    		return variableTable.get(this.getBlockLabel());
    	}
    	if(this.getGenusName().equals("variable-String")){
    		if(variableTable.get(this.getBlockLabel()) == null){
    			throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    		}
    		return variableTable.get(this.getBlockLabel());
    	}
    
    	//if this block is calculation
    	if(this.getGenusName().equals("sum")){
    		return this.sum(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("difference")){
    		return this.difference(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("product")){
    		return this.product(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("surplus")){
    		return this.surplus(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("quotient")){
    		return this.quotient(this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(), 
    				this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
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
    public boolean evaluateBoolean() throws BlockRunException{
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
    	
    	//calculation block
    	if(this.getGenusName().equals("equals")){
    		return this.equals(getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(),
    				getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("not-equals")){
    		return this.notEquals(getBlock(this.getSocketAt(0).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("lessthan")){
    		return this.lessthan(getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(),
    				getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("lessthanorequalto")){
    		return this.lessthanorEqualto(getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(),
    				getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("greaterthan")){
    		return this.greaterthan(getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(),
    				getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
    	}
    	if(this.getGenusName().equals("greaterthanorequalto")){
    		return this.greaterthanorEqualto(getBlock(this.getSocketAt(0).getBlockID()).evaluateValue(),
    				getBlock(this.getSocketAt(1).getBlockID()).evaluateValue());
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
    	//
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
    
    private void createVariable(String name, Object value)throws BlockRunException{
    	if(name == null || value == null){
    		throw new BlockRunException(this, BlockRunException.NO_NAME);
    	}
    	if(variableTable.get(name) != null){
    		throw new BlockRunException(this, BlockRunException.DUPLICATION);
    	}
    	variableTable.put(name, value);
    }
    
    private void setVariavle(String name, Object value)throws BlockRunException{
    	
    	if(variableTable.get(name) != null){
       		Object oldValue = variableTable.get(name);
       		try{
    		if(oldValue instanceof Integer){
    			variableTable.put(name, (Integer)value);
    		}
    		if(oldValue instanceof Double){
    			variableTable.put(name, (Double)value);
    		}
    		if(oldValue instanceof String){
    			variableTable.put(name, (String)value);
    		}
    		if(oldValue instanceof Long){
    			variableTable.put(name, (Long)value);
    		}
       		}catch(ClassCastException e){
       			throw new BlockRunException(this, BlockRunException.CAST_ERROR);
       		}
       		//起こってはならないException
       		catch(Exception e){
       			throw new BlockRunException(this, BlockRunException.UNEXPECTED);
       		}
    	}
    	else{
    		throw new BlockRunException(this, BlockRunException.NO_VARIABLE);
    	}
    }

    

        
    ///////////
    //boolean//
    ///////////
    
    private boolean equals(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		return a.toString().equals(b.toString());
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) == Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) == Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) == Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);    	
    }
    
    private boolean notEquals(Object a)throws BlockRunException{
    	if(a instanceof Boolean){
    		return !(Boolean)a;
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);    	
    }
    
    private boolean lessthan(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) < Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) < Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) < Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);    	
    }
    
    private boolean lessthanorEqualto(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) <= Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) <= Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) <= Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);    	
    }
    
    private boolean greaterthan(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) > Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) > Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) > Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);    	
    }
    
    private boolean greaterthanorEqualto(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) >= Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) >= Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) >= Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);    	
    }
    
    
    ////////////////
    //calculation//
    ///////////////
    
    private Object sum(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		return a.toString() + b.toString();
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) + Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) + Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) + Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    }
    
    private Object difference(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) - Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) - Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) - Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    }
    
    private Object product(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) * Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) * Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) * Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    }
    
    private Object quotient(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) / Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) / Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) / Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    }
    
    private Object surplus(Object a, Object b)throws BlockRunException{
    	if(a instanceof String || b instanceof String){
    		throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    	}
    	else if(a instanceof Double || b instanceof Double) {
    		return Double.valueOf(a.toString()) % Double.valueOf(b.toString());
    	}
    	else if(a instanceof Long || b instanceof Long){
    		return Long.valueOf(a.toString()) % Long.valueOf(b.toString());
    	}
    	else if(a instanceof Integer || b instanceof Integer){
    		return Integer.valueOf(a.toString()) % Integer.valueOf(b.toString());
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    }
        
    //////////
    //other//
    /////////
    @Override
    public String toString(){
    	return "OB_Block:: name is -"+this.getGenusName();
    }

}
