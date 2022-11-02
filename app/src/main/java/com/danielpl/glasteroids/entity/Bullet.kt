package com.danielpl.glasteroids.entity

import android.graphics.PointF
import com.danielpl.glasteroids.GLManager
import com.danielpl.glasteroids.util.Config.SHOOTING_COLOR
import com.danielpl.glasteroids.util.Config.SPEED_BULLET
import com.danielpl.glasteroids.util.Config.TIME_TO_LIVE_BULLET
import com.danielpl.glasteroids.util.polygonVsPoint
import com.danielpl.glasteroids.util.Config.TO_RADIANS
import com.danielpl.glasteroids.util.Jukebox
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class Bullet : GLEntity() {
    private var _ttl = TIME_TO_LIVE_BULLET
    private val bulletMesh =
        Dot.mesh

    init {
        setColors(SHOOTING_COLOR[0], SHOOTING_COLOR[1], SHOOTING_COLOR[2], SHOOTING_COLOR[3])
        mesh = bulletMesh //all bullets use the exact same mesh
    }

    fun fireFrom(source: GLEntity) {
        val theta = (source.rotation + PI / 3).toFloat() * TO_RADIANS
        x = source.x + sin(theta) * (source.width * 0.5f)
        y = source.y - cos(theta) * (source.height * 0.5f)
        velX = source.velX
        velY = source.velY
        velX += sin(theta) * SPEED_BULLET
        velY -= cos(theta) * SPEED_BULLET
        _ttl = TIME_TO_LIVE_BULLET
    }


    fun fireFromEnemy(source: GLEntity) {
        val theta = (source.rotation + PI / 3).toFloat() * TO_RADIANS
        x = source.x + sin(theta) * (source.width * 0.5f)
        y = source.y - cos(theta) * (source.height * 0.5f)
        velX = source.velX
        velY = source.velY
        velX -= sin(theta) * SPEED_BULLET
        velY += cos(theta) * SPEED_BULLET
        _ttl = TIME_TO_LIVE_BULLET
    }


    override fun isDead(): Boolean {
        return _ttl < 1
    }


    override fun update(dt: Float, jukebox: Jukebox) {
        if (_ttl > 0) {
            _ttl -= dt
            super.update(dt, jukebox)
        }
    }

    override fun render(viewportMatrix: FloatArray, glManager: GLManager) {
        if (_ttl > 0) {
            super.render(viewportMatrix, glManager)
        }
    }

    override fun isColliding(that: GLEntity): Boolean {
        if (!areBoundingSpheresOverlapping(this, that)) { //quick rejection
            return false
        }
        val asteroidVert: ArrayList<PointF> = that.getPointList()
        return polygonVsPoint(asteroidVert, x, y)
    }
}