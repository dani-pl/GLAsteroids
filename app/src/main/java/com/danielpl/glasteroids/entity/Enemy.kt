package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.*
import com.danielpl.glasteroids.util.Config
import com.danielpl.glasteroids.util.Config.DRAG
import com.danielpl.glasteroids.util.Config.MAX_VEL_ASTEROID_SMALL
import com.danielpl.glasteroids.util.Config.MIN_VEL_ASTEROID_SMALL
import com.danielpl.glasteroids.util.Config.POINTS_ENEMY
import com.danielpl.glasteroids.util.Config.ROTATION_VELOCITY
import com.danielpl.glasteroids.util.Config.THRUST
import com.danielpl.glasteroids.util.Config.TIME_BETWEEN_SHOTS
import com.danielpl.glasteroids.util.Config.TO_RAD
import com.danielpl.glasteroids.util.Config.TO_RADIANS
import com.danielpl.glasteroids.util.Jukebox
import kotlin.concurrent.fixedRateTimer
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


class Enemy(x: Float, y: Float) : GLEntity() {
    private var _bulletCooldown = 0f
    private var fire = true
    var playerX = 0f
    var playerY = 0f

    init {
        _x = x
        _y = y
        _width = 8f; //TO DO: gameplay values! move to configs
        _height = 12f
        _mesh = Mesh(Triangle.verts.clone(), GLES20.GL_TRIANGLES)
        _mesh.setWidthHeight(_width, _height)
        _velX = between(Config.MIN_VEL_ASTEROID_LARGE, Config.MAX_VEL_ASTEROID_LARGE)
        _velY = between(Config.MIN_VEL_ASTEROID_LARGE, Config.MAX_VEL_ASTEROID_LARGE)
        setColors(1f, 0f, 0f, 1f)

    }


    override fun update(dt: Float, jukebox: Jukebox) {

        _rotation = (atan2((playerY-_y),(playerX- _x)) + (3*PI/2).toFloat())* TO_DEGREES
        _bulletCooldown -= dt


        if (fire && _bulletCooldown <= 0f) {
            engine.fireBulletfromEnemy(this)
            _bulletCooldown = TIME_BETWEEN_SHOTS
            fire = false
            fixedRateTimer("enemy fire timer", period = 100000, action = {
                fire = true
            })
        }
        super.update(dt, jukebox)
    }

    override fun render(viewportMatrix: FloatArray, glManager: GLManager) {
        //_x += _velX
        //_y += _velY
        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix, glManager)
    }

    override fun isColliding(that: GLEntity): Boolean {
        if (!areBoundingSpheresOverlapping(this, that)) {
            return false
        }
        val shipHull = getPointList()
        val asteroidHull = that.getPointList()
        if (polygonVsPolygon(shipHull, asteroidHull)) {
            return true
        }
        return polygonVsPoint(asteroidHull, _x, _y) //finally, check if we're inside the asteroid
    }


}