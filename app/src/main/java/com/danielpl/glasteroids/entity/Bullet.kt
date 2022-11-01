package com.danielpl.glasteroids.entity

import android.graphics.PointF
import com.danielpl.glasteroids.Dot
import com.danielpl.glasteroids.GLManager
import com.danielpl.glasteroids.entity.GLEntity
import com.danielpl.glasteroids.polygonVsPoint
import com.danielpl.glasteroids.util.Config.TO_RADIANS
import com.danielpl.glasteroids.util.Jukebox
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val BULLET_MESH = Dot.mesh //reusing the Dot (defined in Star.kt, but available throughout the package)
const val SPEED = 120f //TO DO: game play settings
const val TIME_TO_LIVE = 3.0f //seconds

class Bullet : GLEntity() {
    var _ttl = TIME_TO_LIVE
    init {
        setColors(1f, 0f, 1f, 1f)
        _mesh = BULLET_MESH //all bullets use the exact same mesh
    }

    fun fireFrom(source: GLEntity) {
        val theta = (source._rotation + PI/3).toFloat() * TO_RADIANS
        _x = source._x + sin(theta) * (source._width * 0.5f)
        _y = source._y - cos(theta) * (source._height * 0.5f)
        _velX = source._velX
        _velY = source._velY
        _velX += sin(theta) * SPEED
        _velY -= cos(theta) * SPEED
        _ttl = TIME_TO_LIVE
    }


    fun fireFromEnemy(source: GLEntity) {
        val theta = (source._rotation + PI/3).toFloat() * TO_RADIANS
        _x = source._x + sin(theta) * (source._width * 0.5f)
        _y = source._y - cos(theta) * (source._height * 0.5f)
        _velX = source._velX
        _velY = source._velY
        _velX -= sin(theta) * SPEED
        _velY += cos(theta) * SPEED
        _ttl = TIME_TO_LIVE
    }



    override fun isDead(): Boolean {
        return _ttl < 1
    }



    override fun update(dt: Float, jukebox: Jukebox) {
        if (_ttl > 0) {
            _ttl -= dt
            super.update(dt,jukebox)
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
        val asteroidVerts: ArrayList<PointF> = that.getPointList()
        return polygonVsPoint(asteroidVerts, _x, _y)
    }
}