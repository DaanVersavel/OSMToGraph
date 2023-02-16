import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphdisplayAalst extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener{
    private final Map<Long, ArrayList<EdgeParser>> incomingEdgesMap;
    private final Map<Integer, Long> nodeIndexToOsmId;
    private Map<Long,NodeParser> usableNodes;
    private Map<Long, NodeParser> nodesMap = new HashMap<>();

//    private double minLongitude = 3.98;//vertical
//    private double maxLongitude = 4.11;
//    private double minLatitude = 50.90; //horizontal
//    private double maxLatitude = 51.0;

    private double minLongitude = 4.02;//vertical
    private double maxLongitude = 4.04;
    private double minLatitude = 50.93; //horizontal
    private double maxLatitude = 50.95;

    private double scaleFactor = 1.0;
    private int x, y; // coordinates of the figure
    private boolean dragging; // flag to indicate if figure is being dragged
    private int lastX, lastY; // last mouse coordinates

    private boolean bool;




    private int getXCoordinate(double latitude) {
        int width =(int)(getWidth()*scaleFactor );
        return (int) (width * (latitude - minLatitude) / (maxLatitude - minLatitude));
    }

    private int getYCoordinate(double longitude) {
        int height = (int)(getHeight()*scaleFactor );
        return (int) (height * (longitude - minLongitude) / (maxLongitude - minLongitude));
    }

    public GraphdisplayAalst(Map<Long, NodeParser> usableNodes, Map nodeMap, Map<Long, ArrayList<EdgeParser>> incomingEdgesMap, Map<Integer, Long> nodeIndexToOsmId,boolean bool) {
        this.usableNodes = usableNodes;
        this.nodesMap = nodeMap;
        this.incomingEdgesMap = incomingEdgesMap;
        this.nodeIndexToOsmId = nodeIndexToOsmId;
        this.bool = bool;

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

    }
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();

        if (lastX >= x && lastX <= x + 50 && lastY >= y && lastY <= y + 50) {
            dragging = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            int dx = e.getX() - lastX;
            int dy = e.getY() - lastY;
            x += dx;
            y += dy;
            lastX = e.getX();
            lastY = e.getY();
            repaint();
        }
    }
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            scaleFactor *= 1.1; // zoom in
        } else {
            scaleFactor /= 1.1; // zoom out
        }
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        // apply translation to move the figure
        g2d.translate(x, y);

        // apply scaling to zoom in and out
        g2d.scale(scaleFactor, scaleFactor);

        // draw your figure here
        boolean dis=false;


        for (NodeParser node : usableNodes.values()) {
            if(bool){ //if bool is true, draw the node
                dis=true;
            } else dis=!node.getDissabled();

//            if(!node.getDissabled()){
            if(dis){
                int x1 = getXCoordinate(node.getLatitude());
                int y1 = getYCoordinate(node.getLongitude());
                int size = 5;
                g2d.setColor(Color.blue);
                g2d.fillOval(x1 - size / 2, y1 - size / 2, size, size);//
                //EDGES
                if(node.getOutgoingEdges()!=null) {
                    for (EdgeParser edge : node.getOutgoingEdges()) {
                        if(included(edge.getEndNodeOsmId())){
                            NodeParser target = nodesMap.get(edge.getEndNodeOsmId());
                            int x2 = getXCoordinate(target.getLatitude());
                            int y2 = getYCoordinate(target.getLongitude());
                            g2d.setColor(Color.red);
                            g2d.drawLine(x1, y1, x2, y2);
                        }

                    }
                }
            }
        }
    }
    public void setZoomLevel(double zoomLevel) {
        this.scaleFactor  = zoomLevel;
        repaint();
    }


    private boolean included(Long nodeId) {
        NodeParser node = nodesMap.get(nodeId);
        return usableNodes.containsValue(node)&&!node.getDissabled();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1200, 700);
    }


    // implement other MouseListener and MouseMotionListener methods as needed
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}




