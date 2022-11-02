package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.util.Config.TO_RADIANS

class Border(x: Float, y: Float, worldWidth: Float, worldHeight: Float) :
    GLEntity() {
    init {
        this.x = x
        this.y = y
        width = worldWidth - 1.0f //-1 so the border isn't obstructed by the screen edge
        height = worldHeight - 1.0f
        setColors(1f, 0f, 0f, 1f) //RED for visibility
        mesh = Mesh(generateLinePolygon(4, 10.0f), GLES20.GL_LINES)
        mesh.rotateZ(45 * TO_RADIANS)
        mesh.setWidthHeight(width, height) //will automatically normalize the mesh!
    }
}