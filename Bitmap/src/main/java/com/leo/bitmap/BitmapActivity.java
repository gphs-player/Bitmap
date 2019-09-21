package com.leo.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * <p>Date:2019/6/11.4:10 PM</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class BitmapActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView;
    StringBuilder msg;
    //Bitmap内存占用 ≈ 像素数据总大小 = 横向像素数量 × 纵向像素数量 × 每个像素的字节大小
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bitmap);
        setTitle("Bitmap");
        msg = new StringBuilder();
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.text);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test,options);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        String config = bitmap.getConfig().toString();
        int byteCount = bitmap.getByteCount();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics );
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        int densityDpi = displayMetrics.densityDpi;
        float density = displayMetrics.density;
        msg.append("width: ").append(width).append("\n");
        msg.append("height: ").append(height).append("\n");
        msg.append("config: ").append(config).append("\n");
        msg.append("byteCount: ").append(byteCount).append("\n");
        msg.append("densityDpi: ").append(densityDpi).append("\n");
        msg.append("xdpi: ").append(xdpi).append("\n");
        msg.append("ydpi: ").append(ydpi).append("\n");
        msg.append("device density: ").append(density).append("\n");

        int inDensity = options.inDensity;
        int inTargetDensity = options.inTargetDensity;
        int inScreenDensity = options.inScreenDensity;
        msg.append("inDensity: ").append(inDensity).append("\n");
        msg.append("inTargetDensity: ").append(inTargetDensity).append("\n");
//        msg.append("inScreenDensity: ").append(inScreenDensity).append("\n");
        mTextView.setText(msg.toString());
        mImageView.setImageBitmap(bitmap);




    }
}
