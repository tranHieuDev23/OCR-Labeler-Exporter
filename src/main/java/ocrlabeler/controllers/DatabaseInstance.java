package ocrlabeler.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;
import ocrlabeler.models.Coordinate;
import ocrlabeler.models.Image;
import ocrlabeler.models.TextRegion;

public class DatabaseInstance {
    private Connection conn = null;

    private DatabaseInstance() {
        Dotenv dotenv = Utils.DOTENV;
        String databaseHost = dotenv.get("POSTGRES_HOST");
        String databasePort = dotenv.get("POSTGRES_PORT");
        String databaseDB = dotenv.get("POSTGRES_DB");
        String databaseUrl = createDatabaseUrl(databaseHost, databasePort, databaseDB);
        String databaseUser = dotenv.get("POSTGRES_USER");
        String databasePassword = dotenv.get("POSTGRES_PASSWORD");
        Properties props = new Properties();
        props.setProperty("user", databaseUser);
        props.setProperty("password", databasePassword);
        try {
            conn = DriverManager.getConnection(databaseUrl, props);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private String createDatabaseUrl(String databaseHost, String databasePort, String databaseDB) {
        return new StringBuilder("jdbc:postgresql://").append(databaseHost).append(':').append(databasePort).append('/')
                .append(databaseDB).toString();
    }

    private static final DatabaseInstance INSTANCE = new DatabaseInstance();

    public static final synchronized DatabaseInstance getInstance() {
        return INSTANCE;
    }

    private final String GET_IMAGES_QUERY = "SELECT \"Images\".\"imageUrl\" AS \"imageUrl\", \"TextRegions\".region AS region, \"TextRegions\".label AS LABEL\n"
            + "FROM public.\"TextRegions\"\n"
            + "LEFT JOIN public.\"Images\" ON \"Images\".\"imageId\" = \"TextRegions\".\"imageId\"\n"
            + "WHERE \"TextRegions\".status = 'Verified'" + "AND \"Images\".status = 'Published'\n"
            + "ORDER BY \"imageUrl\", \"region\", \"label\";";

    public Image[] getImagesToExport() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(GET_IMAGES_QUERY);
        if (!rs.next()) {
            return new Image[] {};
        }
        List<Image> results = new ArrayList<>();
        if (!rs.isAfterLast()) {
            String imageUrl = rs.getString("imageUrl");
            List<TextRegion> textRegions = new ArrayList<>();
            do {
                String itemImageUrl = rs.getString("imageUrl");
                if (!imageUrl.equals(itemImageUrl)) {
                    break;
                }
                String regionString = rs.getString("region");
                Coordinate[] region = parseRegion(regionString);
                String label = rs.getString("label");
                textRegions.add(new TextRegion(region, label));

            } while (rs.next());
            results.add(new Image(imageUrl, textRegions.toArray(new TextRegion[0])));
        }
        return results.toArray(new Image[0]);
    }

    private Coordinate[] parseRegion(String regionString) {
        String[] parts = regionString.split(";");
        Coordinate[] region = new Coordinate[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String[] values = parts[i].split(",");
            region[i] = new Coordinate(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
        }
        return region;
    }
}
