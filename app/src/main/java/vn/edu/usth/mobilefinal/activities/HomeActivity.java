package vn.edu.usth.mobilefinal.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.adapters.ViewPagerAdapter;
import vn.edu.usth.mobilefinal.fragments.CollectionFragment;
import vn.edu.usth.mobilefinal.fragments.FavoritesFragment;
import vn.edu.usth.mobilefinal.fragments.HomeFragment;
import vn.edu.usth.mobilefinal.fragments.SearchFragment;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // layout có fragment_container

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        setupViewPager();
        setupBottomNavigation();
    }



    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        // Add your fragments
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new SearchFragment(), "Search");
        adapter.addFragment(new CollectionFragment(), "Explore");
        adapter.addFragment(new FavoritesFragment(), "Favorites");

        viewPager.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0, true);
            } else if (itemId == R.id.nav_search) {
                viewPager.setCurrentItem(1, true);
            } else if (itemId == R.id.nav_explore) {
                viewPager.setCurrentItem(2, true);
            } else if (itemId == R.id.nav_favorites) {
                viewPager.setCurrentItem(3, true);
            }
            return true;
        });

        // Sync ViewPager with Bottom Navigation
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    bottomNavigation.setSelectedItemId(R.id.nav_home);
                } else if (position == 1) {
                    bottomNavigation.setSelectedItemId(R.id.nav_search);
                } else if (position == 2) {
                    bottomNavigation.setSelectedItemId(R.id.nav_explore);
                } else if (position == 3) {
                    bottomNavigation.setSelectedItemId(R.id.nav_favorites);
                }
            }
        });
    }





}

// thêm lại fetchapi ( xoá db cũ với catagory popular và famous )
// viết lại hàm cho popularartwork ( load mẫu nhiên 10 ảnh cho popular artworkfrag )
// với 100 ảnh ta sẽ lấy ngẫu nhiên sau 24h cho art work of the day và 10 ảnh random cho popular works )
// viết frag cho artwork of the day