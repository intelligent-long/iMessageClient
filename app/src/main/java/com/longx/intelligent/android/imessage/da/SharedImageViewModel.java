package com.longx.intelligent.android.imessage.da;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedImageViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();

    public void setImage(Bitmap bitmap) {
        image.setValue(bitmap);
    }

    public LiveData<Bitmap> getImage() {
        return image;
    }

    public void clear() {
        image.setValue(null);
    }
}
