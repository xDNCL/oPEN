package save;

import java.io.File;
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

public class NormalizeIDs {
	public NormalizeIDs(File inputFile) throws ParserConfigurationException, SAXException, IOException {
		ArrayList<Long> blockIDs = new ArrayList<Long>();
		
		// 拡張子調整
		String fileName = inputFile.getName().toString();
		if(fileName.length() <= 4 || !fileName.substring(fileName.length()-4).equals(".xml")){
			// ファイル名が.xmlではない場合は.xmlとして保存する
			File renameFile = new File(inputFile.getPath()+".xml");
			inputFile = renameFile;
		}
		// パーサを作成
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		// パース
		Document doc = db.parse(inputFile);
		// ルート要素の取得
		Element root = doc.getDocumentElement();
		NodeList rootChildren = root.getElementsByTagName("Block");
		
		for (int i = 0; i < rootChildren.getLength(); i++) {
			Node node = rootChildren.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getNodeName().equals("Block")) {
//					System.out.println("BlockID: " + element.getAttribute("id"));
					long blockID = Long.parseLong(element.getAttribute("id"));
					if(!blockIDs.contains(blockID)) {
						blockIDs.add(blockID);
					}
					NodeList blockChildren = node.getChildNodes();
					for (int j = 0; j < blockChildren.getLength(); j++) {
						Node blockNode = blockChildren.item(j);
						if (blockNode.getNodeType() == Node.ELEMENT_NODE) {
							if (blockNode.getNodeName().equals("BeforeBlockId")) {
//								System.out.println("BeforeBlockID: " + blockNode.getTextContent());
								long beforeBlockID = Long.parseLong(blockNode.getTextContent());
								if(!blockIDs.contains(beforeBlockID)) {
									blockIDs.add(beforeBlockID);
								}
							} 
							if (blockNode.getNodeName().equals("AfterBlockId")) {
//								System.out.println("AfterBlockID:" + blockNode.getTextContent());
								long afterBlockID = Long.parseLong(blockNode.getTextContent());
								if(!blockIDs.contains(afterBlockID)) {
									blockIDs.add(afterBlockID);
								}
							} 
							if (blockNode.getNodeName().equals("Sockets")) {
								NodeList socketChildren = blockNode.getChildNodes();
								for (int k = 0; k < socketChildren.getLength(); k++) {
									Node socketNode = socketChildren.item(k);
									if (socketNode.getNodeName().equals("BlockConnector")) {
//										System.out.println("ConnBlockID:" + ((Element)socketNode).getAttribute("con-block-id"));
										long connBlockID = Long.parseLong(((Element)socketNode).getAttribute("con-block-id"));
										if(!blockIDs.contains(connBlockID)) {
											blockIDs.add(connBlockID);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		// すべて表示
		for (Long id : blockIDs) {
			System.out.println("BlockIDs[" + blockIDs.indexOf(id) + "]: " + id); 
		}
	}
}