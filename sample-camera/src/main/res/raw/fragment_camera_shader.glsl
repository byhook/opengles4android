#version 300 es
//OpenGL ES3.0外部纹理扩展
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
uniform samplerExternalOES yuvTexSampler;
in vec2 yuvTexCoords;
out vec4 vFragColor;
void main() {
     vec4 vCameraColor = texture(yuvTexSampler,yuvTexCoords);
     float fGrayColor = (0.3*vCameraColor.r + 0.59*vCameraColor.g + 0.11*vCameraColor.b);
     vFragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
}