#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
in vec2 vTextureCoord;
out vec4 vFragColor;
void main()
{
  vec4 vCameraColor = texture(uTextureSampler, vTextureCoord);
  float fGrayColor = (0.299*vCameraColor.r + 0.587*vCameraColor.g + 0.114*vCameraColor.b);
  vFragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
}
