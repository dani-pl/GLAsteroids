package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.engine
import com.danielpl.glasteroids.polygonVsPoint
import com.danielpl.glasteroids.polygonVsPolygon
import com.danielpl.glasteroids.util.Config.DRAG
import com.danielpl.glasteroids.util.Config.ROTATION_VELOCITY
import com.danielpl.glasteroids.util.Config.THRUST
import com.danielpl.glasteroids.util.Config.TIME_BETWEEN_SHOTS
import com.danielpl.glasteroids.util.Jukebox
import com.danielpl.glasteroids.util.SFX
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timerTask
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val TO_RADIANS = PI.toFloat() / 180.0f

class Player(x: Float, y: Float) : GLEntity() {
    private val TAG = "Player"
    private var boostSound = true
    private var _bulletCooldown = 0f

    init {
        _x = x
        _y = y
        _width = 8f; //TO DO: gameplay values! move to configs
        _height = 12f;
        _mesh = Mesh(Triangle.verts.clone(), GLES20.GL_TRIANGLES)
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
    }


    override fun update(dt: Float, jukebox: Jukebox) {
        _rotation += dt * ROTATION_VELOCITY * engine._inputs._horizontalFactor
        if (engine._inputs._pressingB) {
            val theta = _rotation * TO_RADIANS
            _velX += sin(theta) * THRUST
            _velY -= cos(theta) * THRUST


            if(boostSound){
                jukebox.play(SFX.boost)
                boostSound = false

                fixedRateTimer("boost sound timer", period = 100000, action = {
                    boostSound = true
                })
            }
        }
        _velX *= DRAG
        _velY *= DRAG

        _bulletCooldown -= dt;
        if (engine._inputs._pressingA && _bulletCooldown <= 0f) {
            setColors(1f, 0f, 1f, 1f);
            if (engine.maybeFireBullet(this)) {
                _bulletCooldown = TIME_BETWEEN_SHOTS;
            }
        } else {
            setColors(1.0f, 1f, 1f, 1f);
        }
        super.update(dt, jukebox)
    }

    override fun render(viewportMatrix: FloatArray) {
        //val uptime = SystemClock.uptimeMillis() //get an (ever-increasing) timestamp to use as a counter
        //val startPosition = WORLD_WIDTH / 2f
        //val range = WORLD_WIDTH / 2f //amplitude of our sine wave (how far to travel, in each direction)
        //val speed = 360f / 2000f //I want the sine wave to complete a full revolution (360 degrees) in 2 seconds (2000 milliseconds).
        //var angle = (uptime * speed) % 360f //use modulus (%) to turn linear, ever growing, timestamp into 0-359 range
        //val five_seconds = uptime % 5000 //turn a timestamp into 0-4999 ms

        //sin() returns a numeric value between [-1.0, 1.0], the sine of the angle given in radians.
        //perfect for moving smoothly up-and-down some range!
        //remember than sin expects the angle in radians, not in degrees.
        //_x = startPosition + (sin(angle * TO_RADIANS) * range)
        //_rotation = (360.0f / 5000.0f) * five_seconds // Do a complete rotation every 5 seconds.
        //_scale = 5f //render at 5x the size
        _x += _velX
        _y += _velY

        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix)
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