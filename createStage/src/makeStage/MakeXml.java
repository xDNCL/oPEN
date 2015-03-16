package makeStage;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MakeXml {
	public MakeXml(Drawer[] drawer, int dnum, File inputFile) {
		String file = inputFile.getName();
		// Documentインスタンスの生成
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = documentBuilder.newDocument();

		// XML文書の作成
		Element root = document.createElement("root");
		document.appendChild(root);

		Element Output = document.createElement("Output");
		root.appendChild(Output);

		Element Language = document.createElement("Language");
		Language.appendChild(document.createTextNode("FREE"));
		Output.appendChild(Language);

		Element FileNameExtention = document.createElement("FileNameExtention");
		FileNameExtention.appendChild(document.createTextNode(".java"));
		Output.appendChild(FileNameExtention);

		Element OutputButton = document.createElement("OutputButton");
		OutputButton.appendChild(document.createTextNode("ON"));
		Output.appendChild(OutputButton);

		Element BlockDrawerSets = document.createElement("BlockDrawerSets");
		root.appendChild(BlockDrawerSets);

		Element BlockDrawerSet = document.createElement("BlockDrawerSet");
		BlockDrawerSet.setAttribute("drawer-draggable", "no");
		BlockDrawerSet.setAttribute("location", "southwest");
		BlockDrawerSet.setAttribute("name", "factory");
		BlockDrawerSet.setAttribute("type", "stack");
		BlockDrawerSet.setAttribute("window-per-drawer", "no");
		BlockDrawerSets.appendChild(BlockDrawerSet);

		// Drawerをセット color対応
		int wd = 0;
		while (wd < dnum) {
			for (int di = 0; di < dnum; di++) {
				if (drawer[di].order == wd + 1) {
					Element BlockDrawer = document.createElement("BlockDrawer");
					BlockDrawer.setAttribute("name", drawer[di].block[0].label);
					BlockDrawer.setAttribute("type", "factory");
					BlockDrawer.setAttribute("button-color", drawer[di].color);
					BlockDrawerSet.appendChild(BlockDrawer);
					
					for (int bi = 1; bi < drawer[di].bi; bi++) {
						Element BlockGenusMember = document.createElement("BlockGenusMember");
						BlockGenusMember.appendChild(document.createTextNode(drawer[di].block[bi].name));
						BlockDrawer.appendChild(BlockGenusMember);
					}
				}
			}
			wd++;
		}
		
		// XMLファイルの作成
//		file = file.substring(0, file.length()-4);
		File outputFile = new File("stage_" + file);
		write(outputFile, document);
	}

	public static boolean write(File file, Document document) {
		// Transformerインスタンスの生成
		Transformer transformer = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(org.apache.xml.serializer.OutputPropertiesFactory.S_KEY_INDENT_AMOUNT,"4");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return false;
		}

		// Transformerの設定
		transformer.setOutputProperty("indent", "yes"); //改行指定
		transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング

		// XMLファイルの作成
		try {
			transformer.transform(new DOMSource(document), new StreamResult(file));
		} catch (TransformerException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}