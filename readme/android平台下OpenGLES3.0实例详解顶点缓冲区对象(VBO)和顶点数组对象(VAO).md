
## 目录

- [顶点缓冲区对象(VBO)]()
- [顶点数组对象(VAO)]()
- [映射缓冲区对象]()

#### 顶点缓冲区对象(VBO)

`顶点缓冲区对象(Vertex Buffer Object)`，简称`VBO`。如果不使用`顶点缓冲区对象(VBO)`则是将顶点、颜色、纹理坐标等数据存放在内存(客户内存)当中，在每次进行`glDrawArxays`或者`gIDrawElements`等绘图调用时，必须从客户内存复制到图形内存。而`顶点缓冲区对象`使OpenGL ES 3.0应用程序可以在高性能的图形内存中分配和缓存顶点数据，并从这个内存进行渲染，从而避免在每次绘制图元的时候重新发送数据。不仅是顶点数据，描述图元顶点索引、作为`glDrawElements`参数传递的元素索引也可以缓存。


OpenGL ES 3.0支持两类缓冲区对象：`数组缓冲区对象`和`元素数组缓冲区对象`。

>`GL_ARRAY_BUFFER`标志指定的数组缓冲区对象用于创建保存顶点数据的缓冲区对象。
>`GL_ELEMENT_ARRAY_BUFFER`标志指定的元素数组缓冲区对象用于创建保存图元索引的缓冲区对象。
>OpenGL ES 3.0中的其他缓冲区对象类型：`统一变量缓冲区`、`变换反馈缓冲区`、`像素解包缓冲区`、 `像素包装缓冲区`和`复制缓冲区`。

`OpenGL ES 3.0`建议应用程序对顶点属性数据和元素索引使用`顶点缓冲区对象`。

基于之前的[工程项目](https://github.com/byhook/opengles4android)，新建`VertexBufferRenderer.java`文件：

```java
/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description: 顶点缓冲区
 */
public class VertexBufferRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "VertexBufferRenderer";

    private static final int VERTEX_POS_INDEX = 0;

    private final FloatBuffer vertexBuffer;

    private static final int VERTEX_POS_SIZE = 3;

    private static final int VERTEX_STRIDE = VERTEX_POS_SIZE * 4;

    private int mProgram;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    /**
     * 缓冲数组
     */
    private int[] vboIds = new int[1];

    public VertexBufferRenderer() {
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
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_buffer_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_buffer_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);

        //1. 生成1个缓冲ID
        GLES30.glGenBuffers(1, vboIds, 0);

        //2. 绑定到顶点坐标数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
        //3. 向顶点坐标数据缓冲送入数据
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexPoints.length * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        //4. 将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(VERTEX_POS_INDEX, VERTEX_POS_SIZE, GLES30.GL_FLOAT, false, VERTEX_STRIDE, 0);
        //5. 启用顶点位置属性
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDEX);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //6. 使用程序片段
        GLES30.glUseProgram(mProgram);

        //7. 开始绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //8. 解绑VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }
}
```

`顶点着色器`：

```java
#version 300 es
layout (location = 0) in vec4 vPosition;
out vec4 vColor;
void main() {
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vColor = vec4(0.2f,0.4f,0.6f,1.0f);
}
```

`片段着色器`：

```java
#version 300 es
precision mediump float;
in vec4 vColor;
out vec4 fragColor;
void main() {
     fragColor = vColor;
}
```

运行输出：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181108125551557.png)

```java
GLES30.glDeleteBuffers
```

#### 顶点数组对象(VAO)

加载顶点属性的两种不同方式：`使用客户顶点数组和使用顶点缓冲区对象`。

顶点缓冲区对象优于客户顶点数组，因为它们能够减少CPU和GPU之间复制的数据量，从而获得更好的性能。在`OpenGL ES 3.0`中引人了一个新特性，使顶点数组的使用更加高效：`顶点数组对象(VAO)`。

使用`顶点缓冲区对象`设置绘图操作可能需要多次调用`glBindBuffer`、`glVertexAttribPointer`和 `glEnableVertexAttribArray`。为了更快地在顶点数组配置之间切换，`OpenGL ES 3.0推出了顶点数组对象`。`VAO`提供包含在 顶点数组/顶点缓冲区对象配置之间切换所需要的所有状态的单一对象。

OpenGLES 3.0中总是有一个活动的顶点数组对象。之前的一些实例` 默认都在顶点数组对象上操作(默认VAO的ID为0)`。要创建新的顶点数组对象，可以使用`glGenVertexArrays`方法。

