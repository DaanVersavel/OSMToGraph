

public class EdgeParser {
	public int headNode;
	private double length;
	private double travelTime;
	private long beginNodeOsmId;
	private long endNodeOsmId;
	
	public EdgeParser(int headNode, double length, double travelTime) {
		this.headNode = headNode;
		this.length = length;
		this.travelTime = travelTime;	
	}
	public EdgeParser(long beginNodeOsmId, long endNodeOsm, double length){
		this.beginNodeOsmId = beginNodeOsmId;
        this.endNodeOsmId = endNodeOsm;
        this.length = length;
	}

	public EdgeParser deepCopy(EdgeParser edge) {
		this.headNode = edge.headNode;
		this.length = edge.length;
		this.travelTime = edge.travelTime;
		return this;
	}

	public int getHeadNode() {
		return headNode;
	}

	public void setHeadNode(int headNode) {
		this.headNode = headNode;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}

	public long getBeginNodeOsmId() {
		return beginNodeOsmId;
	}

	public void setBeginNodeOsmId(long beginNodeOsmId) {
		this.beginNodeOsmId = beginNodeOsmId;
	}

	public long getEndNodeOsmId() {
		return endNodeOsmId;
	}

	public void setEndNodeOsmId(long endNodeOsmId) {
		this.endNodeOsmId = endNodeOsmId;
	}
}
