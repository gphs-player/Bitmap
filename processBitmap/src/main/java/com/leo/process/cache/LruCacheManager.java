package com.leo.process.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * <p>Date:2019-10-17.14:41</p>
 * <p>Author:niu bao</p>
 * <p>Desc:
 * 设计缓存要考虑的问题
 * 整体的内存耗用如何？
 * 屏幕一次显示多少图片？缓存中的图片要有多少处于就绪状态？
 * 屏幕的分辨率是多少？高密度比的手机应该需要更多的内存
 * 待加载图片的尺寸和配置？加载每张图片要消耗多少内存？
 * 多久访问一次图像？是否某些图像的访问频率会高一些？是不是需要更多的LruCache对象。
 * 在质量和数量之间做好平衡，有时候加载大量的低质量图片到内存中也是有用的，以便在后台加载高质量的图片。
 * </p>
 */
public class LruCacheManager extends LruCache<String, Bitmap> {


    private static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    private static int cacheSize = maxMemory / 8;

    private static LruCacheManager mInstance;

    public static LruCacheManager getInstance() {
        if (mInstance == null) {
            synchronized (LruCacheManager.class) {
                if (mInstance == null) {
                    mInstance = new LruCacheManager(cacheSize);
                }
            }
        }
        return mInstance;
    }

    private LruCacheManager(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount() / 1024;
    }

    public void addBitmapToCache(String key,Bitmap bitmap){
        if (getBitmapFromCache(key) == null){
            mInstance.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String key){
        return mInstance.get(key);
    }
}
