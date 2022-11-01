package com.danielpl.glasteroids

import android.graphics.Color
import android.opengl.GLES20
import com.danielpl.glasteroids.entity.GLEntity
import com.danielpl.glasteroids.entity.Mesh

object Dot{
    val mesh = Mesh( floatArrayOf(0f, 0f, 0f), GLES20.GL_POINTS)
}

class Star(x: Float, y: Float) : GLEntity() {
    init {
        _x = x
        _y = y
        _color[0] = Color.red(Color.YELLOW) / 255f
        _color[1] = Color.green(Color.YELLOW) / 255f
        _color[2] = Color.blue(Color.YELLOW) / 255f
        _color[3] = 1f
        _mesh = Dot.mesh //all Stars use the exact same Mesh instance.
    }
}