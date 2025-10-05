package vn.edu.usth.mobilefinal.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.R;

public class ArtWork_Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork_details);
        ImageView ivArtwork = findViewById(R.id.ivArtwork);
        TextView title = findViewById(R.id.tvArtworkTitle);
        TextView artist = findViewById(R.id.tvArtistName);
        TextView year = findViewById(R.id.tvYear);
        TextView description = findViewById(R.id.tvDescription);
        Artwork artwork = (Artwork) getIntent().getSerializableExtra("artwork");
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
}