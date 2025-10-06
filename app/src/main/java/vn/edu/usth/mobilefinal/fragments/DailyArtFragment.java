package vn.edu.usth.mobilefinal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.ArtworkRepository;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.activities.ArtWork_Details;

public class DailyArtFragment extends Fragment {

    private ImageView ivArtwork;
    private MaterialButton btnViewDetails;
    private ArtworkRepository repository;
    private Artwork todayArtwork;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_art, container, false);

        ivArtwork = view.findViewById(R.id.ivArtwork);
        btnViewDetails = view.findViewById(R.id.btnViewDetails);

        repository = new ArtworkRepository(requireContext());

        loadDailyArtwork();

        return view;
    }

    private void loadDailyArtwork() {
        repository.getDailyArtworks(new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                if (artworks == null || artworks.isEmpty()) {
                    Toast.makeText(getContext(), "No artwork found for today", Toast.LENGTH_SHORT).show();
                    return;
                }

                todayArtwork = artworks.get(0);
                String imageUrl = todayArtwork.getImageUrl();

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.artwork_placeholder)
                            .error(R.drawable.artwork_placeholder)
                            .into(ivArtwork);
                } else {
                    ivArtwork.setImageResource(R.drawable.artwork_placeholder);
                }

                btnViewDetails.setOnClickListener(v -> openArtworkDetails());
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openArtworkDetails() {
        if (todayArtwork == null) {
            Toast.makeText(getContext(), "No artwork data available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(requireContext(), ArtWork_Details.class);
        intent.putExtra("artwork", todayArtwork);
        startActivity(intent);
    }
}
