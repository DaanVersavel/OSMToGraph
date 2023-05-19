import java.util.*;

public class Main {
	public static void main(String[] args) throws NumberFormatException {
		String osmFilepath = args[0];
		//String osmFilepath = "D:/Onedrives/OneDrive - KU Leuven/2022-2023/Masterproof/Testen/openstreetmapFiles/Aalst.osm";
		//String osmFilepath = "src/Input/Oost-Vlaanderen.osm";
		//String osmFilepath = "src/Input/Vlaanderen.osm";
		//String osmFilepath = "src/Input/map.osm";
		String region = args[1];
		//String region = "aalst";
		String breakString= "--------------------------------";

		RoadNetwork graph = new RoadNetwork(region);
		graph.parseOsmFile(osmFilepath);
		System.out.println(breakString);

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

		//add all nodes to map
		graph.fillInNodeMap();

		//add waytype to nodes
		for (Way w : graph.ways) {
			if (w.isCanUse()) {
				for(long nodeid : w.getNodeids()){
					graph.nodesMap.get(nodeid).addType(w.getType());
					usableNodesIds.add(nodeid);
				}
			}
		}

		for (Long key : graph.osmIdToNodeIndex.keySet()) {
			nodeIndexToOsmId.put(graph.osmIdToNodeIndex.get(key),key);
		}
		graph.fillInMaps(nodeIndexToOsmId);
		graph.addOutgoingEdges();
		graph.pruneNotImportantNode(usableNodesIds);

		Map<Long,NodeParser> shortest= graph.getLargestConnectedComponent2();


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

//		//add type if begin node has a type if not chose end type
//		for(NodeParser node: shortest.values()) {
//			for(EdgeParser edge: node.getOutgoingEdges()){
//				if(edge.getEdgeType().equals("")){
//					NodeParser begin = shortest.get(edge.getBeginNodeOsmId());
//					NodeParser end = shortest.get(edge.getEndNodeOsmId());
//					boolean changed= false;
//					for (String typebegin : begin.getTypes()) {
//						edge.setEdgeType(typebegin);
//						changed = true;
//						break;
//					}
//					if(Boolean.FALSE.equals(changed)){
//						for(String typeEnd : end.getTypes()){
//							edge.setEdgeType(typeEnd);
//							break;
//						}
//					}
//				}
//			}
//		}

		Map<String,Integer> priorityMap = makePriorityMap();
		//Now based on priority
		//add type if begin node has a type if not chose end type
		for(NodeParser node: shortest.values()) {
			for(EdgeParser edge: node.getOutgoingEdges()){
				if(edge.getEdgeType().equals("")){
					NodeParser begin = shortest.get(edge.getBeginNodeOsmId());
					NodeParser end = shortest.get(edge.getEndNodeOsmId());

					String highestPriority = "L";

					for(String type : begin.getTypes()){
						if(priorityMap.get(type) < priorityMap.get(highestPriority)){
							highestPriority=type;
						}
					}

					for(String type : end.getTypes()){
						if(priorityMap.get(type) < priorityMap.get(highestPriority)){
							highestPriority=type;
						}
					}
					edge.setEdgeType(highestPriority);
				}
			}
		}

		long numberOfEdges=0;
		for(NodeParser node : shortest.values()){
			numberOfEdges+=node.getOutgoingEdges().size();
		}
		System.out.println(breakString);
		System.out.println("Largest component:");
		System.out.println("nodes: " + shortest.size());
		System.out.println("edges: " + numberOfEdges);


		//nodes with 1 outgoing edges and 1 incoming edges
		boolean change=true;
		while(change){
			change=false;
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

					if(!roadType.equals("d") && !roadType.equals("living_street")){
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
		shortest.entrySet().removeIf(entry -> entry.getValue().getDissabled());

		//remove nodes without outgoing edges
		boolean changedNode=true;
		while(changedNode){
			changedNode=false;
			Iterator<NodeParser> itNode = shortest.values().iterator();
			while(itNode.hasNext()){
				NodeParser node = itNode.next();
				if(node.getOutgoingEdges().isEmpty()){
					Iterator<EdgeParser> itEdge =graph.incomingEdgesMap.get(node.getOsmId()).iterator();
					while(itEdge.hasNext()){
						EdgeParser edge = itEdge.next();
						NodeParser begin = shortest.get(edge.getBeginNodeOsmId());
						begin.removeOutgoingEdge(node.getOsmId());
						itEdge.remove();
					}
					changedNode=true;
					itNode.remove();
				}
			}
		}

		Map<String,Double> defaultSpeeds = makeDefaultSpeedMap();

		for(NodeParser node : shortest.values()){
            for(EdgeParser edge : node.getOutgoingEdges()){
				edge.setDefaultTravelTime(edge.getLength()/ defaultSpeeds.get(edge.getEdgeType()));
			}
        }

		System.out.println(breakString);
		numberOfEdges=0;
		for(NodeParser node : shortest.values()){
			numberOfEdges+=node.getOutgoingEdges().size();
		}
		System.out.println("Number after filtering:");
		System.out.println("nodes: " + shortest.size());
		System.out.println("edges: " + numberOfEdges);
		System.out.println(breakString);


		int count=0;
		for(NodeParser node : shortest.values()){
			for( EdgeParser edge : node.getOutgoingEdges()){
				if(edge.getEdgeType().equals("living_street")&& edge.getLength()>200){
					System.out.print("");
				}
			}
		}


		Output2 output = new Output2(shortest);
		System.out.println("Start writing file");
		output.writeToFile(region);
		System.out.println("Done");
	}

	private static Map<String, Double> makeDefaultSpeedMap() {
		Map<String, Double> defaultSpeeds = new HashMap<>();
		defaultSpeeds.put("primary", 19.4444);//70
		defaultSpeeds.put("primary_link", 19.4444);//70
		defaultSpeeds.put("secondary", 13.8888);//50
		defaultSpeeds.put("secondary_link", 13.8888);//50
		defaultSpeeds.put("tertiary", 13.8888);//50
		defaultSpeeds.put("tertiary_link", 13.8888);//50
		defaultSpeeds.put("residential", 13.8888);//50

		//defaultSpeeds.put("living_street", 13.8888);//50
		defaultSpeeds.put("living_street", 8.33333333);//30

		defaultSpeeds.put("motorway_link", 19.4444);//70
        defaultSpeeds.put("trunk", 19.4444);//70
		defaultSpeeds.put("motorway", 33.3333); //120

		return defaultSpeeds;
	}

	private static Map<String, Integer> makePriorityMap() {
		Map<String, Integer> priorityMap = new HashMap<>();
		priorityMap.put("secondary", 1);//50
		priorityMap.put("tertiary", 1);//50
		priorityMap.put("residential", 1);//50

		priorityMap.put("secondary_link", 2);//50
		priorityMap.put("tertiary_link", 2);//50

		priorityMap.put("primary", 3);//70
		priorityMap.put("trunk", 3);//70

		priorityMap.put("primary_link", 4);//70

		priorityMap.put("motorway", 5); //120

		priorityMap.put("motorway_link", 6);//70

		priorityMap.put("living_street", 9);//50

		priorityMap.put("L", 10); //120

		return priorityMap;
	}
}



