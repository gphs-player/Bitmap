package com.leo.glide;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * <p>Date:2019/5/14.7:43 PM</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
public class GlideTestActivity extends AppCompatActivity {

    private ImageView mImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_glide_test);
        mImage = findViewById(R.id.imageHolder);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //为每个应用分配内存，M为单位
        int memoryClass = manager.getMemoryClass();
        System.out.println("memoryClass : "+memoryClass);
    }

    public void load(View view) {
        String url = "http://guolin.tech/book.png";
        String urlGif = "http://p1.pstatp.com/large/166200019850062839d3";
//        loadImage(url);
//        loadWithSize(url);
        loadWithHolder(urlGif);
//        loadWithCache(url);
//        loadWithCallback(mImage, url);
//        downloadImage();
//        loadTransform();
//        loadTransformLib();
    }

    /**
     * //1.基本使用
     *
     * @param url
     */
    private void loadImage(String url) {
        //可以加载网络本地和Drawable资源
        Glide.with(this).load(url).into(mImage);
    }

    /**
     * //2.占位图
     *
     * @param url
     */
    private void loadWithHolder(String url) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(this)
                .load(url)
                .apply(options)
                .into(mImage);
    }

    /**
     * //3.图片尺寸
     *
     * @param url
     */
    private void loadWithSize(String url) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher_round)
                //.override(200,200)//Glide只会加载这么大的图片，不管ImageView大小。清晰度影响
                .override(Target.SIZE_ORIGINAL);//加载原图
        Glide.with(this)
                .load(url)
                .apply(options)
                .into(mImage);
    }


    /**
     * //4.图片缓存
     *
     * @param url
     */
    private void loadWithCache(String url) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher_round)
                //内存缓存策略
                .skipMemoryCache(true)//Glide默认开启内存缓存，除非你想跳过
                //硬盘缓存策略
                .diskCacheStrategy(DiskCacheStrategy.NONE);
//        DiskCacheStrategy.NONE： 表示不缓存任何内容。
//        DiskCacheStrategy.DATA： 表示只缓存原始图片。
//        DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片。
//        DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
//        DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）。
        Glide.with(this)
                .load(url)
                .apply(options)
                .into(mImage);
    }

    /**
     * 5.回调监听
     *
     * @param url
     */
    private void loadWithCallback(final ImageView imageView, String url) {

        SimpleTarget<Drawable> target = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition transition) {
                //I/System.out: resource： android.graphics.drawable-xxhdpi-xxhdpi.BitmapDrawable@c8d4e6c
                System.out.println("resource： " + resource.toString());
                imageView.setImageDrawable(resource);
            }
        };
        Glide.with(this).load(url).into(target);

    }

    /**
     * 6.submit获取文件下载信息
     */
    private void downloadImage() {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://www.guolin.tech/book.png";
                    final Context context = getApplicationContext();
                    FutureTarget<File> target = Glide.with(context)
                            .asFile()
                            .load(url)
                            .submit();
                    final File file = target.get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GlideTestActivity.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 7.监听图片加载成功失败
     */
    private void loadListener() {
        String url = "http://www.guolin.tech/book.png";
        Glide.with(this).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //返回值决定是否继续下传到target
                        return false;
                    }
                }).into(mImage);
    }

    /**
     * 8.图片变化
     */
    private void loadTransform() {
        String url = "http://www.guolin.tech/book.png";
        //图片丰富的显示效果
        RequestOptions options = new RequestOptions().transform(new CenterCrop());
        //Glide默认提供的现成API
        RequestOptions options1 = new RequestOptions().circleCrop();
        Glide.with(this).load(url)
                .apply(options1)
                .into(mImage);
    }

    /**
     * 9 图片变化依赖库
     */
    private void loadTransformLib() {
        String url = "http://www.guolin.tech/book.png";
        //图片丰富的显示效果
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher_round)
                //模糊处理
                .transform(new BlurTransformation());
        Glide.with(this).load(url)
                .apply(options)
                .into(mImage);
    }


    private void custom(String url) {
        //提供了一套和Glide3一样的用法，前提是@GlideModule注解
        GlideApp.with(this)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Target.SIZE_ORIGINAL)
                .circleCrop()
                .into(mImage);
    }
}
