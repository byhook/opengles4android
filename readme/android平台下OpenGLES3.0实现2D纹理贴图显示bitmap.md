
## 目录

- [2D纹理简介]()
- [模型变换和纹理坐标]()
- [纹理坐标和OpenGL坐标]()
- [纹理贴图显示bitmap实践]()
- [纹理过滤的介绍]()
- [正交投影调整显示效果]()

#### 2D纹理简介

2D纹理是OpenGL ES中最基本和常用的纹理形式。2D纹理本质上其实：`是一个图像数据的二维数组`。一个纹理的单独数据元素称作`"纹素(texel)"`。用2D纹理渲染时，纹理坐标用作纹理图像中的索引。2D纹理的纹理坐标用一对2D坐标`(s,t)`指定，有时也 称作`(u,v)`坐标。

﻿纹理图像的左下角由对坐标`(0.0, 0.0)`指定，右上角由对坐标`(1.0,1.0)`指定。在`[0.0,1.0]`区间之外的坐标是允许的，在该区间之外的纹理读取行为由纹理包装模式定义。

`OpenGL 2D纹理坐标：`

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110164009917.png)

`通过指定纹理坐标，可以映射到纹素。`例如一个256x256大小的二维纹理，坐标`(0.5,1.0)`对应的纹素即是`(256x0.5 = 128, 256x1.0 = 256)`。

纹理映射时只需要为物体的顶点指定纹理坐标即可，其余部分由片元着色器插值完成。

![](https://github.com/byhook/opengles4android/blob/master/readme/images/2018111016442284.png)

#### 模型变换和纹理坐标

模型变换，就是对物体进行缩放、旋转、平移等操作，后面会着重介绍。当对物体进行这些操作时，顶点对应的纹理坐标不会进行改变，通过插值后，物体的纹理也像紧跟着物体发生了变化一样。

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110175440780.png)  

经过旋转等变换后，物体和对应的纹理坐标如下图所示，可以看出上面图中纹理部分的房子也跟着发生了旋转。

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110175449693.png)

#### 纹理坐标和OpenGL坐标

由于对一个OpenGL纹理来说，它没有内在的方向性，因此我们可以使用不同的坐标把它定向到任何我们喜欢的方向上，然而大多数计算机图像都有一个默认的方向，它们通常被规定为`y轴向下`，y的值随着向图像的底部移动而增加。

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110183646999.png)

如果想用正确的方向观看图像，那纹理坐标就必须要考虑这点。

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110183239249.png)

可以对照这个坐标系查看本例代码中定义的坐标点：

`灰色为OpenGL坐标系中的顶点坐标`
`红色为纹理坐标系中的纹理坐标`

#### 纹理贴图显示bitmap实践

还是以一个示例来实践一下，基于之前的[项目工程](https://github.com/byhook/opengles4android)，添加一个新的工具类`TextureUtils.java`：

```java
/**
 * @anchor: andy
 * @date: 18-11-10
 */

public class TextureUtils {

    private static final String TAG = "TextureUtils";

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureIds = new int[1];
        //创建一个纹理对象
        GLES30.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //这里需要加载原图未经缩放的数据
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            Log.e(TAG, "Resource ID " + resourceId + " could not be decoded.");
            GLES30.glDeleteTextures(1, textureIds, 0);
            return 0;
        }
        // 绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);

		//设置默认的纹理过滤参数
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        // 加载bitmap到纹理中
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        // 生成MIP贴图
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        // 数据如果已经被加载进OpenGL,则可以回收该bitmap
        bitmap.recycle();

        // 取消绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textureIds[0];
    }

}
```

新建`TextureRenderer.java`文件：

```java
/**
 * @anchor: andy
 * @date: 2018-11-09
 * @description: 基于纹理贴图显示bitmap
 */
public class TextureRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "TextureRenderer";

    private final FloatBuffer vertexBuffer, mTexVertexBuffer;

    private final ShortBuffer mVertexIndexBuffer;

    private int mProgram;

    private int textureId;

        /**
     * 顶点坐标
     * (x,y,z)
     */
    private float[] POSITION_VERTEX = new float[]{
            0f, 0f, 0f,     //顶点坐标V0
            1f, 1f, 0f,     //顶点坐标V1
            -1f, 1f, 0f,    //顶点坐标V2
            -1f, -1f, 0f,   //顶点坐标V3
            1f, -1f, 0f     //顶点坐标V4
    };

    /**
     * 纹理坐标
     * (s,t)
     */
    private static final float[] TEX_VERTEX = {
            0.5f, 0.5f, //纹理坐标V0
            1f, 0f,     //纹理坐标V1
            0f, 0f,     //纹理坐标V2
            0f, 1.0f,   //纹理坐标V3
            1f, 1.0f    //纹理坐标V4
    };

    /**
     * 索引
     */
    private static final short[] VERTEX_INDEX = {
            0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
            0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
            0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
            0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
    };

    public TextureRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(POSITION_VERTEX);
        vertexBuffer.position(0);

        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEX_VERTEX);
        mTexVertexBuffer.position(0);

        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_texture_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_texture_shader));
        //链接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);

        //加载纹理
        textureId = TextureUtils.loadTexture(AppCore.getInstance().getContext(), R.drawable.main);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //使用程序片段
        GLES30.glUseProgram(mProgram);
		//启用顶点坐标属性
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
		//启用纹理坐标属性
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, mTexVertexBuffer);
		//激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

        // 绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

    }
}
```

`顶点着色器`

```java
#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;
//输出纹理坐标(s,t)
out vec2 vTexCoord;
void main() {
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vTexCoord = aTextureCoord;
}
```

`片段着色器`

```java
#version 300 es
precision mediump float;
uniform sampler2D uTextureUnit;
//接收刚才顶点着色器传入的纹理坐标(s,t)
in vec2 vTexCoord;
out vec4 vFragColor;
void main() {
     vFragColor = texture(uTextureUnit,vTexCoord);
}
```

注意：在`OpenGL ES 2.0`中这里的方法是`texture2D`

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110165328638.png)

