package vn.edu.usth.mobilefinal;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import vn.edu.usth.mobilefinal.network.NetworkHelper;

public class ArtworkRepository {
    private static final String TAG = "ArtworkRepo";
    private static final String API_URL = "https://api.artic.edu/api/v1/artworks?limit=100&fields=id,title,artist_display,date_display,image_id";

    private final Context context;
    private final Gson gson;

    public ArtworkRepository(Context context) {
        this.context = context;
        this.gson = new GsonBuilder().create();
    }

    public interface ArtworkCallback {
        void onSuccess(List<Artwork> artworks);
        void onError(String error);
    }

    public void getPopularArtworks(ArtworkCallback callback) {
        NetworkHelper.getInstance(context).getArtworks(API_URL, new NetworkHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ArtworksResponse response = gson.fromJson(result, ArtworksResponse.class);
                    List<Artwork> artworks = mapResponseToArtworks(response); // helper chung

                    if (artworks.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    Collections.shuffle(artworks);
                    int n = Math.min(10, artworks.size());
                    callback.onSuccess(new ArrayList<>(artworks.subList(0, n)));
                } catch (Exception e) {
                    Log.e(TAG, "Parse error", e);
                    callback.onError("Parse error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Volley error: " + error);
                callback.onError(error);
            }
        });
    }

    public void getDailyArtworks(ArtworkCallback callback) {
        NetworkHelper.getInstance(context).getArtworks(API_URL, new NetworkHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ArtworksResponse response = gson.fromJson(result, ArtworksResponse.class);
                    List<Artwork> artworks = mapResponseToArtworks(response); // helper chung

                    if (artworks.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    long daySeed = System.currentTimeMillis() / (1000L * 60L * 60L * 24L);
                    Random random = new Random(daySeed);
                    Artwork today = artworks.get(random.nextInt(artworks.size()));

                    List<Artwork> out = new ArrayList<>();
                    out.add(today);
                    callback.onSuccess(out);
                } catch (Exception e) {
                    Log.e(TAG, "Parse error", e);
                    callback.onError("Parse error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Volley error: " + error);
                callback.onError(error);
            }
        });
    }

    public void searchArtworks(String query, ArtworkCallback callback) {
        String encodedQuery = query.replace(" ", "%20");
        String url = "https://api.artic.edu/api/v1/artworks/search?q=" + encodedQuery +
                "&fields=id,title,artist_display,date_display,image_id,artwork_type_title&limit=20";

        NetworkHelper.getInstance(context).cancelPendingRequests("search");
        NetworkHelper.getInstance(context).getArtworks(url, new NetworkHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    ArtworksResponse response = gson.fromJson(result, ArtworksResponse.class);
                    if (response == null || response.data == null) {
                        callback.onError("Empty response");
                        return;
                    }
                    // Dùng chung helper và gắn category "search"
                    List<Artwork> artworks = mapResponseToArtworks(response);
                    callback.onSuccess(artworks);
                } catch (Exception e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Network error: " + error);
            }
        }).setTag("search");
    }

    private List<Artwork> mapResponseToArtworks(ArtworksResponse response) {
        List<Artwork> artworks = new ArrayList<>();
        if (response == null || response.data == null) return artworks;
        String iiifUrl = "https://www.artic.edu/iiif/2";
        try {
            if (response.config != null &&
                    response.config.iiifUrl != null &&
                    !response.config.iiifUrl.isEmpty()) {
                iiifUrl = response.config.iiifUrl;
            }
        } catch (Exception ignored) {}

        for (ArtworksResponse.ArtworkData ad : response.data) {
            String imageUrl = (ad.imageId != null && !ad.imageId.isEmpty())
                    // dùng biến thể width 843 để tối ưu băng thông, đồng nhất với search
                    ? iiifUrl + "/" + ad.imageId + "/full/!843,843/0/default.jpg"
                    : "";

            artworks.add(new Artwork(
                    "artwork_" + ad.id,
                    ad.title != null ? ad.title : "Untitled",
                    ad.artistDisplay != null ? ad.artistDisplay : "Unknown Artist",
                    imageUrl,
                    ad.dateDisplay != null ? ad.dateDisplay : "",
                    "",
                    ad.category == null ? "" : ad.category
            ));
        }
        return artworks;
    }
}
