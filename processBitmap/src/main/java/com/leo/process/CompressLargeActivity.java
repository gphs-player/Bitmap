package com.leo.process;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * 压缩之后加载一个大图
 * <br>
 * 官方Demo
 * <br>
 * 加载图片需要考虑的点
 *      <ul>
 *          <li>预估加载完整的图需要的内存</li>
 *          <li>考虑应用的内存的整体使用情况，你愿意分配多少内存给到此图</li>
 *          <li>目标View的尺寸</li>
 *          <li>屏幕分辨率</li>
 *      </ul>
 */
public class CompressLargeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_large);
        releaseSource();
    }

    private void releaseSource() {
        InputStream open = null;
        try {
            open = getResources().getAssets().open("world.jpg");
            readOriginBitmap(open);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (open != null) open.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private BitmapFactory.Options options;
    //读取原图的size
    private void readOriginBitmap(InputStream open) {
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(open, null, options);
        //获取原图的宽高
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        String outMimeType = options.outMimeType;
        System.out.println("outWidth : " + outWidth);//6480
        System.out.println("outHeight : " + outHeight);//3888
        System.out.println("outMimeType : " + outMimeType);//image/jpeg
    }

    //根据要求压缩宽高,计算出压缩比
    private int calcTargetViewSize(BitmapFactory.Options options, int width, int height) {
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int sampleSize = 1;
        if (outWidth > width || outHeight > height){
            int halfWidth = outWidth / 2;
            int halfHeight = outHeight / 2;
            while (halfWidth/sampleSize >= width && halfHeight/sampleSize>= height){
                sampleSize*=2;
            }
        }

        return sampleSize;//4
    }

    public void loadBird(View view) throws IOException {
        ImageView image = (ImageView) view;
        int width = image.getWidth();//900
        int height = image.getHeight();//900
        System.out.println("width : "+width);
        System.out.println("height : "+height);
        int sampleSize = calcTargetViewSize(options, width, height);
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("world.jpg"), null, options);
        int resultWidth = bitmap.getWidth();
        int resultHeight = bitmap.getHeight();
        System.out.println("resultWidth : "+resultWidth);//1620
        System.out.println("resultHeight : "+resultHeight);//972
        image.setImageBitmap(bitmap);
    }
}
