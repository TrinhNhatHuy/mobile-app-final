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

    // Views
    private TextView tvFavoritesCount;
    private TextView tvUserEmail;   // <-- thêm
    private TextView tvUserName;    // <-- optional: điền tên nếu có
    private View logoutButton;

    // Firebase
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

        // Map view đúng theo XML bạn gửi
        tvFavoritesCount = v.findViewById(R.id.tvFavoritesCount);
        tvUserEmail      = v.findViewById(R.id.tvUserEmail);
        tvUserName       = v.findViewById(R.id.tvUserName);
        logoutButton     = v.findViewById(R.id.optionLogout);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();


        bindUserIdentity();

        setupLogoutButton();

        observeFavoritesCount();
    }

    private void bindUserIdentity() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            if (tvUserEmail != null) tvUserEmail.setText("user@example.com");
            if (tvUserName  != null) tvUserName.setText("Art Lover");
            return;
        }

        String email = user.getEmail();
        if (tvUserEmail != null) {
            tvUserEmail.setText(email != null && !email.isEmpty() ? email : "user@example.com");
        }

        String displayName = user.getDisplayName();
        if (tvUserName != null) {
            if (displayName != null && !displayName.trim().isEmpty()) {
                tvUserName.setText(displayName.trim());
            } else {
                // fallback theo tiền tố email hoặc mặc định
                if (email != null && email.contains("@")) {
                    String prefix = email.substring(0, email.indexOf('@')).trim();
                    tvUserName.setText(prefix.isEmpty() ? "Art Lover" : prefix);
                } else {
                    tvUserName.setText("Art Lover");
                }
            }
        }
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
                        // Có lỗi có thể giữ nguyên số đang hiển thị hoặc set "0"
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
