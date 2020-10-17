package ocrlabeler.models;

import java.util.Arrays;
import java.util.Objects;

public class Image {
    private String imageUrl;
    private TextRegion[] region;

    public Image(String imageUrl, TextRegion[] region) {
        this.imageUrl = imageUrl;
        this.region = region;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public TextRegion[] getRegion() {
        return region;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRegion(TextRegion[] regions) {
        this.region = regions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl.hashCode(), Arrays.hashCode(region));
    }
}
