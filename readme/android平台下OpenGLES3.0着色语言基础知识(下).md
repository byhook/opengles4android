
本篇整理自《OpenGL ES 3.0 编程指南第2版》

## 目录

- [统一变量和属性]()
- [获取和设置统一变量]()
- [统一变量块]()
- [顶点和片段着色器输入/输出]()
- [插值限定符]()
- [统一变量和插值器打包]()
- [精度限定符]()
- [不变性]()

#### 统一变量和属性

统一变量(`uniform`)是存储应用程序通过`OpenGL ES 3.0 API`传递给着色器的只读常数值的变量。
`统一变量`被组合成两类统一变量块。

- 第一类是命名统一变量块，统一变量的值由所谓的统一变量缓冲区对象支持，命名统一变量块被分配一个`统一变量块索引`。

```java
uniform TransformBlock {
    mat4 matViewProj;
    mat3 matNormal;
    mat3 matTexGen;
};
```

- 第二类是默认的统一变量块，用于在命名统一变量块之外声明的统一变量。和`命名统一变量块`不同，默认统一变量块没有名称或者统一变量块索引。

```java
uniform mat4 matViewProj;
uniform mat3 matNormal;
uniform mat3 matTexGen;
```

#### 获取和设置统一变量

要査询程序中活动`统一变量`的列表，首先要用`GL_ACTIVE_UNIFORMS`参数，调用`glGetProgramiv`。这样可以获得程序中活动统一变量的数量。这个列表包含命名统一变量块中的`统一变量`、着色器代码中声明的默认统一变量块中的统一变量以及着色器代码中使用的`内建统一变量`。如果统一变量被程序使用，就认为它是`"活动"`的。换言之，`如果你在一个着色器中声明了一个统一变量但是从未使用，链接程序可能会在优化时将其去掉，不在活动统一变量列表中返回`。获取到统一变量和存储统一变量名称所需的字符数之后，我们可以用`glGetActiveUniform`和`glGetActiveUniformsiv`找出每个统一变量的细节。

使用`glGetActiveUniform`，可以确定几乎所有统一变量的属性。你可以确定统一变量的`名称和类型`。此外，可以发现变量是不是数组以及数组中使用的最大元素。`统一变量的名称 对于找到统一变量的位置是必要的`，要知道如何加载统一变量的数据，需要统一变量的类型和大小。一旦有了统一变量的名称，就可以用`glGetUniformLocation`找到它的位置。统一变量的位置是一个整数值，用于标识统一变量在程序中的位置(`注意: 命名统一变量块中的统一变量没有指定位置`)。这个位置值用于加载统一变量值的后续调用(例如: `glUniformlf`)。

```java
public static native int glGetUniformLocation(
        int program,
        String name
);
```
这个函数将返回由`name`指定的统一变量的位置。如果这个统一变量不是程序中的活动统一变量，返回值将为-1。有了统一变量的位置及其类型和数组大小，我们就可以加载统一变量的值。加载统一变量值有许多不同的函数，每种统一变量类型都对应不同的函数。

```java
public static native void glUniform1f(int location,float x);
public static native void glUniform1fv(int location,int count,float[] v,int offset);
......
```

加载统一变量所需的函数根据`glGetActiveUniform`函数返回的`type`确定。例如，如果返回的类型是`GL_FLOAT_VEC4`, 那么可以使用`glUniform4f`或`glUnifomi4fv`。如果`gIGetActiveUniform`返回的`size`大于 1, 则使用`glUnifrom4fv`在一次调用中加载整个数组。如果统一变量不是数组，则可以使用`glUniform4f`或`glUniform4fv`

注意: `glUniform*`调用不以程序对象句柄作为参数。原因是: `glUniform*`总是在与`glUseProgram`绑定的当前程序上操作。统一变量值本身保存在程序对象中。也就是说，一旦在程序对象中设置一个统一变量的值，即使你让另一个程序处于活动状态，该值仍然保留在原来的程序对象中。从这个意义上，我们可以说统一变量值是程序对象局部所有的。