#### 纹理过滤的介绍

﻿由于纹理坐标与分辨率无关，因此它们并不总是精确匹配像素。当纹理图像拉伸超过其原始大小或尺寸缩小时，会发生这种情况。当发生这种情况时，OpenGL提供了各种方法来决定采样颜色。此过程称为过滤，针对上述情况，我们可以配置OpenGL使用一个纹理过滤器。

- `GL_NEAREST`：返回最接近坐标的像素。
- `GL_LINEAR`：返回给定坐标周围4个像素的加权平均值。
- `GL_NEAREST_MIPMAP_NEAREST，GL_LINEAR_MIPMAP_NEAREST，
GL_NEAREST_MIPMAP_LINEAR，GL_LINEAR_MIPMAP_LINEAR`：从MIP贴图，而不是样品。

最近和线性插值的区别：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110172117581.png)


虽然线性插值可以提供更平滑的结果，但它并不总是最理想的选择。由于像素化的外观，最近邻插值更适合想要模仿8位图形的游戏。

可以指定哪种插值应用于两种不同的情况：缩小图像并向上缩放图像。这两个案例由关键字`GL_TEXTURE_MIN_FILTER和GL_TEXTURE_MAG_FILTER`。

```java
glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
```

当然了，还有另一种过滤纹理的方法：`mipmap`。`Mipmap`是纹理的较小副本，已预先调整大小并进行过滤。推荐使用它们，因为它们可以带来更高的质量和更高的性能。

```java
glGenerateMipmap(GL_TEXTURE_2D);
```

要使用mipmap，请选择四种mipmap过滤方法之一：

- `GL_NEAREST_MIPMAP_NEAREST`：使用最接近匹配纹理像素大小的mipmap和最近邻插值的样本。
- `GL_LINEAR_MIPMAP_NEAREST`：使用线性插值对最近的mipmap进行采样。
- `GL_NEAREST_MIPMAP_LINEAR`：使用与纹理像素大小最匹配的两个mipmap和最近邻插值的样本。
- `GL_LINEAR_MIPMAP_LINEAR`：采用线性插值的样本最接近两个mipmap。

#### 正交投影调整显示效果

刚才也看到最终的显示效果有种拉伸的感觉，现在使用正交投影来调整这个显示效果，修改一下`顶点着色器`，加入矩阵，可以参考之前的例子：[android平台下OpenGL ES 3.0从矩形中看矩阵和正交投影](https://blog.csdn.net/byhook/article/details/83759218)。

```java
#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;
//矩阵
uniform mat4 u_Matrix;
out vec2 vTexCoord;
void main() {
     gl_Position  = u_Matrix * vPosition;
     gl_PointSize = 10.0;
     vTexCoord = aTextureCoord;
}
```

在`onSurfaceChanged`方法中

```java
@Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            //横屏
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            //竖屏
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }

    }
```

在`onDrawFrame`回调中修改如下：

```java
@Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

		//正交投影矩阵
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);

        ......

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

        // 绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

    }
```

显示效果如下：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181110174836154.png)

项目地址：`opengles-texture`
https://github.com/byhook/opengles4android

参考：
https://open.gl/textures

http://www.opengl-tutorial.org/cn/beginners-tutorials/tutorial-5-a-textured-cube/

https://blog.piasy.com/2016/06/14/Open-gl-es-android-2-part-2/

《OpenGL ES 3.0 编程指南第2版》

《OpenGL ES应用开发实践指南Android卷》
