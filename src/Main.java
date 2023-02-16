import java.io.IOException;
import java.util.*;
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




//		//clean incomming and outgoing edges from largest graph component
//		for(NodeParser node : usableNodes.values()) {
//			node.cleanIncomingEdges(graph.incomingEdgesMap.get(node.getOsmId()),usableNodes, nodeIndexToOsmId);
//			node.cleanOutgoingedges(usableNodes);
//		}





		//TODO largest connected werkt en verkleinen ook
		//TODO nog wegType toekennen aan edges
		//TODO eventueel grafisch fixen uitbreiden

		//nodes with 1 outgoing edges and 1 incomming edges
		boolean change=true;
		int index2=0;
		while(change){
			change=false;
			index2++;
			System.out.println(index2);
			for(NodeParser node : shortest.values()){

				//See if node has one incoming and one outgoing edge
				if(node.getOutgoingEdges().size()==1 && graph.incomingEdgesMap.get(node.getOsmId()).size()==1 && !node.getDissabled()){

					//node id from incoming edge
					EdgeParser incomingEdge = graph.incomingEdgesMap.get(node.getOsmId()).get(0);
					EdgeParser outgoingEdge = node.getOutgoingEdges().get(0);

					//Get Nodes
					NodeParser incomingNode = shortest.get(incomingEdge.getBeginNodeOsmId());
					NodeParser outgoingNode = shortest.get(outgoingEdge.getEndNodeOsmId());

					//Make new edge to add to nodes
					double distance = incomingEdge.getLength() + outgoingEdge.getLength();
					EdgeParser newEdge = new EdgeParser(incomingNode.getOsmId(),outgoingNode.getOsmId(),distance);

					//Incomming Node
					incomingNode.removeOutgoingEdge(incomingEdge.getEndNodeOsmId());
					incomingNode.addOutgoingEdge(newEdge);

					//Outgoing Node
					List<EdgeParser> incommingedgesArray= graph.incomingEdgesMap.get(outgoingNode.getOsmId());
					outgoingNode.removeIncommingEdge(node.getOsmId(),incommingedgesArray);
					graph.incomingEdgesMap.get(outgoingNode.getOsmId()).add(newEdge);

					//Disable node
					shortest.get(node.getOsmId()).setDissabled(true);
					change=true;
				}
			}
		}

		Iterator<Map.Entry<Long, NodeParser>> it = shortest.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, NodeParser> entry = it.next();
			if (entry.getValue().getDissabled()) {
				it.remove();
			}
		}


		//Test
		Dijkstra dijkstra = new Dijkstra(shortest);
		boolean tk= dijkstra.solveDijkstra(258408294L);
		System.out.println(tk);


		//Display graph
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				JFrame frame2 = new JFrame("Show All");
//				frame2.add(new GraphdisplayAalst(shortest, graph.nodesMap, graph.incomingEdgesMap, nodeIndexToOsmId,true));
//				frame2.pack();
//				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				frame2.setVisible(true);
//			}
//		});
//
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				JFrame frame2 = new JFrame("Show Selected");
//				frame2.add(new GraphdisplayAalst(shortest, graph.nodesMap, graph.incomingEdgesMap, nodeIndexToOsmId,false));
//				frame2.pack();
//				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				frame2.setVisible(true);
//			}
//		});




	}
}



