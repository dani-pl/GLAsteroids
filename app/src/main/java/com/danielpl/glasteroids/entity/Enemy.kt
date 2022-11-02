package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.*
import com.danielpl.glasteroids.util.Config
import com.danielpl.glasteroids.util.Config.ANGLE_DIFFERENCE_WITH_SHOOTING_VERTEX
import com.danielpl.glasteroids.util.Config.ENEMY_COLOR
import com.danielpl.glasteroids.util.Config.ENEMY_HEIGHT
import com.danielpl.glasteroids.util.Config.ENEMY_WIDTH
import com.danielpl.glasteroids.util.Config.INITIAL_BULLET_COOL_DOWN_ENEMY
import com.danielpl.glasteroids.util.Config.TIME_BETWEEN_SHOTS_ENEMY
import com.danielpl.glasteroids.util.Config.TO_DEGREES
import com.danielpl.glasteroids.util.Jukebox
import com.danielpl.glasteroids.util.polygonVsPoint
import com.danielpl.glasteroids.util.polygonVsPolygon
import kotlin.math.atan2


class Enemy(x: Float, y: Float) : GLEntity() {
    private var _bulletCoolDown = INITIAL_BULLET_COOL_DOWN_ENEMY
    private var fire = true
    var playerX = 0f
    var playerY = 0f

    init {
        this.x = x
        this.y = y
        width = ENEMY_WIDTH
        height = ENEMY_HEIGHT
        mesh = Mesh(Triangle.vert.clone(), GLES20.GL_TRIANGLES)
        mesh.setWidthHeight(width, height)
        velX = between(Config.MIN_VEL_ASTEROID_LARGE, Config.MAX_VEL_ASTEROID_LARGE)
        velY = between(Config.MIN_VEL_ASTEROID_LARGE, Config.MAX_VEL_ASTEROID_LARGE)
        setColors(ENEMY_COLOR[0], ENEMY_COLOR[1], ENEMY_COLOR[2], ENEMY_COLOR[3])

    }


    override fun update(dt: Float, jukebox: Jukebox) {

        rotation = (atan2(
            (playerY - y),
            (playerX - x)
        ) + ANGLE_DIFFERENCE_WITH_SHOOTING_VERTEX.toFloat()) * TO_DEGREES

        _bulletCoolDown -= dt


        if (fire && _bulletCoolDown <= 0f) {
            engine.fireBulletFromEnemy(this)
            _bulletCoolDown = TIME_BETWEEN_SHOTS_ENEMY
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