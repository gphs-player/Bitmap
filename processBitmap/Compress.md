####  准备工作

​	首先要确定待加载BitMap的尺寸和类型，`BitmapFactory`的`decodeXXX`系列方法可以解析不同的资源生成BitMap对象，但是也很容易OOM。每个decode方法会有一个额外的参数方法接收`BitmapFactory.Options`。如果把`inJustDecodeBounds`设置为true，那么decode方法就会返回null，但是BitMap的相关参数信息还是会加载。这就方便我们预处理图片然后再加载。

```java
private void releaseSource() {
    InputStream open = null;
    try {
        open = getResources().getAssets().open("world.jpg");
        readOriginBitmap(open);
    } catch (IOException e) {
    } finally {
        if (open != null) open.close();
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
```

#### 处理图片

​	知道原图参数之后，我们就能决定是对原图进行加载还是进行抽样加载了，但是还要考虑几个问题：

* 预估加载完整的图需要的内存

* 考虑应用的内存的整体使用情况，你愿意分配多少内存给到此图
* 目标View的尺寸
* 屏幕分辨率	

```java
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
```

#### 加载图片



```java
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
```

[官方文档](https://developer.android.com/topic/performance/graphics/load-bitmap)