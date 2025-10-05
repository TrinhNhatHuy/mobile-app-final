package vn.edu.usth.mobilefinal;
public class Artwork implements java.io.Serializable{
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

    public String getDescription() {
        if (description != null && !description.trim().isEmpty()) return description.trim();

        StringBuilder s = new StringBuilder();
        if (title != null && !title.isEmpty()) s.append(title);
        if (artist != null && !artist.isEmpty()) {
            if (s.length() > 0) s.append(" by ");
            s.append(artist);
        }
        if (date != null && !date.isEmpty()) s.append(" (").append(date).append(")");

        String out = s.toString().trim();
        return out.isEmpty() ? "Details unavailable." : out;
    }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}