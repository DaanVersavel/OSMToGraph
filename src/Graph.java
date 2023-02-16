import java.util.HashMap;
import java.util.Map;

public class Graph {
    private Map<Long,Node> nodeList;
    private Map<Long,EdgeParser> outgoingEdges;
    private Map<Long,EdgeParser> incommingEdges;


    public Graph() {
        nodeList = new HashMap<>();
    }
    public Graph(Map<Long,Node> nodeList) {
        this.nodeList = nodeList;
    }

    public Map<Long,Node> getNodeList() {
        return nodeList;
    }

    public void setNodelist(Map<Long,Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void addNode(Node node) {
        nodeList.put(node.getNodeID(),node);
    }

    public void setNodeList(Map<Long, Node> nodeList) {
        this.nodeList = nodeList;
    }

    public Map<Long, EdgeParser> getOutgoingEdges() {
        return outgoingEdges;
    }

    public void setOutgoingEdges(Map<Long, EdgeParser> outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }

    public Map<Long, EdgeParser> getIncommingEdges() {
        return incommingEdges;
    }

    public void setIncommingEdges(Map<Long, EdgeParser> incommingEdges) {
        this.incommingEdges = incommingEdges;
    }
}