下面来实践一下査询程序对象中的统一变量信息的方法。

新建一个`UniformRenderer.java`文件:

```java
/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class UniformRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "UniformRenderer";

    private int mProgram;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_uniform_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_uniform_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //在OpenGLES环境中使用程序片段
        GLES30.glUseProgram(mProgram);


        final int[] maxUniforms = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_ACTIVE_UNIFORM_MAX_LENGTH, maxUniforms, 0);
        final int[] numUniforms = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_ACTIVE_UNIFORMS, numUniforms, 0);

        Log.d(TAG, "maxUniforms=" + maxUniforms[0] + " numUniforms=" + numUniforms[0]);

        int[] length = new int[1];
        int[] size = new int[1];
        int[] type = new int[1];
        byte[] nameBuffer = new byte[maxUniforms[0] - 1];
        for (int index = 0; index < numUniforms[0]; index++) {
            GLES30.glGetActiveUniform(mProgram, index, maxUniforms[0], length, 0, size, 0, type, 0, nameBuffer, 0);
            String uniformName = new String(nameBuffer);
            int location = GLES30.glGetUniformLocation(mProgram, uniformName);

            Log.d(TAG, "uniformName=" + uniformName + " location=" + location + " type=" + type[0] + " size=" + size[0]);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
    }
}
```

`顶点着色器`

