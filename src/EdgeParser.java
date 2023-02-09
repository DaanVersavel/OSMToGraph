import java.util.ArrayList;

public class EdgeParser {
	public int headNode;
	public double length;
	public double travelTime;
	
	public EdgeParser(int headNode, double length, double travelTime) {
		this.headNode = headNode;
		this.length = length;
		this.travelTime = travelTime;	
	}

	public EdgeParser deepCopy(EdgeParser edge) {
		this.headNode = edge.headNode;
		this.length = edge.length;
		this.travelTime = edge.travelTime;
		return this;
	}
}
