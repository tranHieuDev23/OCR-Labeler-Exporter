package ocrlabeler;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ocrlabeler.controllers.DatabaseInstance;
import ocrlabeler.controllers.Zipper;
import ocrlabeler.models.Image;

@Path("export")
public class Export {
    public static class ExportRequestBody {
        public String uploadedFolder;
    }

    private static final DatabaseInstance DB = DatabaseInstance.getInstance();
    private static final Zipper ZIP = Zipper.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(ExportRequestBody body) {
        try {
            Image[] verifiedImages = DB.getImagesToExport();
            ZIP.zip(verifiedImages, body.uploadedFolder, "compressed.zip");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return "Got it!";
    }
}
