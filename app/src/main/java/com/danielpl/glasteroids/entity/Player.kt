package com.danielpl.glasteroids.entity

import android.opengl.GLES20
import com.danielpl.glasteroids.GLManager
import com.danielpl.glasteroids.engine
import com.danielpl.glasteroids.util.Config.BOOST_SOUND_DELAY
import com.danielpl.glasteroids.util.Config.DRAG
import com.danielpl.glasteroids.util.Config.PLAYER_COLOR
import com.danielpl.glasteroids.util.Config.PLAYER_HEIGHT
import com.danielpl.glasteroids.util.Config.PLAYER_WIDTH
import com.danielpl.glasteroids.util.Config.ROTATION_VELOCITY
import com.danielpl.glasteroids.util.Config.SHOOTING_COLOR
import com.danielpl.glasteroids.util.Config.THRUST
import com.danielpl.glasteroids.util.Config.TIME_BETWEEN_SHOTS_PLAYER
import com.danielpl.glasteroids.util.Config.TO_RADIANS
import com.danielpl.glasteroids.util.Jukebox
import com.danielpl.glasteroids.util.SFX
import com.danielpl.glasteroids.util.polygonVsPoint
import com.danielpl.glasteroids.util.polygonVsPolygon
import kotlin.concurrent.fixedRateTimer
import kotlin.math.cos
import kotlin.math.sin


class Player(x: Float, y: Float) : GLEntity() {
    private var boostSound = true
    private var _bulletCoolDown = 0f

    init {
        this.x = x
        this.y = y
        width = PLAYER_WIDTH
        height = PLAYER_HEIGHT
        mesh = Mesh(Triangle.vert.clone(), GLES20.GL_TRIANGLES)
        mesh.setWidthHeight(width, height)
        mesh.flipY()
    }


    override fun update(dt: Float, jukebox: Jukebox) {
        rotation += dt * ROTATION_VELOCITY * engine.inputs.horizontalFactor
        if (engine.inputs.pressingB) {
            val theta = rotation * TO_RADIANS
            velX += sin(theta) * THRUST
            velY -= cos(theta) * THRUST


            if (boostSound) {
                jukebox.play(SFX.boost)
                boostSound = false

                fixedRateTimer("boost sound timer", period = BOOST_SOUND_DELAY.toLong(), action = {
                    boostSound = true
                })
            }
        }
        velX *= DRAG
        velY *= DRAG

        _bulletCoolDown -= dt
        if (engine.inputs.pressingA && _bulletCoolDown <= 0f) {
            setColors(SHOOTING_COLOR[0], SHOOTING_COLOR[1], SHOOTING_COLOR[2], SHOOTING_COLOR[3])
            if (engine.maybeFireBulletFromPlayer(this)) {
                _bulletCoolDown = TIME_BETWEEN_SHOTS_PLAYER
            }
        } else {
            setColors(PLAYER_COLOR[0], PLAYER_COLOR[1], PLAYER_COLOR[2], PLAYER_COLOR[3])
        }
        super.update(dt, jukebox)
    }

    override fun render(viewportMatrix: FloatArray, glManager: GLManager) {
        x += velX
        y += velY

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
        return polygonVsPoint(asteroidHull, x, y) //finally, check if we're inside the asteroid
    }


}