package vn.edu.usth.mobilefinal.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.R;

public class ArtWork_Details extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private boolean isLiked = false; // check xem la da like hay chua
    private ImageButton btnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork_details);
        ImageView ivArtwork = findViewById(R.id.ivArtwork);
        TextView title = findViewById(R.id.tvArtworkTitle);
        TextView artist = findViewById(R.id.tvArtistName);
        TextView year = findViewById(R.id.tvYear);
        TextView description = findViewById(R.id.tvDescription);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);

        // Lay databse tu Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu từ Intent
        Artwork artwork = (Artwork) getIntent().getSerializableExtra("artwork");

        if (artwork != null) {
            title.setText(artwork.getTitle());
            artist.setText(artwork.getArtist());
            year.setText(artwork.getDate());
            description.setText(artwork.getDescription());
            Glide.with(this)
                    .load(artwork.getImageUrl())
                    .placeholder(R.drawable.artwork_placeholder)
                    .error(R.drawable.artwork_placeholder)
                    .into(ivArtwork);
        }

        btnBack.setOnClickListener(v -> finish());

        // Check xem artwork da duoc like hay chua
        if (currentUser != null && artwork != null) {
            checkIfLiked(currentUser.getUid(), artwork.getId());
        } else {
            // Truong hop user chua log in
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }

        // Check xem user da dang nhap/dang ky chua -> chua thi ai cho ma like
        btnFavorite.setOnClickListener(v -> {
            if (currentUser == null || artwork == null) {
                Toast.makeText(this, "Please sign in to like artwork", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = currentUser.getUid(); // Lay id cua user
            String artworkId = artwork.getId();
            // Tao pointer(path) trong database favorite cua user
            DocumentReference docRef = db.collection("favorites")
                    .document(userId)
                    .collection("artwork")
                    .document(artworkId);

            if (isLiked) {
                // Case da like roi
                docRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            isLiked = false;
                            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                // Case chua like
                Map<String, Object> artworkData = new HashMap<>();
                artworkData.put("artist", artwork.getArtist());
                artworkData.put("category", artwork.getCategory());
                artworkData.put("date", artwork.getDate());
                artworkData.put("description", artwork.getDescription());
                artworkData.put("id", artwork.getId());
                artworkData.put("imageUrl", artwork.getImageUrl());
                artworkData.put("title", artwork.getTitle());

                docRef.set(artworkData)
                        .addOnSuccessListener(aVoid -> {
                            isLiked = true;
                            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
                            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });



    }

    // Check xem artwork da like hay chua
    private void checkIfLiked(String userId, String artworkId) {
        db.collection("favorites")
                .document(userId)
                .collection("artwork")
                .document(artworkId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        isLiked = true;
                        btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
                    } else {
                        isLiked = false;
                        btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking favorite status", Toast.LENGTH_SHORT).show());
    }
}






