```java
#version 300 es
uniform mat4 mMatrix4;
uniform mat3 mMatrix3;

layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;
out vec4 vColor;
void main() {
     gl_Position  = mMatrix4 * vPosition;
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

输出如下日志:

```java
11-07 11:46:22.099 28987-29005/? D/UniformRenderer: maxUniforms=9 numUniforms=1
11-07 11:46:22.099 28987-29005/? D/UniformRenderer: uniformName=mMatrix4 location=0 type=35676 size=1
```

>为什么只输出了`mMatrix4`，那`mMatrix3`呢?  刚刚也说到了，虽然我们在顶点着色器中声明了`mMatrix3`，但是我们并没有使用它，导致它被链接程序优化掉了

#### 统一变量块

`统一变量缓冲区对象`可以通过一个`缓冲区对象`支持统一变量数据的存储。`统一变量缓冲区对象`在某些条件下比单独的统一变量有更多优势。利用`统一变量缓冲区对象`，`统一变量缓冲区数据`可以在多个程序中共享，但只需要设置一次。而且`统一变量缓冲区对象`一般可以存储更大量的`统一变量数据`。最后，在`统一缓冲区对象`之间切换比一次单独加载一个统一变最更高效。

`统一缓冲区对象`可以在`OpenGL ES`着色语言中通过应用统一变量块使用。

```java
uniform TransformBlock {
    mat4 matViewProj;
    mat3 matNormal;
    mat3 matTexGen;
};
```

上述声明了一个名为`TransformBlock`且包含3个矩阵的统一变量块。名称`TransformBlock`将供应用程序使用，统一缓冲区对象函数`glGetUniformBlocklndex`中的`blockName`参数。统一变量块声明中的变量在着色器中都可以访问，就像常规形式声明的变量一样。

```java
#version 300 es
uniform TransformBlock {
    mat4 matViewProj;
    mat3 matNormal;
    mat3 matTexGen;
};
layout(location = 0) in vec4 a_position;
void main{
     gl_Position = matViewProj * a_position;
}
```

`布局限定符`可用于指定支持统一变量块的统一缓冲区对象在内存中的布局方式。`布局限定符`可以提供给单独的`统一变量块`，或者用于所有统一变量块。在全局作用域内，为所有统一变量块设置默认布局的方法如下：

```java
layout(shared, column_major) uniform; // 如果未指定，则为默认
layout(packed, row_major) uniform;
```

单独的统一变量块也可以通过覆盖全局作用域上的默认设置来设置布局。此外统一变量块中的单独统一变量也可以指定布局限定符

```java
layout(stdl40) uniform TransformBlock
{
mat4 matViewProj;
layout(row_major) mat3 matNormal;
mat3 matTexGen;
};
```

可以用于统一变量块的所有布局限定符:

|限定符|描述|
|:-|:-|
|`shared`|`shared`限定符指定多个着色器或者多个程序中统一变量块的内存布局相同。要使用这个限定符，不同定义中的`row_major/column_major`值必须相等。覆盖`stdl40`和`packed`(默认)|
|`packed`|`packed`布局限定符指定编译器可以优化统一变量块的内存布局。使用这个限定符时必须查询偏移位置，而且统一变量块无法在顶点/片段着色器或者程序间共享。覆盖`stdl40`和`shared`|
|`stdl40`|`sldl40`布局限定符指定统一变童块的布局基于`OpenGL ES 3.0`规范中定义的一组标准规则。覆盖`shared`和`packed`|
|`row_major`|矩阵在内存中以行优先顺序布局|
|`column_major`|矩阵在内存中以列优先顺序布局(默认)|

#### 顶点和片段着色器输入/输出
`OpenGL ES`着色器语言的另一个特殊变量类型是`顶点输入(或者属性)`变量。顶点输入变量用于指定顶点着色器中每个顶点的输入，用`in关键字`指定。它们通常存储`位置、法线、 纹理坐标和颜色`这样的数据。这里的关键是理解顶点输入是为绘制的每个顶点指定的数据。

`顶点着色器`

```java
#version 300 es
uniform mat4 u_matViewProjection;
//输入
layout(location = 0) in vec4 a_position;
layout(location = 1) in vec3 a_color;
out vec3 v_color;
void main{)
{
     gl_Position = u_matViewProjection * a_position;
     v_color = a_color;
}
```

`片段着色器`

```java
#version 300 es
precision mediump float;
in vec4 vColor;
//输出片段着色器
layout (location = 0) out vec4 fragColor;
void main() {
     fragColor = vColor;
}
```

这个着色器的两个顶点输入变量`a_position和a_color`的数据由应用程序加载。本质上，应用程序将为每个顶点创建一个顶点数组，该数组包含`位置和颜色`。注意上面的例子中顶点输入变量之前使用了`layout限定符`。这种情况下的布局限定符用于指定顶点属性的索引。`布局限定符是可选的，如果没有指定，链接程序将自动为顶点输入变量分配位置`。

和`统一变量`—样，底层硬件通常在可输入顶点着色器的`属性变量数目上有限制`。OpenGL ES实现支持的最大属性数量由内建变量`gl_MaxVertexAttribs`给出(也可以使用`glGetlntegerv`査询`GL_MAX_VERTEX_ATTRIBS`得到)。`OpenGL ES 3.0实现可支持的最小属性为16个`。 不同的实现可以支持更多变量，但是如果想要编写保证能在任何OpenGL ES 3.0实现上运行的着色器，则应该将属性限制为不多于16个。

来自顶点着色器的`输出变量`由`out关键字`指定。上面的示例代码中，`v_color`变量被声明为输出变量，其内容从`a_color输入变量`中复制而来。每个顶点着色器将在一个或者多个输出变量中输出需要传递给片段着色器的数据。然后，这些变量也会在片段着色器中声明为`in变量` (相符类型)，`在光栅化阶段中对图元进行线性插值`。

`片段着色器`中与`顶点着色器`的`顶点输出v_Color`相匹配的输入声明如下：

```java
in vec3 v_color;
```

`注意：` `与顶点着色器输入不同，顶点着色器输出/片段着色器输入变量不能有布局限定符`。`OpenGL ES`实现自动选择位置，与统一变量和顶点输入属性相同，`底层硬件通常限制顶点着色器输出/片段着色器输入`(在硬件上，这些变量通常被称作插值器)的数量。 OpenGL ES实现支持的顶点着色器输出的数量由内建变量`gl_MaxVertexOutputVectors`给出(用`glGetlntegerv`査询`GL_MAX_VERTEX_OUTPUT_COMPONENTS`将提供总分量值数量，而非向量数量)。`OpenGLES 3.0实现可以支持的最小顶点输出向量数为16`。与此类似，OpenGL ES 3.0实现支持的片段着色器输入的数量由`gl_MaxFragmentInputVectors`给出(用`glGetlntegerv`查询`GL_MAX_FRAGMENT_INPUT_COMPONENTS`将提供总分量值数量，而非向量数量)。`OpenGL ES 3.0实现可以支持的最小片段输入向量数为15`。

上述的片段着色器将输出一个或者多个颜色。在正常情况下，我们只渲染到一个颜色缓冲区，在这种时候，布局限定符是可选的`(假定输出变量进入位置0)`。但是，当渲染到多个渲染目标`(MRT)`时，我们可以使用布局限定符指定每个输出前往的渲染目标。对于这种情况，在片段着色器中会有个输出变量，该值将是传递给管线逐片段操作部分的输出颜色。

#### 插值限定符

上面的示例中，我们声明了自己的顶点着色器输出和片段着色器输入，没有使用任何限定符。在没有限定符时，默认的插值行为是执行平滑着色。也就是说，来自`顶点着色器的输出变量在图元中线性插值，片段着色器接收线性插值之后的数值作为输入`。我们可以明确地请求平滑着色，在这种情况下，输出/输入如下：

```java
// 顶点着色器输出
smooth out vec3 v_color;
// 片段着色器输入
smooth in vec3 v_color;
```

`OpenGL ES 3.0`还引入了另一种插值——`平面着色`。在平面着色中，图元中的值没有进行插值，而是将其中一个顶点视为驱动顶点(Provoking Vertex，取决于图元类型)，该顶点的值被用于图元中的所有片段。我们可以声明如下的平面着色输出/ 输入：

```java
// 顶点着色器输出
flat out vec3 v_color;
// 片段着色器输入
flat in vec3 v_color;
```
最后，可以用`centroid`关键字在插值器中添加另一个限定符。使用多重采样渲染时，`centroid关键字`可用于强制插值发生在被渲染图元内部(否则，在图元的边缘可能出现伪像)。

`质心采样`的输出/输入变量的方法。

```java
// 顶点着色器输出
smooth centroid out vec3 v_color
// 顶点着色器输出
smooth centroid in vec3 v_color
```
#### 统一变量和插值器打包

底层硬件中可用于每个变量存储的资源是固定的。统一变量通常保存在所谓的`"常量存储"`中，这可以看作向量的物理数组。`顶点着色器输出/片段着色器输入一般保存在插值器中`，这通 常也保存为一个向量数组。着色器可能声明各种类型的统一变量和着色器输入/输出，包括标量、各种向量分量和矩阵。但是，这些变量声明如何映射到硬件上的可用物理空间呢？换言之，如果一个`OpenGL ES 3.0`实现支持16个顶点着色器输出向量，那 么物理存储实际上是如何使用的呢？

`在OpenGL ES 3.0中，这个问题通过打包规则处理，该规则定义插值器和统一变量映射到物理存储空间的方式`。打包规则基于物理存储空间被组织为一个每个存储位置4列(每个向量分量一列)和1行的网格的概念。打包规则寻求打包变量，使生成代码的复杂度保持不变。换言之，打包规则不进行重排序操作，而是试图在不对运行时性能产生负面影响的情况下，优化物理地址空间的使用。

```java
uniform mat3 m;
uniform float f[6];
uniform vec3 v;
```
如果完全不进行打包，许多常量存储空间将被浪费。`矩阵m`将占据3行，`数组f`占据6行，`向量v`占据1行，共需要10行才能存储这些变量。

`未打包的统一变量存储`

|位置|Ｘ|Y|Z|W|
|:--|:--|:--|:--|:--|
|**0**|**m[0].x**|**m[0].y**|**m[0].z**|**m[0].w**|
|**1**|**m[1].x**|**m[1].y**|**m[1].z**|**m[1].w**|
|**2**|**m[2].x**|**m[2].y**|**m[2].z**|**m[2].w**|
|**3**|**f[0]**|**－**|**－**|**－**|
|**4**|**f[1]**|**－**|**－**|**－**|
|**5**|**f[2]**|**－**|**－**|**－**|
|**6**|**f[3]**|**－**|**－**|**－**|
|**7**|**f[4]**|**－**|**－**|**－**|
|**8**|**f[5]**|**－**|**－**|**－**|
|**9**|**v.x**|**v.y**|**v.z**|**-6**|

`打包的统一变量存储`

|位置|Ｘ|Y|Z|W|
|:--|:--|:--|:--|:--|
|**0**|**m[0].x**|**m[0].y**|**m[0].z**|**f[0]**|
|**1**|**m[1].x**|**m[1].y**|**m[1].z**|**f[1]**|
|**2**|**m[2].x**|**m[2].y**|**m[2].z**|**f[2]**|
|**3**|**v.x**|**v.y**|**v.z**|**f[3]**|
|**4**|**－**|**－**|**－**|**f[4]**|
|**5**|**－**|**－**|**－**|**f[5]**|


﻿在使用打包规则时，只需使用6个物理常量位置。`数组f`的元素会跨越行的边界，原因是GPU通常会按照向量位置索引对常量存储进行索引。打包必须使数组跨越行边界，这样索引才能够起作用。
所有打包对OpenGL ES着色语言的用户都是完全透明的，除了一个细节：`打包影响统一变量和顶点着色器输出/片段着色器输入的计数方式`。如果想要编写保证能够在所有OpenGL ES 3.0实现上运行的着色器，就不应该使用打包之后超过最小运行存储大小的统一变量或者插值器。

#### 精度限定符

梢度限定符使着色器创作者可以指定着色器变量的计算精度。变量可以声明为低、中或者高精度。这些限定符用于提示编译器允许在较低的范围和精度上执行变量计算。在较低的精度上，有些OpenGLES实现在运行着色器时可能更快，或者电源效率更高。
当然，这种效率提升是以精度为代价的，在没有正确使用精度限定符时可能造成伪像。

`精度限定符可以用于指定任何基于浮点数或者整数的变量的精度`。指定精度的关键字是`lowp、mediump和highp`。

```java
highp vec4 position;
varying lowp vec4 color;
mediump float specularExp;
```
`如果变量声明时没有使用精度限定符，它将拥有该类型的默认精度`。默认精度限定符在顶点或者片段着色器的开头指定：

```java
precision highp float;
precision mediump int;
```
为`float类型`指定的精度将用作所有基于浮点值的变量的默认精度。同样，为`int类型`指定的精度将用作所有基于整数的变量的默认精度。

在顶点着色器中，如果没有指定默认精度，则int和float的默认精度都为highp。也就是说，`顶点着色器中所有没用精度限定符声明的变量都使用最高的精度`。片段着色器的规则与此不同。在片段着色器中，浮点值没有默认的精度值，每个着色器必须声明一个默认的float精度，或者为每个float变量指定精度。

#### 不变性

OpenGL ES着色语言中引入的`invariant关键字`可以用于任何可变的顶点着色器输出。
由于着色器需要编译，而`编译器可能进行导致指令重新排序的优化。这种指令重排意味着两个着色器之间的等价计算不能保证产生完全相同的结果`。这种不一致性在多遍着色器特效时尤其可能成为问题，在这种情况下，相同的对象用Alpha混合绘制在自身上方。如果用于计算输出位置的数值的精度不完全一样，精度差异就会导致伪像。

因为编译器需要保证不变性，所以可能限制它所做的优化。因此，`invariant限定符`应该只在必要时使用，否则可能导致性能下降。

项目地址：
https://github.com/byhook/opengles4android

参考：
《OpenGL ES 3.0 编程指南第2版》
