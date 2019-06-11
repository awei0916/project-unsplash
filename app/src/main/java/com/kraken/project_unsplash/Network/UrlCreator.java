package com.kraken.project_unsplash.Network;

import android.support.annotation.Nullable;

import com.kraken.project_unsplash.Utils.Constants;

public class UrlCreator {
    private String url;

    /**
     * url to get all photos
     * @param count @Nullable int
     * @return url
     */
    public String getAllPhotos(@Nullable Integer count) {
        url = Constants.getBaseUrl();
        url += count == null ?
                "/photos?per_page=50" : "/photos?per_page=" + count + "\"";
        return url;
    }

    /**
     * sort photos on keywords "latest", "oldest", "popular"
     * @param param : "latest", "oldest", "popular"
     * @return url
     */
    public String getAllPhotosSorted(String param) {
        url = Constants.getBaseUrl();
        url += "/photos?per_page=50&order_by=\"" + param + "\"";
        return url;
    }

    /**
     * url of curated photos
     * @param count @Nullable int
     * @return url
     */
    public String getCuratedPhotos(@Nullable Integer count) {
        url = Constants.getBaseUrl();
        url += count == null ? "/photos/curated?per_page=50" :
                "/photos/curated?per_page=" + count + "\"";
        return url;
    }

    /**
     * url for one photo
     * @param id : ID of the photo
     * @return url
     */
    public String getPhoto(String id) {
        url = Constants.getBaseUrl();
        url += "/photos/" + id;
        return url;
    }

    /**
     * url for featured collections
     * @param count : count of collections (default 10)
     * @return url
     */
    public String getFeaturedCollections(@Nullable Integer count) {
        url = Constants.getBaseUrl();
        url += count == null ? "/collections/featured" :
                "/collections/featured?per_page=" + count + "\"";
        return url;
    }

    /**
     * url for curated collections
     * @param count : count of collections (default 10)
     * @return url
     */
    public String getCuratedCollections(@Nullable Integer count) {
        url = Constants.getBaseUrl();
        url += count == null ? "/collections/curated" :
                "/collections/curated?per_page=" + count + "\"";
        return url;
    }

    /**
     * get a collection
     * @param id : id of collection
     * @return url
     */
    public String getCollection(String id) {
        url = Constants.getBaseUrl();
        url += "/collections/" + id;
        return url;
    }

    /**
     * get photos in a collection
     * @param id : id of collection
     * @return url
     */
    public String getCollectionPhotos(String id) {
        url = Constants.getBaseUrl();
        url += "/collections/" + id + "/photos";
        return url;
    }

    /**
     * search photos related to @param "key"
     * @param key : keyword
     * @return url
     */
    public String searchPhoto(String key) {
        url = Constants.getBaseUrl();
        url += "/search/photos?per_page=50&query=" + key + "\"";
        return url;
    }

    /**
     * search collections related to @param "key"
     * @param key : keyword
     * @return url
     */
    public String searchCollections(String key) {
        url = Constants.getBaseUrl();
        url += "/search/collections?per_page=25&query=" + key + "\"";
        return url;
    }

    /**
     * like <POST> / unlike <DELETE> a photo
     * @param id : id of photo
     * @return url
     */
    public String likePhoto(String id) {
        url = Constants.getBaseUrl();
        url += "/photos/" + id + "/like";
        return url;
    }

    /**
     * get a random photo
     * @return url
     */
    public String getRandomPhoto() {
        url = Constants.getBaseUrl();
        url += "/photos/random";
        return url;
    }

    /**
     * related collections
     * @param id : id source collection
     * @return url
     */
    public String getRelatedCollections(String id) {
        url = Constants.getBaseUrl();
        url += "/collections/" + id + "/related";
        return url;
    }
}
