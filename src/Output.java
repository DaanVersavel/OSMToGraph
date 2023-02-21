import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Output {

    private Map<Long,NodeParser> nodes = new HashMap<>();
    private String fileName;
    private JSONObject jo;

    Output(Map<Long,NodeParser> nodes) {
        this.nodes = nodes;
        this.jo = new JSONObject();

        //NODES
//        JSONArray ja = new JSONArray();
        for (Map.Entry<Long, NodeParser> entry : this.nodes.entrySet()) {
            if(!entry.getValue().getDissabled()){
                // create a new JSON object for this node
                JSONObject nodeJson = new JSONObject();

                // add the node's attributes to the JSON object
                nodeJson.put("osmId", entry.getValue().getOsmId());
                nodeJson.put("latitude", entry.getValue().getLatitude());
                nodeJson.put("longitude", entry.getValue().getLongitude());
                //nodeJson.put("currentCost", entry.getValue().getCurrentCost());
                //nodeJson.put("disabled", entry.getValue().getDissabled());
                //nodeJson.put("types", new JSONArray(entry.getValue().getTypes()));

                // add the node's outgoing edges to the JSON object
                JSONArray edgesJson = new JSONArray();
                for (EdgeParser edge : entry.getValue().getOutgoingEdges()) {
                    JSONObject edgeJson = new JSONObject();
                    edgeJson.put("length", edge.getLength());
                    edgeJson.put("beginNodeOsmId", edge.getBeginNodeOsmId());
                    edgeJson.put("endNodeOsmId", edge.getEndNodeOsmId());
                    edgeJson.put("edgeType", edge.getEdgeType());
                    // add any other attributes for the edge
                    edgesJson.add(edgeJson);
                }
                nodeJson.put("outgoingEdges", edgesJson);

                // add the node's JSON object to the root JSON object, using the node's ID as the key
                this.jo.put(entry.getValue().getOsmId(),nodeJson);
            }
        }
//        this.jo.put("nodes", ja);


    }


    public void writeToFile(String fileName) throws IOException {
        PrintWriter pw = new PrintWriter(fileName + ".json");
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
}
