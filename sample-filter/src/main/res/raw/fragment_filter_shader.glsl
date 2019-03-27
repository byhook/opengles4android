#version 300 es
precision mediump float;
uniform sampler2D uTextureUnit;
//传入滤镜类型
uniform int vFilterType;
//传入滤镜数据
in vec3 vFilter;
in vec2 vTexCoord;

//输出
out vec4 vFragColor;
void main() {
     vec4 vTextureColor = texture(uTextureUnit,vTexCoord);
     switch(vFilterType){
        case 1:
            //黑白滤镜
            float vFilterColor = (vFilter.x * vTextureColor.r + vFilter.y * vTextureColor.g + vFilter.z * vTextureColor.b);
            vFragColor = vec4(vFilterColor, vFilterColor, vFilterColor, 1.0);
            break;
        case 0:
            //默认原图
        default:
            //原图显示
            vFragColor = vTextureColor;
            break;
     }

}