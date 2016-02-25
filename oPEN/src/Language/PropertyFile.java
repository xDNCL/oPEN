package Language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFile extends Properties{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String BR = "new_line";
	private final String COMPLEMENT = "code_complement";
	
	private int br;
	private int codeComplement;
	
	private final String PROPERTY_FILE_PATH = "resources/Property.txt";
	
	PropertyFile(){
		InputStream inputStream = null;
		try{
			inputStream = new FileInputStream(new File(PROPERTY_FILE_PATH));
			load(inputStream);
		}catch(IOException e){
//			System.err.println("PropertyFile is not found.");
		}
		
		
		if(inputStream == null){
			this.setDefault();
		}else{
			this.loadProperties(inputStream);
		}
		
	}
	
	private void setDefault(){
		br = 0;
		codeComplement = 0;
		
		
	}
	
	
	private void loadProperties(InputStream inputStream){
	
		try{
			this.br = Integer.parseInt(this.getProperty(BR));
		}catch(NullPointerException np){
			System.err.println('"' + BR + '"' + "can not be found. set the default value.");
			this.br = 0;
		}
		
		try{
			this.codeComplement = Integer.parseInt(this.getProperty(COMPLEMENT));
		}catch(NullPointerException np){
			System.err.println('"' + COMPLEMENT + '"' + "can not be found. set the default value.");
			this.codeComplement = 0;
		}
		
	
	}
	
	/**
	 * @return true: new line is ON. false is OFF.
	 */
	public boolean newLineFlag(){
		return this.br == 1 ? true : false;
	}

	/**
	 * @return true:complement is ON.  false is OFF.
	 */
	public boolean complementFlag(){
		return this.codeComplement == 1 ? true : false;
	}

}
