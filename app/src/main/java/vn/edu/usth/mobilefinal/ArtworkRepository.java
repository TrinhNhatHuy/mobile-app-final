package vn.edu.usth.mobilefinal;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.mobilefinal.network.ApiClient;
import vn.edu.usth.mobilefinal.network.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArtworkRepository {
    private ApiService apiService;
    private FirebaseFirestore db;

    public ArtworkRepository() {
        this.apiService = ApiClient.getApiService();
        this.db = FirebaseFirestore.getInstance();
    }

    public interface ArtworkCallback {
        void onSuccess(List<Artwork> artworks);
        void onError(String error);
    }

    public void fetchAndStoreArtworks() {
        Call<ArtworksResponse> call = apiService.getArtworks(50, "id,title,artist_display,date_display,image_id");
        call.enqueue(new Callback<ArtworksResponse>() {
            @Override
            public void onResponse(Call<ArtworksResponse> call, Response<ArtworksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArtworkData> artworkDataList = response.body().getData();
                    String iiifUrl = response.body().getConfig().getIiifUrl();
                    List<Artwork> artworks = new ArrayList<>();

                    for (ArtworkData artworkData : artworkDataList) {
                        String imageUrl = "";
                        if (artworkData.getImageId() != null && !artworkData.getImageId().isEmpty()) {
                            imageUrl = iiifUrl + "/" + artworkData.getImageId() + "/full/843,/0/default.jpg";
                        }

                        Random random = new Random();
                        String category = random.nextInt(2) == 0 ? "popular" : "recommended";

                        Artwork artwork = new Artwork(
                                "artwork_" + artworkData.getId(),
                                artworkData.getTitle() != null ? artworkData.getTitle() : "Untitled",
                                artworkData.getArtistDisplay() != null ? artworkData.getArtistDisplay() : "Unknown Artist",
                                imageUrl,
                                artworkData.getDateDisplay() != null ? artworkData.getDateDisplay() : "",
                                "",
                                category
                        );
                        artworks.add(artwork);
                    }

                    // Store in Firestore
                    for (Artwork artwork : artworks) {
                        db.collection("artworks")
                                .document(artwork.getId())
                                .set(artwork)
                                .addOnSuccessListener(aVoid -> {
                                    android.util.Log.d("ArtworkRepository", "Artwork " + artwork.getTitle() + " stored successfully");
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("ArtworkRepository", "Error storing artwork", e);
                                });
                    }
                } else {
                    android.util.Log.e("ArtworkRepository", "API call failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ArtworksResponse> call, Throwable t) {
                android.util.Log.e("ArtworkRepository", "API call failed", t);
            }
        });
    }

    public void getPopularArtworks(ArtworkCallback callback) {
        db.collection("artworks")
                .whereEqualTo("category", "popular")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Artwork> artworks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Artwork artwork = document.toObject(Artwork.class);
                        artworks.add(artwork);
                    }
                    callback.onSuccess(artworks);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    public void getRecommendedArtworks(ArtworkCallback callback) {
        db.collection("artworks")
                .whereEqualTo("category", "recommended")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Artwork> artworks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Artwork artwork = document.toObject(Artwork.class);
                        artworks.add(artwork);
                    }
                    callback.onSuccess(artworks);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }
}