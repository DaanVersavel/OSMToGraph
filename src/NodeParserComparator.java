import java.util.Comparator;

public class NodeParserComparator implements Comparator<NodeParser> {

    @Override
    public int compare(NodeParser a, NodeParser b) {
        return Double.compare(a.getCurrenCost(), b.getCurrenCost());
    }
}
