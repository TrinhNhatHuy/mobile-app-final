package vn.edu.usth.mobilefinal;

import android.content.Context;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vn.edu.usth.mobilefinal.network.NetworkHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArtworkRepository {
    private FirebaseFirestore db;
    private Context context;
    private Gson gson;

    public ArtworkRepository(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.gson = new GsonBuilder().create();
    }

    public interface ArtworkCallback {
        void onSuccess(List<Artwork> artworks);
        void onError(String error);
    }

    public void fetchAndStoreArtworks() {
        String url = "https://api.artic.edu/api/v1/artworks?limit=50&fields=id,title,artist_display,date_display,image_id";
        NetworkHelper.getInstance(context).getArtworks(url, new NetworkHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                //result được lưu dưới dạng String Json rồi dc gson biến thành Object đã được viết sẵn trong artworkResponse
                ArtworksResponse response = gson.fromJson(result, ArtworksResponse.class);
                if (response != null && response.data != null) {
                    String iiifUrl = response.config.iiifUrl;
                    List<Artwork> artworks = new ArrayList<>();

                    for (ArtworksResponse.ArtworkData ad : response.data) {
                        String imageUrl = (ad.imageId != null && !ad.imageId.isEmpty())
                                ? iiifUrl + "/" + ad.imageId + "/full/843,/0/default.jpg"
                                : "";

                        Random random = new Random();
                        String category = random.nextInt(2) == 0 ? "popular" : "recommended";

                        Artwork artwork = new Artwork(
                                "artwork_" + ad.id,
                                ad.title != null ? ad.title : "Untitled",
                                ad.artistDisplay != null ? ad.artistDisplay : "Unknown Artist",
                                imageUrl,
                                ad.dateDisplay != null ? ad.dateDisplay : "",
                                "",
                                category
                        );
                        artworks.add(artwork);
                    }

                    // Lưu vào Firestore
                    for (Artwork artwork : artworks) {
                        db.collection("artworks")
                                .document(artwork.getId())
                                .set(artwork)
                                .addOnSuccessListener(aVoid -> Log.d("ArtworkRepo", "Stored " + artwork.getTitle()))
                                .addOnFailureListener(e -> Log.e("ArtworkRepo", "Error storing", e));
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ArtworkRepo", "Volley error: " + error);
            }
        });
    }

    public void getPopularArtworks(ArtworkCallback callback) {
        db.collection("artworks")
                .whereEqualTo("category", "popular")
                .limit(10)
                .get()
                .addOnSuccessListener(query -> {
                    List<Artwork> artworks = new ArrayList<>();
                    for (var doc : query) artworks.add(doc.toObject(Artwork.class));
                    callback.onSuccess(artworks);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getRecommendedArtworks(ArtworkCallback callback) {
        db.collection("artworks")
                .whereEqualTo("category", "recommended")
                .limit(10)
                .get()
                .addOnSuccessListener(query -> {
                    List<Artwork> artworks = new ArrayList<>();
                    for (var doc : query) artworks.add(doc.toObject(Artwork.class));
                    callback.onSuccess(artworks);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void initializeArtworkData(ArtworkCallback callback) {
        db.collection("artworks")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Log.d("ArtworkRepo", "No artworks, fetching...");
                        fetchAndStoreArtworks();
                        callback.onSuccess(new ArrayList<>());
                    } else {
                        Log.d("ArtworkRepo", "Artworks exist");
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
