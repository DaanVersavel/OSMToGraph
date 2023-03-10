import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Dijkstra {

    private Map<Long,NodeParser> nodes;
    private Map<Long,Double> shortestTimeMap;
    private Map<Long,Long> parents;



    public Dijkstra(Map<Long,NodeParser> nodes){
        this.nodes = nodes;
        this.shortestTimeMap = new HashMap<>();
        this.parents =  new HashMap<>();
    }

    public Boolean solveDijkstra(Long start){
        PriorityQueue<NodeParser> pq = new PriorityQueue<>(new NodeParserComparator());
        pq.addAll(nodes.values());

        for(NodeParser node : nodes.values()){
            node.setCurrenCost(Integer.MAX_VALUE);
            shortestTimeMap.put(node.getOsmId(),Double.MAX_VALUE);
            parents.put(node.getOsmId(),-1L);
        }

        shortestTimeMap.put(start,0.0);
        NodeParser tempNode = nodes.get(start);
        tempNode.setCurrenCost(0);
        pq.remove(nodes.get(start));
        pq.add(tempNode);

        int numberOfNodes= nodes.size();

        //dijkstra algorithm
        for (int i = 1; i <= numberOfNodes; i++) {
            //shortest time search
            NodeParser removedNode= pq.remove();

            if(shortestTimeMap.get(removedNode.getOsmId())==Double.MAX_VALUE){
                break;
            }

            List<EdgeParser> edgeArrayList = removedNode.getOutgoingEdges();
            //update the adjacent node-time
            for(EdgeParser edge: edgeArrayList){
                //when reaching the node
                double distanceAtNode = shortestTimeMap.get(edge.getBeginNodeOsmId())+ edge.getLength();
                //If better time update time and read to pq
                boolean t=shortestTimeMap.containsKey(edge.getEndNodeOsmId());
                if(distanceAtNode<shortestTimeMap.get(edge.getEndNodeOsmId())){
                    shortestTimeMap.put(edge.getEndNodeOsmId(),distanceAtNode);
                    NodeParser tempnode=nodes.get(edge.getEndNodeOsmId());
                    tempnode.setCurrenCost(distanceAtNode);
                    if(pq.remove(tempnode)){
                        pq.add(tempnode);
                    }
                    parents.put(edge.getEndNodeOsmId(), removedNode.getOsmId());
                }
            }
        }

        for(Double time: shortestTimeMap.values()){
            if(time==Double.MAX_VALUE){
                return false;
            }
        }
        return true;
    }






}
