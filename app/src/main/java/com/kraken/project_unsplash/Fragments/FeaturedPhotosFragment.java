package com.kraken.project_unsplash.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kraken.project_unsplash.Adapters.PhotosRecyclerViewAdapter;
import com.kraken.project_unsplash.Models.Photo;
import com.kraken.project_unsplash.MyApplication;
import com.kraken.project_unsplash.Network.UrlBuilder;
import com.kraken.project_unsplash.R;
import com.kraken.project_unsplash.Utils.Constants;
import com.kraken.project_unsplash.Utils.Params;
import com.kraken.project_unsplash.Utils.Serializer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fragment class for the Featured Photo section
 * selected by default
 */

public class FeaturedPhotosFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String TAG = "FeaturedPhotosFragment";

  // root view of the fragment
  private View rootView;
  int page = 1;
  private String orderBy = "latest";

  // data
  List<Photo> photos;

  // recycler view scroll stuff
  private PhotosRecyclerViewAdapter recyclerViewAdapter;
  private int pastItemsCount, visibleItemCount, totalItemCount;
  private ProgressBar progressBar;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyApplication.preferences.registerOnSharedPreferenceChangeListener(this);

    // init data
    photos = new ArrayList<>();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    // inflate the view and keep in rootView
    rootView = inflater.inflate(R.layout.fragment_photos, container, false);
    progressBar = rootView.findViewById(R.id.photos_progress_bar);
    showProgressBar();
    // init recycler view
    initRecyclerView();
    // sorting parameter
    Bundle bundle = this.getArguments();
    if (bundle != null) {
      String param = bundle.getString(this.getResources().getString(R.string.order_by_param_transaction_key));
      if (param != null) {
        orderBy = param;
      }
    }
    // get the sorted list of photos
    sortBy(orderBy);
    return rootView;
  }

  private void showProgressBar() {
    progressBar.setVisibility(View.VISIBLE);
  }

  private void hideProgressBar() {
    progressBar.setVisibility(View.INVISIBLE);
  }

  /**
   * Use the localRequestQueue to fetch featured photos
   */
  private void fetchPhotos() {
    Log.d(TAG, "fetchPhotos: " + UrlBuilder.getAllPhotos(50, orderBy, page));
    // string request fetches raw json using volley
    StringRequest allPhotosRequest = new StringRequest(Request.Method.GET,
//      UrlBuilder.getAllPhotos(50, orderBy, page),
      "https://api.unsplash.com/search/photos?per_page=50&page=1&order_by=\"latest\"&query=\"featured\"",
      new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
          hideProgressBar();
          Log.d(TAG, "onResponse: 200 OK\n" + response);
          try {
            // convert response into JSON object and get the "results" array
            JSONObject jsonObject = new JSONObject(response);
            String raw = jsonObject.getString("results");
            Log.d(TAG, "onResponse: RAW " + raw);
            // serializer converts the raw json into a Photo[]
            Serializer serializer = new Serializer();
            photos.addAll(serializer.listPhotos(raw));
            Log.d(TAG, "onResponse: " + photos.size());
            // notify recycler view adapter
            recyclerViewAdapter.notifyDataSetChanged();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        hideProgressBar();
        Log.d(TAG, "onErrorResponse: " + error.toString());
      }
    }) {
      // the header parameters
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Log.d(TAG, "getHeaders: " + Params.getGeneralParams().toString());
        return Params.getGeneralParams();
      }
    };

    // increment page
    page += 1;

    // add the string request to the local request queue in the application context;
    MyApplication.getLocalRequestQueue().add(allPhotosRequest);
  }

  /**
   * create the recycler view
   */
  private void initRecyclerView() {
    RecyclerView recyclerView = rootView.findViewById(R.id.rv_featured_photos);

    recyclerViewAdapter = new PhotosRecyclerViewAdapter(getContext(), photos);
    final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), Constants.NUM_COLUMNS);

    recyclerView.setAdapter(recyclerViewAdapter);
    recyclerView.setLayoutManager(layoutManager);

    // add onScroll listener
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        // if scroll up
        if (dy > 0) {
          // get the needed item counts
          visibleItemCount = recyclerView.getChildCount();
          totalItemCount = layoutManager.getItemCount();
          pastItemsCount = layoutManager.findFirstVisibleItemPosition();

          if (visibleItemCount + pastItemsCount >= totalItemCount) {
            Toast.makeText(getContext(), "reached end", Toast.LENGTH_LONG).show();
            // fetch new photos
            fetchPhotos();
          }
        }
      }
    });
  }

  /**
   * sorts photos based on @param "param"
   * @param param : parameter {"latest", "oldest", "popular"}
   */
  public void sortBy(String param) {
    // change the parameter
    orderBy = param;
    // clear current photos
    photos.clear();
    Log.d(TAG, "sortBy: " + photos.size());
    // clean the recycler view
    recyclerViewAdapter.notifyDataSetChanged();
    // fetch new photos
    fetchPhotos();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    switch (key) {
      case "pref_theme":
        Log.d(TAG, "onSharedPreferenceChanged: theme changes");
        break;
      case "pref_layout":
        Log.d(TAG, "onSharedPreferenceChanged: layout changes");
    }
  }
}
