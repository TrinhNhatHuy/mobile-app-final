package vn.edu.usth.mobilefinal.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.SharedViewModel;

public class ProfileFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private TextView tvFavoritesCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount);

        // Dùng cùng ViewModel với FavoritesFragment
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Quan sát thay đổi favorite count
        sharedViewModel.getFavoriteCount().observe(getViewLifecycleOwner(), count -> {
            tvFavoritesCount.setText(String.valueOf(count));
        });

        return view;
    }
}