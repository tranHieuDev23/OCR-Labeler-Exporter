package ocrlabeler.controllers;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

public class ThreadSafeStorage {
    public static final String RESULT_HASH_KEY = "RESULT_HASH_KEY";
    public static final String RESULT_TIMESTAMP_KEY = "RESULT_TIMESTAMP_KEY";
    public static final String RESULT_PATH_KEY = "RESULT_PATH_KEY";
    public static final String EXPORT_STATE_KEY = "EXPORT_STATE_KEY";

    public final String jsonDumpFile;

    private static final JsonTool JSON_TOOL = JsonTool.getInstance();
    private Map<String, String> map;

    private ThreadSafeStorage() {
        Dotenv dotenv = Dotenv.load();
        String exportDirectory = dotenv.get("EXPORT_DIRECTORY");
        jsonDumpFile = Paths.get(exportDirectory, "dump.json").toString();
        try {
            map = Collections.synchronizedMap(JSON_TOOL.readFromFile(jsonDumpFile));
        } catch (Exception e) {
            map = Collections.synchronizedMap(new HashMap<>());
        }
    }

    private static final ThreadSafeStorage INSTANCE = new ThreadSafeStorage();

    public static final synchronized ThreadSafeStorage getInstance() {
        return INSTANCE;
    }

    public String getValue(String field) {
        return map.get(field);
    }

    public String getValueOrDefault(String field, String defaultValue) {
        return map.getOrDefault(field, defaultValue);
    }

    public void setValue(String field, String value) {
        map.put(field, value);
    }

    public void dump() {
        JSON_TOOL.writeFromMap(map, jsonDumpFile);
    }
}
