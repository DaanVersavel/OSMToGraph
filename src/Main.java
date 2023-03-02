import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class Main {
	public static void main(String[] args) throws NumberFormatException, IOException {
		String osmFilepath = "src/Input/Aalst";
		//String osmFilepath = "src/Input/map1.osm";
		//String osmFilepath = "src/Input/map.osm";
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

		//join ways based on streetname
		graph.joinWays();
		Set<Long> usableNodesIds = new LinkedHashSet<>();


		//add all on basis of type of road from all nodes
		graph.fillInNodeMap();

		//add waytype to nodes
		for (Way w : graph.ways) {
			if (w.isCanUse()) {
				for(long nodeid : w.getNodeids()){
					graph.nodesMap.get(nodeid).addType(w.getType());
					usableNodesIds.add(nodeid);
				}
				//usableNodesIds.addAll(w.getNodeids());
			}
		}



		for (Long key : graph.osmIdToNodeIndex.keySet()) {
			nodeIndexToOsmId.put(graph.osmIdToNodeIndex.get(key),key);
		}
		graph.fillInMaps(nodeIndexToOsmId);
		graph.addOutgoingEdges();
		graph.pruneNotImportantNode(usableNodesIds);

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

		//clean incomming and outgoing edges from the largest graph component
		for(NodeParser node : shortest.values()) {
			node.cleanIncomingEdges(graph.incomingEdgesMap.get(node.getOsmId()),shortest, nodeIndexToOsmId);
			node.cleanOutgoingedges(shortest);
		}

		//add type if begin and end node have same type
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


        //add type if begin node has a type if not chose end type
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
				}
			}
		}


		for(NodeParser node: shortest.values()) {
			for(EdgeParser edge: node.getOutgoingEdges()){
				if(edge.getEdgeType().equals("")){
					System.out.println("fefef");
				}
			}
		}



		System.out.println("Largest component number of nodes:");
		System.out.println("nodes: " + shortest.size());


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
					}else {
						System.out.println();
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

//		List<NodeParser> nodeWithoutOutgoing = new ArrayList<>();
//		for(NodeParser node: shortest.values()) {
//			if(node.getOutgoingEdges().size()<1){
//				//node.setDissabled(true);
//				nodeWithoutOutgoing.add(node);
//				List<EdgeParser> tg =  graph.incomingEdgesMap.get(node.getOsmId());
//			}
//		}

		//remove nodes without outgoing edges
		boolean changedNode=true;
		int index=0;
		while(changedNode){
			changedNode=false;
			Iterator<NodeParser> itNode = shortest.values().iterator();
			while(itNode.hasNext()){
				NodeParser node = itNode.next();
				if(node.getOutgoingEdges().isEmpty()){
					int s=graph.incomingEdgesMap.get(node.getOsmId()).size();
					Iterator<EdgeParser> itEdge =graph.incomingEdgesMap.get(node.getOsmId()).iterator();
					while(itEdge.hasNext()){
						EdgeParser edge = itEdge.next();
						NodeParser begin = shortest.get(edge.getBeginNodeOsmId());
						begin.removeOutgoingEdge(node.getOsmId());
						itEdge.remove();
						//graph.incomingEdgesMap.get(node.getOsmId()).remove(edge);
					}
//					for(int i=0;i<graph.incomingEdgesMap.get(node.getOsmId()).size();i++){
//						EdgeParser edge = graph.incomingEdgesMap.get(node.getOsmId()).get(0);
//						NodeParser begin = shortest.get(edge.getBeginNodeOsmId());
//						begin.removeOutgoingEdge(node.getOsmId());
//						graph.incomingEdgesMap.get(node.getOsmId()).remove(edge);
//					}
					changedNode=true;
					itNode.remove();
				}
			}
			index++;
			System.out.println(index);
		}


		//Test
		int numberOfFaults=0;
		List<Long> nodesWithError = new ArrayList<>();
		for(NodeParser node : shortest.values()){
			Dijkstra dijkstra = new Dijkstra(shortest);
			if(!dijkstra.solveDijkstra(node.getOsmId())){
				numberOfFaults++;
				nodesWithError.add(node.getOsmId());
			}
		}
		System.out.println("Number of faults: " + numberOfFaults);
		System.out.println("Number of nodes: " + shortest.size());

//		Dijkstra dijkstra = new Dijkstra(shortest);
//		boolean tb=shortest.containsKey(258408294L);
//		boolean tk= dijkstra.solveDijkstra(258408294L);
//		System.out.println(tk);


		//Display graph
//		SwingUtilities.invokeLater(() -> {
//			JFrame frame2 = new JFrame("Show All");
//			frame2.add(new GraphdisplayAalst(shortest, graph, nodeIndexToOsmId,true));
//			frame2.pack();
//			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame2.setVisible(true);
//		});

		SwingUtilities.invokeLater(() -> {
			JFrame frame2 = new JFrame("Show Selected");
			frame2.add(new GraphdisplayAalst(shortest,graph, nodeIndexToOsmId,false,nodesWithError));
			frame2.pack();
			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame2.setVisible(true);
		});


		Set<String> waytypes= new HashSet<>();
		for(NodeParser node : shortest.values()){
            for(EdgeParser edge : node.getOutgoingEdges()){
				waytypes.add(edge.getEdgeType());
			}
        }
		//print out the types of ways
		for(String type : waytypes){
			System.out.println(type);
		}


		Output output = new Output(shortest);
		output.writeToFile("out");


	}
}



