package vn.edu.usth.mobilefinal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.activities.Login;

public class ProfileFragment extends Fragment {

    private TextView tvFavoritesCount;
    private View logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration favoritesListener;

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // Map view
        tvFavoritesCount = v.findViewById(R.id.tvFavoritesCount);
        logoutButton     = v.findViewById(R.id.optionLogout);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // Nút logout
        setupLogoutButton();

        // Đếm số favorites
        observeFavoritesCount();
    }

    private void setupLogoutButton() {
        if (logoutButton == null) return;
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void observeFavoritesCount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (tvFavoritesCount == null) return;

        if (user == null) {
            tvFavoritesCount.setText("0");
            return;
        }

        favoritesListener = db.collection("favorites")
                .document(user.getUid())
                .collection("artwork")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (!isAdded() || tvFavoritesCount == null) return;
                    if (e != null || querySnapshot == null) {
                        return;
                    }
                    tvFavoritesCount.setText(String.valueOf(querySnapshot.size()));
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (favoritesListener != null) {
            favoritesListener.remove();
            favoritesListener = null;
        }
    }
}
