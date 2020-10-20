package ocrlabeler.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThreadSafeStorage {
    public static final String RESULT_HASH_KEY = "RESULT_HASH_KEY";
    public static final String RESULT_TIMESTAMP_KEY = "RESULT_TIMESTAMP_KEY";
    public static final String RESULT_PATH_KEY = "RESULT_PATH_KEY";
    public static final String EXPORT_STATE_KEY = "EXPORT_STATE_KEY";

    private static final JsonTool JSON_TOOL = JsonTool.getInstance();
    private Map<String, String> map;

    private ThreadSafeStorage() {
        try {
            map = Collections.synchronizedMap(JSON_TOOL.readFromFile(Utils.JSON_DUMP_FILE));
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
        try {
            JSON_TOOL.writeFromMap(map, Utils.JSON_DUMP_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
