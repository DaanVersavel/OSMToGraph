import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class Main {
	public static void main(String[] args) throws NumberFormatException, IOException {
		String osmFilepath = "src/Input/Aalst";
		//String osmFilepath = "src/Input/map1.osm";
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

		graph.joinWays();
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
		graph.pruneNotImportantNode(usableNodesIds);
		graph.fillInNodeMap();

		//graph.reduceToLargestConnectedComponent();
		Map<Long,NodeParser> shortest= graph.getLargestConnectedComponent2();

		System.out.println("number of connected nodes: " + shortest.size());

//		for(NodeParser node : shortest.values()) {
//			for(EdgeParser edge: node.getOutgoingEdges()){
//				for(Way way : graph.ways){
//					if(way.getNodeids().contains(edge.getBeginNodeOsmId())&&way.getNodeids().contains(edge.getEndNodeOsmId())){
//						edge.setEdgeType(way.getType());
//						break;
//					}
//				}
//			}
//		}

		for(Way way : graph.wayMap.values()){
			if(way.isCanUse()==false){
				System.out.println();
			}
		}

		for(NodeParser node : shortest.values()) {
			for(EdgeParser edge: node.getOutgoingEdges()){
				for(Way way : graph.wayMap.values()){
					if(way.getNodeids().contains(edge.getBeginNodeOsmId())&&way.isCanUse()&&way.getNodeids().contains(edge.getEndNodeOsmId())){
						edge.setEdgeType(way.getType());
						node.addType(way.getType());
                        break;
					}
				}
			}
		}

		List<EdgeParser> tl = new ArrayList<>();


		//TODO MAg dit wel
		for(NodeParser node: shortest.values()) {
			for(EdgeParser edge: node.getOutgoingEdges()){
				if(edge.getEdgeType().equals("")){
					NodeParser begin = shortest.get(edge.getBeginNodeOsmId());
					NodeParser end = shortest.get(edge.getEndNodeOsmId());
					for(String typebegin : begin.getTypes()){
						if(end.getTypes().contains(typebegin)){
							edge.setEdgeType(typebegin);
                            break;
						}
					}
				}
			}
		}

		for(NodeParser node: shortest.values()) {
			for(EdgeParser edge: node.getOutgoingEdges()){
				if(edge.getEdgeType().equals("")){
					NodeParser begin = shortest.get(edge.getBeginNodeOsmId());
					NodeParser end = shortest.get(edge.getEndNodeOsmId());
					boolean changed= false;
					for (String typebegin : begin.getTypes()) {
						edge.setEdgeType(typebegin);
						changed = true;
						break;
					}
					if(Boolean.FALSE.equals(changed)){
						for(String typeEnd : end.getTypes()){
							edge.setEdgeType(typeEnd);
							break;
						}
					}
					if(edge.getEdgeType().equals("")){
						tl.add(edge);
					}
				}
			}
		}

		System.out.println(tl);


		System.out.println("Largest component number of nodes:");
		System.out.println("nodes: " + shortest.size());

		//clean incomming and outgoing edges from largest graph component
		for(NodeParser node : shortest.values()) {
//			node.cleanIncomingEdges(graph.incomingEdgesMap.get(node.getOsmId()),usableNodes, nodeIndexToOsmId);
			node.cleanOutgoingedges(shortest);
		}

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

					//See if they have a common type of road
					String roadType="d";
					for(String type : incomingNode.getTypes()){
						if(outgoingNode.getTypes().contains(type)){
                            roadType = type;
                            break;
                        }
					}

					if(!roadType.equals("d")){
						//Make new edge to add to nodes
						double distance = incomingEdge.getLength() + outgoingEdge.getLength();
						EdgeParser newEdge = new EdgeParser(incomingNode.getOsmId(),outgoingNode.getOsmId(),distance);
						newEdge.setEdgeType(roadType);

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
		}


		//remove dissabled nodes
		Iterator<Map.Entry<Long, NodeParser>> it = shortest.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, NodeParser> entry = it.next();
			if (entry.getValue().getDissabled()) {
				it.remove();
			}
		}


		//Test
		Dijkstra dijkstra = new Dijkstra(shortest);
		boolean tb=shortest.containsKey(533710846L);
		boolean tk= dijkstra.solveDijkstra(533710829L);
		System.out.println(tk);


//		//Display graph
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				JFrame frame2 = new JFrame("Show All");
//				frame2.add(new GraphdisplayAalst(shortest, graph.nodesMap, graph.incomingEdgesMap, nodeIndexToOsmId,true));
//				frame2.pack();
//				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				frame2.setVisible(true);
//			}
//		});

//		SwingUtilities.invokeLater(() -> {
//			JFrame frame2 = new JFrame("Show Selected");
//			frame2.add(new GraphdisplayAalst(shortest, graph.nodesMap, graph.incomingEdgesMap, nodeIndexToOsmId,false));
//			frame2.pack();
//			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame2.setVisible(true);
//		});


		Set<String> wautypes= new HashSet<>();
		for(NodeParser node : shortest.values()){
            for(EdgeParser edge : node.getOutgoingEdges()){
				wautypes.add(edge.getEdgeType());
			}
        }
		for(String type : wautypes){
			System.out.println(type);
		}


		Output output = new Output(shortest);
		output.writeToFile("out");

		Map<String,Double[][]> speedMatrixMap= getSpeedMatrixMap();
		System.out.println();
	}

	private static Map<String, Double[][]> getSpeedMatrixMap() {
		Map<String, Double[][]> speedMatrixMap = new HashMap<>();
		//Primary

		Double[][] speedMatrix = {{0.0,25200.0,19.4444},{25200.0,32400.0,11.1111},{32400.0,43200.0,19.4444},{43200.0,46800.0,16.6666},{46800.0,55800.0,19.4444},{55800.0,68400.0,11.1111},{68400.0,86400.0,19.4444}};
		speedMatrixMap.put("primary", speedMatrix);
		//Secondary
		Double[][] speedMatrix2 = {{0.0, 25200.0, 13.8888}, {25200.0, 32400.0, 8.3333}, {32400.0, 43200.0, 13.8888},{43200.0,46800.0,13.8888}, {46800.0,55800.0,13.8888}, {55800.0,68400.0,8.3333}, {68400.0,86400.0,13.8888}};
		speedMatrixMap.put("secondary", speedMatrix2);
		//Tertiary
		Double[][] speedMatrix3 = {{0.0, 25200.0, 13.8888}, {25200.0, 32400.0, 8.3333}, {32400.0, 43200.0, 13.8888},{43200.0,46800.0,13.8888}, {46800.0,55800.0,13.8888}, {55800.0,68400.0,8.3333}, {68400.0,86400.0,13.8888}};
		speedMatrixMap.put("tertiary", speedMatrix3);
		//Residential
		Double[][] speedMatrix4 = {{0.0, 25200.0, 13.8888}, {25200.0, 32400.0, 8.3333}, {32400.0, 43200.0, 13.8888},{43200.0,46800.0,8.3333}, {46800.0,55800.0,13.8888}, {55800.0,68400.0,8.3333}, {68400.0,86400.0,13.8888}};
		speedMatrixMap.put("residential", speedMatrix4);
		//living_street
		Double[][] speedMatrix5 = {{0.0, 25200.0, 5.5555}, {25200.0, 32400.0, 5.5555}, {32400.0, 43200.0, 5.55550},{43200.0,46800.0,5.5555}, {46800.0,55800.0,5.5555}, {55800.0,68400.0,5.5555}, {68400.0,86400.0,5.5555}};
		speedMatrixMap.put("living_street", speedMatrix5);
		//motor_link
		Double[][] speedMatrix6 = {{0.0,25200.0,19.4444},{25200.0,32400.0,11.1111},{32400.0,43200.0,19.4444},{43200.0,46800.0,16.6666},{46800.0,55800.0,19.4444},{55800.0,68400.0,11.1111},{68400.0,86400.0,19.4444}};
		speedMatrixMap.put("motor_link", speedMatrix6);
		//trunk
		Double[][] speedMatrix7 = {{0.0, 25200.0,19.4444}, {25200.0,32400.0,19.4444}, {32400.0, 43200.0, 19.4444},{43200.0,46800.0,19.4444}, {46800.0,55800.0,19.4444}, {55800.0,68400.0,19.4444}, {68400.0,86400.0,19.4444}};
		speedMatrixMap.put("trunk", speedMatrix7);
		//motorway
		Double[][] speedMatrix8 = {{0.0, 25200.0, 33.3333}, {25200.0, 32400.0, 27.7777}, {32400.0, 43200.0, 30.5555},{43200.0,46800.0,27.7777}, {46800.0,55800.0,30.5555}, {55800.0,68400.0,25.0}, {68400.0,86400.0,33.3333}};
		speedMatrixMap.put("motorway", speedMatrix8);

		return speedMatrixMap;
	}
}



