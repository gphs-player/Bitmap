## Drawable加载细节



> ####  在res下存在多个drawable文件目录，一张图片放在哪个目录下最合适呢？


#### 测试过程
在`drawable`目录下（没有放在xxdpi目录）存放一张800*600的图片，然后通过`BitmapFactory.decodeResource(getResources(), R.drawable.test)`进行加载并查看结果。

<image src="./src/pic/load_default.jpg" width=300/>

能看到的结果是：一张800\*600的图片加载到bitmap之后分辨率成了2400\*1800, 内存占用直接超过了17M，niubility！
从图片最终的宽高来看，它似乎被放大了3倍，为什么会放大？为什么是3倍？不知道。

再把图片换个目录试试看，从左到右依次是 `hdpi` `xhdpi` `xxhdpi`


<image src="./src/pic/load_hdpi.jpg" width=30%/>
<image src="./src/pic/load_xhdpi.jpg" width=30%/>
<image src="./src/pic/load_xxhdpi.jpg" width=30%/>


貌似在`xxhdpi`目录下图片没有被缩放，是正常显示的。然后看一个表格


|dpi范围 | 密度 |
|---|---|
| 0dpi ~ 120dpi | ldpi |
| 120dpi ~ 160dpi |	mdpi |
| 160dpi ~ 240dpi |	hdpi |
| 240dpi ~ 320dpi |	xhdpi |
| 320dpi ~ 480dpi |	xxhdpi |
| 480dpi ~ 640dpi |	xxxhdpi |


#### 结论

这就是官方推荐的适配方案，适配某个dpi的手机，图片资源就建议放在相对应的密度目录下。比如我的手机dpi是480，那么图片就要放在xxdpi目录下，这样图片才不会被缩放。


#### 再回头看图片为什么会缩放？

假设在10000像素的手机上显示5000像素的图片，正好能占据一半的位置，但是这张图在不缩放的前提下要显示在20000像素的手机上，就只能占据1/4的位置，那么将其放大2倍就能在20000像素的手机上达成适配的效果。

那么官方的做法是怎样的呢？

Java层BitmapFactory.decodeResource方法最终都是调用native层的nativeDecodeStream方法，具体可以查看[BitmapFactory.cpp](https://android.googlesource.com/platform/frameworks/base/+/android-8.0.0_r34/core/jni/android/graphics/BitmapFactory.cpp)，其中有一段代码是：


```
if (env->GetBooleanField(options, gOptions_scaledFieldID)) {
            const int density = env->GetIntField(options, gOptions_densityFieldID);
            const int targetDensity = env->GetIntField(options, gOptions_targetDensityFieldID);
            const int screenDensity = env->GetIntField(options, gOptions_screenDensityFieldID);
            if (density != 0 && targetDensity != 0 && density != screenDensity) {
                scale = (float) targetDensity / density;//在这里做缩放操作
            }
        }
```

代码里缩放的两个参数是从options中取出，在Java层涉及的就是`BitmapFactory.Options`,两个参数分别是
`options.inDensity`和`options.inTargetDensity`,.

`inTargetDensity`代表的是你手机的密度比，`inDensity`代表图片所在目录下的密度比。如果二者匹配，图片将不会缩放，否则根据实际情况进行缩放。

####  结果

看完上面的步骤，关于BitMap优化可以得到3点...


> 使用低色彩的解析模式，如RGB565，减少单个像素的字节大小
> 
> 资源文件合理放置，高分辨率图片可以放到高分辨率目录下
> 
> 图片缩小，减少尺寸
