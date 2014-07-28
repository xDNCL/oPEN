package Language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CodeWritter {
		
	private String code;
	private String DEF_LOCATION = "save";
	private String DEF_FILENAME = "test";
	private String DEF_DOMAIN = ".java";
		
	private PrintWriter pw;
	
	public CodeWritter(String code){
		this.code = code == null ? "/*no code*/": code;
		pw = null;
	}
	
	public void writting(){
		writting(DEF_LOCATION, DEF_FILENAME+DEF_DOMAIN);
	}
	
	public void writting(String fullFileName){
		writting(DEF_LOCATION, fullFileName);
	}
	
	public void writting(String location, String fullFileName){
		String fullpass = location + "/"+ fullFileName;
		openFile(fullpass);
		
		if(pw == null){
			System.err.println("Writting Error. File Name:"+fullpass);
			return;
		}
		
		pw.println(this.code);
		pw.close();
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


}
