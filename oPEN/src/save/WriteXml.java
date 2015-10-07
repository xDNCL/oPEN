package save;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class WriteXml {
	public WriteXml(Document document, File outputFile) throws Exception {
		FileOutputStream fos = new FileOutputStream(outputFile); 
		StreamResult result = new StreamResult(fos); 
		
		// Transformerファクトリを生成
		TransformerFactory transFactory = TransformerFactory.newInstance();
		// Transformerを取得
		Transformer transformer = transFactory.newTransformer(); 
		// エンコード：UTF-8、インデントありを指定
		transformer.setOutputProperty("encoding", "UTF-8");
		transformer.setOutputProperty("indent", "yes");
		// transformerに渡すソースを生成
		DOMSource source = new DOMSource(document);

		// 出力実行
		transformer.transform(source, result); 
		fos.close(); 
	}
}
