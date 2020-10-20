package ocrlabeler.controllers;

import java.io.File;
import java.nio.file.Paths;

import io.github.cdimascio.dotenv.Dotenv;

public class Utils {
    private Utils() {
    }

    public static final Dotenv DOTENV;
    public static final String UPLOAD_DIRECTORY;
    public static final String EXPORT_DIRECTORY;
    public static final String JSON_DUMP_FILE;

    static {
        DOTENV = Dotenv.configure().directory("./.env").ignoreIfMalformed().ignoreIfMissing().load();
        UPLOAD_DIRECTORY = DOTENV.get("UPLOADED_DIRECTORY");
        EXPORT_DIRECTORY = DOTENV.get("EXPORT_DIRECTORY");
        new File(EXPORT_DIRECTORY).mkdirs();
        JSON_DUMP_FILE = joinPath(EXPORT_DIRECTORY, "/dump.json");
    }

    public static String joinPath(String firstPart, String... others) {
        return Paths.get(firstPart, others).normalize().toAbsolutePath().toString();
    }
}
