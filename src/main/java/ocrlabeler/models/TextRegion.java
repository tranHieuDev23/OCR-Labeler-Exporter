package ocrlabeler.models;

import java.util.Arrays;
import java.util.Objects;

public class TextRegion {
    private Coordinate[] vertices;
    private String label;

    public TextRegion(Coordinate[] vertices, String label) {
        this.vertices = vertices;
        this.label = label;
    }

    public Coordinate[] getVertices() {
        return vertices;
    }

    public String getLabel() {
        return label;
    }

    public void setVertices(Coordinate[] vertices) {
        this.vertices = vertices;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(label.hashCode(), Arrays.hashCode(this.vertices));
        return result;
    }
}
