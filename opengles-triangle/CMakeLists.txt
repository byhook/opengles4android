
cmake_minimum_required(VERSION 3.4.1)


##官方标准配置
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

##ANDROID_PLATFORM_LEVEL=16
add_definitions("-DDYNAMIC_ES3")
set(OPENGL_LIB GLESv2)

add_library(native-triangle
           SHARED
           src/main/cpp/native_triangle.c)

target_link_libraries(native-triangle
            ${OPENGL_LIB}
            android
            EGL
            log
            m)
