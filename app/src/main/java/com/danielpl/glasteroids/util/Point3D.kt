package com.danielpl.glasteroids.util

class Point3D {
    var x = 0.0f
    var y = 0.0f
    var z = 0.0f

    operator fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun set(p: FloatArray) {
        assert(p.size == 3)
        x = p[0]
        y = p[1]
        z = p[2]
    }
}