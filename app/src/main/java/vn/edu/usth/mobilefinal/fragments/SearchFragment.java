package vn.edu.usth.mobilefinal.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.ArtworkRepository;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.activities.ArtWork_Details;
import vn.edu.usth.mobilefinal.adapters.ArtworkAdapter;
import vn.edu.usth.mobilefinal.adapters.CategoryAdapter;
import vn.edu.usth.mobilefinal.adapters.SearchHistoryAdapter;

public class SearchFragment extends Fragment {
    // Constants
    private static final String TAG = "SearchFragment";

    // UI Components
    private EditText searchInput;
    private RecyclerView rvSearchHistory;
    private ArtworkAdapter artworkAdapter;
    private SearchHistoryAdapter historyAdapter;
    private List<String> searchHistoryList = new ArrayList<>();
    private boolean isLoadingHistory = false;
    private ArtworkRepository artworkRepository;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private Chip chipAll, chipFilterBy;
    private List<Artwork> allArtworks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        searchInput = view.findViewById(R.id.etSearch);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);
        chipAll = view.findViewById(R.id.chipAll);
        chipFilterBy = view.findViewById(R.id.chipFilterBy);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerSearchResults);
        rvSearchHistory = view.findViewById(R.id.rvSearchHistory);

        // Setup RecyclerViews for Searched Artworks
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artworkAdapter = new ArtworkAdapter(new ArrayList<>(), this::openArtworkDetails, R.layout.item_artwork_search);
        recyclerView.setAdapter(artworkAdapter);
        artworkRepository = new ArtworkRepository(getContext());

        // Setup RecyclerViews for Search History
        rvSearchHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new SearchHistoryAdapter(searchHistoryList, query -> {
            searchInput.setText(query);
            searchInput.setSelection(query.length());
            performSearch(query);
            rvSearchHistory.setVisibility(View.GONE);
        });
        rvSearchHistory.setAdapter(historyAdapter);

        // Search action listener
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    rvSearchHistory.setVisibility(View.GONE);
                    performSearch(query);

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                    }

                    saveSearchHistory(query);
                } else {
                    Toast.makeText(getContext(), "Enter search input", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        // Focus change listener
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && searchInput.getText().toString().trim().isEmpty()) {
                loadRecentSearches();
            } else {
                rvSearchHistory.setVisibility(View.GONE);
            }
        });

        // Text change listener
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchInput.hasFocus()) {
                    if (s.toString().trim().isEmpty()) {
                        if (!searchHistoryList.isEmpty()) {
                            rvSearchHistory.setVisibility(View.VISIBLE);
                        } else {
                            loadRecentSearches();
                        }
                    } else {
                        rvSearchHistory.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Chip listeners
        chipAll.setOnClickListener(v -> {
            highlightSelectedChip("All");
            artworkAdapter.setArtworkList(allArtworks);
            emptyState.setVisibility(View.GONE);
            chipFilterBy.setText("Filter By");
        });

        chipFilterBy.setOnClickListener(v -> {
            highlightSelectedChip("Filter By");
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View sheetView = getLayoutInflater().inflate(R.layout.select_category, null);
            dialog.setContentView(sheetView);

            RecyclerView rvCategories = sheetView.findViewById(R.id.rvCategories);
            rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

            List<String> categories = Arrays.asList(
                    "Armor", "Book", "Drawing and Watercolor", "Glass",
                    "Painting", "Photograph", "Print", "Sculpture", "Vessel"
            );

            CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
                chipFilterBy.setText(category);
                filterArtwork(category, allArtworks);
                dialog.dismiss();
            });

            rvCategories.setAdapter(adapter);
            dialog.show();
        });

        return view;
    }

    private void filterArtwork(String type, List<Artwork> allArtworks) {
        emptyState.setVisibility(View.GONE);
        List<Artwork> filteredList = new ArrayList<>();

        for (Artwork artwork : allArtworks) {
            if (artwork.getCategory() != null && artwork.getCategory().equalsIgnoreCase(type)) {
                filteredList.add(artwork);
            }
        }

        if (filteredList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
        }
        artworkAdapter.setArtworkList(filteredList);
    }

    private void performSearch(String query) {
        allArtworks.clear();
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
                artworkAdapter.setArtworkList(allArtworks);
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Search error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void highlightSelectedChip(String type) {
        chipAll.setChipBackgroundColorResource(R.color.brown_100);
        chipFilterBy.setChipBackgroundColorResource(R.color.brown_100);

        switch (type) {
            case "All":
                chipAll.setChipBackgroundColorResource(R.color.brown_500);
                break;
            case "Filter By":
                chipFilterBy.setChipBackgroundColorResource(R.color.brown_500);
                break;
        }
    }

    private void saveSearchHistory(String query) {
        searchHistoryList.remove(query);
        searchHistoryList.add(0, query);

        while (searchHistoryList.size() > 5) {
            searchHistoryList.remove(searchHistoryList.size() - 1);
        }

        historyAdapter.notifyDataSetChanged();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        CollectionReference userSearchRef = FirebaseFirestore.getInstance()
                .collection("search_history")
                .document(currentUser.getUid())
                .collection("queries");

        userSearchRef.whereEqualTo("text", query)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot existingDoc = snapshot.getDocuments().get(0);
                        existingDoc.getReference().update("timestamp", FieldValue.serverTimestamp())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Updated timestamp: " + query))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to update timestamp", e));
                    } else {
                        addNewSearchQuery(userSearchRef, query);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking existing query", e);
                    addNewSearchQuery(userSearchRef, query);
                });
    }

    private void addNewSearchQuery(CollectionReference userSearchRef, String query) {
        userSearchRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(allDocsSnapshot -> {
                    List<DocumentSnapshot> docs = allDocsSnapshot.getDocuments();

                    if (docs.size() >= 5) {
                        DocumentSnapshot oldest = docs.get(docs.size() - 1);
                        oldest.getReference().delete()
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Removed oldest query"))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete oldest", e));
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("text", query);
                    data.put("timestamp", FieldValue.serverTimestamp());

                    userSearchRef.add(data)
                            .addOnSuccessListener(ref -> Log.d(TAG, "Added query: " + query))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to add query", e));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to check query count", e);
                    Map<String, Object> data = new HashMap<>();
                    data.put("text", query);
                    data.put("timestamp", FieldValue.serverTimestamp());
                    userSearchRef.add(data);
                });
    }

    private void loadRecentSearches() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || isLoadingHistory) return;

        isLoadingHistory = true;

        FirebaseFirestore.getInstance()
                .collection("search_history")
                .document(currentUser.getUid())
                .collection("queries")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshot -> {
                    searchHistoryList.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String text = doc.getString("text");
                        if (text != null && !searchHistoryList.contains(text)) {
                            searchHistoryList.add(text);
                        }
                    }

                    historyAdapter.notifyDataSetChanged();

                    if (getView() != null && searchInput.hasFocus() &&
                            searchInput.getText().toString().trim().isEmpty() &&
                            !searchHistoryList.isEmpty()) {
                        rvSearchHistory.setVisibility(View.VISIBLE);
                    }

                    isLoadingHistory = false;
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load search history", e);
                    isLoadingHistory = false;
                });
    }

    private void openArtworkDetails(Artwork artwork) {
        Intent intent = new Intent(getActivity(), ArtWork_Details.class);
        intent.putExtra("artwork", artwork);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        artworkAdapter = null;
        historyAdapter = null;
    }
}