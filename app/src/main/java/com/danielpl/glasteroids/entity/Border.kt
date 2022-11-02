package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.util.Config
import com.danielpl.glasteroids.util.Config.BORDER_COLOR
import com.danielpl.glasteroids.util.Config.POINTS_BORDER
import com.danielpl.glasteroids.util.Config.RADIUS_BORDER
import com.danielpl.glasteroids.util.Config.TO_RADIANS

class Border(x: Float, y: Float, worldWidth: Float, worldHeight: Float) :
    GLEntity() {
    init {
        this.x = x
        this.y = y
        width = worldWidth - 1.0f //-1 so the border isn't obstructed by the screen edge
        height = worldHeight - 1.0f
        setColors(BORDER_COLOR[0], BORDER_COLOR[1], BORDER_COLOR[2], BORDER_COLOR[3])
        mesh = Mesh(generateLinePolygon(POINTS_BORDER, RADIUS_BORDER), GLES20.GL_LINES)
        mesh.rotateZ(45 * TO_RADIANS)
        mesh.setWidthHeight(width, height) //will automatically normalize the mesh!
    }
}