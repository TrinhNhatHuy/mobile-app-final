package vn.edu.usth.mobilefinal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.ArrayList;

import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.adapters.ArtworkAdapter;
import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.activities.ArtWork_Details;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerFavorites;
    private ArtworkAdapter adapter;
    private List<Artwork> favoriteList = new ArrayList<>();

    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private TextView tvFavoritesCount;
    private MaterialButton btnClearFavorites;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerFavorites = view.findViewById(R.id.recyclerFavorites);
        emptyState = view.findViewById(R.id.emptyState);
        progressBar = view.findViewById(R.id.progressBar);
        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount);
        btnClearFavorites = view.findViewById(R.id.btnClearFavorites);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArtworkAdapter(
                favoriteList,
                artwork -> {
                    // When user clicks on an artwork
                    Intent intent = new Intent(getContext(), ArtWork_Details.class);
                    intent.putExtra("artwork", artwork);
                    startActivity(intent);
                },
                R.layout.item_artwork_search // Re-use artwork search chip
        );
        recyclerFavorites.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadFavorites();

        btnClearFavorites.setOnClickListener(v -> clearAllFavorites());

        return view;
    }

    // Load data vao recycle view
    private void loadFavorites() {
        if (currentUser == null) return;
        progressBar.setVisibility(View.VISIBLE);

        db.collection("favorites")
                .document(currentUser.getUid())
                .collection("artwork")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    favoriteList.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Artwork artwork = doc.toObject(Artwork.class);
                        if (artwork != null) {
                            favoriteList.add(artwork);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    updateUI();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI() {
        if (favoriteList.isEmpty()) {
            recyclerFavorites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            tvFavoritesCount.setText("0 artworks saved");
        } else {
            recyclerFavorites.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            tvFavoritesCount.setText(favoriteList.size() + " artworks saved");
        }
    }

    private void clearAllFavorites() {
        if (currentUser == null) return;

        db.collection("favorites")
                .document(currentUser.getUid())
                .collection("artwork")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit().addOnSuccessListener(aVoid -> {
                        favoriteList.clear();
                        adapter.notifyDataSetChanged();
                        updateUI();
                        Toast.makeText(getContext(), "All favorites cleared!", Toast.LENGTH_SHORT).show();
                    });
                });
    }
}
























