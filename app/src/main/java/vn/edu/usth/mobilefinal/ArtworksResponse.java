package vn.edu.usth.mobilefinal;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ArtworksResponse {
    @SerializedName("data")
    public List<ArtworkData> data;

    @SerializedName("config")
    public Config config;

    public static class ArtworkData {
        @SerializedName("id") public int id;
        @SerializedName("title") public String title;
        @SerializedName("artist_display") public String artistDisplay;
        @SerializedName("date_display") public String dateDisplay;
        @SerializedName("image_id") public String imageId;
    }

    public static class Config {
        @SerializedName("iiif_url") public String iiifUrl;
    }
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

//ArtworksResponse
//├─ data: List<ArtworkData>
//│   ├─ ArtworkData (id=1, title="Mona Lisa", ...)
//│   ├─ ArtworkData (id=2, title="Starry Night", ...)
//│   └─ ...
//└─ config: Config
//    └─ iiifUrl: "https://..."

//@SerializedName biến cái iiif_url ( được trả từ Json ) thành iiifurl ( phù hợp với Java vi java ko hỗ trợ biến có dấu _)