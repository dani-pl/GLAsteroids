package com.danielpl.glasteroids.entity

import android.graphics.Color
import android.opengl.GLES20


object Dot {
    val mesh = Mesh(floatArrayOf(0f, 0f, 0f), GLES20.GL_POINTS)
}

class Star(x: Float, y: Float) : GLEntity() {
    init {
        this.x = x
        this.y = y
        color[0] = Color.red(Color.YELLOW) / 255f
        color[1] = Color.green(Color.YELLOW) / 255f
        color[2] = Color.blue(Color.YELLOW) / 255f
        color[3] = 1f
        mesh = Dot.mesh //all Stars use the exact same Mesh instance.
    }
}