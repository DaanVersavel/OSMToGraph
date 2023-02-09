import java.util.ArrayList;

public class NodeParser {
	// The OSM id of the node.
	private long osmId;
	private double latitude;
	private double longitude;
	private boolean dissabled;
	private ArrayList<Edge> outgoingEdges = new ArrayList<>();

	public NodeParser(long osmId, double latitude, double longitude) {
		this.osmId = osmId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dissabled = false;
	}

	public long getOsmId() {
		return osmId;
	}

	public void setOsmId(long osmId) {
		this.osmId = osmId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void addOutgoingEdge(Edge edge) {
		outgoingEdges.add(edge);
	}

	public ArrayList<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}

	public void setOutgoingEdges(ArrayList<Edge> outgoingEdges) {
		this.outgoingEdges = outgoingEdges;
	}

	@Override
	public String toString() {
		return (String.valueOf(latitude) + "," + String.valueOf(longitude));
	}

	public void removeOutgoingEdge(long nodeToRemove) {
		for (int i=0;i<outgoingEdges.size();i++) {
			Edge edge = outgoingEdges.get(i);
			if(edge.getBeginNodeId() == nodeToRemove) {
				outgoingEdges.remove(edge);
			}
		}
	}

	public void setDissabled(boolean b) {
		this.dissabled= b;
	}

	public boolean getDissabled() {
		return dissabled;
	}

	public NodeParser deepCopy(NodeParser node) {
		this.osmId = node.osmId;
		this.latitude = node.getLatitude();
		this.longitude = node.getLongitude();
		this.dissabled = node.getDissabled();
		return this;
	}
}
