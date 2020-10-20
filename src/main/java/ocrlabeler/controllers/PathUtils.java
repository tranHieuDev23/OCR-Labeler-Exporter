package ocrlabeler.controllers;

import java.io.File;
import java.nio.file.Paths;

import io.github.cdimascio.dotenv.Dotenv;

public class PathUtils {
    private PathUtils() {
    }

    public static final String UPLOAD_DIRECTORY;
    public static final String EXPORT_DIRECTORY;
    public static final String JSON_DUMP_FILE;

    static {
        Dotenv dotenv = Dotenv.load();
        UPLOAD_DIRECTORY = dotenv.get("UPLOADED_DIRECTORY");
        EXPORT_DIRECTORY = dotenv.get("EXPORT_DIRECTORY");
        new File(EXPORT_DIRECTORY).mkdirs();
        JSON_DUMP_FILE = joinPath(EXPORT_DIRECTORY, "/dump.json");
    }

    public static String joinPath(String firstPart, String... others) {
        return Paths.get(firstPart, others).normalize().toString();
    }
}
