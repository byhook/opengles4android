#version 300 es
layout (location = 0) in vec4 vPosition;
out vec4 vColor;
void main() { 
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vColor = vec4(0.3f,0.3f,0.3f,1.0f);
}