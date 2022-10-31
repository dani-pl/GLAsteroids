package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import android.util.Log

import com.danielpl.glasteroids.util.Config.MAX_VEL_ASTEROID_LARGE
import com.danielpl.glasteroids.util.Config.MAX_VEL_ASTEROID_MEDIUM
import com.danielpl.glasteroids.util.Config.MAX_VEL_ASTEROID_SMALL
import com.danielpl.glasteroids.util.Config.MIN_VEL_ASTEROID_LARGE
import com.danielpl.glasteroids.util.Config.MIN_VEL_ASTEROID_MEDIUM
import com.danielpl.glasteroids.util.Config.MIN_VEL_ASTEROID_SMALL
import kotlin.random.Random


fun between(min: Float, max: Float): Float = min + Random.nextFloat() * (max - min)

private const val TAG = "Asteroid"
class Asteroid(x: Float, y: Float, points: Int, var type: AsteroidType = AsteroidType.NONE) : GLEntity() {


    init {
        assert(points >= 3, { "triangles or more, please. :)" })
        _x = x
        _y = y
        if (type == AsteroidType.NONE) {
            // if type has not been defined, choose a random type for the Asteroid
            when (Random.nextInt(1, 4)) {
                1 -> {
                    type = AsteroidType.SMALL
                }
                2 -> {
                    type = AsteroidType.MEDIUM
                }
                3 -> {
                    type = AsteroidType.LARGE
                }
            }
        }

        when (type) {
            AsteroidType.SMALL -> {
                _width = 8f
                _velX = between(MIN_VEL_ASTEROID_SMALL, MAX_VEL_ASTEROID_SMALL)
                _velY = between(MIN_VEL_ASTEROID_SMALL, MAX_VEL_ASTEROID_SMALL)
            }
            AsteroidType.MEDIUM -> {
                _width = 12f
                _velX = between(MIN_VEL_ASTEROID_MEDIUM, MAX_VEL_ASTEROID_MEDIUM)
                _velY = between(MIN_VEL_ASTEROID_MEDIUM, MAX_VEL_ASTEROID_MEDIUM)
            }
            AsteroidType.LARGE -> {
                _width = 15f
                _velX = between(MIN_VEL_ASTEROID_LARGE, MAX_VEL_ASTEROID_LARGE)
                _velY = between(MIN_VEL_ASTEROID_LARGE, MAX_VEL_ASTEROID_LARGE)
            }
            else -> {
                Log.d(TAG, "Asteroid should not be of None Type")
            }
    }


        _height = _width
        val radius = _width * 0.5f
        _mesh = Mesh(
            generateLinePolygon(points, radius),
            GLES20.GL_LINES
        )
        _mesh.setWidthHeight(_width, _height)
    }
}