package Language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import OverrideOpenblocks.OB_Workspace;

public class OutputCode {
//    	private Document document;
    	public static final String NULL_STRING = "";
    	private final String[] FILE_PATH = {"resources/JAVA.txt", "resources/NQC.txt"};
    	
    	
    	private OB_Workspace workspace;
    	/**
    	 * @param REMOVE 読飛ばす文字群
    	 */
    	private final char[] REMOVE = {' ', '\t', '\n'};
    	
    	public OutputCode(OB_Workspace workspace){
    		this.workspace = workspace;
    	}
    
//    	public OutputCode(Document doc){
//    		this.document = doc;
//    		XMLParse();
//    	}
//    	
//    	private void XMLParse(){
//            Node root = document.getDocumentElement();
//            NodeList pagesList = root.getChildNodes();
//            for(int a=0; a<pagesList.getLength(); a++){
//            	Node pages = pagesList.item(a);
//            	
//            	if("Pages".equals(pages.getNodeName())){
//            		NodeList PageList = pages.getChildNodes();
//            		for(int b=0; b<PageList.getLength(); b++){
//            			Node page = PageList.item(b);
//            			
//            			if("Page".equals(page.getNodeName())){
//            				NodeList pageBlocksList = page.getChildNodes();
//            				for(int c=0; c<pageBlocksList.getLength(); c++){
//            					Node pageBlocks = pageBlocksList.item(c);
//            					
//            					if("PageBlocks".equals(pageBlocks.getNodeName())){
//            						NodeList blockList = pageBlocks.getChildNodes();
//            						for(int d=0; d<blockList.getLength(); d++){
//            							Node block = blockList.item(d);
//            							
//            							/**ここからBlock操作**/
//            						
//            							if("Block".equals(block.getNodeName())){
//            								NamedNodeMap blockInfomation = block.getAttributes();
//            								
//            								//名前とid取得
//            								String blockName = blockInfomation.getNamedItem("genus-name").getNodeValue();
//            								int blockId=-1;
//            								ArrayList<ConnectorInfo> conId = new ArrayList<ConnectorInfo>();
//            								int nextId=-1;
//            								int beforeId =-1;
//            								String label="";
//            								
//            								
//            								String id = blockInfomation.getNamedItem("id").getNodeValue();
////	            								System.err.println("debug::BlockID::"+id);
//            								if(!(id == null)){
//            									blockId = Integer.parseInt(id);
//            								}
//            								
//            								NodeList blockInfoList = block.getChildNodes();
//            								for(int i=0; i<blockInfoList.getLength(); i++){
//            									Node blockInfo = blockInfoList.item(i);
//            									
//            									if("Sockets".equals(blockInfo.getNodeName())){
//            										NodeList socketList = blockInfo.getChildNodes();
//            										int conNum=0;//コネクションの箇所の順番：上から１、２、３
//            										
//            										for(int z=0; z<socketList.getLength(); z++){
//            											Node socket = socketList.item(z);
//            											
//            											if("BlockConnector".equals(socket.getNodeName())){
//            												NamedNodeMap socketInfomation = socket.getAttributes(); 
//            											
//            												String conType = socketInfomation.getNamedItem("connector-type").getNodeValue();
//            												String conBlock = socketInfomation.getNamedItem("con-block-id") != null ?
//            														socketInfomation.getNamedItem("con-block-id").getNodeValue(): "-1";
//           													
//            												conId.add(new ConnectorInfo(conNum++, conType, Integer.parseInt(conBlock)));
//            												
//            											}           											 
//            										}
//            									}
//            									
////	            									if("Plug".equals(blockInfo.getNodeName())){
////	            										NodeList plugList = blockInfo.getChildNodes();
////	            										for(int y=0; y<plugList.getLength(); y++){
////	            											Node plug = plugList.item(y);
////	            											
////	            											if("BlockConnector".equals(plug.getNodeName())){
////	            												NamedNodeMap plugInfomation = plug.getAttributes();
////	            												
////	            												nextId = plugInfomation.getNamedItem("con-block-id") != null ? 
////	            														Integer.parseInt(plugInfomation.getNamedItem("con-block-id").getNodeValue()): -1;
////	            												
////	            											}
////	            										}
////	            									}
//            									if("AfterBlockId".equals(blockInfo.getNodeName())){
//            										nextId = Integer.parseInt(blockInfo.getTextContent());
//            									}
//            									
//            									if("BeforeBlockID".equals(blockInfo.getNodeName())){
//            										beforeId = Integer.parseInt(blockInfo.getTextContent());
//            									}
//            									
//            									if("Label".equals(blockInfo.getNodeName())){
//            										label = blockInfo.getTextContent();
//            									}
//            								}
//            								
//            								BlockData blockdata = new BlockData(blockName, blockId, conId, nextId, beforeId, label);
//            								this.addBlockData(blockdata);
//            							}
//            						}
//            					}
//            				}
//            			}
//            		}
//            	}
//            }
//        }               	
//	    	private String getLanguage(Language lang){
//	    		setEncoding(filePath[lang.ordinal()]);
//	    	}
//	    	
//	    	private void setEncoding(String ){
//	    		
//	    	}
    	
