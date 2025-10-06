package vn.edu.usth.mobilefinal.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.edu.usth.mobilefinal.ArtworksResponse;

public interface ApiService {
    @GET("artworks")
    Call<ArtworksResponse> getArtworks(
            @Query("limit") int limit,
            @Query("fields") String fields
    );

    @GET("artworks/search")
    Call<ArtworksResponse> searchArtworks(
            @Query("q") String query,
            @Query("fields") String fields,
            @Query("limit") int limit
    );
}