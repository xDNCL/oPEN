package Language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CodeWritter {
	
	private final String BR = "\n";
	private final String TAB = "\t";
	
//	private String header = 
//			"public class OpenblocksCreateCode{\n" +
//			"public static void main(String[] args){\n";
//	private String futter = "}\n}";
	
	private String code;
	private String DEF_LOCATION = "save";
	private String DEF_FILENAME = "test";
	private String DEF_DOMAIN = ".java";
	
	private boolean isBR = false;
	private boolean isTAB = false;
	
	private PrintWriter pw;
	
	public CodeWritter(String code){
		this.code = code == null ? "/*no code*/": code;
		pw = null;
	}
	
//	private void addHeaderandFutter(){
//		this.code = header + code + futter;
//	}
	
	public void writting(){
		writting(DEF_LOCATION, DEF_FILENAME, DEF_DOMAIN);
	}
	
	public void writting(String filename, String domain){
		writting(DEF_LOCATION, filename, domain);
	}
	
	public void writting(String location, String filename, String domain){
		String fullpass = location + "/"+ filename + domain;
		openFile(fullpass);
		
		if(pw == null){
			System.err.println("Writting Error. File Name:"+fullpass);
			return;
		}
		
		pw.println(this.code);
		pw.close();
	}
	
	private String newLine(){
		if(isBR){
			return this.BR;
		}
		return "";
	}
	
	private String tab(int x){
		if(isTAB){
			String tab = "";
			for(int i=0;i<x;i++) tab += this.TAB;
			return tab;
		}
		return "";
	}
	
	private void openFile(String fullpass){		
		
		try{
			File file = new File(fullpass);
//			if (checkBeforeWritefile(file)){
			
				this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8")));
				
//			}
		}catch(FileNotFoundException e){
			System.err.println("File not found. FileName:"+fullpass+"\n"+e);
		}catch(IOException e2){
			e2.printStackTrace();
		}catch(Exception e3){
			e3.printStackTrace();
		}
	}
	
	  private boolean checkBeforeWritefile(File file){
		    if (file.exists()){
		    	if (file.isFile() && file.canWrite()){
		    		return true;
		    	}
		    }
		    return false;
	  }
	
	  public String getCode(){
		  return this.code;
	  }

}
