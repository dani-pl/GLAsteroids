package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.*
import com.danielpl.glasteroids.util.Config
import com.danielpl.glasteroids.util.Config.TIME_BETWEEN_SHOTS
import com.danielpl.glasteroids.util.Config.TO_DEGREES
import com.danielpl.glasteroids.util.Jukebox
import com.danielpl.glasteroids.util.polygonVsPoint
import com.danielpl.glasteroids.util.polygonVsPolygon
import kotlin.concurrent.fixedRateTimer
import kotlin.math.PI
import kotlin.math.atan2


class Enemy(x: Float, y: Float) : GLEntity() {
    private var _bulletCoolDown = 5f
    private var fire = true
    var playerX = 0f
    var playerY = 0f

    init {
        this.x = x
        this.y = y
        width = 8f //TO DO: gameplay values! move to configs
        height = 12f
        mesh = Mesh(Triangle.vert.clone(), GLES20.GL_TRIANGLES)
        mesh.setWidthHeight(width, height)
        velX = between(Config.MIN_VEL_ASTEROID_LARGE, Config.MAX_VEL_ASTEROID_LARGE)
        velY = between(Config.MIN_VEL_ASTEROID_LARGE, Config.MAX_VEL_ASTEROID_LARGE)
        setColors(1f, 0f, 0f, 1f)

    }


    override fun update(dt: Float, jukebox: Jukebox) {

        rotation = (atan2((playerY - y), (playerX - x)) + (3 * PI / 2).toFloat()) * TO_DEGREES

        _bulletCoolDown -= dt


        if (fire && _bulletCoolDown <= 0f) {
            engine.fireBulletFromEnemy(this)
            _bulletCoolDown = TIME_BETWEEN_SHOTS
            fire = false
            fixedRateTimer("enemy fire timer", period = 100000, action = {
                fire = true
            })
        }
        super.update(dt, jukebox)
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
        return polygonVsPoint(asteroidHull, x, y) //finally, check if we're inside the asteroid
    }


}