还是基于之前的[工程项目](https://github.com/byhook/opengles4android)，新建`VertexArrayRenderer.java`文件：

```java
/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class VertexArrayRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "VertexBufferRenderer";

    private static final int VERTEX_POS_INDEX = 0;

    private final FloatBuffer vertexBuffer;

    private static final int VERTEX_POS_SIZE = 3;

    private static final int VERTEX_STRIDE = VERTEX_POS_SIZE * 4;

    private int mProgram;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    /**
     * 缓冲数组
     */
    private int[] vaoIds = new int[1];

    private int[] vboIds = new int[1];

    public VertexArrayRenderer() {
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
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_array_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_array_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

        //生成1个缓冲ID
        GLES30.glGenVertexArrays(1, vaoIds, 0);

        //绑定VAO
        GLES30.glBindVertexArray(vaoIds[0]);

        //1. 生成1个缓冲ID
        GLES30.glGenBuffers(1, vboIds, 0);
        //2. 向顶点坐标数据缓冲送入数据把顶点数组复制到缓冲中
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexPoints.length * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        //3. 将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(VERTEX_POS_INDEX, VERTEX_POS_SIZE, GLES30.GL_FLOAT, false, VERTEX_STRIDE, 0);
        //启用顶点位置属性
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDEX);

        //4. 解绑VAO
        GLES30.glBindVertexArray(0);
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

        //5. 绑定VAO
        GLES30.glBindVertexArray(vaoIds[0]);

        //6. 开始绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //7. 解绑VAO
        GLES30.glBindVertexArray(0);

    }
}
```

当应用程序结束一个或者多个顶点数组对象的使用时，可以删除它们。

```java
void glDeleteVeitexAnays();
```

#### 映射缓冲区对象

上面的两个例子基本已经了解了`glBufferData`的使用，应用程序也可以将缓冲区对象数据存储映射到应用程序的地址空间(也可以解除映射)。应用程序映射缓冲区而不使用`glBufferData或者glBufferSubData`加载数据的原因在于：

- 映射缓冲区可以减少应用程序的内存占用，因为可能只需要存储数据的一个副本。
- 在使用共享内存的架构上，映射缓冲区返回GPU存储缓冲区的地址空间的直接指针。

通过映射缓冲区，应用程序可以避免复制步骤，从而实现更好的更新性能。 `glMapBufferRange`方法返回指向所有或者一部分(范围)缓冲区对象数据存储的`Buffer`。 这个`Buffer`可以供应用程序使用，以读取或者更新缓冲区对象的内容。

```java
Buffer GLES30.glMapBufferRange(int target,int offset,int length,int access);
```

|访问标志|描述|
|:-|:-|
|GL_MAP_READ_BIT|应用程序将从返回的指针读取|
|GL_MAP_WRITE_BIT|应用程序将写人返回的指针|

此外，应用程序可以包含如下可选访问标志:

|访问标志|描述|
|:-|:-|
|GL_MAP_INVALIDATE_RANGE_BIT|表示指定范围内的缓冲区内容可以在返回指针之前由驱动程序放弃。这个标志不能与`GL_MAP_READ_BIT`组合使用|
|GL_MAP_INVALIDATE_BUFFER_BIT|表示整个缓冲区的内容可以在返回指针之前由驱动程序放弃。这个标志不能与`GL_MAP_READ_BIT`组合使用|
|GL_MAP_FLUSH_EXPLICIT_BIT|表示应用程序将明确地用`glFlushMappedBufferRange`刷新对映射范围子范围的操作。这个标志不能与`GL_MAP_WRITE_BIT`组合使用|
|GL一MAP一UNSYNCHRONIZED一BIT|表示驱动程序在返回缓冲区范围的指针之前不需要等待缓冲对象上的未决操作。如果有未决的操作，则未决操作的结果和缓冲区对象上的任何未来操作都变为未定义|

基于SDK中的`glMapBufferRange`返回的实际上是`java.nio.Buffer`对象，这个对象可以直接操作本地内存。

还是基于之前的[工程项目](https://github.com/byhook/opengles4android)，新建`MapBufferRenderer.java`文件：

```java
/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description: 映射缓冲区对象
 */
public class MapBufferRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "VertexBufferRenderer";

    private static final int VERTEX_POS_INDEX = 0;

    private static final int VERTEX_POS_SIZE = 3;

    private static final int VERTEX_STRIDE = VERTEX_POS_SIZE * 4;

    private int mProgram;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private int[] vboIds = new int[1];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_map_buffer_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_map_buffer_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);

        //1. 生成1个缓冲ID
        GLES30.glGenBuffers(1, vboIds, 0);
        //2. 向顶点坐标数据缓冲送入数据把顶点数组复制到缓冲中
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexPoints.length * 4, null, GLES30.GL_STATIC_DRAW);

        //3. 映射缓冲区对象
        ByteBuffer buffer = (ByteBuffer) GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER, 0, vertexPoints.length * 4, GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT);
        //4. 填充数据
        buffer.order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexPoints).position(0);

        //5. 将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(VERTEX_POS_INDEX, VERTEX_POS_SIZE, GLES30.GL_FLOAT, false, VERTEX_STRIDE, 0);
        //启用顶点位置属性
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDEX);

        //解除映射
        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);
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

        //6. 开始绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
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
     vColor = vec4(0.1f,0.2f,0.3f,1.0f);
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

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181108145715297.png)

取消映射`glUnmapBuffer`方法如下：

```java
//target	必须设置为 GL_ARRAY_BUFFER
boolean glUnmapBuffer(int target);
```

如果取消映射操作成功，则`glUnmapBuffer`glUnmapBuffer返回`true`，`glMapBufferRange`返回的`Buffer`在成功执行取消映射之后不再可以使用，如果顶点缓冲区对象数据存储中的数据在缓冲区映射之后已经破坏，`glUnmapBuffer`将返回`false`，这可能是因为屏幕分辨率的变化，OpenGL ES上下文使用多个屏幕或者导致映射内存被抛弃的内存不足事件所导致。

项目地址：
https://github.com/byhook/opengles4android

参考：

《OpenGL ES 3.0 编程指南第2版》

《OpenGL ES应用开发实践指南Android卷》
