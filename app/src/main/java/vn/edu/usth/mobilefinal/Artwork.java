package vn.edu.usth.mobilefinal;
public class Artwork {
    private String id;
    private String title;
    private String artist;
    private String imageUrl;
    private String date;
    private String description;
    private String category;

    public Artwork() {
        // Required empty constructor for Firestore
    }

    public Artwork(String id, String title, String artist, String imageUrl, String date, String description, String category) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.date = date;
        this.description = description;
        this.category = category;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}