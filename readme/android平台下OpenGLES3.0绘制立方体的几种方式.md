
## 目录

- [绘制图元的几个方法]()
- [基于线段的方式]()
- [基于顶点法绘制立方体]()
- [基于索引法绘制立方体]()

#### 绘制图元的几个方法

`OpenGL ES`中有5个绘制图元的API调用：

>`glDrawArrays、gIDrawElements、glDrawRangeHonents、 glDrawArraysInstanced和glDrawElementsInstanced`。

 `glDrawArrays`用元素索引为`first`到`first+count-1`的元素指定的顶点绘制mode指定的图元。

>调用`glDrawArrays(GL_TRIANGLES, 0, 6)`将绘制两个三角形：一个三角形由元素索引`(0, 1, 2)`指定，另一个三角形由元素索引`(3, 4, 5)`指定。
调用`glDrawArrays(GL_TRIANGLE_STRIP，0, 5)`将绘制3个三角形：一个由元素索引`(0, 1, 2)`指定，第二个三角形由元素索引`(2, 1, 3)`指定，最后一个三角形由元素索引`(2, 3, 4)`指定。

#### 基于线段的方式

基于之前的[工程项目](https://github.com/byhook/opengles4android)，新建`LineCubeRenderer.java`类

```java
/**
 * @anchor: andy
 * @date: 2018-11-09
 * @description:
 */
public class LineCubeRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer;

    private int mProgram;

    private static final int POSITION_COMPONENT_COUNT = 3;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.25f, 0.25f, 0.0f,  //V0
            -0.75f, 0.25f, 0.0f, //V1
            -0.75f, -0.75f, 0.0f, //V2
            0.25f, -0.75f, 0.0f, //V3

            0.75f, -0.25f, 0.0f, //V4
            0.75f, 0.75f, 0.0f, //V5
            -0.25f, 0.75f, 0.0f, //V6
            -0.25f, -0.25f, 0.0f, //V7

            -0.25f, 0.75f, 0.0f, //V6
            -0.75f, 0.25f, 0.0f, //V1

            0.75f, 0.75f, 0.0f, //V5
            0.25f, 0.25f, 0.0f, //V0

            -0.25f, -0.25f, 0.0f, //V7
            -0.75f, -0.75f, 0.0f, //V2

            0.75f, -0.25f, 0.0f, //V4
            0.25f, -0.75f, 0.0f //V3
    };

    public LineCubeRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_linecube_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_linecube_shader));
        //链接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

        GLES30.glVertexAttribPointer(0, POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        GLES30.glEnableVertexAttribArray(0);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //指定线宽
        GLES30.glLineWidth(5);

        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 4);

        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 4, 4);

        GLES30.glDrawArrays(GLES30.GL_LINES, 8, 8);

    }
}
```

`顶点着色器`

```java
#version 300 es
layout (location = 0) in vec4 vPosition;
out vec4 vColor;
void main() {
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vColor = vec4(0.8,0.8,0.8,1.0);
}
```

`片段着色器`

```java
#version 300 es
precision mediump float;
in vec4 vColor;
out vec4 fragColor;
void main() {
     fragColor = vColor;
}
```

输出如下：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181109171605263.png)

上面的坐标点注释已经很清楚了，可以对照下面这张图来理解对应的坐标

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181109133849605.png)

#### 基于顶点法绘制立方体

