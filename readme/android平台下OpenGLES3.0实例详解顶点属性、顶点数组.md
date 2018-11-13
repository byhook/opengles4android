
## 目录

- [顶点属性]()
- [常量顶点属性]()
- [顶点数组]()
- [性能提示]()
- [在常置顶点属性和顶点数组之间选择]()
- [顶点着色器中声明顶点属性]()
- [将顶点属性绑定到顶点着色器中的属性变量]()

#### 顶点属性

顶点数据也称作顶点属性，指定每个顶点的数据。如果你想要绘制`固定颜色`的三角形，可以指定一个常量值，用于三角形的全部3个顶点。但是，组成三角形的3个顶点的位置不同，所以我们指定一个顶点数组来存储3个位置值。

顶点属性数据可以用一个顶点数组对每个顶点指定，也可以将一个常量值用于一个图元的所有顶点。
所有`OpenGL ES 3.0实现必须支持最少16个顶点属性`。

査询`OpenGL ES 3.0`实现真正支持的顶点属性的准确数：

```java
int[] maxVertexAttribs = new int[1];
GLES30.glGetIntegerv(GLES30.GL_MAX_VERTEX_ATTRIBS, maxVertexAttribs, 0);
```
#### 常量顶点属性

`常量顶点属性`对于一个图元的所有顶点都相同，所以对一个图元的所有顶点只需指定一个值。可以用如下任何一个函数指定：

```java
void GLES30.glVertexAttrib1f(int index, float x);
void GLES30.glVertexAttrib2£(int index, float x, float y);
void GLES30.glVertexAttrib3£(int index, float x, float y, float z);
void GLES30.glVertexAttrib4£(int index, float x, float y, float z, float w);
void GLES30.glVertexAttrib1fv (int index, float[] values,int offset);
void GLES30.glVertexAttrib2£v(int index, float[] values,int offset);
void GLES30.glVertexAttrib3£v(int index, float[] values,int offset);
void GLES30.glVertexAttrib4£v(int index, float[] values,int offset);
```

其中`glVertexAttrib*`方法用于加载`index`指定的通用顶点属性。
- `glVertexAttrib1f和glVertexAttrib1fv`在通用顶点属性中加载`(x, 0.0, 0.0, 1.0)`。
- `glVertexAttrib2f和glVertexAttrib2fv`在通用顶点属性中加载`(x, y, 0.0, 1.0)`。
- `glVertexAttrib3f和glVertexAttrib3fV`在通用顶点属性中加载`(x, y, z, 1.0)`。
- `glVertexAltrib4f和glVertexAttrib4fv`在通用顶点属性中加载`(x, y, z, w)`。

#### 顶点数组

顶点数组指定每个顶点的属性，是保存在应用程序地址空间(`OpenGLES称为客户空间`)的缓冲区。它们作为顶点缓冲对象的基础，提供指定顶点属性数据的一种高效、灵活的手段，`顶点数组用glVertexAttribPointer或glVertexAttribIPointer指定。`

分配和存储顶点属性数据有两种常用的方法：

- 在一个缓冲区中存储顶点属性：这种方法称为`结构数组`。结构表示顶点的所有属性，每个顶点有一个属性的数组。
- 在单独的缓冲区中保存每个顶点属性：这种方法称为`数组结构`。

这里要注意的是`glVertexAttribPointer`的倒数第二个参数`stride`

>﻿每个顶点由`size`指定的顶点属性分量顺序存储。stride指定顶点索引 I 和(I+1)表示的顶点数据之间的位移。如果`stride`为0，则每个顶点的属性数据顺序存储。如果`stride`大于0，则使用该值作为获取下一个索引表示的顶点数据的跨距。

现在假定每个顶点有5个顶点属性：`位置和颜色`，这些属性一起保存在为所有顶点分配的一个缓冲区中。`顶点位置`属性以`2个浮点数`的向量的形式指定，`顶点颜色`以`3个浮点数`向量的形式指定。

这个缓冲区的内存布局如下图所示：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181108124712983.png)

缓冲区的`跨距(STRIDE)`为组成顶点的所有属性总大小(8个字节用于位置，12个字节用于颜色)，等于5个浮点数即：`20个字节`

下面来实践一下用`glVertexAttribPointer`指定`顶点属性`。

