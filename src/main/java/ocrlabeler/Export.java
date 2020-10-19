package ocrlabeler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ocrlabeler.controllers.DatabaseInstance;
import ocrlabeler.controllers.ThreadSafeStorage;
import ocrlabeler.controllers.Zipper;
import ocrlabeler.models.ExportState;
import ocrlabeler.models.Image;

@Path("export")
public class Export {
    public static class ExportRequestBody {
        public String uploadedFolder;
    }

    private static final DatabaseInstance DB = DatabaseInstance.getInstance();
    private static final Zipper ZIP = Zipper.getInstance();
    private static final ThreadSafeStorage STORAGE = ThreadSafeStorage.getInstance();

    private synchronized boolean checkReadyState() {
        if (ExportState.EXPORTING.toString().equals(STORAGE.getValue(ThreadSafeStorage.EXPORT_STATE_KEY))) {
            return false;
        }
        STORAGE.setValue(ThreadSafeStorage.EXPORT_STATE_KEY, ExportState.EXPORTING.toString());
        return true;
    }

    private synchronized void setStateReady() {
        STORAGE.setValue(ThreadSafeStorage.EXPORT_STATE_KEY, ExportState.READY.toString());
    }

    private String getOutputFileName(long resultHash, long timestamp) {
        return new StringBuilder("compressed-").append(resultHash).append('-').append(timestamp).append(".zip")
                .toString();
    }

    private boolean isDuplicateHash(long resultHash) {
        String oldHashStr = STORAGE.getValue(ThreadSafeStorage.RESULT_HASH_KEY);
        if (oldHashStr == null) {
            return false;
        }
        return Long.parseLong(oldHashStr) == resultHash;
    }

    private void deleteOldFile() {
        String oldFilePath = (String) STORAGE.getValue(ThreadSafeStorage.RESULT_PATH_KEY);
        if (oldFilePath == null) {
            return;
        }
        new File(oldFilePath).delete();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String handle(ExportRequestBody body) {
        long timestamp = new Date().getTime();
        if (!checkReadyState()) {
            return "Error";
        }
        try {
            Image[] verifiedImages = DB.getImagesToExport();
            long resultHash = Arrays.hashCode(verifiedImages);
            if (isDuplicateHash(resultHash)) {
                STORAGE.setValue(ThreadSafeStorage.RESULT_TIMESTAMP_KEY, String.valueOf(timestamp));
                setStateReady();
                return "Error";
            }
            String outputFileName = getOutputFileName(resultHash, timestamp);
            ZIP.zip(verifiedImages, body.uploadedFolder, outputFileName);
            deleteOldFile();
            STORAGE.setValue(ThreadSafeStorage.RESULT_HASH_KEY, String.valueOf(resultHash));
            STORAGE.setValue(ThreadSafeStorage.RESULT_TIMESTAMP_KEY, String.valueOf(timestamp));
            STORAGE.setValue(ThreadSafeStorage.RESULT_PATH_KEY, outputFileName);
            setStateReady();
            STORAGE.dump();
            return "Got it!";
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
