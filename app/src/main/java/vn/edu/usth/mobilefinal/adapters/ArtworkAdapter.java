package vn.edu.usth.mobilefinal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.R;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ArtworkViewHolder> {
    private List<Artwork> artworks; // This is the field name used everywhere
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Artwork artwork);
    }

    public ArtworkAdapter(List<Artwork> artworks, OnItemClickListener onItemClickListener) {
        this.artworks = artworks;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ArtworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artwork_main, parent, false);
        return new ArtworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtworkViewHolder holder, int position) {
        Artwork artwork = artworks.get(position);
        holder.bind(artwork, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return artworks.size();
    }

    // Remove the duplicate method and keep only one
    public void setArtworkList(List<Artwork> artworkList) {
        this.artworks = artworkList; // Fixed: changed 'artworkList' to 'artworks'
        notifyDataSetChanged();
    }

    static class ArtworkViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivArtwork;
        private TextView tvArtworkTitle;
        private TextView tvArtistName;

        public ArtworkViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArtwork = itemView.findViewById(R.id.ivArtwork);
            tvArtworkTitle = itemView.findViewById(R.id.tvArtworkTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
        }

        public void bind(Artwork artwork, OnItemClickListener listener) {
            tvArtworkTitle.setText(artwork.getTitle());
            tvArtistName.setText(artwork.getArtist());

            // Load image with Glide
            if (artwork.getImageUrl() != null && !artwork.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(artwork.getImageUrl())
                        .placeholder(R.drawable.artwork_placeholder)
                        .error(R.drawable.artwork_placeholder)
                        .into(ivArtwork);
            } else {
                ivArtwork.setImageResource(R.drawable.artwork_placeholder);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(artwork);
                }
            });
        }
    }
}