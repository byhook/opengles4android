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