package ocrlabeler.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;

import ocrlabeler.models.Image;

public class Zipper {

    private Zipper() {
    }

    private static final Zipper INSTANCE = new Zipper();
    private static final Gson GSON = new Gson();

    public static final synchronized Zipper getInstance() {
        return INSTANCE;
    }

    private static final int BUFFER_SIZE = 1024;

    public void zip(Image[] images, String outputFile) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        FileOutputStream os = new FileOutputStream(Utils.joinPath(Utils.EXPORT_DIRECTORY, outputFile));
        ZipOutputStream zos = new ZipOutputStream(os);

        for (Image item : images) {
            File srcFile = new File(Utils.joinPath(Utils.UPLOAD_DIRECTORY, item.getImageUrl()));
            FileInputStream is = new FileInputStream(srcFile);
            zos.putNextEntry(new ZipEntry(srcFile.getName()));

            int length;
            while ((length = is.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            is.close();
        }

        zos.putNextEntry(new ZipEntry("metadata.json"));
        String metaJson = GSON.toJson(images);
        zos.write(metaJson.getBytes());
        zos.closeEntry();

        zos.close();
    }
}
