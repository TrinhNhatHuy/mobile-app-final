package vn.edu.usth.mobilefinal.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.ArtworkRepository;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.activities.ArtWork_Details;
import vn.edu.usth.mobilefinal.activities.HomeActivity;
import vn.edu.usth.mobilefinal.adapters.ArtworkAdapter;

public class SearchFragment extends Fragment {
    private ArtworkAdapter artworkAdapter;
    private ArtworkRepository artworkRepository;
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText searchInput = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerSearchResults);

        // setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artworkAdapter = new ArtworkAdapter(new ArrayList<>(), artwork -> openArtworkDetails(artwork), R.layout.item_artwork_search);
        artworkRepository = new ArtworkRepository(getContext());

        // When user enter the searchText => perform
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            progressBar.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            } else {
                Toast.makeText(getContext(), "Nhập từ khóa để tìm kiếm", Toast.LENGTH_SHORT).show();
            }
            return true; // trả về true để ẩn bàn phím sau khi nhấn
        });
        recyclerView.setAdapter(artworkAdapter);

        return view;
    }

    private void performSearch(String query) {
        artworkAdapter.setArtworkList(new ArrayList<>());
        emptyState.setVisibility(View.GONE);

        artworkRepository.searchArtworks(query, new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                progressBar.setVisibility(View.GONE);
                if (artworks.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                }
                artworkAdapter.setArtworkList(artworks); // update UI
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi search: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openArtworkDetails(Artwork artwork) {
        Intent intent = new Intent(getActivity(), ArtWork_Details.class);
        intent.putExtra("artwork", artwork);
        startActivity(intent);
    }
}