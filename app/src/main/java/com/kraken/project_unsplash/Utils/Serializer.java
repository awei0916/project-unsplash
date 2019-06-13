package com.kraken.project_unsplash.Utils;

import android.util.Log;

import com.google.gson.Gson;
import com.kraken.project_unsplash.Models.Photo;

import java.util.ArrayList;
import java.util.List;

public class Serializer {

    private static final String TAG = "Serializer";

    private Gson gson;

    public Serializer() {
        gson = new Gson();
    }

    public Photo[] listPhotos(String raw) {
        Photo[] photos = gson.fromJson(raw, Photo[].class);
        Log.d(TAG, "listPhotos: " + photos.length);
        return photos;
    }
}