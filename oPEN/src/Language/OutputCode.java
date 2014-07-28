package Language;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Exe.BlockRunException;
import OverrideOpenblocks.OB_Workspace;

public class OutputCode {
//    	private Document document;
    	public static final String NULL_STRING = "";
    	private ArrayList<BlockString> blockString;
    	
    	private OB_Workspace workspace;
    	
    	private String code;
    	/**
    	 * @param REMOVE 読飛ばす文字群
    	 */
    	private final char[] REMOVE = {' ', '\t', '\n'};
    	
    	public OutputCode(OB_Workspace workspace){
    		this.workspace = workspace;
    	}
    		
    	public boolean loadCodeFile(String filePath){
    		try{
    			this.blockString = this.getBlockStringArray(filePath);
    			return true;
    		}catch(Exception e){
    			return false;
    		}
    	}
    	
    	/**
    	 * デフォルトフォルダ(BlockEducation/save/)に保存
    	 * @param outFileName
    	 * @param outFileExtends
    	 */
    	public void writteCode(String outFileFullName){
    		ToCode toCode = new ToCode(this.blockString, this.workspace);
    		try{
    			this.code = toCode.getCode();
    		}catch(BlockRunException e1){
    			System.out.println("コード作成に失敗しました。");
    			return;
    		}catch(Exception e2){
    			e2.printStackTrace();
    			System.out.println("予期せぬエラーが発生したため、コード作成に失敗しました。");
    			return;
    		}
    		CodeWritter codeWritter = new CodeWritter(this.getCode());
    		codeWritter.writting(outFileFullName);
    		System.out.println("コードを生成しました。");
    	}
   
    	/**
    	 * 指定した場所に保存
    	 * @param location
    	 * @param outFileName
    	 * @param outFileExtends
    	 */
    	public void writteCode(String location, String outFileFullName){
    		ToCode toCode = new ToCode(this.blockString, this.workspace);
    		try{
    			this.code = toCode.getCode();
    		}catch(BlockRunException e1){
    			System.out.println("コード作成に失敗しました。");
    			return;
    		}catch(Exception e2){
    			System.out.println("予期せぬエラーが発生したため、コード作成に失敗しました。");
    			return;
    		}
    		CodeWritter codeWritter = new CodeWritter(this.getCode());
    		codeWritter.writting(location, outFileFullName);
    		System.out.println("コードを生成しました。");
    	}
    	
    	public String getCode(){
    		if(this.code != null){
    			return this.code;
    		}
    		return "";
    	}
    	    	
    	private ArrayList<BlockString> getBlockStringArray(String filePath) throws FileNotFoundException, IOException{
    		this.blockString = new ArrayList<BlockString>();
    		loadBlockCodeFile(filePath);
    		return blockString;
    	}
    	
    	private void loadBlockCodeFile(String filePath) throws FileNotFoundException, IOException{
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document doc;
            Element originalRoot = null;
            
            try {
                builder = factory.newDocumentBuilder();
                doc = builder.parse(new FileInputStream(filePath));
                originalRoot = doc.getDocumentElement();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            if(originalRoot == null){
            	System.err.println("output xml ERROR;");
            }

            NodeList root = originalRoot.getElementsByTagName("BlockCodes");
    		Node blockCodes;
    		
    		for(int i=0; i<root.getLength(); i++){
    			blockCodes = root.item(i);
    			
    			if(blockCodes.getNodeName().equals("BlockCodes")){
    				NodeList blockCodeList = blockCodes.getChildNodes();
    				Node blockCode;
    				
    				for(int j=0; j<blockCodeList.getLength(); j++){
    					blockCode = blockCodeList.item(j);
    					
    					if(blockCode.getNodeName().equals("BlockCode")){
    						String blockName = blockCode.getAttributes().getNamedItem("name").getNodeValue().toString();
    						String code = NULL_STRING;
    						String preText = NULL_STRING;
    						NodeList codeInfoList = blockCode.getChildNodes();
    						Node codeInfo;
    						
    						for(int k=0; k<codeInfoList.getLength(); k++){
    							codeInfo = codeInfoList.item(k);
    							
    	    					if(codeInfo.getNodeName().equals("CodeText")){
    	    						code = codeInfo.getTextContent();
    	    					}
    	    					else if(codeInfo.getNodeName().equals("PreText")){
    	    						preText = codeInfo.getTextContent();
    	    					}
    						}
    						this.blockString.add(new BlockString(blockName, codeDivide(code), codeDivide(preText)));
    					}
    				}
    			}			
    		}
    	}
    	
    	private String erase(String original){
    		String result = "";
    		char ch;
    		
    		for(int i=0; i<original.length(); i++){
    			ch = original.charAt(i);
    			boolean erase = false;
    			
    			for(int j=0; j<REMOVE.length; j++){
    				if(ch == REMOVE[j]){
    					erase = true;
    					break;
    				}
				}
    			if(erase){
    				continue;
    			}
    			result += ch;
    		}
    		return result;
    	}
    	
    	private String[] codeDivide(String original){
    		String processing = erase(original);
    		String[] divCode = processing.split("@");
    		return divCode;
    	}
    	    	
}

