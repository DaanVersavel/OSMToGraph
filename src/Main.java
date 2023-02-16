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
		String osmFilepath = "src/Input/map.osm";
//		String osmFilepath = "src/Input/Aalst";
		String region = "Aalst";

		RoadNetwork graph = new RoadNetwork(region);
		graph.parseOsmFile(osmFilepath);

		System.out.println("Total number of nodes and edges:");
		System.out.println("nodes: " + graph.numNodes);
		System.out.println("edges: " + graph.numEdges);

		Map<Integer, Long> nodeIndexToOsmId = new HashMap<>();
		for (Long key : graph.osmIdToNodeIndex.keySet()) {
			nodeIndexToOsmId.put(graph.osmIdToNodeIndex.get(key), key);
		}

		Set<Long> usableNodesIds = new LinkedHashSet<>();
		//add all on basis of type of road from all nodes
		for (Way w : graph.ways) {
			if (w.isCanUse()) {
				usableNodesIds.addAll(w.getNodeids());
			}
		}
		for (Long key : graph.osmIdToNodeIndex.keySet()) {
			nodeIndexToOsmId.put(graph.osmIdToNodeIndex.get(key),key);
		}
		graph.fillInMaps(nodeIndexToOsmId);
		graph.addOutgoingEdges();
		graph.pruneNotIpmortantNode(usableNodesIds);
		graph.fillInNodeMap();



		//graph.reduceToLargestConnectedComponent();
		Map<Long,NodeParser> shortest= graph.getLargestConnectedComponent2();

		System.out.println("Largest component number of nodes:");
		System.out.println("nodes: " + shortest.size());



//		JFrame frame1 = new JFrame("Graph Display hefore prunning");
//		frame1.add(new GraphDisplay2(usableNodes, graph.nodesMap));
//		frame1.pack();
//		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame1.setVisible(true);

//		//clean incomming and outgoing edges from largest graph component
//		for(NodeParser node : usableNodes.values()) {
//			node.cleanIncomingEdges(graph.incomingEdgesMap.get(node.getOsmId()),usableNodes, nodeIndexToOsmId);
//			node.cleanOutgoingedges(usableNodes);
//		}



		//removing of useless nodes
		//nodes without edges
//		int index=0;
//		for(NodeParser node : usableNodes.values()){
//			if(node.getOutgoingEdges()!=null){
//				if(node.getOutgoingEdges().isEmpty() && graph.incomingEdgesMap.get(node.getOsmId()).isEmpty()){
//					graph.nodes.get(index).setDissabled(true);
//				}
//				index++;
//			}
//		}

		//nodes with 1 outgoing edges and 1 incomming edges
//		boolean change=true;
//		int index2=0;
//		while(change){
//			change=false;
//			index2++;
//			System.out.println(index2);
//			for(NodeParser node : nodesMap.values()){
//				int numberOfIncommingEdges=graph.incomingEdgesMap.get(node.getOsmId()).size();
//				//See if node has one incomming and one outgoing edge
//				if(node.getOutgoingEdges().size()==1 && graph.incomingEdgesMap.get(node.getOsmId()).size()==1 && !node.getDissabled()){
//					//node id from incomming edge
//					EdgeParser incommingEdge = graph.incomingEdgesMap.get(node.getOsmId()).get(0);
//					Edge outgoingEdge = node.getOutgoingEdges().get(0);
//
//					long t=nodeIndexToOsmId.get(incommingEdge.headNode);
//					boolean tl= nodesMap.containsKey(t);
//					NodeParser incomingNode = nodesMap.get(nodeIndexToOsmId.get(incommingEdge.headNode));
//					NodeParser outgoingNode = nodesMap.get(outgoingEdge.getEndNodeOsmId());
//
//					double distance = incommingEdge.length+ outgoingEdge.getDistance();
//					Edge newEdge = new Edge(incomingNode.getOsmId(),outgoingNode.getOsmId(),distance);
//
//					//Add new edge to outoing of incomming edge
//					incomingNode.removeOutgoingEdge(nodeIndexToOsmId.get(incommingEdge.headNode));
//					incomingNode.addOutgoingEdge(newEdge);
//
//					nodesMap.get(node.getOsmId()).setDissabled(true);
//					change=true;
//					node.setDissabled(true);
////					nodesMap.remove(node.getOsmId());
//				}
//			}
//		}

//		boolean change=true;
//		int index2=0;
//		while(change){
//			change=false;
//			index2++;
//			System.out.println(index2);
//			for(NodeParser node : usableNodes.values()){
//				int numberOfIncommingEdges=graph.incomingEdgesMap.get(node.getOsmId()).size();
//				//See if node has one incomming and one outgoing edge
//				if(node.getOutgoingEdges().size()==1 && graph.incomingEdgesMap.get(node.getOsmId()).size()==1 && !node.getDissabled()){
//					//node id from incomming edge
//					EdgeParser incommingEdge = graph.incomingEdgesMap.get(node.getOsmId()).get(0);
//					EdgeParser outgoingEdge = node.getOutgoingEdges().get(0);
//
//					long t=nodeIndexToOsmId.get(incommingEdge.getHeadNode());
//					boolean tl= usableNodes.containsKey(t);
//					NodeParser incomingNode = usableNodes.get(incommingEdge.getBeginNodeOsmId());
//					NodeParser outgoingNode = usableNodes.get(outgoingEdge.getEndNodeOsmId());
//
//					double distance = incommingEdge.getLength()+ outgoingEdge.getLength();
//					EdgeParser newEdge = new EdgeParser(incomingNode.getOsmId(),outgoingNode.getOsmId(),distance);
//
//					//Add new edge to outoing of incomming edge
//					incomingNode.removeOutgoingEdge(node.getOsmId());
//					incomingNode.addOutgoingEdge(newEdge);
//
//					//add new incomming to outgoing edgeNode
//					outgoingNode.removeIncommingEdge(node.getOsmId(),graph.incomingEdgesMap.get(outgoingNode.getOsmId()));
//					graph.incomingEdgesMap.get(outgoingNode.getOsmId()).add(newEdge);
//
//					usableNodes.get(node.getOsmId()).setDissabled(true);
//					change=true;
//					node.setDissabled(true);
////					nodesMap.remove(node.getOsmId());
//				}
//			}
//		}
//
//

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame2 = new JFrame("Graph Display after prunning");
				frame2.add(new GraphdisplayAalst(graph.nodesMap, graph.nodesMap, graph.incomingEdgesMap, nodeIndexToOsmId));
				frame2.pack();
				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame2.setVisible(true);
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame2 = new JFrame("Graph Display of shortest connected components");
				frame2.add(new GraphdisplayAalst(shortest, graph.nodesMap, graph.incomingEdgesMap, nodeIndexToOsmId));
				frame2.pack();
				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame2.setVisible(true);
			}
		});





	}
}



