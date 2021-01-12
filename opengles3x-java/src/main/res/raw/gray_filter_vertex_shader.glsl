#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;

uniform mat4 u_Matrix;
uniform vec3 a_Filter;

out vec3 vFilter;
out vec2 vTexCoord;
void main() {
     gl_Position  = u_Matrix * vPosition;
     vTexCoord = aTextureCoord;

     vFilter = a_Filter;
}