package OverrideOpenblocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pen.IntVgOutputWindow.IntVgOutputWindow;
import Exe.BlockRunException;
import Exe.ConsoleWindow;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.workspace.Workspace;

public class OB_Block extends Block{

	protected static Hashtable<String, Object> variableTable = new Hashtable<String, Object>();
	private static long counter = 0;
	private final int STOP = 10000;
	private IntVgOutputWindow ivw = new IntVgOutputWindow();

	void resetAll(){
		variableTable .clear();
	}

	public static Hashtable<String, Object> getVariableList(){
		return variableTable;
	}


	/**
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

    public OB_Block(Workspace workspace, Long id, String genusName, String label, boolean b) {
		super(workspace, id, genusName, label, b);
	}


    protected OB_Block(Block block){
    	super(block.getWorkspace(), block.getGenusName(), block.getBlockLabel());
    }

    public OB_Block(Workspace workspace, String genusName) {
        this(workspace, genusName, workspace.getEnv().getGenusWithName(genusName).getInitialLabel());
    }



    ///////
    //exe//
    ///////

    public ArrayList<OB_Block> runBlock() throws BlockRunException{
//    	System.out.println("now:"+this.getGenusName());
    	ArrayList<OB_Block> runList = new ArrayList<OB_Block>();

    	try{
	    	if(this.getGenusName().equals("start")){
	    		resetAll();
	    	}

	    	//set Value
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

	    	//System I/O
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


	    	//assigned Variable
	    	if(this.getGenusName().equals("substitution-number")){
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		this.setVariavle(this.getBlockLabel(), value);
	    	}
	    	if(this.getGenusName().equals("substitution-string")){
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		this.setVariavle(this.getBlockLabel(), value);
	    	}

	    	//Control
	    	if(this.getGenusName().equals("if")){
	    		boolean value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean();
	    		if(value == true){
	    			OB_Block next = this.getBlock(this.getSocketAt(1).getBlockID());
	    			if(next != null){
	    				runList.add(next);
	    			}
	    		}
	    		else{
	    			//nothing
	    		}
	    	}
	    	if(this.getGenusName().equals("ifelse")){
	    		boolean value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean();
	    		if(value){
	    			OB_Block nextTrue = this.getBlock(this.getSocketAt(1).getBlockID());
					if(nextTrue != null){
						runList.add(nextTrue);
					}
	    		}
	    		else{
	    			OB_Block nextFalse = this.getBlock(this.getSocketAt(2).getBlockID());
	    			if(nextFalse != null){
	    				runList.add(nextFalse);
	    			}
	    		}
	    	}
	    	if(this.getGenusName().equals("repeat")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}else if(value instanceof Double){
	    			throw new BlockRunException(this, "「回数」に実数型は認められません。");
	    		}else{
	    			throw new BlockRunException(this, BlockRunException.CAST_ERROR);
	    		}
	    		for(int i=0; i<x; i++){
	    			runList.add(this.getBlock(this.getSocketAt(1).getBlockID()));
	    		}
	    	}
	    	if(this.getGenusName().equals("repeat-if")){
	    		boolean value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateBoolean();

	    		if(value){
	    			runList.add(this.getBlock(this.getSocketAt(1).getBlockID()));
	    			runList.add(this);
	    			return runList;
	    		}
	    	}


	    	//add other...

	    	//gSetLine

	    	if(this.getGenusName().equals("gSetLineShape")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetLineShape(x);
	    	}

	    	if(this.getGenusName().equals("gSetLineWidth")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetLineWidth(x);
	    	}

	    	if(this.getGenusName().equals("gSetArrowType")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetArrowType(x);
	    	}

	    	if(this.getGenusName().equals("gSetArrowDir")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetArrowDir(x);
	    	}

	    	if(this.getGenusName().equals("gSetFillColor")){
	    		int r = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			r = Integer.valueOf(value.toString());
	    		}
	    		int g = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			g = Integer.valueOf(value.toString());
	    		}
	    		int b = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			b = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetFillColor(r, g, b);
	    	}

	    	if(this.getGenusName().equals("gSetDotShape")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetDotShape(x);
	    	}

	    	if(this.getGenusName().equals("gSetTextColor")){
	    		int r = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			r = Integer.valueOf(value.toString());
	    		}
	    		int g = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			g = Integer.valueOf(value.toString());
	    		}
	    		int b = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			b = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetTextColor(r, g, b);
	    	}

	    	//gSetFont
	    	/*
	    	if(this.getGenusName().equals("gSetFont")){
	    		String str = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();

	    		ivw.gSetFont(str);
	    	}
			*/
			
