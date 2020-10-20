package ocrlabeler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import ocrlabeler.controllers.Utils;
import ocrlabeler.controllers.ThreadSafeStorage;

@Path("download")
public class Dowload {
    private static final ThreadSafeStorage STORAGE = ThreadSafeStorage.getInstance();

    @POST
    public Response handle() {
        String filePath = (String) STORAGE.getValue(ThreadSafeStorage.RESULT_PATH_KEY);
        StreamingOutput os = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    String path = Utils.joinPath(Utils.EXPORT_DIRECTORY, filePath);
                    byte[] data = Files.readAllBytes(Paths.get(path));
                    output.write(data);
                    output.flush();
                } catch (Exception e) {
                    throw new WebApplicationException("File Not Found !!");
                }
            }
        };
        return Response.ok(os, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = dataset.zip").build();
    }
}
