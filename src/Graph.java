import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
    private Map<Long,Node> nodeList;

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


}