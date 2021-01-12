cmake_minimum_required(VERSION 3.4.1)
project(native-es3x)

##官方标准配置
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

##ANDROID_PLATFORM_LEVEL=18
add_definitions("-DDYNAMIC_ES3")
set(OPENGL_LIB GLESv3)

file(GLOB native_files *.cpp)

add_library(${PROJECT_NAME}
        SHARED
        ${native_files})

target_link_libraries(${PROJECT_NAME}
        ${OPENGL_LIB}
        android
        EGL
        log
        m)