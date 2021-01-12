
#ifndef OPENGLES_3X_NATIVE_COLOR_RENDERER_H
#define OPENGLES_3X_NATIVE_COLOR_RENDERER_H

#include "native_base_renderer.h"

class NativeColorRenderer : public NativeBaseRenderer {

public:

    void surfaceCreated();

    void surfaceChanged(unsigned int width, unsigned int height);

    void onDrawFrame();

};

#endif
