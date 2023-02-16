import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphDisplay2 extends JPanel {
    private Map<Long,NodeParser> usableNodes;
    private Map<Long, NodeParser> nodesMap = new HashMap<>();

    //private double minLongitude = 4.01940;
    private double minLongitude = 4.018;//vertical
    //private double maxLongitude = 4.0254;
    private double maxLongitude = 4.025;
    private double minLatitude = 50.9375; //horizontal
    private double maxLatitude = 50.9425;

    private int getXCoordinate(double latitude) {
        int width = getWidth();
        return (int) (width * (latitude - minLatitude) / (maxLatitude - minLatitude));
    }

    private int getYCoordinate(double longitude) {
        int height = getHeight();
        return (int) (height * (longitude - minLongitude) / (maxLongitude - minLongitude));
    }

    public GraphDisplay2(Map<Long, NodeParser> usableNodes, Map nodeMap) {
        this.usableNodes = usableNodes;
        this.nodesMap = nodeMap;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (NodeParser node : usableNodes.values()) {
            boolean usableEdge=false;
            for(EdgeParser edge: node.getOutgoingEdges()) {
                if(included(edge.getEndNodeOsmId())) usableEdge=true;
            }
            if(!node.getDissabled()&&usableEdge) {
                int x1 = getXCoordinate(node.getLatitude());
                int y1 = getYCoordinate(node.getLongitude());
                int size = 5;
                g.setColor(Color.blue);
                g.fillOval(x1 - size / 2, y1 - size / 2, size, size);
                //EDGES
                for (EdgeParser edge : node.getOutgoingEdges()) {
                    if(included(edge.getEndNodeOsmId())){
                        NodeParser target = nodesMap.get(edge.getEndNodeOsmId());
                        int x2 = getXCoordinate(target.getLatitude());
                        int y2 = getYCoordinate(target.getLongitude());
                        g.setColor(Color.red);
                        g.drawLine(x1, y1, x2, y2);
                    }

                }
            }
        }
//        for (NodeParser node : nodes) {
//            if(!node.getDissabled()) {
//                int x1 = getXCoordinate(node.getLatitude());
//                int y1 = getYCoordinate(node.getLongitude());
//                int size = 5;
//                g.setColor(Color.blue);
//                g.fillOval(x1 - size / 2, y1 - size / 2, size, size);
//                //EDGES
//                for (Edge edge : node.getOutgoingEdges()) {
//                    if(included(edge.getEndNodeOsmId())){
//                        NodeParser target = nodesMap.get(edge.getEndNodeOsmId());
//                        int x2 = getXCoordinate(target.getLatitude());
//                        int y2 = getYCoordinate(target.getLongitude());
//                        g.setColor(Color.red);
//                        g.drawLine(x1, y1, x2, y2);
//                    }
//
//                }
//            }
//        }
    }
    private boolean included(Long endNodeOsmId) {
        NodeParser node = nodesMap.get(endNodeOsmId);
        return usableNodes.containsValue(node)&&!node.getDissabled();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1200, 700);
    }
}




