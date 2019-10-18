package com.leo.process;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.leo.process.cache.LruCacheManager;

/**
 * 缓存图片
 */
public class CacheBitmapActivity extends AppCompatActivity {

    private LruCacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_bitmap);
        mCacheManager = LruCacheManager.getInstance();
    }


    private void loadBitmao(ImageView target, int resId) {
        String key = String.valueOf(resId);
        Bitmap bitmap = mCacheManager.get(key);
        if (bitmap != null) {
            target.setImageBitmap(bitmap);
        } else {
            target.setImageResource(resId);
            //然后将BitMap存入缓存
            BitMapTask bitMapTask = new BitMapTask();
            bitMapTask.execute(resId);
        }
    }

    class BitMapTask extends AsyncTask<Integer,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            //从资源加载，
            Bitmap bitmap = null;
            mCacheManager.put(String.valueOf(integers[0]),bitmap);
            return bitmap;
        }
    }

}
