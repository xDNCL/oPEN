package save;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class NormalizeIDs {
	public NormalizeIDs(File preFile) {
		// 拡張子調整
		String fileName = preFile.getName().toString();
		if(fileName.length() <= 4 || !fileName.substring(fileName.length()-4).equals(".xml")) {
			// ファイル名が.xmlではない場合は.xmlとして保存する
			File renameFile = new File(preFile.getPath()+".xml");
			preFile = renameFile;
		}
		try {
			ReadIDs rid = new ReadIDs(preFile);
			ArrayList<Long> blockIDs = rid.getIDs();
			
			SetIDs sid = new SetIDs(preFile, blockIDs);
			Document doc = sid.getDoc();
			
			File outputFile = new File(preFile.getParent() + "/" + preFile.getName().replaceFirst("pre_", ""));
			WriteXml wx = new WriteXml(doc, outputFile);
			
			// 仮セーブデータ削除
			if(preFile.exists()) {
				preFile.delete();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}