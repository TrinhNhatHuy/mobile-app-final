package vn.edu.usth.mobilefinal.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.edu.usth.mobilefinal.Artwork;
import vn.edu.usth.mobilefinal.R;
import vn.edu.usth.mobilefinal.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

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

        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, homeFragment);
            transaction.commit();
        }
    }

    // ✅ Hàm này cho phép HomeFragment mở ArtWork_Details
    public void openArtworkDetails(Artwork artwork) {
        Intent intent = new Intent(this, ArtWork_Details.class);
        intent.putExtra("artwork", artwork);
        startActivity(intent);
    }
}

// thêm lại fetchapi
// thêm Artwork of the Day
// sửa nut back ở popular artwork ( DONE)