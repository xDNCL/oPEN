package save;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SetIDs {
	Document doc;
	public SetIDs(File inputFile, ArrayList<Long> blockIDs) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// パーサを作成
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		// パース
		doc = db.parse(inputFile);
		// ルート要素の取得
		Element root = doc.getDocumentElement();
		NodeList rootChildren = root.getElementsByTagName("Block");

		for (int i = 0; i < rootChildren.getLength(); i++) {
			Node node = rootChildren.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getNodeName().equals("Block")) {
					long blockID = Long.parseLong(element.getAttribute("id"));
					String newBlockID = String.valueOf(blockIDs.indexOf(blockID) + 1);
					element.setAttribute("id", newBlockID);
					
					NodeList blockChildren = node.getChildNodes();
					for (int j = 0; j < blockChildren.getLength(); j++) {
						Node blockNode = blockChildren.item(j);
						if (blockNode.getNodeType() == Node.ELEMENT_NODE) {
							if (blockNode.getNodeName().equals("BeforeBlockId")) {
								long beforeBlockID = Long.parseLong(blockNode.getTextContent());
								String newBeforeBlockID = String.valueOf(blockIDs.indexOf(beforeBlockID) + 1);
								blockNode.replaceChild(doc.createTextNode(newBeforeBlockID), blockNode.getFirstChild());
							} 
							if (blockNode.getNodeName().equals("AfterBlockId")) {
								long afterBlockID = Long.parseLong(blockNode.getTextContent());
								String newAfterBlockID = String.valueOf(blockIDs.indexOf(afterBlockID) + 1);
								blockNode.replaceChild(doc.createTextNode(newAfterBlockID), blockNode.getFirstChild());
							}
							if (blockNode.getNodeName().equals("Plug")) {
								NodeList plugChildren = blockNode.getChildNodes();
								for (int k = 0; k < plugChildren.getLength(); k++) {
									Node plugNode = plugChildren.item(k);
									if (plugNode.getNodeName().equals("BlockConnector")) {
										long connBlockID = Long.parseLong(((Element)plugNode).getAttribute("con-block-id"));
										String newConnBlockID = String.valueOf(blockIDs.indexOf(connBlockID) + 1);
										((Element) plugNode).setAttribute("con-block-id", newConnBlockID);
									}
								}
							}
							if (blockNode.getNodeName().equals("Sockets")) {
								NodeList socketChildren = blockNode.getChildNodes();
								for (int k = 0; k < socketChildren.getLength(); k++) {
									Node socketNode = socketChildren.item(k);
									if (socketNode.getNodeName().equals("BlockConnector")) {
										long connBlockID = Long.parseLong(((Element)socketNode).getAttribute("con-block-id"));
										String newConnBlockID = String.valueOf(blockIDs.indexOf(connBlockID) + 1);
										((Element) socketNode).setAttribute("con-block-id", newConnBlockID);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	public Document getDoc() {
		return this.doc;
	}
}