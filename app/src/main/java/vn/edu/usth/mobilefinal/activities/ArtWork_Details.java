package vn.edu.usth.mobilefinal.activities;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton favourite_heart = findViewById(R.id.btnFavorite);
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
        favourite_heart.setOnClickListener(new View.OnClickListener() {
            private boolean isFavourite = false;
            @Override
            public void onClick(View v) {
                if(isFavourite){
                    isFavourite = false;
                    favourite_heart.setImageResource(R.drawable.ic_favorite_border);
                }else{
                    isFavourite = true;
                    favourite_heart.setImageResource(R.drawable.ic_favorite_filled);
                }
            }
        });
    }
    // update UI when click icon heart
    //flow in here : when user click the icon heart -> change image to heart filled inside

}