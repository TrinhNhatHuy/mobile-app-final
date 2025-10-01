package vn.edu.usth.mobilefinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ImageButton button;
    TextView textView;
    FirebaseUser user;

    // Artwork functionality variables
    private RecyclerView recyclerPopular;
    private ArtworkRepository artworkRepository;
    private ArtworkAdapter popularAdapter;
    private List<Artwork> popularArtworks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Auth initialization
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.tvUserName);
        user = auth.getCurrentUser();

        // Check if user is logged in
        if(user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return; // Exit early if user is not logged in
        }
        else {
            // Set user email or display name
            textView.setText(user.getEmail());
        }

        // Logout button listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize artwork functionality AFTER auth check
        initializeArtworkViews();
        setupRecyclerView();
        loadData();

        // Fetch and store artworks from API
        fetchArtworksFromApi();
    }

    private void initializeArtworkViews() {
        recyclerPopular = findViewById(R.id.recyclerPopular);
        artworkRepository = new ArtworkRepository();
    }

    private void setupRecyclerView() {
        popularAdapter = new ArtworkAdapter(popularArtworks, new ArtworkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Artwork artwork) {
                showArtworkDetails(artwork);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerPopular.setLayoutManager(layoutManager);
        recyclerPopular.setAdapter(popularAdapter);
    }

    private void loadData() {
        Log.d("MainActivity", "Loading artwork data from Firestore for user: " + user.getEmail());

        artworkRepository.getPopularArtworks(new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                Log.d("MainActivity", "Successfully loaded " + artworks.size() + " artworks");

                // Debug: Print artwork details
                for (Artwork artwork : artworks) {
                    Log.d("MainActivity", "Artwork: " + artwork.getTitle() +
                            ", Image URL: " + artwork.getImageUrl());
                }

                popularArtworks.clear();
                popularArtworks.addAll(artworks);
                popularAdapter.updateData(popularArtworks);

                // Show success message if artworks are loaded
                if (!artworks.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Loaded " + artworks.size() + " artworks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("MainActivity", "Error loading artworks: " + error);
                Toast.makeText(MainActivity.this, "Error loading artworks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchArtworksFromApi() {
        // You might want to add logic to check if data needs refreshing
        // For now, we'll fetch new data every time the app starts
        Log.d("MainActivity", "Fetching artworks from API...");
        artworkRepository.fetchAndStoreArtworks();

        // Note: The repository will automatically trigger data reload after storing
    }

    private void showArtworkDetails(Artwork artwork) {
        // Navigate to detail screen or show dialog
        Toast.makeText(this, "Clicked: " + artwork.getTitle() + " by " + artwork.getArtist(), Toast.LENGTH_SHORT).show();

        // Example: Start detail activity (you can implement this later)
        /*
        Intent intent = new Intent(this, ArtworkDetailActivity.class);
        intent.putExtra("artwork", artwork);
        startActivity(intent);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optional: Refresh data when returning to the activity
        // loadData();
    }
}