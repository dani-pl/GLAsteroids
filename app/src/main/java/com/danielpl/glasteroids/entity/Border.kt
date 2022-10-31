package com.danielpl.glasteroids.entity

import android.opengl.GLES20

class Border(x: Float, y: Float, worldWidth: Float, worldHeight: Float) :
    GLEntity() {
    init {
        _x = x
        _y = y
        _width = worldWidth - 1.0f; //-1 so the border isn't obstructed by the screen edge
        _height = worldHeight - 1.0f;
        setColors(1f, 0f, 0f, 1f) //RED for visibility
        _mesh = Mesh(generateLinePolygon(4, 10.0f), GLES20.GL_LINES)
        _mesh.rotateZ(45 * TO_RADIANS)
        _mesh.setWidthHeight(_width, _height); //will automatically normalize the mesh!
    }
}