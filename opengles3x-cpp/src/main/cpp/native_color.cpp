
#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>

#include "native_color.h"

void NativeColorRenderer::surfaceCreated() {
    int color = 0x0000FF;
    GLfloat redF = ((color >> 16) & 0xFF) * 1.0f / 255;
    GLfloat greenF = ((color >> 8) & 0xFF) * 1.0f / 255;
    GLfloat blueF = (color & 0xFF) * 1.0f / 255;
    GLfloat alphaF = ((color >> 24) & 0xFF) * 1.0f / 255;
    glClearColor(redF, greenF, blueF, alphaF);
}

void NativeColorRenderer::surfaceChanged(unsigned int width, unsigned int height) {
    glViewport(0, 0, width, height);
}

void NativeColorRenderer::onDrawFrame() {
    //把颜色缓冲区设置为我们预设的颜色
    glClear(GL_COLOR_BUFFER_BIT);
}



