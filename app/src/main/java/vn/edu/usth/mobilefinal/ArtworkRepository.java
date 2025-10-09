package vn.edu.usth.mobilefinal;

import android.content.Context;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vn.edu.usth.mobilefinal.network.NetworkHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArtworkRepository {
    private final FirebaseFirestore db;
    private final Context context;
    private final Gson gson;
    private static final String TAG = "ArtworkRepo";

    public ArtworkRepository(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.gson = new GsonBuilder().create();
    }

    public interface ArtworkCallback {
        void onSuccess(List<Artwork> artworks);
        void onError(String error);
    }

    /** --- 1. Fetch API và lưu vào Firestore --- */
    public void fetchAndStoreArtworks() {
        String url = "https://api.artic.edu/api/v1/artworks?limit=50&fields=id,title,artist_display,date_display,image_id";
        NetworkHelper.getInstance(context).getArtworks(url, new NetworkHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ArtworksResponse response = gson.fromJson(result, ArtworksResponse.class);
                if (response != null && response.data != null) {
                    String iiifUrl = response.config.iiifUrl;
                    List<Artwork> artworks = new ArrayList<>();

                    for (ArtworksResponse.ArtworkData ad : response.data) {
                        String imageUrl = (ad.imageId != null && !ad.imageId.isEmpty())
                                ? iiifUrl + "/" + ad.imageId + "/full/full/0/default.jpg"
                                : "";

                        Artwork artwork = new Artwork(
                                "artwork_" + ad.id,
                                ad.title != null ? ad.title : "Untitled",
                                ad.artistDisplay != null ? ad.artistDisplay : "Unknown Artist",
                                imageUrl,
                                ad.dateDisplay != null ? ad.dateDisplay : "",
                                "",
                                "" // bỏ category
                        );
                        artworks.add(artwork);
                    }

                    // Lưu Firestore
                    for (Artwork artwork : artworks) {
                        db.collection("artworks")
                                .document(artwork.getId())
                                .set(artwork)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Stored " + artwork.getTitle()))
                                .addOnFailureListener(e -> Log.e(TAG, "Error storing", e));
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Volley error: " + error);
            }
        });
    }

    public void getPopularArtworks(ArtworkCallback callback) {
        db.collection("artworks")
                .get()
                .addOnSuccessListener(query -> {
                    List<Artwork> artworks = new ArrayList<>();
                    for (var doc : query) artworks.add(doc.toObject(Artwork.class));
                    Collections.shuffle(artworks);
                    callback.onSuccess(artworks.subList(0, Math.min(10, artworks.size())));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getDailyArtworks(ArtworkCallback callback) {
        db.collection("artworks")
                .get()
                .addOnSuccessListener(query -> {
                    List<Artwork> artworks = new ArrayList<>();
                    for (var doc : query) artworks.add(doc.toObject(Artwork.class));

                    if (artworks.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    long currentTime = System.currentTimeMillis();
                    long day = currentTime / (1000 * 60 * 60 * 24); // mỗi ngày khác nhau

                    Random random = new Random(day); // dùng seed theo ngày
                    Artwork todayArtwork = artworks.get(random.nextInt(artworks.size()));

                    List<Artwork> daily = new ArrayList<>();
                    daily.add(todayArtwork);
                    callback.onSuccess(daily);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** --- 4. Kiểm tra khởi tạo dữ liệu --- */
    public void initializeArtworkData(ArtworkCallback callback) {
        db.collection("artworks")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Log.d(TAG, "No artworks, fetching...");
                        fetchAndStoreArtworks();
                        callback.onSuccess(new ArrayList<>());
                    } else {
                        Log.d(TAG, "Artworks exist");
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void searchArtworks(String query, ArtworkCallback callback) {
        // Encode query để tránh lỗi URL khi có dấu cách
        String encodedQuery = query.replace(" ", "%20");
        String url = "https://api.artic.edu/api/v1/artworks/search?q=" + encodedQuery +
                "&fields=id,title,artist_display,date_display,image_id,artwork_type_title&limit=20";

        NetworkHelper.getInstance(context).getArtworks(url, new NetworkHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ArtworksResponse response = gson.fromJson(result, ArtworksResponse.class);
                    if (response == null || response.data == null) {
                        callback.onError("Empty response");
                        return;
                    }

                    String iiifUrl = response.config.iiifUrl;
                    List<Artwork> artworks = new ArrayList<>();

                    for (ArtworksResponse.ArtworkData ad : response.data) {
                        String imageUrl = (ad.imageId != null && !ad.imageId.isEmpty())
                                ? iiifUrl + "/" + ad.imageId + "/full/843,/0/default.jpg"
                                : "";

                        Artwork artwork = new Artwork(
                                "artwork_" + ad.id,
                                ad.title != null ? ad.title : "Untitled",
                                ad.artistDisplay != null ? ad.artistDisplay : "Unknown Artist",
                                imageUrl,
                                ad.dateDisplay != null ? ad.dateDisplay : "",
                                "",
                                "search" // category đặc biệt cho search
                        );
                        artworks.add(artwork);
                    }
                    callback.onSuccess(artworks);

                } catch (Exception e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Network error: " + error);
            }
        });
    }



}
