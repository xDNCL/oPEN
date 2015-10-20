package makeStage;

public class Drawer {
	public String name;
	public Block[] block = new Block[ReadXml.MAX_BLOCKS];
	public int bi = 0; // Blockのインデックス
	public int order = 0;
	public String color = null;

	public Drawer() {
	}

	public Drawer(String name, Block block, int order, String color) {
		this.name = name;
		this.block[0] = block;
		this.order = order;
		this.color = color;
		bi = 1;
	}

	public void setBlock(Block block) {
		this.block[bi] = block;
		bi++;
	}
}