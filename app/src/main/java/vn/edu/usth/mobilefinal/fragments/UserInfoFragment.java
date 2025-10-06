package vn.edu.usth.mobilefinal.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.usth.mobilefinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageButton;
import android.widget.TextView;


public class UserInfoFragment extends Fragment {
    private TextView tvUserName;
    private ImageButton logoutButton;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        tvUserName = view.findViewById(R.id.tvUserName);
        logoutButton = view.findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();

        loadUserData();
        setupLogoutButton();

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String initial = "?";

            if (name != null && !name.trim().isEmpty()) {
                initial = name.trim().substring(0, 1).toUpperCase();
            } else if (email != null) {
                String prefix = email.split("@")[0];
                if (!prefix.isEmpty()) initial = prefix.substring(0, 1).toUpperCase();
            }

            tvUserName.setText(initial);
        }
    }

    private void setupLogoutButton() {
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}