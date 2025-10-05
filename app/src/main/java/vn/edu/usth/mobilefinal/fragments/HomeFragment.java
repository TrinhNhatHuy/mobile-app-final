package vn.edu.usth.mobilefinal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.ArtworkRepository;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.adapters.ArtworkAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerPopular;
    private ArtworkAdapter popularAdapter;
    private ArtworkRepository artworkRepository;
    private TextView tvUserName;
    private ImageButton logoutButton;
    private FirebaseAuth mAuth;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artworkRepository = new ArtworkRepository();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupRecyclerView();
        loadUserData();
        initializeArtworkData();
        setupLogoutButton();

        return view;
    }

    private void initializeViews(View view) {
        recyclerPopular = view.findViewById(R.id.recyclerPopular);
        tvUserName = view.findViewById(R.id.tvUserName);
        logoutButton = view.findViewById(R.id.logout);
    }

    private void setupRecyclerView() {
        // Setup Popular Artworks RecyclerView
        recyclerPopular.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        // Create adapter with both parameters
        popularAdapter = new ArtworkAdapter(
                new ArrayList<>(),
                new ArtworkAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Artwork artwork) {
                        // Handle item click here
                        Toast.makeText(getContext(), "Clicked: " + artwork.getTitle(), Toast.LENGTH_SHORT).show();
                        // You can navigate to detail activity/fragment here
                    }
                }
        );
        recyclerPopular.setAdapter(popularAdapter);
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                tvUserName.setText(displayName);
            } else if (email != null) {
                // Use email username part as display name
                String username = email.split("@")[0];
                tvUserName.setText(username);
            }
        }
    }

    private void initializeArtworkData() {
        // Check if we have data and load popular artworks
        artworkRepository.initializeArtworkData(new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                // Data initialization complete, now load popular artworks
                loadPopularArtworks();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error initializing data: " + error, Toast.LENGTH_SHORT).show();
                // Still try to load existing data
                loadPopularArtworks();
            }
        });
    }

    private void loadPopularArtworks() {
        artworkRepository.getPopularArtworks(new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                if (artworks.isEmpty()) {
                    // If no popular artworks found, try loading recommended ones as fallback
                    loadRecommendedArtworksAsFallback();
                } else {
                    // FIXED: Use setArtworkList instead of updateData
                    popularAdapter.setArtworkList(artworks);

                    // Show success message
                    Toast.makeText(getContext(), "Loaded " + artworks.size() + " popular artworks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading popular artworks: " + error, Toast.LENGTH_SHORT).show();
                // Try loading recommended as fallback
                loadRecommendedArtworksAsFallback();
            }
        });
    }

    private void loadRecommendedArtworksAsFallback() {
        artworkRepository.getRecommendedArtworks(new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                if (!artworks.isEmpty()) {
                    // FIXED: Use setArtworkList instead of updateData
                    popularAdapter.setArtworkList(artworks);
                    Toast.makeText(getContext(), "Loaded " + artworks.size() + " recommended artworks", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No artworks found. Pull to refresh.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading artworks: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupLogoutButton() {
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            // Navigate back to login activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    // Method to refresh data (can be called on pull-to-refresh)
    public void refreshData() {
        artworkRepository.fetchAndStoreArtworks();

        // Reload data after a short delay to allow for storage
        recyclerPopular.postDelayed(this::loadPopularArtworks, 2000);
    }
}