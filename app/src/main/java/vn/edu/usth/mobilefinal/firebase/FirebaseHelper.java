package vn.edu.usth.mobilefinal.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class FirebaseHelper {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Luu artwork vao favorite
    public static void saveFavorite(String userId, String imageId) {
        Map<String, Object> favoriteData = new HashMap<>(); // Tao map
        favoriteData.put("imageId", imageId); // Tao field imageId

        db.collection("users") // truy cap users collection
                .document(userId)
                .collection("favorites")
                .document(imageId)
                .set(favoriteData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Added to favorites successfully");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding favorite: " + e.getMessage());
                });
    }
}
