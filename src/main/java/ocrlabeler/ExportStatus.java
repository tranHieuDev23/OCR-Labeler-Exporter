package ocrlabeler;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ocrlabeler.controllers.ThreadSafeStorage;
import ocrlabeler.models.ExportState;

@Path("export-status")
public class ExportStatus {
    public static class ExportStatusResponseBody {
        public String state;
        public long timestamp;
        public boolean exported;
    }

    private static final ThreadSafeStorage STORAGE = ThreadSafeStorage.getInstance();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ExportStatusResponseBody handle() {
        ExportStatusResponseBody resp = new ExportStatusResponseBody();
        resp.state = STORAGE.getValueOrDefault(ThreadSafeStorage.EXPORT_STATE_KEY, ExportState.READY.toString());
        resp.timestamp = Long.parseLong(
                STORAGE.getValueOrDefault(ThreadSafeStorage.RESULT_TIMESTAMP_KEY, String.valueOf(Long.MIN_VALUE)));
        resp.exported = (null != STORAGE.getValue(ThreadSafeStorage.RESULT_PATH_KEY));
        return resp;
    }
}
