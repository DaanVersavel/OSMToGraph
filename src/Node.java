import java.util.ArrayList;
import java.util.List;

public class Node {
    private Long nodeID;
    private ArrayList<Edge> outgoingEdges = new ArrayList<>();

    public Node(Long nodeID) {
        this.nodeID = nodeID;
    }
    public Node(Long nodeID, ArrayList<Edge> outgoingEdges) {
        this.nodeID = nodeID;
        this.outgoingEdges = outgoingEdges;
    }

    public Long getNodeID() {
        return nodeID;
    }

    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }

    public void addOutgoingEdge(Edge edge) {
        outgoingEdges.add(edge);
    }

    public  List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public void setOutgoingEdges(ArrayList<Edge> outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }
}