    	private final String BR = "\n", TAB = "\t";
    	private final String START = "#";
    	private final String NAME_END = ":";
    	private final String DIV = "@";
    	private final String END = "$";
    	private ArrayList<BlockString> blockString;
    	
    	public ArrayList<BlockString> getBrockStringArray() throws FileNotFoundException, IOException{
    		if(blockString == null){
    			loadBlockDataFile(FILE_PATH[1]);
    		}
    		return blockString;
    	}
    	
    	public ArrayList<BlockString> getBrockStringArray(String filePath) throws FileNotFoundException, IOException{
    		this.blockString = new ArrayList<BlockString>();
    		loadBlockDataFile(filePath);
    		return blockString;
    	}
    	
    	
    	private void loadBlockDataFile(String filePath) throws FileNotFoundException, IOException{
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
//    						this.blockString.add(new BlockString(blockName, codeDivide(code), codeDivide(preText)));
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
    	
    	
    	
    	private Element getRootElement(String filePath){
    		Document doc = openFile(filePath);
    		
    		if(doc != null){
    			return doc.getDocumentElement();
    		}
    		System.err.println("ファイルが見つかりませんでした:"+filePath);
    		return null;
    	}
    	
    	private Document openFile(String filePath){
    		InputStream in = null;
            try {
                in = new FileInputStream(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document doc = null;
            
            try {
                builder = factory.newDocumentBuilder();
                doc = builder.parse(in);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            return doc;
    	}
    	
//    	private void loadBlockDataFile(String filePath) throws FileNotFoundException, IOException{
//    		
//    		InputStreamReader isr = null;
//    		try{
//    			  FileInputStream fis = new FileInputStream(filePath); 
//    			  isr = new InputStreamReader(fis, "UTF-8");
//    			  
//    			}catch(FileNotFoundException e){
//    			  System.out.println(e);
//    			}
//    		
//    		if(isr == null) return;
//    		else{
//    			blockString = new ArrayList<BlockString>();
//    			
//    			/**
//    			 * @param read true:読み込み中 false:非読み込み中
//    			 * @param tmp 一時保存
//    			 */
//    			int ch = 0;
//    			boolean read = false;
//    			String tmp="";
//    			String blockName="";
//    			
//    			while(ch != -1){ 
//       				ch = isr.read();
//    				String input = String.valueOf((char)ch);
//    				
////	    				System.out.print(input);
//    				
//    				if(!read){
//    					if(input.equals(START))	read = true;
//    					continue;
//    				}    				
//    				
//    				if(input.equals(BR) || input.equals(TAB)){
//    					continue;
//    				}
//    		
//    				if(input.equals(NAME_END)){
//    					blockName = tmp;
//    					tmp = "";
//    				}
//    				else if(input.equals(END)){
//    					String[] code = tmp.split(DIV);
//    					blockString.add(new BlockString(blockName, code));
//    					tmp="";
//    					blockName="";
//    					read=false;
//    				}else{
//    					tmp+=input;
//    				}
//    			}			
//    		}
//    	}
    	
 
//    	@Override
//    	public String toString(){
//    		String s="***Block Infomation***\n";
//    		
//    		ArrayList<BlockData> blockList = getBlockList();
//    		
//    		for(BlockData blockData: blockList){
//    			s += "BlockName::"+blockData.getName()+"\n";
//    			s += "BlockID::"+blockData.getId()+"\n";
//    			for(ConnectorInfo ci: blockData.getConnectionList()){
//        			s += "BlockSocketConnectionID::"+ci.getId()+"  ";
//        			s += "ConnectionNumber::"+ci.getconNum()+"  ";
//    				s += "ConnectorType::"+ci.getType()+"  ";
//    				s += "ConnectionID::"+Integer.toString(ci.getId())+"\n";
//    			}
//    			s += "NextBlockID::"+blockData.getNextId()+"\n\n";
//    		}
//    		return s;
//    	}
    	
}

