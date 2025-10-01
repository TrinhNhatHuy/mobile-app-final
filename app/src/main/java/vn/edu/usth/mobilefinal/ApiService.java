package vn.edu.usth.mobilefinal;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("artworks")
    Call<ArtworksResponse> getArtworks(
            @Query("limit") int limit,
            @Query("fields") String fields
    );
}