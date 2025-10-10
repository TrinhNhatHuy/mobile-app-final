package vn.edu.usth.mobilefinal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.ArtworkRepository;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.activities.ArtWork_Details;
import vn.edu.usth.mobilefinal.adapters.ArtworkAdapter;

public class CollectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArtworkAdapter artworkAdapter;
    private ArtworkRepository artworkRepository;
    private MaterialButton btnLoadMore;
    private List<Artwork> allArtworks = new ArrayList<>();
    private int currentPage = 1;
    private Chip chipPhotograph, chipPainting, chipVessel, chipPrint;
    private String currentType = null;
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewExplore);
        btnLoadMore = view.findViewById(R.id.btnLoadMore);
        chipPhotograph = view.findViewById(R.id.chipPhotograph);
        chipPainting = view.findViewById(R.id.chipPainting);
        chipVessel = view.findViewById(R.id.chipVessel);
        chipPrint = view.findViewById(R.id.chipPrint);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);

        // setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artworkAdapter = new ArtworkAdapter(new ArrayList<>(), artwork -> openArtworkDetails(artwork), R.layout.item_artwork_search);
        recyclerView.setAdapter(artworkAdapter);

        artworkRepository = new ArtworkRepository(getContext());

        // Gắn chip click
        chipPhotograph.setOnClickListener(v -> selectCategory("Photograph"));
        chipPainting.setOnClickListener(v -> selectCategory("Painting"));
        chipPrint.setOnClickListener(v -> selectCategory("Print"));
        chipVessel.setOnClickListener(v -> selectCategory("Vessel"));

        btnLoadMore.setOnClickListener(v -> {
            if (currentType != null) {
                currentPage++;
                loadArtworks(currentType, currentPage);
            } else {
                Toast.makeText(getContext(), "Please choose the type", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void selectCategory(String type) {
        currentPage = 1;
        currentType = type;
        allArtworks.clear();
        artworkAdapter.setArtworkList(new ArrayList<>());

        highlightSelectedChip(type);

        loadArtworks(currentType, currentPage);
    }

    // Đổi màu category được chọn cho đến khi chuyển sang category khác
    private void highlightSelectedChip(String type) {

        // Reset màu tất cả chip
        chipPhotograph.setChipBackgroundColorResource(R.color.brown_100);
        chipPainting.setChipBackgroundColorResource(R.color.brown_100);
        chipPrint.setChipBackgroundColorResource(R.color.brown_100);
        chipVessel.setChipBackgroundColorResource(R.color.brown_100);

        // Đặt màu cho chip hiện tại
        switch (type) {
            case "Photograph":
                chipPhotograph.setChipBackgroundColorResource(R.color.brown_500);
                break;
            case "Painting":
                chipPainting.setChipBackgroundColorResource(R.color.brown_500);
                break;
            case "Print":
                chipPrint.setChipBackgroundColorResource(R.color.brown_500);
                break;
            case "Vessel":
                chipVessel.setChipBackgroundColorResource(R.color.brown_500);
                break;
        }
    }

    private void loadArtworks(String type, int page) {
        emptyState.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        artworkRepository.filterArtworks(type, page, new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                progressBar.setVisibility(View.GONE);
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (allArtworks == null || allArtworks.isEmpty()) {
                        // Không có kết quả
                        emptyState.setVisibility(View.VISIBLE);
                    }
                    if (!(artworks == null || artworks.isEmpty())){
                        // Có dữ liệu
                        emptyState.setVisibility(View.GONE);

                        allArtworks.addAll(artworks);
                        artworkAdapter.setArtworkList(allArtworks);
                    }
                });
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