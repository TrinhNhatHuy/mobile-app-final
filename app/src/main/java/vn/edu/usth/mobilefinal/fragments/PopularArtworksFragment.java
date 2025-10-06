package vn.edu.usth.mobilefinal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.ArtworkRepository;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.activities.ArtWork_Details;
import vn.edu.usth.mobilefinal.adapters.ArtworkAdapter;

public class PopularArtworksFragment extends Fragment {
    private RecyclerView recyclerPopular;
    private ArtworkAdapter popularAdapter;
    private ArtworkRepository artworkRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_artworks, container, false);
        recyclerPopular = view.findViewById(R.id.recyclerPopular);

        artworkRepository = new ArtworkRepository(getContext());
        setupRecyclerView();
        loadPopularArtworks();

        return view;
    }

    private void setupRecyclerView() {
        recyclerPopular.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        popularAdapter = new ArtworkAdapter(new ArrayList<>(), this::openArtworkDetails);
        recyclerPopular.setAdapter(popularAdapter);

        recyclerPopular.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                rv.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }


    private void loadPopularArtworks() {
        artworkRepository.getPopularArtworks(new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                popularAdapter.setArtworkList(artworks);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openArtworkDetails(Artwork artwork) {
        Intent intent = new Intent(getActivity(), ArtWork_Details.class);
        intent.putExtra("artwork", artwork);
        startActivity(intent);
    }
}
