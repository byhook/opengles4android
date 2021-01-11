
#ifndef OPENGLES_3X_NATIVE_BASE_RENDERER_H
#define OPENGLES_3X_NATIVE_BASE_RENDERER_H


class NativeBaseRenderer {


public:

    virtual void surfaceCreated() = 0;

    virtual void surfaceChanged(unsigned int width, unsigned int height) = 0;

    virtual void onDrawFrame() = 0;

};


#endif //OPENGLES_3X_NATIVE_BASE_RENDERER_H
