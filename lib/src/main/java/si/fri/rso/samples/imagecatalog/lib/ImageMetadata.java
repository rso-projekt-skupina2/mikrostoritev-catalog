package si.fri.rso.samples.imagecatalog.lib;

import java.time.Instant;

public class ImageMetadata {

    private Integer imageId;
    private String title;
    private String description;
    private String translatedDescription;
    private String uri;
    private Integer numberOfRatings;
    private Double avergeRating;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(Integer numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public Double getAvergeRating() {
        return avergeRating;
    }

    public void setAvergeRating(Double avergeRating) {
        this.avergeRating = avergeRating;
    }

    public String getTranslatedDescription() {
        return translatedDescription;
    }

    public void setTranslatedDescription(String translatedDescription) {
        this.translatedDescription = translatedDescription;
    }
}
