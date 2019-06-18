package com.kraken.project_unsplash.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kraken.project_unsplash.Adapters.CollectionsRecyclerViewAdapter;
import com.kraken.project_unsplash.Models.Collection;
import com.kraken.project_unsplash.MyApplication;
import com.kraken.project_unsplash.Network.UrlBuilder;
import com.kraken.project_unsplash.R;
import com.kraken.project_unsplash.Utils.Constants;
import com.kraken.project_unsplash.Utils.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionsFragment extends Fragment {

    // TAG for log messages
    private static final String TAG = "SearchAndCollectionsFra";

    // root view holding the layout of the fragment
    private View rootView;
    private int page = 1;

    // data
    List<Collection> collections;

    //----------------- experimental -----------------

    // recycler view scroll stuff
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private CollectionsRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init data
        collections = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the root view
        rootView = inflater.inflate(R.layout.fragment_collections, container, false);
        // fetch the collections
        fetchCuratedCollections();
        // init the recycler view
        initRecyclerView();
        return rootView;
    }

    /**
     * fetch the featured collections
     */
    private void fetchCuratedCollections() {
        Log.d(TAG, "fetchPhotos: " + UrlBuilder.getFeaturedCollections(30, page));
        // StringRequest to fetch raw JSON
        StringRequest curatedCollectionsRequest = new StringRequest(Request.Method.GET,
                UrlBuilder.getFeaturedCollections(30, page), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: 200 OK\n" + response);
                // serializer converts the raw JSON into Collection[]
                Serializer serializer = new Serializer();
                collections.addAll(serializer.listCollections(response));
                // create the recycler view with rest of the Collection[]
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        }) {
            // the header parameters
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept-Version", "v1");
                params.put("Authorization", "Client-ID " + Constants.getAccessKey());
                return params;
            }
        };

        // increment page
        page += 1;

        // add the string request to app wide local request queue
        MyApplication.getLocalRequestQueue().add(curatedCollectionsRequest);
    }

    /**
     * Init the recycler view
     */
    private void initRecyclerView() {
        RecyclerView collectionsRecyclerView = rootView.findViewById(R.id.collectionsRecyclerView);

        recyclerViewAdapter = new CollectionsRecyclerViewAdapter(collections, getContext());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        collectionsRecyclerView.setAdapter(recyclerViewAdapter);
        collectionsRecyclerView.setLayoutManager(layoutManager);

        //------------------- experimental --------------------

        // add onScrollListener on recycler view to enable continuous indefinite scroll
        collectionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // if scroll possible
                if (dy > 0) {

                    // get counts of past items, currently visible items and total items
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        // if visible cnt + past item cnt >= total items cnt then we've reached
                        // the end
                        // we can also change the total item count to be some integer less than
                        // that to allow for continuous scroll without breaks
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            Toast.makeText(getContext(), "reached end", Toast.LENGTH_SHORT).show();
                            // when end reached fetch more content
                            fetchCuratedCollections();
                        }
                    }
                }
            }
        });
    }
}
