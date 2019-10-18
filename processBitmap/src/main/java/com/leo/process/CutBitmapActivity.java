package com.leo.process;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.ImageView;

/**
 * 截取原图一部分
 */
public class CutBitmapActivity extends AppCompatActivity {

    private ImageView mCutImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_bitmap);
        mCutImage = (findViewById(R.id.cutPiece));
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        //从原图的(0，0)位置，截取一半
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth()/2, source.getHeight()/2);
        mCutImage.setImageBitmap(bitmap);
    }
}
