package vn.edu.usth.mobilefinal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// To pass information between fragment
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> favoriteCount = new MutableLiveData<>(0);

    public LiveData<Integer> getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int count) {
        favoriteCount.setValue(count);
    }
}
