package vn.edu.usth.mobilefinal;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ArtworksResponse {
    @SerializedName("data")
    private List<ArtworkData> data;

    @SerializedName("config")
    private Config config;

    public List<ArtworkData> getData() { return data; }
    public void setData(List<ArtworkData> data) { this.data = data; }

    public Config getConfig() { return config; }
    public void setConfig(Config config) { this.config = config; }
}

class ArtworkData {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("artist_display")
    private String artistDisplay;

    @SerializedName("date_display")
    private String dateDisplay;

    @SerializedName("image_id")
    private String imageId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtistDisplay() { return artistDisplay; }
    public void setArtistDisplay(String artistDisplay) { this.artistDisplay = artistDisplay; }

    public String getDateDisplay() { return dateDisplay; }
    public void setDateDisplay(String dateDisplay) { this.dateDisplay = dateDisplay; }

    public String getImageId() { return imageId; }
    public void setImageId(String imageId) { this.imageId = imageId; }
}

class Config {
    @SerializedName("iiif_url")
    private String iiifUrl;

    public String getIiifUrl() { return iiifUrl; }
    public void setIiifUrl(String iiifUrl) { this.iiifUrl = iiifUrl; }
}
