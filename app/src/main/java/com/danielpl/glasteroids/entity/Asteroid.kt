package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import android.util.Log
import com.danielpl.glasteroids.util.Config.LARGE_ASTEROID_SIZE

import com.danielpl.glasteroids.util.Config.MAX_VEL_ASTEROID_LARGE
import com.danielpl.glasteroids.util.Config.MAX_VEL_ASTEROID_MEDIUM
import com.danielpl.glasteroids.util.Config.MAX_VEL_ASTEROID_SMALL
import com.danielpl.glasteroids.util.Config.MEDIUM_ASTEROID_SIZE
import com.danielpl.glasteroids.util.Config.MIN_POINTS_ASTEROIDS
import com.danielpl.glasteroids.util.Config.MIN_VEL_ASTEROID_LARGE
import com.danielpl.glasteroids.util.Config.MIN_VEL_ASTEROID_MEDIUM
import com.danielpl.glasteroids.util.Config.MIN_VEL_ASTEROID_SMALL
import com.danielpl.glasteroids.util.Config.SMALL_ASTEROID_SIZE
import kotlin.random.Random


fun between(min: Float, max: Float): Float = min + Random.nextFloat() * (max - min)

private const val TAG = "Asteroid"

class Asteroid(x: Float, y: Float, points: Int, var type: AsteroidType = AsteroidType.NONE) :
    GLEntity() {


    init {
        assert(points >= MIN_POINTS_ASTEROIDS) { "triangles or more, please. :)" }
        this.x = x
        this.y = y
        if (type == AsteroidType.NONE) {
            // if type has not been defined, choose a random type for the Asteroid
            when (Random.nextInt(1, AsteroidType.values().size)) {
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
                width = SMALL_ASTEROID_SIZE
                velX = between(MIN_VEL_ASTEROID_SMALL, MAX_VEL_ASTEROID_SMALL)
                velY = between(MIN_VEL_ASTEROID_SMALL, MAX_VEL_ASTEROID_SMALL)
            }
            AsteroidType.MEDIUM -> {
                width = MEDIUM_ASTEROID_SIZE
                velX = between(MIN_VEL_ASTEROID_MEDIUM, MAX_VEL_ASTEROID_MEDIUM)
                velY = between(MIN_VEL_ASTEROID_MEDIUM, MAX_VEL_ASTEROID_MEDIUM)
            }
            AsteroidType.LARGE -> {
                width = LARGE_ASTEROID_SIZE
                velX = between(MIN_VEL_ASTEROID_LARGE, MAX_VEL_ASTEROID_LARGE)
                velY = between(MIN_VEL_ASTEROID_LARGE, MAX_VEL_ASTEROID_LARGE)
            }
            else -> {
                Log.d(TAG, "Asteroid should not be of None Type")
            }
        }


        height = width
        val radius = width * 0.5f
        mesh = Mesh(
            generateLinePolygon(points, radius),
            GLES20.GL_LINES
        )
        mesh.setWidthHeight(width, height)
    }
}