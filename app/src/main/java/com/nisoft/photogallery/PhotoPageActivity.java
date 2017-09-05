package com.nisoft.photogallery;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Administrator on 2017/9/5.
 */

public class PhotoPageActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context, Uri photoPageUri){
        Intent intent = new Intent(context,PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }
}
