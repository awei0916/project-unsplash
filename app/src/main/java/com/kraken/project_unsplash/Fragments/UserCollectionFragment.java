package com.kraken.project_unsplash.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.kraken.project_unsplash.Adapters.PhotosRecyclerViewAdapter;
import com.kraken.project_unsplash.Models.Collection;
import com.kraken.project_unsplash.Models.Photo;
import com.kraken.project_unsplash.MyApplication;
import com.kraken.project_unsplash.Network.UrlBuilder;
import com.kraken.project_unsplash.R;
import com.kraken.project_unsplash.Utils.Constants;
import com.kraken.project_unsplash.Utils.Params;
import com.kraken.project_unsplash.Utils.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserCollectionFragment extends Fragment {

    private static final String TAG = "UserCollectionFragment";

    View rootView;

    private String username;
    private int page = 1;

    List<Collection> collections;
    CollectionsRecyclerViewAdapter adapter;
    private int pastItemsCount, visibleItemCount, totalItemCount;

    public void putUsername(String username) {
        this.username = username;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collections = new ArrayList<>();
        adapter = new CollectionsRecyclerViewAdapter(getActivity(), collections);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_collections, container, false);
        initRecyclerView();
        fetchCollections();
        return rootView;
    }

    private void fetchCollections() {
        StringRequest userPhotosRequest = new StringRequest(Request.Method.GET, UrlBuilder.getUserCollections(username, page), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: 200 OK\n" + response);
                collections.addAll(new Serializer().listCollections(response));
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Params.getParams();
            }
        };

        page++;

        MyApplication.getLocalRequestQueue().add(userPhotosRequest);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = rootView.findViewById(R.id.collectionsRecyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), Constants.NUM_COLUMNS);

        recyclerView.setAdapter(adapter);
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
                        fetchCollections();
                    }
                }
            }
        });
    }
}
