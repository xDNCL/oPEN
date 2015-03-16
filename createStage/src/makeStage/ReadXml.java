package makeStage;
import java.io.File;
import java.io.IOException;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ReadXml {
	public static final int MAX_BLOCKS = 100;
	public static final int MAX_DRAWERS = 30;
	public ReadXml(File inputFile) {	
		String file = inputFile.getAbsolutePath();
		
		DOMParser parser = new DOMParser(); // パーサの生成
		try {
			parser.parse(file);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		} // XMLファイルのパース

		Document document = parser.getDocument(); // DOCUMENTノードの取得
		if (document.hasChildNodes()) { // 子ノードがある場合
			Block[] block = new Block[MAX_BLOCKS]; 
			Drawer[] drawer = new Drawer[MAX_DRAWERS];
			int bi = 0; // Blockのインデックス
			int di = 0; // Drawerのインデックス
			int bnum = bi; // ブロックの個数
			int dnum = di; // Drawerの個数
			
			Node rootNode = (Node) (document.getDocumentElement());
			Node pagesNode = rootNode.getFirstChild().getNextSibling();
			Node pageNode = pagesNode.getFirstChild().getNextSibling();
			Node pageBlocksNode = pageNode.getFirstChild().getNextSibling();
			Node blockNode = pageBlocksNode.getFirstChild().getNextSibling();

			while (blockNode != null) {
				if (!blockNode.getNodeName().equals("Block")) {
					blockNode = blockNode.getNextSibling();
					continue;
				}

				NamedNodeMap attrs = blockNode.getAttributes(); // NamedNodeMapの取得
				Node attr = attrs.getNamedItem("genus-name");
				String name = attr.getNodeValue();

				attr = attrs.getNamedItem("id");
				int id = new Integer(attr.getNodeValue()).intValue();
				String label = "";
				int beforeId = 0;	
				int afterId = 0;
				
				Node labelNode = blockNode.getFirstChild();
				while (labelNode != null) {
					if (!labelNode.getNodeName().equals("Label")) {
						labelNode = labelNode.getNextSibling();
						continue;
					}
					Node node = labelNode.getFirstChild();
					if (node != null) {
						label = node.getNodeValue();
					}
					break;
				}
				
				Node beforeBlockIdNode = blockNode.getFirstChild();
				while (beforeBlockIdNode != null) {
					if (!beforeBlockIdNode.getNodeName().equals("BeforeBlockId")) {
						beforeBlockIdNode = beforeBlockIdNode.getNextSibling();
						continue;
					}
					Node node = beforeBlockIdNode.getFirstChild();
					if (node != null) {
						beforeId = new Integer(node.getNodeValue()).intValue();
					}
					break;
				}

				Node afterBlockIdNode = blockNode.getFirstChild();
				while (afterBlockIdNode != null) {
					if (!afterBlockIdNode.getNodeName().equals("AfterBlockId")) {
						afterBlockIdNode = afterBlockIdNode.getNextSibling();
						continue;
					}
					Node node = afterBlockIdNode.getFirstChild();
					if (node != null) {
						afterId = new Integer(node.getNodeValue()).intValue();
					}
					break;
				}

				// コネクタに接続されているブロックId
				Node socketsNode = blockNode.getFirstChild();
				while (socketsNode != null) {
					if (!socketsNode.getNodeName().equals("Sockets")) {
						socketsNode = socketsNode.getNextSibling();
						continue;
					}
					break;
				}
				
				int conId = 0;
				int conId2 = 0;
				if(socketsNode != null) {
					Node blockConnectorNode = socketsNode.getFirstChild();
					while (blockConnectorNode != null) {
						if (!blockConnectorNode.getNodeName().equals("BlockConnector")) {
							blockConnectorNode = blockConnectorNode.getNextSibling();
							continue;
						}
						
						attrs = blockConnectorNode.getAttributes(); // NamedNodeMapの取得
						attr = attrs.getNamedItem("con-block-id");
						if (attr != null) {
							conId = new Integer(attr.getNodeValue()).intValue();
						}
						
						if (name.equals("newDrawer")) {
							blockConnectorNode = blockConnectorNode.getNextSibling();
							while (blockConnectorNode != null) {
								if (!blockConnectorNode.getNodeName().equals("BlockConnector")) {
									blockConnectorNode = blockConnectorNode.getNextSibling();
									continue;
								}
								attrs = blockConnectorNode.getAttributes();
								attr = attrs.getNamedItem("con-block-id");
								if (attr != null) {
									conId2 = new Integer(attr.getNodeValue()).intValue();
								}
								break;
							}
						}
						break;
					}
				}
				
				block[bi] = new Block(name, id, label, beforeId, afterId, conId, conId2);
				bi++;
				blockNode = blockNode.getNextSibling();
			}

			// drawer作成
			bnum = bi;
			int oi = 0; // orderブロック用インデックス
			int ci = 0; // colorブロック用インデックス
			int order = 0;
			String color = null;
			for (bi = 0; bi < bnum; bi++) {
				if (block[bi].name.equals("newDrawer")) {
					for (oi = 0; oi < bnum; oi++) {
						if (block[oi].id == block[bi].conId) {
							order = new Integer(block[oi].label).intValue();
						}
					}
					
					for (ci = 0; ci < bnum; ci++) {
						if (block[ci].id == block[bi].conId2) {
							color = block[ci].name;
							if (color.equals("red")) color = "255 0 0";
							else if (color.equals("orange")) color = "255 165 0";
							else if (color.equals("yellow")) color = "255 255 0";
							else if (color.equals("lime")) color = "0 255 0";
							else if (color.equals("green")) color = "50 205 50";
							else if (color.equals("turquoise")) color = "64 224 208";
							else if (color.equals("cyan")) color = "0 255 255";
							else if (color.equals("sky")) color = "135 206 255";
							else if (color.equals("blue")) color = "0 0 255";
							else if (color.equals("purple")) color = "128 0 128";
							else if (color.equals("pink")) color = "255 192 203";
							else if (color.equals("magenta")) color = "255 0 255";
							else if (color.equals("brown")) color = "165 42 42";
							else if (color.equals("white")) color = "255 255 255";
							else if (color.equals("gray")) color = "128 128 128";
							else if (color.equals("black")) color = "0 0 0";
							else color = "0 0 255";
						}
					}
					drawer[di] = new Drawer(block[bi].label, block[bi], order, color);
					di++;
				}
			}

			dnum = di;
			for (di = 0; di < dnum; di++) {
				int nextId = drawer[di].block[0].id; // 接続されている次のブロックID
				int bci = 0; // コネクタに接続されているブロックを探す用のインデックス
				
				// drawer[di]のidとbeforeIdが一致してたら最初のブロックなのでdrawer.block[1]に保存
				for (bi = 0; bi < bnum; bi++) {
					if (block[bi].beforeId == nextId) {
						if (block[bi].label.equals("コネクタ")) {
							for (bci = 0; bci < bnum; bci++) {
								if (block[bci].id == block[bi].conId) {
									drawer[di].setBlock(block[bci]);
									nextId = block[bi].afterId;
								}
							}
						} else {
							if (block[bi].name.equals("dummyStart")) {
								block[bi].name = "start";
							}
							drawer[di].setBlock(block[bi]);
							nextId = block[bi].afterId;
						}
						break;
					}
				}

				// 2番目の以降のブロックを追加
				while (nextId != 0) {
					for (bi = 0; bi < bnum; bi++) {
						if (block[bi].id == nextId) {
							if (block[bi].label.equals("コネクタ")) {
								for (bci = 0; bci < bnum; bci++) {
									if (block[bci].id == block[bi].conId) {
										drawer[di].setBlock(block[bci]);
										nextId = block[bi].afterId;
										break;
									}
								}
							} else {
								if (block[bi].name.equals("dummyStart")) {
									block[bi].name = "start";
								}
								drawer[di].setBlock(block[bi]);
								nextId = block[bi].afterId;
							}
						}
					}
				}
				
			}
			// 全drawerの中身
			for (di = 0; di < dnum; di++) {
				System.out.println(
						"name = " + drawer[di].name +
						"  order = " + drawer[di].order +
						"  color = " + drawer[di].color
						);
			}
			
			// 全ブロックの中身
//			for (bi = 0; bi < bnum; bi++) {
//				System.out.println(
//						"name = " + block[bi].name +
//						"  id = " + block[bi].id +
//						"  label = " + block[bi].label +
//						"  beforeId = " + block[bi].beforeId +
//						"  afterId = " + block[bi].afterId +
//						"  conId = " + block[bi].conId +
//						"  conId2 = " + block[bi].conId2
//						);
//			}
			
			// xml作成
//			MakeXml xml = new MakeXml(drawer, dnum);
			MakeXml xml = new MakeXml(drawer, dnum, inputFile);
		}
	}
}