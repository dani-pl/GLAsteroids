package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.*
import com.danielpl.glasteroids.util.Config.DRAG
import com.danielpl.glasteroids.util.Config.POINTS_ENEMY
import com.danielpl.glasteroids.util.Config.ROTATION_VELOCITY
import com.danielpl.glasteroids.util.Config.THRUST
import com.danielpl.glasteroids.util.Config.TIME_BETWEEN_SHOTS
import com.danielpl.glasteroids.util.Config.TO_RADIANS
import com.danielpl.glasteroids.util.Jukebox
import kotlin.concurrent.fixedRateTimer
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


class Enemy(x: Float, y: Float) : GLEntity() {
    private val TAG = "Player"
    private var boostSound = true
    private var _bulletCooldown = 0f
    private var fire = true
    var playerX = 0f
    var playerY = 0f

    init {
        _x = x
        _y = y
        _width = 16f; //TO DO: gameplay values! move to configs
        _height = _width
        _mesh = Mesh(Triangle.verts.clone(), GLES20.GL_TRIANGLES)
        _mesh.setWidthHeight(_width, _height)


        /*
        _height = 12f;
        _mesh = Mesh(Triangle.verts.clone(), GLES20.GL_TRIANGLES)
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();

         */
    }


    override fun update(dt: Float, jukebox: Jukebox) {
        var randomAngle = Random.nextInt(1,360) * TO_RADIANS
        val theta = (atan2((playerY-_y),(playerX-_x)) + PI/4).toFloat()
        _rotation = (theta* TO_DEGREES).toFloat()
        _shootingAngle = (theta-randomAngle + PI/4).toFloat()




        _velX += cos(theta-randomAngle)* THRUST
        _velY += sin(theta-randomAngle)* THRUST

        _velX *= DRAG
        _velY *= DRAG

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
        _x += _velX
        _y += _velY
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