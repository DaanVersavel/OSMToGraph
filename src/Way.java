import java.util.ArrayList;

public class Way {
    private long id;
    private String type;
    private ArrayList<Long> nodeids;

    public Way(long id, String type, ArrayList<Long> nodeids) {
        this.id = id;
        this.type = type;
        this.nodeids = nodeids;
    }
    public Way(long id) {
        this.id = id;
        this.nodeids = new ArrayList<>();
    }

    public void addNodeid(long nodeid) {
        nodeids.add(nodeid);
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Long> getNodeids() {
        return nodeids;
    }

    public void setNodeids(ArrayList<Long> nodeids) {
        this.nodeids = nodeids;
    }
}

