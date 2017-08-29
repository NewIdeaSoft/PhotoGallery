package com.nisoft.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/27.
 */

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "8b3efd6614aea1379b4c5e1e2795f91b";
    private static final String API_KEY_PASSWORD ="3585e59573fb322e";
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage()
                        +": with "
                        +urlSpec);
            }
            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while((byteRead = in.read(buffer))>0){
                out.write(buffer,0,byteRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }

    }
    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems(){
        List<GalleryItem> items = new ArrayList<>();
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method","flickr.photos.getRecent")
                    .appendQueryParameter("api_key",API_KEY)
                    .appendQueryParameter("format","json")
                    .appendQueryParameter("nojsoncallback","1")
                    .appendQueryParameter("extras","url_s")
                    .build().toString();
            String jsonString = getUrl(url);
            Log.i(TAG,"received json:" + jsonString);

            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items,jsonBody);
        } catch (IOException e) {
            Log.e(TAG,"failed to fetch items:",e);
        } catch (JSONException e) {
            Log.e(TAG,"failed to parse Json:",e);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items ,JSONObject jsonBody)
            throws IOException,JSONException{
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject jsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(jsonObject.getString("id"));
            item.setCaption(jsonObject.getString("title"));
            if (!jsonObject.has("url_s")){
                continue;
            }
            item.setUrl(jsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
