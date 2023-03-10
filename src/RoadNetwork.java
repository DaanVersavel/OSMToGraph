import java.io.File;
import java.util.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RoadNetwork extends DefaultHandler {
	
	// Name of the region (used when to save and load the graph)
	String region;
	
	// Number of Nodes and Edges in the Graph
	public int numNodes;
	public int numEdges;
	
	//Graph Adjacency lists for outgoing and incoming Edges
	public List<ArrayList<EdgeParser>> outgoingEdges;
	public Map<Long,ArrayList<EdgeParser>> outgoingEdgesMap;
	public List<ArrayList<EdgeParser>> incomingEdges;
	public Map<Long,ArrayList<EdgeParser>> incomingEdgesMap;

	// List of all the nodes in the Graph
	public List<NodeParser> nodes;

	Map<Long, NodeParser> nodesMap;


	// Maps osmId of a node to its index in the nodes list
	public Map<Long, Integer> osmIdToNodeIndex;
	
	// List of all road types in the osm file 
	public List<String> roadTypes;
	
	// Used to reference all the nodes that make up that particular "way" in the .osm file
	public List<Long> wayNodes;
	
	// Speed values based on road type, should be set according to the region
	public Map<String, Integer> speeds;

	// List of all the Ways in the Graph
	public List<Way> ways;
	public Map<String,Way> wayMap = new HashMap<>();


	private boolean inWay = false;
	private boolean isHighway = false;
	private String key, valHighway, valOneway;
	
	// Constructor  
	public RoadNetwork(String region) throws NumberFormatException {
		this.region = region;
				
		numNodes = 0;
		numEdges = 0;
		
		outgoingEdges = new ArrayList<>();
		outgoingEdgesMap= new HashMap<>();
		incomingEdges = new ArrayList<>();
		incomingEdgesMap = new HashMap<>();
		nodesMap = new HashMap<>();


		nodes = new ArrayList<>();
		
		speeds = new HashMap<>();
		speeds.put("motorway", 110);
		speeds.put("trunk", 110);
		speeds.put("primary", 70);
		speeds.put("secondary", 60);
		speeds.put("tertiary", 50);
		speeds.put("motorway_link", 50);
		speeds.put("trunk_link", 50);
		speeds.put("primary_link", 50);
		speeds.put("secondary_link", 50);
		speeds.put("road", 40);
		speeds.put("unclassified", 40);
		speeds.put("residential", 30);
		speeds.put("unsurfaced", 30);
		speeds.put("living_street", 10);
		speeds.put("service", 5);
	}
	
	private void addNode(long osmId, double latitude, double longitude) {
		NodeParser node = new NodeParser(osmId, latitude, longitude);
		
		outgoingEdges.add(new ArrayList<>());
		incomingEdges.add(new ArrayList<>());
		nodes.add(node);
		osmIdToNodeIndex.put(osmId, numNodes);
		numNodes += 1;
	}
	
	private void addEdge(int baseNode, int headNode, double length, double travelTime ) {
		EdgeParser outgoingEdge = new EdgeParser(headNode, length, travelTime);
		EdgeParser incomingEdge = new EdgeParser(baseNode, length, travelTime);

		outgoingEdges.get(baseNode).add(outgoingEdge);
		incomingEdges.get(headNode).add(incomingEdge);
		numEdges += 1;
	}

	
	public void parseOsmFile(String osmFilepath) {
		osmIdToNodeIndex = new HashMap<>();
		wayNodes = new ArrayList<>();
		roadTypes = new ArrayList<>();
		ways = new ArrayList<>();
		valOneway = "no";
		
		File xmlDoc = new File(osmFilepath);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();			
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(xmlDoc, this);		  
		}
		catch(Exception e) {
			System.out.println("Problem, " + e.toString());
		} 
	}
	
	// At beginning of tag
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("node")) {
			long osmId = Long.parseLong(attributes.getValue("id"));
			float lat = Float.parseFloat(attributes.getValue("lat"));
			float lon = Float.parseFloat(attributes.getValue("lon"));
			addNode(osmId, lat, lon);
		}
		else if (qName.equalsIgnoreCase("way")) {
			inWay = true;
		}
		else if (qName.equalsIgnoreCase("nd") && inWay) {
			wayNodes.add(Long.parseLong(attributes.getValue("ref")));
		}
		else if (qName.equalsIgnoreCase("tag") && inWay) {
			key = attributes.getValue("k");
			if (key.equals("highway")) {
				isHighway = true;
				valHighway = attributes.getValue("v");
				if (!roadTypes.contains(valHighway)) {
					roadTypes.add(valHighway);
				}
			}
			if (key.equals("oneway")) {
				valOneway = attributes.getValue("v");
			}
		}
		//zelf bijgezet
		if (qName.equalsIgnoreCase("way")) {
			long id=Long.parseLong(attributes.getValue("id"));
			Way way = new Way(id);
			ways.add(way);
			inWay =true;
		}
		if (qName.equalsIgnoreCase("nd") && inWay) {
			long nodeid= Long.parseLong(attributes.getValue("ref"));
			ways.get(ways.size()-1).addNodeid(nodeid);
		}
		if (qName.equalsIgnoreCase("tag") && inWay) {
			key = attributes.getValue("k");
			if (key.equals("highway")) {
				String atr=attributes.getValue("v");
				if(atr.equals("primary") || atr.equals("primary_link")||atr.equals("secondary")||atr.equals("secondary_link")
				||atr.equals("tertiary")||atr.equals("tertiary_link")||atr.equals("residential")||atr.equals("living_street") ||
						atr.equals("motorway_link")||atr.equals("trunk")||atr.equals("motorway")) {
					ways.get(ways.size()-1).setType(atr);
					ways.get(ways.size()-1).setCanUse(true);
				}
				else{
					ways.get(ways.size()-1).setType(atr);
					ways.get(ways.size()-1).setCanUse(false);
				}

				isHighway = true;
			}
			if(ways.get(ways.size()-1).getType()==null){
				ways.get(ways.size()-1).setCanUse(false);
			}
			if (key.equals("name")) {
				ways.get(ways.size()-1).setName(attributes.getValue("v"));
				isHighway = true;
			}
//			if(key.equals("building")) {
//				ways.get(ways.size()-1).setCanUse(false);
//				isHighway = true;
//			}
//			if(key.equals("leisure")) {
//				ways.get(ways.size()-1).setCanUse(false);
//				isHighway = true;
//			}
		}
	}
  
	// At end of tag
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("way") && isHighway && speeds.keySet().contains(valHighway)) {
			int speed = speeds.get(valHighway);
			
			// Insert edges for each two consecutive nodes in the way
			long baseNodeOsmId = wayNodes.get(0);
			int baseNode = osmIdToNodeIndex.get(baseNodeOsmId);
			
			for(int i=1; i<wayNodes.size(); i++) {
				long headNodeOsmId = wayNodes.get(i);
				int headNode = osmIdToNodeIndex.get(headNodeOsmId);
				
				double length = HaversineDistance.distance(nodes.get(baseNode).getLatitude(), nodes.get(baseNode).getLongitude(), nodes.get(headNode).getLatitude(), nodes.get(headNode).getLongitude());

				double travelTime = length/speed;
				
				if (valOneway.equals("yes") || valOneway.equals("1")) {
					addEdge(baseNode, headNode, length, travelTime);
				}
				
				else if (valOneway.equals("-1")) {
					addEdge(headNode, baseNode, length, travelTime );
				}
				
				else {
					addEdge(baseNode, headNode, length, travelTime);
					addEdge(headNode, baseNode, length, travelTime);
				}				
				
				baseNodeOsmId = headNodeOsmId;
				baseNode = headNode;
			}
			wayNodes.clear();
			valOneway = "no";
			inWay = false;
		}
	}

	public void fillInMaps(Map<Integer, Long> nodeIndexToOsmId) {
		//incomming edges Map
		for(int i=0; i<incomingEdges.size(); i++) {
			ArrayList<EdgeParser> edgesArray =incomingEdges.get(i);
			long osmid= nodeIndexToOsmId.get(i);
			for(EdgeParser edgeParser : edgesArray) {
				edgeParser.setBeginNodeOsmId(nodeIndexToOsmId.get(edgeParser.headNode));
				edgeParser.setEndNodeOsmId(osmid);
            }
			incomingEdgesMap.put(osmid, edgesArray);
		}
		//outgoing edges Map
		for(int i=0; i<outgoingEdges.size(); i++) {
			ArrayList<EdgeParser> edgesArray =outgoingEdges.get(i);
			long beginosmid= nodeIndexToOsmId.get(i);
			for(EdgeParser edgeParser : edgesArray) {
				edgeParser.setBeginNodeOsmId(beginosmid);
				edgeParser.setEndNodeOsmId(nodeIndexToOsmId.get(edgeParser.headNode));
			}
			outgoingEdgesMap.put(beginosmid, edgesArray);
		}
	}

	public void fillInNodeMap(){
		//nodes map
		for (NodeParser node : nodes) {
			nodesMap.put(node.getOsmId(), node);
		}
	}

	public void addOutgoingEdges(){
		for(NodeParser node : nodes) {
			ArrayList<EdgeParser> edgesArray = outgoingEdgesMap.get(node.getOsmId());
			node.setOutgoingEdges(edgesArray);
		}
	}

	public void pruneNotImportantNode(Set<Long> usableNodesIds) {
		ArrayList<NodeParser> nodesToRemove = new ArrayList<>();
		List<NodeParser> nodesToKeep = new ArrayList<>();
		for(int i =0; i<nodes.size(); i++) {
			NodeParser node = nodes.get(i);
			if(!usableNodesIds.contains(node.getOsmId())) {
				//remove incoming edges
				ArrayList<EdgeParser> incomingEdgesArray = incomingEdges.get(i);
				for(int j =0; j<incomingEdgesArray.size(); j++) {
					EdgeParser incommingEdge = incomingEdgesArray.get(j);
                    NodeParser beginNode= nodes.get(incommingEdge.getHeadNode());
					ArrayList<EdgeParser> outgoingEdgesArray2 = outgoingEdges.get(incommingEdge.getHeadNode());
					beginNode.removeOutgoingEdgeHeadNode(i,outgoingEdgesArray2);
				}
				incomingEdges.set(i,new ArrayList<>());

				//remove outgoing edges
				ArrayList<EdgeParser> outGoingEdgesArray = outgoingEdges.get(i);
				for(int j =0; j<outGoingEdgesArray.size(); j++) {
					EdgeParser outgoingEdge = outGoingEdgesArray.get(j);
					NodeParser endNode= nodes.get(outgoingEdge.getHeadNode());
					ArrayList<EdgeParser> incomingEdgesArray2 = incomingEdges.get(outgoingEdge.getHeadNode());
					endNode.removeIncommingEdgeHeadNode(j,incomingEdgesArray2);
				}
				outgoingEdges.set(i,new ArrayList<>());
				nodesToRemove.add(node);
			}
			//TODO NEW
			else {
                nodesToKeep.add(node);
            }
		}



		//remove nodes itself
		//nodes.removeAll(nodesToRemove);
		this.nodes= nodesToKeep;

	}

	public Map<Long,NodeParser> getLargestConnectedComponent2() {
		Map<Long,Boolean> visited = new HashMap<>();
		int largestComponentSize = 0;
		List<NodeParser> largestComponent = new ArrayList<>();

		for(NodeParser node : nodes) {
			visited.put(node.getOsmId(), false);
		}

		for (NodeParser node : nodes) {
			if (Boolean.FALSE.equals(visited.get(node.getOsmId()))) {
				List<NodeParser> currentComponent = new ArrayList<>();
				int currentComponentSize = dfs(node, visited, currentComponent);
				if (currentComponentSize > largestComponentSize) {
					largestComponentSize = currentComponentSize;
					largestComponent = currentComponent;
				}
			}
		}

		Map<Long, NodeParser> result = new HashMap<>();
		for(NodeParser node : largestComponent) {
			result.put(node.getOsmId(), node);
		}



		return result;
	}

	private int dfs(NodeParser node, Map<Long,Boolean> visited, List<NodeParser> component) {
		visited.put(node.getOsmId(),true);
		component.add(node);
		int componentSize = 1;

		for (EdgeParser neighbor : node.getOutgoingEdges()) {
			NodeParser endNode = nodesMap.get(neighbor.getEndNodeOsmId());
			if (Boolean.FALSE.equals(visited.get(neighbor.getEndNodeOsmId()))) {
				componentSize += dfs(endNode, visited, component);
			}
		}

		return componentSize;
	}

	public void joinWays() {

		wayMap = new HashMap<>();
		for(Way way : ways) {
			if(way.isCanUse()){
				if(wayMap.containsKey(way.getName())) {
					Way wayOfMap= wayMap.get(way.getName());
					wayOfMap.addNodeids(way.getNodeids());
				}
				else wayMap.put(way.getName(), way);
			}

		}
	}


}
