import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphDisplay extends JPanel {
//    private ArrayList<NodeParser> nodes;
    RoadNetwork graph;
    //private double minLongitude = 4.01940;
    private double minLongitude = 4.0195;//vertical
    //private double maxLongitude = 4.0254;
    private double maxLongitude = 4.0245;
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

    public GraphDisplay(RoadNetwork graph ) {
        this.graph = graph;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (NodeParser node : graph.nodes) {
            if(!node.getDissabled()) {
                int x1 = getXCoordinate(node.getLatitude());
                int y1 = getYCoordinate(node.getLongitude());
                int size = 5;
                g.setColor(Color.blue);
                g.fillOval(x1 - size / 2, y1 - size / 2, size, size);
                for (Edge edge : node.getOutgoingEdges()) {
                    int nodeId= graph.osmIdToNodeIndex.get(edge.getEndNodeId());
                    if(graph.nodes.size()>nodeId) {
                        NodeParser target = graph.nodes.get(nodeId);
                        int x2 = getXCoordinate(target.getLatitude());
                        int y2 = getYCoordinate(target.getLongitude());
                        g.setColor(Color.red);
                        g.drawLine(x1, y1, x2, y2);
                    }
                }
            }
//            else {
//                int x1 = getXCoordinate(node.getLatitude());
//                int y1 = getYCoordinate(node.getLongitude());
//                int size = 5;
//                g.setColor(Color.green);
//                g.fillOval(x1 - size / 2, y1 - size / 2, size, size);
//                for (Edge edge : node.getOutgoingEdges()) {
//                    int nodeId= graph.osmIdToNodeIndex.get(edge.getEndNodeId());
//                    if(graph.nodes.size()>nodeId) {
//                        NodeParser target = graph.nodes.get(nodeId);
//                        int x2 = getXCoordinate(target.getLatitude());
//                        int y2 = getYCoordinate(target.getLongitude());
//                        g.setColor(Color.MAGENTA);
//                        g.drawLine(x1, y1, x2, y2);
//                    }
//                }
//            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1200, 700);
    }
}

