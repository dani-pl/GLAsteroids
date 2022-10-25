package com.danielpl.glasteroids

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.danielpl.glasteroids.Config.ASTEROID_COUNT
import com.danielpl.glasteroids.Config.METERS_TO_SHOW_X
import com.danielpl.glasteroids.Config.METERS_TO_SHOW_Y
import com.danielpl.glasteroids.Config.NANOSECONDS_TO_SECONDS
import com.danielpl.glasteroids.Config.STAR_COUNT
import com.danielpl.glasteroids.Config.WORLD_HEIGHT
import com.danielpl.glasteroids.Config.WORLD_WIDTH
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random


lateinit var engine: Game
class Game(ctx: Context, attrs: AttributeSet? = null) : GLSurfaceView(ctx, attrs),
    GLSurfaceView.Renderer {
    private val TAG = "Game"

    private val _stars = ArrayList<Star>()

    private val _asteroids = ArrayList<Asteroid>()

    private val BG_COLOR = floatArrayOf(135f / 255f, 206f / 255f, 235f / 255f, 1f) //RGBA
    val _player = Player(WORLD_WIDTH/2f, WORLD_HEIGHT/2f)
    val _border = Border(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    private val _viewportMatrix = FloatArray(4 * 4) //In essence, it is our our Camera

    init {
        for (i in 0 until STAR_COUNT) {
            val x = Random.nextInt(WORLD_WIDTH.toInt()).toFloat()
            val y = Random.nextInt(WORLD_HEIGHT.toInt()).toFloat()
            _stars.add(Star(x, y))
        }

        for (i in 0 until ASTEROID_COUNT){
            val x = Random.nextInt(WORLD_WIDTH.toInt()).toFloat()
            val y = Random.nextInt(WORLD_HEIGHT.toInt()).toFloat()
            val asteroid = Asteroid(x,y,Random.nextInt(3,6))
            _asteroids.add(asteroid)
        }

        engine = this
        setEGLContextClientVersion(2)
        setRenderer(this)
    }

    fun hexColorToFloat(hex: Int) = hex / 255f

    override fun onSurfaceCreated(unused: GL10, p1: EGLConfig) {
        GLManager.buildProgram() //compile, link and upload our shaders to the GPU
        GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]) //set clear color
        //center the player in the world
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height)
    }

    private val dt = 0.01f
    override fun onDrawFrame(unused: GL10?) {
        update(dt)
        render()
    }

    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/Links to an external site.
    var accumulator = 0.0f
    var currentTime = (System.nanoTime() * NANOSECONDS_TO_SECONDS).toFloat()
    private fun update(dt: Float) {
        val newTime = (System.nanoTime() * NANOSECONDS_TO_SECONDS).toFloat()
        val frameTime = newTime - currentTime
        currentTime = newTime
        accumulator += frameTime
        while (accumulator >= dt) {
            for (a in _asteroids) {
                a.update(dt)
            }
            _player.update(dt)
            accumulator -= dt
        }
    }

    private fun render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT) //clear buffer to background color
        val offset = 0
        val left = 0f
        val right = METERS_TO_SHOW_X
        val bottom = METERS_TO_SHOW_Y
        val top = 0f
        val near = 0f
        val far = 1f
        Matrix.orthoM(_viewportMatrix, offset, left, right, bottom, top, near, far)
        _border.render(_viewportMatrix)
        for (s in _stars) {
            s.render(_viewportMatrix)
        }
        for (a in _asteroids) {
            a.render(_viewportMatrix)
        }
        _player._mesh.normalize()
        _player._mesh.updateBounds()
        _player.render(_viewportMatrix)

    }
}