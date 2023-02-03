import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class DijkstraAlgorithm {
	
	public RoadNetwork graph;
	public ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
	
	private final int inf = Integer.MAX_VALUE;	
	private Comparator<Vertex> comparator;
	private PriorityQueue<Vertex> distances;
	private ArrayList<Vertex> setteled;
  
	// Constructor
	public DijkstraAlgorithm(RoadNetwork graph) {
		this.graph = graph;
		
		for (int i=0; i<graph.numNodes; i++) {
			visitedNodes.add(0);
		}
	  
		comparator = new DistanceComparator();
		distances = new PriorityQueue<Vertex>(comparator);
		setteled = new ArrayList<Vertex>();
	}
  
	// Compute the shortest paths from the given source to the given target node.
	// Returns the cost of the shortest path.
	// NOTE: If called with target node -1, Dijkstra is run until all nodes
	// reachable from the source are settled.
	public double computeShortestPathCost(int sourceNodeId, int targetNodeId) {
		
		distances.clear();
		setteled.clear();
	  
		//Initialize distances to Infinity for all nodes except source to 0 
		for (int i=0; i<graph.numNodes; i++) {
			if (i == sourceNodeId)
				distances.add(new Vertex(i, 0, sourceNodeId));
			else
				distances.add(new Vertex(i, inf, i));    	  
			setteled.add(null);
		}
      
		while (distances.size() > 0){
			
			Vertex node = new Vertex(distances.poll());
			int id = node.id;
			double dist = node.dist;
			
			if (node.parent == -1)
				System.out.println(id);
			
			if (setteled.get(id) != null)
				continue;
    	  
			setteled.set(id, node);
    	  
			if(targetNodeId != -1 && setteled.get(targetNodeId) != null) {
				return setteled.get(targetNodeId).dist;
			}
    	  
			ArrayList<EdgeParser> edges = graph.outgoingEdges.get(id);

			for(int i=0; i<edges.size(); i++) {    		  
				double distToNode = dist + edges.get(i).travelTime;
				if (distToNode < inf && distToNode >= 0) {
					distances.add(new Vertex(edges.get(i).headNode, distToNode, id));
				}		  
			}   	  
		}
		return 0;
	}

	// Mark all nodes visited by the previous call to computeShortestPath
	public void setVisitedNodeMark(int mark) {
		for (int i=0; i<setteled.size(); i++) {
			if (setteled.get(i).dist != inf)
				visitedNodes.set(i, mark);
		}
	}
  
	// Get list of nodes that make the shortest path
	public ArrayList<Integer> getShortestPath(int source, int target) {
		ArrayList<Integer> path = new ArrayList<Integer>();		
		
		if (setteled.get(target).dist == inf)
			return null; 
		
		path.add(target);		
		int parent = setteled.get(target).parent;
		
		while (parent != source) {
			path.add(0, parent);
			parent = setteled.get(parent).parent;
		}
		path.add(0, source);
		
		return path;
	}
	
	public class DijkstraResult {
		ArrayList<Integer> path;
		double cost;
		
		public DijkstraResult(ArrayList<Integer> path, double cost) {
			this.path = path;
			this.cost = cost;
		}
		public ArrayList<Integer> getPath() {return path;};
		public double getCost() {return cost;};
	}
}

class DistanceComparator implements Comparator<Vertex> {	

	@Override
	public int compare(Vertex v1, Vertex v2) {
		
		if (v1.dist < v2.dist) {
            return -1;
        }
        if (v1.dist > v2.dist) {
            return 1;
        }		
		return 0;
	}
}

class Vertex {
	
	public int id;
	public double dist;
	public int parent;
	
	public Vertex(int id, double dist, int parent) {
		this.id = id;
		this.dist = dist;
		this.parent = parent;
	}

	public Vertex(Vertex node) {
		this.id = node.id;
		this.dist = node.dist;
		this.parent = node.parent;
	}
}