```java
/**
 * @anchor: andy
 * @date: 2018-11-09
 * @description:
 */
public class ColorCubeRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer, colorBuffer;

    private int mProgram;

    private static final int VERTEX_POSITION_SIZE = 3;

    private static final int VERTEX_COLOR_SIZE = 4;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            //背面矩形
            0.75f, 0.75f, 0.0f, //V5
            -0.25f, 0.75f, 0.0f, //V6
            -0.25f, -0.25f, 0.0f, //V7
            0.75f, 0.75f, 0.0f, //V5
            -0.25f, -0.25f, 0.0f, //V7
            0.75f, -0.25f, 0.0f, //V4

            //左侧矩形
            -0.25f, 0.75f, 0.0f, //V6
            -0.75f, 0.25f, 0.0f, //V1
            -0.75f, -0.75f, 0.0f, //V2
            -0.25f, 0.75f, 0.0f, //V6
            -0.75f, -0.75f, 0.0f, //V2
            -0.25f, -0.25f, 0.0f, //V7

            //底部矩形
            0.75f, -0.25f, 0.0f, //V4
            -0.25f, -0.25f, 0.0f, //V7
            -0.75f, -0.75f, 0.0f, //V2
            0.75f, -0.25f, 0.0f, //V4
            -0.75f, -0.75f, 0.0f, //V2
            0.25f, -0.75f, 0.0f, //V3

            //正面矩形
            0.25f, 0.25f, 0.0f,  //V0
            -0.75f, 0.25f, 0.0f, //V1
            -0.75f, -0.75f, 0.0f, //V2
            0.25f, 0.25f, 0.0f,  //V0
            -0.75f, -0.75f, 0.0f, //V2
            0.25f, -0.75f, 0.0f, //V3

            //右侧矩形
            0.75f, 0.75f, 0.0f, //V5
            0.25f, 0.25f, 0.0f, //V0
            0.25f, -0.75f, 0.0f, //V3
            0.75f, 0.75f, 0.0f, //V5
            0.25f, -0.75f, 0.0f, //V3
            0.75f, -0.25f, 0.0f, //V4

            //顶部矩形
            0.75f, 0.75f, 0.0f, //V5
            -0.25f, 0.75f, 0.0f, //V6
            -0.75f, 0.25f, 0.0f, //V1
            0.75f, 0.75f, 0.0f, //V5
            -0.75f, 0.25f, 0.0f, //V1
            0.25f, 0.25f, 0.0f  //V0
    };


    //立方体的顶点颜色
    private float[] colors = {
            //背面矩形颜色
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,

            //左侧矩形颜色
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,

            //底部矩形颜色
            1f, 0f, 0.5f, 1f,
            1f, 0f, 0.5f, 1f,
            1f, 0f, 0.5f, 1f,
            1f, 0f, 0.5f, 1f,
            1f, 0f, 0.5f, 1f,
            1f, 0f, 0.5f, 1f,

            //正面矩形颜色
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,
            0.2f, 0.3f, 0.2f, 1f,

            //右侧矩形颜色
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,
            0.1f, 0.2f, 0.3f, 1f,

            //顶部矩形颜色
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f,
            0.3f, 0.4f, 0.5f, 1f
    };

    public ColorCubeRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        colorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的数据
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_colorcube_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_colorcube_shader));
        //链接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

        GLES30.glVertexAttribPointer(0, VERTEX_POSITION_SIZE, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        //启用位置顶点属性
        GLES30.glEnableVertexAttribArray(0);


        GLES30.glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, false, 0, colorBuffer);
        //启用颜色顶点属性
        GLES30.glEnableVertexAttribArray(1);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);

    }
}
```

输出显示如下：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181109133909448.png)

聪明的你一定发现了，其实绘制立方体我们只需要绘制外层的三个面就可以了，去掉`顶点位置和颜色属性`中的，`背面，左侧以及底部相关的属性`，只保留最外层的三个可见面，运行试试看，结果也是一样

#### 基于索引法绘制立方体

```java
/**
 * @anchor: andy
 * @date: 2018-11-09
 * @description: 基于索引法绘制立方体
 */
public class IndicesCubeRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer, colorBuffer;

    private final ShortBuffer indicesBuffer;

    private int mProgram;

    private static final int VERTEX_POSITION_SIZE = 3;

    private static final int VERTEX_COLOR_SIZE = 4;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            //正面矩形
            0.25f, 0.25f, 0.0f,  //V0
            -0.75f, 0.25f, 0.0f, //V1
            -0.75f, -0.75f, 0.0f, //V2
            0.25f, -0.75f, 0.0f, //V3

            //背面矩形
            0.75f, -0.25f, 0.0f, //V4
            0.75f, 0.75f, 0.0f, //V5
            -0.25f, 0.75f, 0.0f, //V6
            -0.25f, -0.25f, 0.0f //V7
    };

    /**
     * 定义索引
     */
    private short[] indices = {
            //背面
            5, 6, 7, 5, 7, 4,
            //左侧
            6, 1, 2, 6, 2, 7,
            //底部
            4, 7, 2, 4, 2, 3,
            //顶面
            5, 6, 7, 5, 7, 4,
            //右侧
            5, 0, 3, 5, 3, 4,
            //正面
            0, 1, 2, 0, 2, 3
    };

    //立方体的顶点颜色
    private float[] colors = {
            0.3f, 0.4f, 0.5f, 1f,   //V0
            0.3f, 0.4f, 0.5f, 1f,   //V1
            0.3f, 0.4f, 0.5f, 1f,   //V2
            0.3f, 0.4f, 0.5f, 1f,   //V3
            0.6f, 0.5f, 0.4f, 1f,   //V4
            0.6f, 0.5f, 0.4f, 1f,   //V5
            0.6f, 0.5f, 0.4f, 1f,   //V6
            0.6f, 0.5f, 0.4f, 1f    //V7
    };

    public IndicesCubeRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        colorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的数据
        colorBuffer.put(colors);
        colorBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        indicesBuffer = ByteBuffer.allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的数据
        indicesBuffer.put(indices);
        indicesBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_colorcube_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_colorcube_shader));
        //链接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

        GLES30.glVertexAttribPointer(0, VERTEX_POSITION_SIZE, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        //启用位置顶点属性
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, false, 0, colorBuffer);
        //启用颜色顶点属性
        GLES30.glEnableVertexAttribArray(1);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indicesBuffer);

    }
}
```

输出如下：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181109133926509.png)

如果我们采用`glDrawArrays`的方式，需要给这个立方体指定很多的顶点，而使用`glDrawElements`方法绘制的时候，我们只需要指定8个顶点即可，因此当涉及到顶点共享的时候，应该尽可能的使用`glDrawElements`方法完成绘制。

项目地址：
https://github.com/byhook/opengles4android

参考：

《OpenGL ES 3.0 编程指南第2版》

《OpenGL ES应用开发实践指南Android卷》
