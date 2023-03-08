import java.util.*;

public class NodeParser {
	// The OSM id of the node.
	private long osmId;
	private double latitude;
	private double longitude;
	private double currenCost;
	private boolean dissabled;
	private Set<String> types;
	private List<EdgeParser> outgoingEdges = new ArrayList<>();

	public NodeParser(long osmId, double latitude, double longitude) {
		this.osmId = osmId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dissabled = false;
		this.types= new HashSet<>();
	}

	public double getCurrenCost() {
		return currenCost;
	}

	public Set<String> getTypes() {
		return types;
	}

	public void addType(String type) {
		types.add(type);
	}

	public void setCurrenCost(double currenCost) {
		this.currenCost = currenCost;
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

	public void addOutgoingEdge(EdgeParser edge) {
		outgoingEdges.add(edge);
	}

	public List<EdgeParser> getOutgoingEdges() {
		return outgoingEdges;
	}

	public void setOutgoingEdges(List<EdgeParser> outgoingEdges) {
		this.outgoingEdges = outgoingEdges;
	}

	@Override
	public String toString() {
		return (String.valueOf(latitude) + "," + longitude);
	}

	public void removeOutgoingEdge(long nodeToRemove) {
		for (int i = 0; i < outgoingEdges.size(); i++) {
			EdgeParser edge = outgoingEdges.get(i);
			if (edge.getEndNodeOsmId() == nodeToRemove) {
				outgoingEdges.remove(edge);
				break;
			}
		}
	}

	public void setDissabled(boolean b) {
		this.dissabled = b;
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

	public void cleanOutgoingedges(Map<Long, NodeParser> usableNodes) {
		List<EdgeParser> teRemoveEdge = new ArrayList<>();
		if (outgoingEdges != null) {
			for (int i = 0; i < outgoingEdges.size(); i++) {
				EdgeParser edge = outgoingEdges.get(i);
				long osmEndId = edge.getEndNodeOsmId();
				if (!usableNodes.containsKey(osmEndId))
					teRemoveEdge.add(edge);
			}
			outgoingEdges.removeAll(teRemoveEdge);
		}
	}

	public void cleanIncomingEdges(List<EdgeParser> incommingEdgeParsers, Map<Long, NodeParser> usableNodes, Map<Integer, Long> nodeIndexToOsmId) {
		ArrayList<EdgeParser> teRemoveEdgeParser = new ArrayList<>();
		if (incommingEdgeParsers != null) {
			for (int i = 0; i < incommingEdgeParsers.size(); i++) {
				EdgeParser edgeParser = incommingEdgeParsers.get(i);
//				long osmBeginId = nodeIndexToOsmId.get(edgeParser.getHeadNode());
				if (!usableNodes.containsKey(edgeParser.getBeginNodeOsmId()))
					teRemoveEdgeParser.add(edgeParser);
			}
			incommingEdgeParsers.removeAll(teRemoveEdgeParser);
		}
	}

	public void removeIncommingEdge(long osmId, List<EdgeParser> edgeParsers) {
		for (int i = 0; i < edgeParsers.size(); i++) {
			EdgeParser edgeParser = edgeParsers.get(i);
			if (edgeParser.getBeginNodeOsmId() == osmId) {
				edgeParsers.remove(i);
			}
		}
	}

	public void removeOutgoingEdgeHeadNode(int nodeId, List<EdgeParser> outgoingEdgesArray) {
		for (int i = 0; i < outgoingEdgesArray.size(); i++) {
			EdgeParser edge = outgoingEdgesArray.get(i);
			if (edge.getHeadNode() == nodeId) {
				outgoingEdgesArray.remove(edge);
			}
		}
	}

	public void removeIncommingEdgeHeadNode(int nodeId, List<EdgeParser> incomingEdgesArray) {
		for (int i = 0; i < incomingEdgesArray.size(); i++) {
			EdgeParser edge = incomingEdgesArray.get(i);
			if (edge.getHeadNode() == nodeId) {
				incomingEdgesArray.remove(i);
			}
		}
	}
}


