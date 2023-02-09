import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {
	public static double calculateDistance(NodeParser begin, NodeParser end) {
		double latBegin =begin.getLatitude() ;
		double longBegin = begin.getLongitude();
		double latEnd = end.getLatitude();
		double longEnd = end.getLongitude();

		double distanceKM= Math.sqrt(Math.pow(longEnd - longBegin, 2) + Math.pow(latEnd - latBegin, 2));
		double distancceM = distanceKM*1000;
		return distancceM;
	}

	public static void main(String[] args) throws NumberFormatException, IOException, ParserConfigurationException, SAXException {
		String osmFilepath = "src/Input/Aalst";
		String region = "Aalst";

		RoadNetwork graph = new RoadNetwork(region);
		graph.parseOsmFile(osmFilepath);

		System.out.println("Total number of nodes and edges:");
		System.out.println("nodes: " + graph.numNodes);
		System.out.println("edges: " + graph.numEdges);
		System.out.println();

		//graph.reduceToLargestConnectedComponent();

		System.out.println("Largest component number of nodes and edges:");
		System.out.println("nodes: " + graph.numNodes);
		System.out.println("edges: " + graph.numEdges);

		int aantal = 0;
		for (Way w : graph.ways) {
			for (Long i : w.getNodeids()) aantal++;
		}
		System.out.println("Number of nodes in ways: " + aantal / 2);

		Map<Integer, Long> nodeIndexToOsmId = new HashMap<>();


		for (Long key : graph.osmIdToNodeIndex.keySet()) {
			nodeIndexToOsmId.put(graph.osmIdToNodeIndex.get(key), key);
		}

		Graph myGraph = new Graph();

		for (int i = 0; i < graph.outgoingEdges.size(); i++) {
			NodeParser nodeParser = graph.nodes.get(i);
			ArrayList<EdgeParser> edges = graph.outgoingEdges.get(i);
			for (int j = 0; j < edges.size(); j++) {
				EdgeParser p = edges.get(j);
				Edge e = new Edge(nodeIndexToOsmId.get(i), nodeIndexToOsmId.get(p.headNode));
				double distanceM = calculateDistance(nodeParser, graph.nodes.get(p.headNode));
				e.setDistance(distanceM);
				nodeParser.addOutgoingEdge(e);
			}
		}
		double max = Double.MIN_VALUE;
		for (NodeParser node : graph.nodes) {
			if (node.getLatitude() > max) max = node.getLatitude();
		}
		System.out.println("Lat MAX: " + max);
//		System.out.println("Lengte: "+graph.nodes.get(graph.osmIdToNodeIndex.get(533710827)));


//		JFrame frame = new JFrame("Graph Display");
//		frame.add(new GraphDisplay(graph));
//		frame.pack();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);


		Map<Long, NodeParser> nodesMap = new HashMap<>();

		for (NodeParser node : graph.nodes) {
			nodesMap.put(node.getOsmId(), node);
		}

		//display ways
		Set<Long> usableNodesIds = new LinkedHashSet<>();

		for (Way w : graph.ways) {
			if (w.isCanUse()) {
				usableNodesIds.addAll(w.getNodeids());
			}
		}

		ArrayList<NodeParser> usableNodes = new ArrayList<>();
		for (Long id : usableNodesIds) {
            usableNodes.add(nodesMap.get(id));
        }


		JFrame frame2 = new JFrame("Graph Display after prunning");
		frame2.add(new GraphdisplayAalst(usableNodes, nodesMap));
		frame2.pack();
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setVisible(true);


//		//removing of useless nodes
//		//nodes without edges
//		int index=0;
//		for(NodeParser node : nodesMap.values()){
//			if(node.getOutgoingEdges().isEmpty() && graph.incomingEdges.get(index).isEmpty()){
////				int nodeId=graph.osmIdToNodeIndex.get(node.getOsmId());
////				graph.osmIdToNodeIndex.remove(node.getOsmId());
////				nodeIndexToOsmId.remove(nodeId);
////				graph.nodes.remove(index);
//				graph.nodes.get(index).setDissabled(true);
//			}
//			index++;
//		}
//
//		//nodes with 1 outgoing edges and 1 incomming edges
//		for(int i=0; i< graph.nodes.size();i++){
//			NodeParser node = graph.nodes.get(i);
//			//See if node has one incomming and one outgoing edge
//			if(node.getOutgoingEdges().size()==1 && graph.incomingEdges.get(i).size()==1){
//				//node id from incomming edge
//				EdgeParser incommingEdge = graph.incomingEdges.get(i).get(0);
//				Edge outgoingEdge = node.getOutgoingEdges().get(0);
//
//				NodeParser incomingNode = graph.nodes.get(incommingEdge.headNode);
//				NodeParser outgoingNode = graph.nodes.get(graph.osmIdToNodeIndex.get(outgoingEdge.getEndNodeId()));
//
//				double distance = incommingEdge.length+ outgoingEdge.getDistance();
//				Edge newEdge = new Edge(incomingNode.getOsmId(),outgoingNode.getOsmId(),distance);
//
//				//Add new edge to outoing of incomming edge
//				incomingNode.removeOutgoingEdge(nodeIndexToOsmId.get(incommingEdge.headNode));
//				incomingNode.addOutgoingEdge(newEdge);
//
//				graph.nodes.get(i).setDissabled(true);
//			}
//		}
//
////		JFrame frame2 = new JFrame("Graph Display after prunning");
////		frame2.add(new GraphDisplay(graph));
////		frame2.pack();
////		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		frame2.setVisible(true);
//
	}
}



