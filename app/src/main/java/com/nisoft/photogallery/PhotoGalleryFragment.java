package com.nisoft.photogallery;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/27.
 */

public class PhotoGalleryFragment extends Fragment{
    public static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;


    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemTask().execute();
        Handler handler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(handler);
        mThumbnailDownloader.setListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(),thumbnail);
                target.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started.");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mPhotoRecyclerView = (RecyclerView) v
                .findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        setupAdapter();
        return v;
    }

    private class FetchItemTask extends AsyncTask<Void,Void,List<GalleryItem>>{
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
//            try {
//                String result = new FlickrFetchr().getUrl("https://www.bignerdranch.com");
//                Log.i(TAG, "Fetched contents of URL: " + result);
//            } catch (IOException e) {
//                Log.e(TAG, "Failed to fetch URL: ", e);
//            }
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
//        private TextView mTitleTextView;
        private ImageView mImageView;
        public PhotoHolder(View itemView) {
            super(itemView);
//            mTitleTextView = (TextView) itemView;
            mImageView = (ImageView) itemView;
        }
//        public void bindGalleryItem(GalleryItem item){
//            mTitleTextView.setText(item.toString());

//        }
        public void bindDrawable(Drawable drawable){
            mImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mItems;

        public PhotoAdapter(List<GalleryItem> items) {
            mItems = items;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = new TextView(getActivity());
            View view = View.inflate(getActivity(),R.layout.gallery_item,null);

            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.mipmap.ic_launcher);
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder,galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private void setupAdapter(){
        if (isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
}
