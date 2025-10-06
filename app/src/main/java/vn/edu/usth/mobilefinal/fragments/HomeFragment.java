package vn.edu.usth.mobilefinal.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.fragments.UserInfoFragment;
import vn.edu.usth.mobilefinal.fragments.PopularArtworksFragment;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_user_info, new UserInfoFragment())
                    .replace(R.id.fragment_daily, new DailyArtFragment())
                    .replace(R.id.fragment_popular, new PopularArtworksFragment())
                    .commit();
        }

        return view;
    }
}