还是基于之前的[工程项目](https://github.com/byhook/opengles4android)，新建`VertexPointerRenderer.java`文件

```java
/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class VertexPointerRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer;

    private int mProgram;

    /**
     * 位置顶点属性的大小
     * 包含(x,y)
     */
    private static final int VERTEX_POSITION_SIZE = 2;

    /**
     * 颜色顶点属性的大小
     * 包含(r,g,b)
     */
    private static final int VERTEX_COLOR_SIZE = 3;

    /**
     * 浮点型数据占用字节数
     */
    private static final int BYTES_PER_FLOAT = 4;

    /**
     * 跨距
     */
    private static final int VERTEX_ATTRIBUTES_SIZE = (VERTEX_POSITION_SIZE + VERTEX_COLOR_SIZE) * BYTES_PER_FLOAT;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            //前两个为坐标,后三个为颜色
            0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            //两个点的顶点属性
            0.0f, 0.25f, 0.5f, 0.5f, 0.5f,
            0.0f, -0.25f, 0.5f, 0.5f, 0.5f,
    };

    public VertexPointerRenderer() {
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
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_pointer_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_pointer_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(0, VERTEX_POSITION_SIZE, GLES30.GL_FLOAT, false, VERTEX_ATTRIBUTES_SIZE, vertexBuffer);
        //启用顶点属性
        GLES30.glEnableVertexAttribArray(0);
        //定位本地内存的位置
        vertexBuffer.position(VERTEX_POSITION_SIZE);
        GLES30.glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, false, VERTEX_ATTRIBUTES_SIZE, vertexBuffer);

        GLES30.glEnableVertexAttribArray(1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 6);

        //绘制两个点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 6, 2);

    }
}
```

`顶点着色器`

```java
#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;
out vec4 vColor;
void main() {
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vColor = aColor;
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


>注意：`OpenGLES 3.0`建议应用程序使用顶点缓冲区对象，避免使用客户端顶点数组，以实现最佳性能，在OpenGLES3.0中，总是建议使用顶点缓冲区对象。


#### 性能提示

OpenGLES 3.0硬件实现，在大部分情况下，答案是结构数组。因为每个顶点的属性数据可以顺序方式读取，这最有可能造成高效的内存访问模式。使用结构数组的缺点在应用程序需要修改特定属性时变得很明显。如果顶点属性数据的一个子集需要修改（例如：纹理坐标)，这将造成顶点缓冲区的跨距更新。当顶点缓冲区以缓冲区对象的形式提供时，需要重新加载整个顶点属性缓冲区。可以通过将动态的顶点属性保存在单独的 缓冲区来避免这种效率低下的情况。


#### 在常置顶点属性和顶点数组之间选择

应用程序可以让`OpenGL ES`使用常量数据或者来自顶点数组的数据，`glEnableVertexAttribArray`和`glDisableVertexAttribArray`方法分别用于`启用和禁用`通用顶点属性数组。`如果某个通用属性索引的顶点属性数组被禁用，将使用为该索引指定的常量顶点属性数据。`

选择常量或者顶点数组顶点属性：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/2018110812474381.png)

还是基于之前的[工程项目](https://github.com/byhook/opengles4android)，新建`EnableVertexRenderer.java`文件

```java
/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class EnableVertexRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer, colorBuffer;

    private int mProgram;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private float[] vertexColors = new float[]{
            0.5f, 0.5f, 0.8f, 1.0f
    };

    public EnableVertexRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
        //为颜色数据分配本地内存空间
        colorBuffer = ByteBuffer.allocateDirect(vertexColors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        colorBuffer.put(vertexColors);
        colorBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_enable_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_enable_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //颜色数据都是一致的
        GLES30.glVertexAttrib4fv(1, colorBuffer);

        //获取位置的顶点数组
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        //启用位置顶点属性
        GLES30.glEnableVertexAttribArray(0);

        //绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //禁用顶点属性
        GLES30.glDisableVertexAttribArray(0);
    }
}
```

`顶点着色器`

```java
#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;
out vec4 vColor;
void main() {
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vColor = aColor;
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

上面的注释已经很清楚了，顶点的颜色是一个`常量值`，直接使用`glVertexAttrib4fv`指定，`不启用顶点属性数组1`，而位置属性通过`glVertexAttribPointer`获取，并且通过`glEnableVertexAttribArray`启用，用来处理不同的位置坐标。


#### 顶点着色器中声明顶点属性

在`OpenGL ES 3.0`顶点着色器中，变量通过使用`in限定符`声明为顶点属性。`属性变量也可以选择包含一个布局限定符，提供属性索引`。

```java
layout(location = 0) in vec4 apposition;
layout(location = 1) in vec2 a_texcoord;
layout(location = 2) in vec3 a_normal;
```
`in限定符`只能用于数据类型`float、vec2、vec3、vec4、int、ivec2、ivec3、ivec4、uint、 uvec2、uvec3、uvec4、mat2、mat2x2、mat2x3、mat2x4、mat3、mat3x3、mat3x4、mat4、 mat4x2和mat4x3`。属性变量不能声明为数组或者结构。

`OpenGL ES 3.0`实现支持`GL_MAX_VERTEX_ATTRIBS`四分量向量顶点属性。声明为`标量、二分量向量或者三分量向量`的顶点属性将被当作一个`四分量向量`属性计算。声明为`二维、三维或者四维矩阵`的顶点属性将分别被作为2、3或者4个四分量向量属性计算。与编译器自动打包的统一变量及顶点着色器输出/片段着色器输入变量不同，属性不进行打包。在用小于四分量向量的尺寸声明顶点属性时请小心考虑，因为可用的最少顶点属性是有限的资源。将它们一起打包到单个四分量属性可能比在顶点着色器中声明为单个顶点属性更好。

>注意：`在顶点着色器中声明为顶点属性的变量是只读变量，不能修改`。属性可以在顶点着色器内部声明，但是如果没有使用，就不会被认为是活动属性，从而不会被计入限制。如果在顶点着色器中使用的属性数量大于`GL_MAX_VERTEX_ ATTRIBS`，这个顶点着色器就无法链接。

#### 将顶点属性绑定到顶点着色器中的属性变量

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181108124632149.png)

