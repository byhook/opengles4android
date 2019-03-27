#version 300 es
precision mediump float;
uniform sampler2D uTextureUnit;
in vec3 vFilter;
in vec2 vTexCoord;
out vec4 vFragColor;
void main() {
     vec4 vTextureColor = texture(uTextureUnit,vTexCoord);
     //黑白滤镜
     float vFilterColor = (vFilter.x * vTextureColor.r + vFilter.y * vTextureColor.g + vFilter.z * vTextureColor.b);
     vFragColor = vec4(vFilterColor, vFilterColor, vFilterColor, 1.0);
}