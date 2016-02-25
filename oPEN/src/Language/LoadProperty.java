package Language;

import java.io.*;
import java.util.Properties;


public class LoadProperty {

	private InputStream inputStream;
	private Properties configuration = new Properties();
	
	private final String DEFAULT_PATH = "resources/blockInfo.xml";
	private final String DEFAULT_FOLDER = "Stage/";
	
	public LoadProperty(String filePath) {
		try {
			inputStream = new FileInputStream(new File(filePath));
			configuration.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		if(inputStream == null){
			System.err.println("not found "+ filePath );
		}
		else{
			load();
		}
	}
	
	
	private String select_BlockAllData_Address;
	private String select_BlockDrawerList_Address;
	private String select_BlockDrawerList_Folder;
	
	
	private void load(){
		
		try{
			select_BlockAllData_Address = configuration.getProperty("select_BlockAllData").toString();
		}catch(Exception e){
			select_BlockAllData_Address = DEFAULT_PATH;
		}
		
		if (configuration.getProperty("select_BlockDrawerList") == null) {
			select_BlockDrawerList_Address = "FREE";
		} else {
			select_BlockDrawerList_Address = configuration.getProperty("select_BlockDrawerList").toString();
		}
		
		try{
			select_BlockDrawerList_Folder = DEFAULT_FOLDER +configuration.getProperty("select_BlockDrawerList_Folder").toString();
		}catch(Exception e){
			select_BlockDrawerList_Folder = DEFAULT_FOLDER;
		}
		
	}
	
	public boolean isSelected(){
		if(select_BlockDrawerList_Address.equals("FREE")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String getBlockDrawerListAddress(){
		return select_BlockDrawerList_Address.equals("yes") ? DEFAULT_PATH : select_BlockDrawerList_Folder + "/" + this.select_BlockDrawerList_Address;
	}
	
	public String getResourcesFolderPath(){
		return this.select_BlockDrawerList_Folder;
	}
	
//	public String getStageNam(){
//		
//	}
	
	public String getBlockAllDataAddress(){
//		System.out.println(this.select_BlockAllData_Address);
		return this.select_BlockAllData_Address;
	}
	
	
}
