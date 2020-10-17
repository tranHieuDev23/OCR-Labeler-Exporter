package ocrlabeler.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThreadSafeStorage {
    public static final String RESULT_HASH_KEY = "RESULT_HASH_KEY";
    public static final String RESULT_TIMESTAMP_KEY = "RESULT_TIMESTAMP_KEY";
    public static final String RESULT_PATH_KEY = "RESULT_PATH_KEY";
    public static final String EXPORT_STATE_KEY = "EXPORT_STATE_KEY";

    private ThreadSafeStorage() {
    }

    private static final ThreadSafeStorage STORAGE = new ThreadSafeStorage();

    public static final synchronized ThreadSafeStorage getInstance() {
        return STORAGE;
    }

    private final Map<String, Object> map = Collections.synchronizedMap(new HashMap<>());

    public Object getValue(String field) {
        return map.get(field);
    }

    public void setValue(String field, Object value) {
        map.put(field, value);
    }
}
