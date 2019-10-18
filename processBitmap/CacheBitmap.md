#### 缓存之前的思考

 * 应用整体的内存耗用如何？
 * 屏幕一次显示多少张图片？缓存中的图片要有多少处于就绪状态？
 * 屏幕的分辨率是多少？高密度比的手机应该需要更多的内存
 * 待加载图片的尺寸和配置？加载每张图片要消耗多少内存？
 * 多久访问一次图像？是否某些图像的访问频率会高一些？是不是需要更多的LruCache对象。
 * 在质量和数量之间做好平衡，有时候加载大量的低质量图片到内存中也是有用的，以便在后台加载高质量的图片。



#### 内存缓存（LRUCache）

​	高速访问内存中的图片，但是要占用CPU资源，下方给出例子，但并不是绝对公式，要根据实际情况使用，缓存太小导致缓存作用微乎其微，缓存太大可能会OOM。

```java
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
```



#### 磁盘缓存（DiskLruCache）

​	内存缓存固然有效，但也有万一的时候，比如你的应用被一个电话切入了后台，内存被销毁掉，或者GridView组件很快的就把内存缓存占满，这种情况下仍需要一层保险箱，也就是DiskLruCache来保证你的应用程序能较快的加载图片，而不用从头处理每张图片。

[源文件下载](https://android.googlesource.com/platform/libcore/+/android-4.1.1_r1/luni/src/main/java/libcore/io/DiskLruCache.java)

[使用参考](https://blog.csdn.net/guolin_blog/article/details/28863651)

##### 初始化

```java
new InitDiskCacheTask().execute(cacheDir);
class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
    @Override
    protected Void doInBackground(File... params) {
        synchronized (diskCacheLock) {
            File cacheDir = params[0];
            diskLruCache = DiskLruCache.open(cacheDir, DISK_CACHE_SIZE);
            diskCacheStarting = false; // Finished initialization
            diskCacheLock.notifyAll(); // Wake any waiting threads
        }
        return null;
    }
}
```

##### 写入

```java
synchronized (diskCacheLock) {
    if (diskLruCache != null && diskLruCache.get(key) == null) {
        String key = hashKeyFromUrl(url);//将图片URL进行MD5
        DiskLruCache.Editor editor = mDiskCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(CACHE_INDEX);//CACHE_INDEX默认0即可
                if (downloadUrlToStram(url, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskCache.flush();
            }
        }
}
```

##### 读取

```java

synchronized (diskCacheLock) {//检验是否初始化，否则要阻塞
    while (diskCacheStarting) {
        try {
            diskCacheLock.wait();
        } catch (InterruptedException e) {}
    }
    if (diskLruCache != null) {
       Bitmap bitmap = null;
			String key = hashKeyFromUrl(url);
			DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
			if (snapshot != null) {
				FileInputStream inputStream = (FileInputStream) snapshot.getInputStream(CACHE_INDEX);
		    FileDescriptor descriptor = inputStream.getFD();
    		bitmap = decodeSampleBitmapFromFileDescriptor(descriptor, reqWidth, reqHeight);
    		//硬盘加载成功后缓存到内存中去
    		if (bitmap != null) {
    		    addBitmapToMemoryCache(key, bitmap);
		    }
		}
    return diskLruCache.get(key);
}
```

```java
public static Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFileDescriptor(fd, null, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);//压缩
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFileDescriptor(fd, null, options);
}
```

需要注意的是：磁盘缓存涉及耗时操作，需要检验UI线程问题。

#### Configuration改变

屏幕方向发生改变的时候，Activity会重建，这个时候图片重新加载一遍吗？

当然不是，我会禁用屏幕旋转。

官方提供给`Fragment`的`setRetainInstance(true);`方法可以让内存缓存保持下来，再次填充页面速度几乎无变化。