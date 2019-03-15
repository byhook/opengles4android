#version 300 es
precision mediump float;
uniform sampler2D uTextureUnit;
in vec2 vTexCoord;
out vec4 vFragColor;
void main() {
     vec4 vTextureColor = texture(uTextureUnit,vTexCoord);
     //黑白滤镜
     float fGrayColor = (0.299*vTextureColor.r + 0.587*vTextureColor.g + 0.114*vTextureColor.b);
     vFragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
}