package vn.edu.usth.mobilefinal.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.ArtworkRepository;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.activities.ArtWork_Details;
import vn.edu.usth.mobilefinal.activities.HomeActivity;
import vn.edu.usth.mobilefinal.adapters.ArtworkAdapter;
import vn.edu.usth.mobilefinal.adapters.CategoryAdapter;

public class SearchFragment extends Fragment {
    private ArtworkAdapter artworkAdapter;
    private ArtworkRepository artworkRepository;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private Chip chipAll, chipFilterBy;
    private List<Artwork> allArtworks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        EditText searchInput = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);
        chipAll = view.findViewById(R.id.chipAll);
        chipFilterBy = view.findViewById(R.id.chipFilterBy);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerSearchResults);

        // setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artworkAdapter = new ArtworkAdapter(new ArrayList<>(), artwork -> openArtworkDetails(artwork), R.layout.item_artwork_search);
        artworkRepository = new ArtworkRepository(getContext());

        // When user enter the searchText => perform
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            // Kiểm tra xem có đúng là người dùng bấm nút OK/Search/Done không
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String query = searchInput.getText().toString().trim();

                if (!query.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    performSearch(query);

                    // Ẩn bàn phím
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                } else {
                    Toast.makeText(getContext(), "Nhập từ khóa để tìm kiếm", Toast.LENGTH_SHORT).show();
                }
                return true; // đã xử lý xong sự kiện
            }

            return false; // chưa xử lý gì
        });
        recyclerView.setAdapter(artworkAdapter);


        chipAll.setOnClickListener(v -> highlightSelectedChip("All"));
        chipFilterBy.setOnClickListener(v -> highlightSelectedChip("Filter"));
        // Handle when enter FilterBy
        chipFilterBy.setOnClickListener(v -> {
            // Create BottomSheetDialog
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View sheetView = getLayoutInflater().inflate(R.layout.select_category, null);
            dialog.setContentView(sheetView);

            // Map RecyclerView
            RecyclerView rvCategories = sheetView.findViewById(R.id.rvCategories);
            rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

            // Create categories list
            List<String> cities = Arrays.asList(
                    "Photograph", "Film, Video, New Media", "Glass", "Coin", "Architectural Drawing",
                    "Decorative Arts", "Miniature room", "Installation", "Arms",
                    "Mask", "Drawing and Watercolor", "Ceramics",
                    "Textile", "Armor", "Vessel", "Sculpture", "Painting", "Book",
                    "Print", "Furniture"
            );

            // Set up adapter
            CategoryAdapter adapter = new CategoryAdapter(cities, category -> {
                chipFilterBy.setText(category);
                filterArtwork(category, allArtworks);
                dialog.dismiss();
            });

            rvCategories.setAdapter(adapter);

            // Show popup
            dialog.show();
        });

        return view;
    }

    private void filterArtwork(String type, List<Artwork> allArtworks) {
        artworkAdapter.setArtworkList(new ArrayList<>());

        // Dùng list mới, không đụng list gốc
        List<Artwork> filteredList = new ArrayList<>();
        for (Artwork artwork : allArtworks) {
            if (artwork.getCategory() != null && artwork.getCategory().equalsIgnoreCase(type)) {
                filteredList.add(artwork);
            }
        }
        artworkAdapter.setArtworkList(filteredList);
    }
    private void performSearch(String query) {
        allArtworks.clear();
        artworkAdapter.setArtworkList(new ArrayList<>());
        emptyState.setVisibility(View.GONE);

        artworkRepository.searchArtworks(query, new ArtworkRepository.ArtworkCallback() {
            @Override
            public void onSuccess(List<Artwork> artworks) {
                progressBar.setVisibility(View.GONE);
                if (artworks.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                } else {
                    allArtworks.addAll(artworks);
                }
                artworkAdapter.setArtworkList(allArtworks); // update UI
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi search: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Đổi màu category được chọn cho đến khi chuyển sang category khác
    private void highlightSelectedChip(String type) {

        // Reset màu tất cả chip
        chipAll.setChipBackgroundColorResource(R.color.brown_100);
        chipFilterBy.setChipBackgroundColorResource(R.color.brown_100);

        // Đặt màu cho chip hiện tại
        switch (type) {
            case "All":
                chipAll.setChipBackgroundColorResource(R.color.brown_500);
                break;
            case "Filter By":
                chipFilterBy.setChipBackgroundColorResource(R.color.brown_500);
                break;
        }
    }
    private void openArtworkDetails(Artwork artwork) {
        Intent intent = new Intent(getActivity(), ArtWork_Details.class);
        intent.putExtra("artwork", artwork);
        startActivity(intent);
    }
}