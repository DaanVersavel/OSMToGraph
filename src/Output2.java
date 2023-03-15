import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class Output2 {
    private Map<Long,NodeParser> nodes;


    public Output2(Map<Long,NodeParser> nodes){
        this.nodes = nodes;
    }

    public void writeToFile(String path){
        try (Writer writer = new FileWriter(path+".json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(nodes, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
