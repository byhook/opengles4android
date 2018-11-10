#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;
out vec2 vTexCoord;
void main() { 
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vTexCoord = aTextureCoord;
}