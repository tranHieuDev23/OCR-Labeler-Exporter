package ocrlabeler.controllers;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonTool {
    private JsonTool() {
    }

    private static final JsonTool INSTANCE = new JsonTool();

    public static final synchronized JsonTool getInstance() {
        return INSTANCE;
    }

    private final Gson gson = new Gson();

    @SuppressWarnings("unchecked")
    public Map<String, String> readFromFile(String jsonFile) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(jsonFile));
            return (Map<String, String>) gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Exception happened while reading JSON file", e);
        }
    }

    public void writeFromMap(Map<String, String> map, String jsonFile) {
        JsonObject result = new JsonObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.addProperty(entry.getKey(), String.valueOf(entry.getValue()));
        }
        String resultJson = result.toString();
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(jsonFile));
            writer.write(resultJson);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception happened while reading JSON file", e);
        }
    }
}