顶点属性索引绑映射到顶点着色器中的一个属性变量名称，有三种方法：

- 索引可以在顶点着色器源代码中用`layout (location = N)`限定符指定，`推荐使用这种方式`：
  　 将属性绑定到一个位置的最简单方法是简单地使用`layout(location = N)`限定符，这种方法需要的代码最少。
- OpenGL ES 3.0将通用顶点属性索引绑定到属性名称。
- 应用程序将顶点属性索引绑定到属性名称。

`glBindAttribLocation`方法可用于将通用顶点属性索引绑定到顶点着色器中的一个属性变量。这种绑定在下一次程序链接时生效，不会改变当前链接的程序中使用的绑定。` 这个方法要在link program程序之前调用。`

```java
void GLES30.glBindAttribLocation(int program,int index,String name);
```

如果之前绑定了`name`，则它所指定的绑定被索引代替。`glBindAttribLocation`甚至可以在顶点着色器连接到程序对象之前调用，因此，这个调用可以用于绑定任何属性名称。不存在的属性名称或者在连接到程序对象的顶点着色器中不活动的属性将被忽略。

```java
public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        if (programId != 0) {
            //将顶点着色器加入到程序
            GLES30.glAttachShader(programId, vertexShaderId);
            //将片元着色器加入到程序中
            GLES30.glAttachShader(programId, fragmentShaderId);

			//绑定自定义的属性索引
            GLES30.glBindAttribLocation(programId, 5, "aColor");

            //链接着色器程序
            GLES30.glLinkProgram(programId);
            final int[] linkStatus = new int[1];

            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                String logInfo = GLES30.glGetProgramInfoLog(programId);
                System.err.println(logInfo);
                GLES30.glDeleteProgram(programId);
                return 0;
            }
            return programId;
        } else {
            //创建失败
            return 0;
        }
    }
```

在链接阶段，OpenGL ES 3.0实现为每个属性变量执行如下操作：
>对于每个属性变量，检查是否已经通过`glBindAttribLocation`指定了绑定。如果指定了一个绑定，则使用指定的对应属性索引。否则，OpenGL ES实现将分配一个通用顶点属性索引。

```java
int GLES30.glGetAttribLocation(int program,String name);
```

`glGetAttribLocation`根据属性名称返回`属性索引`。

```java
11-08 12:38:49.534 32752-640/com.onzhou.opengles.shader D/EnableVertexRenderer: color location = 5
```

项目地址：
https://github.com/byhook/opengles4android

参考：

《OpenGL ES 3.0 编程指南第2版》

《OpenGL ES应用开发实践指南Android卷》