	    	if(this.getGenusName().equals("gSetFontType")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetFontType(x);
	    	}

	    	if(this.getGenusName().equals("gSetFontSize")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}

	    		ivw.gSetFontSize(x);
	    	}

	    	/*gDrawText
	    	if(this.getGenusName().equals("gDrawText")){
	    		String str ="";
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof String){
	    			str = value.toString();
	    		}
	    		int x = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y = Integer.valueOf(value.toString());
	    		}
	    		ivw.gDrawText(str,x, y);
	    	}
	    	*/

	    	if(this.getGenusName().equals("gDrawPoint")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y = Integer.valueOf(value.toString());
	    		}
	    		ivw.gDrawPoint(x, y);
	    	}

	    	if(this.getGenusName().equals("gDrawLine")){
	    		int x1 = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x1 = Integer.valueOf(value.toString());
	    		}
	    		int y1 = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y1 = Integer.valueOf(value.toString());
	    		}
	    		int x2 = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x2 = Integer.valueOf(value.toString());
	    		}
	    		int y2 = 0;
	    		value = this.getBlock(this.getSocketAt(3).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y2 = Integer.valueOf(value.toString());
	    		}
	    		ivw.gDrawLine(x1,y1,x2,y2);
	    	}

	    	if(this.getGenusName().equals("gDrawBox")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    			x = Integer.valueOf(value.toString());
	    			System.out.println(x);
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    			y = Integer.valueOf(value.toString());
	    			System.out.println(y);
	    		int width = 100;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    			width = Integer.valueOf(value.toString());
	    		int height = 120;
	    		value = this.getBlock(this.getSocketAt(3).getBlockID()).evaluateValue();
	    			height = Integer.valueOf(value.toString());
	    		ivw.gDrawBox(x, y,width,height);
	    	}

	    	if(this.getGenusName().equals("gFillBox")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y = Integer.valueOf(value.toString());
	    		}
	    		int width = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			width = Integer.valueOf(value.toString());
	    		}
	    		int height = 0;
	    		value = this.getBlock(this.getSocketAt(3).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			height = Integer.valueOf(value.toString());
	    		}
	    		ivw.gFillBox(x, y,width,height);
	    	}

	    	if(this.getGenusName().equals("gDrawOval")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y = Integer.valueOf(value.toString());
	    		}
	    		int width = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			width = Integer.valueOf(value.toString());
	    		}
	    		int height = 0;
	    		value = this.getBlock(this.getSocketAt(3).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			height = Integer.valueOf(value.toString());
	    		}
	    		ivw.gDrawOval(x, y,width,height);
	    	}

	    	if(this.getGenusName().equals("gFillOval")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y = Integer.valueOf(value.toString());
	    		}
	    		int width = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			width = Integer.valueOf(value.toString());
	    		}
	    		int height = 0;
	    		value = this.getBlock(this.getSocketAt(3).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			height = Integer.valueOf(value.toString());
	    		}
	    		ivw.gFillOval(x, y,width,height);
	    	}

	    	if(this.getGenusName().equals("gDrawCircle")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y = Integer.valueOf(value.toString());
	    		}
	    		int r = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			r = Integer.valueOf(value.toString());
	    		}

	    		ivw.gDrawCircle(x, y, r);
	    	}

	    	if(this.getGenusName().equals("gFillCircle")){
	    		int x = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			x = Integer.valueOf(value.toString());
	    		}
	    		int y = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			y = Integer.valueOf(value.toString());
	    		}
	    		int r = 0;
	    		value = this.getBlock(this.getSocketAt(2).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			r = Integer.valueOf(value.toString());
	    		}

	    		ivw.gFillCircle(x, y, r);
	    	}

	    	if(this.getGenusName().equals("gOpenWindow")){
	    		int width = 0;
	    		Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			width = Integer.valueOf(value.toString());
	    		}
	    		int height = 0;
	    		value = this.getBlock(this.getSocketAt(1).getBlockID()).evaluateValue();
	    		if(value instanceof Integer){
	    			height = Integer.valueOf(value.toString());
	    		}
	    		ivw.gOpenWindow(width, height);
	    	}


	    	if(this.getGenusName().equals("gSaveWindow")){
	    		ivw.gSaveWindow("","");
	    	}

	    	if(this.getGenusName().equals("gCloseWindow")){
	    		ivw.gCloseWindow();
	    	}

	    	if(this.getGenusName().equals("gClearWindow")){
	    		ivw.gClearWindow();
	    	}
	    	//

	    	if(this.next() != null){
	    		runList.add(this.next());
	    	}
			return runList;

    	}catch(NullPointerException e1){
    		//null = no Block
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}

    }

	/**
	 * @return Returns the value as a String
	 * @throws BlockRunException
	 */
    public Object evaluateValue() throws BlockRunException{

    	try{
	    	//if this BlockType is data
	    	if(this.getGenusName().equals("number")){
	    		String value = this.getBlockLabel();
	    		if(value.contains(".")){
	    			try{
    					return new Double(Double.parseDouble(value));
    				}catch(Exception e){
    					throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
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
	    	if(this.getGenusName().equals("pi")){
	    		return new Double(Math.PI);
	    	}
	    	if(this.getGenusName().equals("random")){
	    		 Object value = this.getBlock(this.getSocketAt(0).getBlockID()).evaluateValue();
	    		 if(value instanceof Integer){
	    			 return new Integer((int)Math.random() * Integer.valueOf(value.toString()));
	    		 }
	    		 if(value instanceof Double){
	    			 return new Double(Math.random() * Double.valueOf(value.toString()));
	    		 }
	    	}
	    	if(this.getGenusName().equals("keyboard-input-number") || this.getGenusName().equals("keyboard-input-string")){
	    		ConsoleWindow.setForcus();
	    		String inputData = "";
	    		try {
	    		      while(!ConsoleWindow.isEntered()){
							Thread.sleep(100);
	    		      	}
	    		      inputData = ConsoleWindow.getInputformText();
	    		    }catch (InterruptedException e) {
	    		      throw new BlockRunException(this, BlockRunException.UNEXPECTED);
	    		     }
	    		Object result = null;
	    		//文字列用
	    		if(this.getGenusName().equals("keyboard-input-string")){
	    			return new String(inputData);
	    		}
	    		//以下数値用
		    	try{
		    		if(inputData.contains(".")){
		    			result = new Double(Double.valueOf(inputData));
		    		}else{
		    			result = new Integer(Integer.valueOf(inputData));
		    		}
		 		}catch(NumberFormatException nm){
		 			counter++;
		 			if(counter > STOP){throw new BlockRunException("無限ループが発生した可能性があります。");}
		    		System.out.println("数値以外が入力されました。もう一度入力してください。");
		    		return this.evaluateValue();
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		throw new BlockRunException(this, BlockRunException.UNEXPECTED);
		    	}
		    	return result;
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

	    	//add other ...

	    	//


    	}catch(NullPointerException e1){
    		// null = no connection Block
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}

    	throw new BlockRunException(this);
    }

    /**
     * Boolean
     * boolean connector
     * @return boolean
     */
    public boolean evaluateBoolean() throws BlockRunException{
    	try{
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
	    	if(this.getGenusName().equals("equals") || this.getGenusName().equals("equals-s")){
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
    	}catch(NullPointerException e1){
    		//null = no connection block
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
    	//add other...

    	throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
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
    	if(name == null || name.equals("") || value == null){
    		throw new BlockRunException(this, BlockRunException.NO_NAME);
    	}
    	if(variableTable.get(name) != null){
    		throw new BlockRunException(this, BlockRunException.DUPLICATION);
    	}
    	variableTable.put(name, value);
    	ConsoleWindow.setVariableTable(variableTable);
    }

    private void setVariavle(String name, Object value)throws BlockRunException{

    	if(variableTable.get(name) != null){
       		try{
       			Object oldValue = variableTable.get(name);
       			//String variable
       			if(oldValue instanceof String){
       				variableTable.put(name, new String(value.toString()));
       			}
       			//Number variable
       			else if(value instanceof Double){
       				if(oldValue instanceof Double){
       					variableTable.put(name, Double.valueOf(value.toString()));
       				}
       				else{
       					throw new BlockRunException(this, "--");
       				}
       			}
       			else if(value instanceof Long){
       				if(oldValue instanceof Long){
       					variableTable.put(name, Long.valueOf(value.toString()));
       				}
       				else if(oldValue instanceof Double){
       					variableTable.put(name, Double.valueOf(value.toString()));
       				}
       				else{
       					//
       					throw new BlockRunException(this, "--");
       				}
       			}
       			else if(value instanceof Integer){
       				if(oldValue instanceof Integer){
       					variableTable.put(name, Integer.valueOf(value.toString()));
       				}
       				else if(oldValue instanceof Double){
       					variableTable.put(name, Double.valueOf(value.toString()));
       				}
       				else if(oldValue instanceof Long){
       					variableTable.put(name, Long.valueOf(value.toString()));
       				}
       			}
       			//
       			ConsoleWindow.setVariableTable(variableTable);
       		}catch(BlockRunException e){
       			throw new BlockRunException(this, BlockRunException.CAST_ERROR);
       		}
       		//
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
    	if(a instanceof Boolean){
    		return !(Boolean)a;
    	}
    	throw new BlockRunException(this, BlockRunException.TRANSLATION_MISSING);
    }

    private boolean lessthan(Object a, Object b)throws BlockRunException{
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
    	if(a instanceof String || b instanceof String){
    		//
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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
    	if(a == null || b == null){
    		throw new BlockRunException(this, BlockRunException.BLOCK_IS_NULL);
    	}
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

    /**
     * Loads Block information from the specified node and return a Block
     * instance with the loaded information
     * @param workspace The workspace in use
     * @param node Node cantaining desired information
     * @return Block instance containing loaded information
     */
    public static OB_Block loadBlockFrom(Workspace workspace, Node node, HashMap<Long, Long> idMapping){
        OB_Block block = null;
        Long id = null;
        String genusName = null;
        String label = null;
        String pagelabel = null;
        String badMsg = null;
        Long beforeID = null;
        Long afterID = null;
        BlockConnector plug = null;
        ArrayList<BlockConnector> sockets = new ArrayList<BlockConnector>();
        HashMap<String, String> blockLangProperties = null;
        boolean hasFocus = false;

        //stub information if this node contains a stub
        boolean isStubBlock = false;
        String stubParentName = null;
        String stubParentGenus = null;
        Pattern attrExtractor = Pattern.compile("\"(.*)\"");
        Matcher nameMatcher;

        if (node.getNodeName().equals("BlockStub")) {
            isStubBlock = true;
            Node blockNode = null;
            NodeList stubChildren = node.getChildNodes();
            for (int j = 0; j < stubChildren.getLength(); j++) {
                Node infoNode = stubChildren.item(j);
                if (infoNode.getNodeName().equals("StubParentName")) {
                    stubParentName = infoNode.getTextContent();
                } else if (infoNode.getNodeName().equals("StubParentGenus")) {
                    stubParentGenus = infoNode.getTextContent();
                } else if (infoNode.getNodeName().equals("Block")) {
                    blockNode = infoNode;
                }
            }
            node = blockNode;
        }

        if (node.getNodeName().equals("Block")) {
            //load attributes
            nameMatcher = attrExtractor.matcher(node.getAttributes().getNamedItem("id").toString());
            if (nameMatcher.find()) {
                id = translateLong(workspace, Long.parseLong(nameMatcher.group(1)), idMapping);
            }
            nameMatcher = attrExtractor.matcher(node.getAttributes().getNamedItem("genus-name").toString());
            if (nameMatcher.find()) {
                genusName = nameMatcher.group(1);
            }
            //load optional items
            Node opt_item = node.getAttributes().getNamedItem("has-focus");
            if (opt_item != null) {
                nameMatcher = attrExtractor.matcher(opt_item.toString());
                if (nameMatcher.find()) //will be true
                {
                    hasFocus = nameMatcher.group(1).equals("yes") ? true : false;
                }
            }

            //load elements
            NodeList children = node.getChildNodes();
            Node child;
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (child.getNodeName().equals("Label")) {
                    label = child.getTextContent();
                } else if (child.getNodeName().equals("PageLabel")) {
                    pagelabel = child.getTextContent();
                } else if (child.getNodeName().equals("CompilerErrorMsg")) {
                    badMsg = child.getTextContent();
                } else if (child.getNodeName().equals("BeforeBlockId")) {
                    beforeID = translateLong(workspace, Long.parseLong(child.getTextContent()), idMapping);
                } else if (child.getNodeName().equals("AfterBlockId")) {
                    afterID = translateLong(workspace, Long.parseLong(child.getTextContent()), idMapping);
                } else if (child.getNodeName().equals("Plug")) {
                    NodeList plugs = child.getChildNodes(); //there should only one child
                    Node plugNode;
                    for (int j = 0; j < plugs.getLength(); j++) {
                        plugNode = plugs.item(j);
                        if (plugNode.getNodeName().equals("BlockConnector")) {
                            plug = BlockConnector.loadBlockConnector(workspace, plugNode, idMapping);
                        }
                    }
                } else if (child.getNodeName().equals("Sockets")) {
                    NodeList socketNodes = child.getChildNodes();
                    Node socketNode;
                    for (int k = 0; k < socketNodes.getLength(); k++) {
                        socketNode = socketNodes.item(k);
                        if (socketNode.getNodeName().equals("BlockConnector")) {
                            sockets.add(BlockConnector.loadBlockConnector(workspace, socketNode, idMapping));
                        }
                    }
                } else if (child.getNodeName().equals("LangSpecProperties")) {
                    blockLangProperties = new HashMap<String, String>();
                    NodeList propertyNodes = child.getChildNodes();
                    Node propertyNode;
                    String key = null;
                    String value = null;
                    for (int m = 0; m < propertyNodes.getLength(); m++) {
                        propertyNode = propertyNodes.item(m);
                        if (propertyNode.getNodeName().equals("LangSpecProperty")) {
                            nameMatcher = attrExtractor.matcher(propertyNode.getAttributes().getNamedItem("key").toString());
                            if (nameMatcher.find()) //will be true
                            {
                                key = nameMatcher.group(1);
                            }
                            opt_item = propertyNode.getAttributes().getNamedItem("value");
                            if (opt_item != null) {
                                nameMatcher = attrExtractor.matcher(opt_item.toString());
                                if (nameMatcher.find()) //will be true
                                {
                                    value = nameMatcher.group(1);
                                }
                            } else {
                                value = propertyNode.getTextContent();
                            }
                            if (key != null && value != null) {
                                blockLangProperties.put(key, value);/*
                                if(key.equals("xml"))
                                System.err.println("VALUE OF XML: "+value);*/
                                key = null;
                                value = null;
                            }
                        }
                    }
                }
            }

            assert genusName != null && id != null : "Block did not contain required info id: " + id + " genus: " + genusName;
            //create block or block stub instance
            if (!isStubBlock) {
                if (label == null) {
                    block = new OB_Block(workspace, id, genusName, workspace.getEnv().getGenusWithName(genusName).getInitialLabel(), true);
                } else {
                    block = new OB_Block(workspace, id, genusName, label, true);
                }
            } else {
                assert label != null : "Loading a block stub, but has a null label!";
                block = (OB_Block)Block.loadBlockFrom(workspace, node, idMapping);
            }

            if (plug != null) {
                // Some callers can change before/after/plug types. We have
                // to synchronize so that we never have both.
                assert beforeID == null && afterID == null;
                block.plug = plug;
                block.removeBeforeAndAfter();
            }

            if (sockets.size() > 0) {
            	block.sockets = sockets;
            }

            if (beforeID != null) {
                block.before.setConnectorBlockID(beforeID);
            }
            if (afterID != null) {
                block.after.setConnectorBlockID(afterID);
            }
            if (pagelabel != null) {
                block.pageLabel = pagelabel;
            }
            if (badMsg != null) {
                block.isBad = true;
                block.badMsg = badMsg;
            }
            block.hasFocus = hasFocus;

            //load language dependent properties
            if (blockLangProperties != null && !blockLangProperties.isEmpty()) {
                block.properties = blockLangProperties;
            }

            return block;
        }

        return null;
    }
//
//    void setSockets(List<BlockConnector> sockets){
//    	super.sockets = sockets;
//    }
//
//    void setProperties(HashMap<String, String> properties){
//    	super.properties = properties;
//    }
//
    void removeBeforeAndAfter(){
    	super.after = null;
    	super.before = null;
    }
    //////////
    //other//
    /////////
    @Override
    public String toString(){
    	return "OB_Block:: name is -"+this.getGenusName();
    }

}
