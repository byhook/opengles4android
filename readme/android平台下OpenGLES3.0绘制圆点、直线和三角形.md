
## 目录

- [新建`SimpleRenderer`]()
- [定义圆点坐标]()
- [分配本地内存]()
- [顶点着色器]()
- [片段着色器]()
- [编译和加载着色器]()
- [设置视口]()
- [清除颜色缓冲区]()
- [绘制圆点]()
- [绘制直线]()
- [绘制三角形]()

#### 新建`SimpleRenderer`

```java
public class SimpleRenderer implements GLSurfaceView.Renderer
```

#### 定义圆点坐标

```java
private float[] vertexPoints = new float[]{
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
};
```

`理想状态下的屏幕坐标系`，我们定义的就是下图中的三角形的三个顶点。

![](https://github.com/byhook/opengles4android/blob/master/readme/images/2018110420590915.png)

#### 分配本地内存

因为`OpenGL`作为本地系统库运行在系统中，虚拟机需要分配本地内存，供其存取。

```java
public SimpleRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
}
```

#### 顶点着色器

```java
#version 300 es
layout (location = 0) in vec4 vPosition;
void main() {
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
}
```
上述顶点着色器的描述：
- 第一行表示：着色器的版本，OpenGL ES 2.0版本可以不写。
- 第二行表示：`输入属性的数组`(一个名为`vPosition`的4分量向量)，`layout (location = 0)`表示这个变量的位置是顶点属性0。
- 第三行表示：声明一个`main`函数。
- 第四行表示：它将`vPosition`输入属性拷贝到名为`gl_Position`的特殊输出变量。
- 第五行表示：它将浮点数据`10.0`拷贝到`gl_PointSize`的变量中。

#### 片段着色器
```java
#version 300 es
precision mediump float;
out vec4 fragColor;
void main() {
     fragColor = vec4(1.0,1.0,1.0,1.0);
}
```
上述片段着色器的描述：
- 第一行表示：着色器的版本，OpenGL ES 2.0版本可以不写。
- 第二行表示：声明着色器中浮点变量的默认精度。
- 第三行表示：着色器声明一个`输出变量fragColor`，这个是一个4分量的向量。
- 第五行表示：表示将颜色值`(1.0,1.0,1.0,1.0)`，输出到颜色缓冲区。

#### 编译和加载着色器

```java
	/**
     * 编译
     *
     * @param type  顶点着色器:GLES30.GL_VERTEX_SHADER
     *               片段着色器:GLES30.GL_FRAGMENT_SHADER
     * @param shaderCode
     * @return
     */
    private static int compileShader(int type, String shaderCode) {
    	//创建一个着色器
        final int shaderId = GLES30.glCreateShader(type);
        if (shaderId != 0) {
        	//加载到着色器
            GLES30.glShaderSource(shaderId, shaderCode);
            //编译着色器
            GLES30.glCompileShader(shaderId);
            //检测状态
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                String logInfo = GLES30.glGetShaderInfoLog(shaderId);
                System.err.println(logInfo);
                //创建失败
                GLES30.glDeleteShader(shaderId);
                return 0;
            }
            return shaderId;
        } else {
            //创建失败
            return 0;
        }
    }

```

#### 链接到着色器

```java
    /**
     * 链接小程序
     *
     * @param vertexShaderId 顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        if (programId != 0) {
            //将顶点着色器加入到程序
            GLES30.glAttachShader(programId, vertexShaderId);
            //将片元着色器加入到程序中
            GLES30.glAttachShader(programId, fragmentShaderId);
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
#### 设置视口
这一步通常在`onSurfaceChanged`中完成
```java
@Override
public void onSurfaceChanged(GL10 gl, int width, int height) {
     GLES30.glViewport(0, 0, width, height);
}
```
#### 清除颜色缓冲区

```java
GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
```
缓冲区将会用`GLES30.glClearColor`指定的颜色清除，清除的颜色被设置为`(0.5f, 0.5f, 0.5f, 0.5f)`，所以屏幕显示为灰色。

#### 绘制圆点

在`public void onDrawFrame(GL10 gl)`回调方法中：

```java
//准备坐标数据
GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
//启用顶点的句柄
GLES30.glEnableVertexAttribArray(0);
//绘制三个点
GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 3);

//禁止顶点数组的句柄
GLES30.glDisableVertexAttribArray(0);
```
刚才的顶点着色器已经将`vPosition`变量与输入属性位置`0`绑定了，顶点着色器中每个属性都由一个`无符号整数值唯一标识的位置`。

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181104212813161.png)

#### 绘制直线

```java
//绘制直线
GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 2);
GLES30.glLineWidth(10);
```

绘制如图所示：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181104215339364.png)

#### 绘制三角形

```java
//绘制三角形
GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 2);
```
![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181104215322927.png)

#### 常用图元类型

|图元类型|描述|
|:-|:-|
|GL_POINTS|点精灵图元，对指定的每个顶点进行绘制。|
|GL_LINES|绘制一系列不相连的线段。|
|GL_LINE_STRIP|绘制一系列相连的线段。|
|GL_LINE_LOOP|绘制一系列相连的线段，首尾相连。|
|GL_TRIANGLES|绘制一系列单独的三角形。|
|GL_TRIANGLE_STRIP|绘制一系列相互连接的三角形。|
|GL_TRIANGLE_FAN|绘制一系列相互连接的三角形。|

直线图元类型：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181104223035672.png)

三角形图元类型

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181104221645167.png)

项目地址：
https://github.com/byhook/opengles4android

参考：
《OpenGL ES 3.0 编程指南第2版》
《OpenGL ES应用开发实践指南Android卷》
