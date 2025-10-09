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
    private static final String API_URL =
            "https://api.artic.edu/api/v1/artworks?limit=1000&fields=id,title,artist_display,date_display,image_id";

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

    /** Popular: gọi trực tiếp API, xáo trộn và chọn 10 item */
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

    /** Daily: gọi trực tiếp API, chọn 1 item theo seed theo ngày (ổn định trong ngày) */
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

    /** Search: dùng chung helper, set category = "search" */
    public void searchArtworks(String query, ArtworkCallback callback) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
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
                    // Dùng chung helper và gắn category "search"
                    List<Artwork> artworks = mapResponseToArtworks(response, "search");
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

    /* ============================
       Helper chung: JSON -> List<Artwork>
       ============================ */

    /** Mặc định category rỗng (cho popular/daily) */
    private List<Artwork> mapResponseToArtworks(ArtworksResponse response) {
        return mapResponseToArtworks(response, "");
    }

    /** Cho phép truyền category (vd: "search") */
    private List<Artwork> mapResponseToArtworks(ArtworksResponse response, String category) {
        List<Artwork> artworks = new ArrayList<>();
        if (response == null || response.data == null) return artworks;

        // Fallback nếu thiếu config.iiifUrl
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
                    ? iiifUrl + "/" + ad.imageId + "/full/843,/0/default.jpg"
                    : "";

            artworks.add(new Artwork(
                    "artwork_" + ad.id,
                    ad.title != null ? ad.title : "Untitled",
                    ad.artistDisplay != null ? ad.artistDisplay : "Unknown Artist",
                    imageUrl,
                    ad.dateDisplay != null ? ad.dateDisplay : "",
                    "",
                    (category == null ? "" : category)
            ));
        }
        return artworks;
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

    public void filterArtworks(String type, ArtworkCallback callback) {
        String url = "https://api.artic.edu/api/v1/artworks?limit=50&fields=id,title,artist_display,date_display,image_id,artwork_type_title";
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
                        // Lọc theo category
                        if (ad.category == null || !ad.category.equalsIgnoreCase(type)) {
                            continue; // bỏ qua nếu k khớp loại
                        }

                        // Tạo image URL
                        String imageUrl = (ad.imageId != null && !ad.imageId.isEmpty())
                                ? iiifUrl + "/" + ad.imageId + "/full/full/0/default.jpg"
                                : "";

                        // Tạo đối tượng Artwork
                        Artwork artwork = new Artwork(
                                "artwork_" + ad.id,
                                ad.title != null ? ad.title : "Untitled",
                                ad.artistDisplay != null ? ad.artistDisplay : "Unknown Artist",
                                imageUrl,
                                ad.dateDisplay != null ? ad.dateDisplay : "",
                                "",
                                ad.category != null ? ad.category : ""
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
