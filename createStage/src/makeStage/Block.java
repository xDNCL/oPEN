package makeStage;

public class Block {
	public String name;
	public int id;
	public String label;
	public int beforeId;
	public int afterId;
	public int conId;
	public int conId2;

	public Block() {}
	public Block(String name, int id, String label, int beforeId, int afterId, int conId, int conId2) {
		this.name = name;
		this.id = id;
		this.label = label;
		this.beforeId = beforeId;
		this.afterId = afterId;
		this.conId = conId;
		this.conId2 = conId2;
	}
}