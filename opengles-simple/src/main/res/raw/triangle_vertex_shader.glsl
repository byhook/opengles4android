#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;
out vec4 vColor;
void main() {
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vColor = aColor;
}