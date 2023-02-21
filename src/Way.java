import java.util.ArrayList;
import java.util.List;

public class Way {
    private long id;
    private String type="default";
    private String name;
    private boolean canUse;
    private ArrayList<Long> nodeids;

    public Way(long id, String type, ArrayList<Long> nodeids) {
        this.id = id;
        this.type = type;
        this.nodeids = nodeids;
        this.canUse = true;
    }
    public Way(long id) {
        this.id = id;
        this.nodeids = new ArrayList<>();
        this.canUse = false;
        this.name = "";
    }

    public void addNodeid(long nodeid) {
        nodeids.add(nodeid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public void setNodeids(ArrayList<Long> nodeids) {
        this.nodeids = nodeids;
    }

    public void addNodeids(List<Long> nodeids) {
        this.nodeids.addAll(nodeids);
    }
}

