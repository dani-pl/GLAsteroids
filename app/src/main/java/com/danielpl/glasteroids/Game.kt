package com.danielpl.glasteroids

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import com.danielpl.glasteroids.entity.*
import com.danielpl.glasteroids.util.Config.ASTEROID_COUNT
import com.danielpl.glasteroids.util.Config.METERS_TO_SHOW_X
import com.danielpl.glasteroids.util.Config.METERS_TO_SHOW_Y
import com.danielpl.glasteroids.util.Config.NANOSECONDS_TO_SECONDS
import com.danielpl.glasteroids.util.Config.STAR_COUNT
import com.danielpl.glasteroids.util.Config.WORLD_HEIGHT
import com.danielpl.glasteroids.util.Config.WORLD_WIDTH
import com.danielpl.glasteroids.gamepad.InputManager
import com.danielpl.glasteroids.util.Config.BREAK_APART_LARGE_ASTEROID
import com.danielpl.glasteroids.util.Config.BREAK_APART_MEDIUM_ASTEROID
import com.danielpl.glasteroids.util.Config.BULLET_COUNT_ENEMY
import com.danielpl.glasteroids.util.Config.BULLET_COUNT_PLAYER
import com.danielpl.glasteroids.util.Config.DISPLAY_FRAME_COUNTER
import com.danielpl.glasteroids.util.Config.INCREASE_ASTEROIDS_NUMBER
import com.danielpl.glasteroids.util.Config.INITIAL_ASTEROID_COUNT
import com.danielpl.glasteroids.util.Config.LARGE_ASTEROID_REWARDING_FACTOR
import com.danielpl.glasteroids.util.Config.MAX_POINTS_ASTEROIDS
import com.danielpl.glasteroids.util.Config.MEDIUM_ASTEROID_REWARDING_FACTOR
import com.danielpl.glasteroids.util.Config.MIN_POINTS_ASTEROIDS
import com.danielpl.glasteroids.util.Config.SMALL_ASTEROID_REWARDING_FACTOR
import com.danielpl.glasteroids.util.Config.destroyedAsteroids
import com.danielpl.glasteroids.util.Config.isGameOver
import com.danielpl.glasteroids.util.Config.isLevelSuccessful
import com.danielpl.glasteroids.util.Config.level
import com.danielpl.glasteroids.util.Config.playerHealth
import com.danielpl.glasteroids.util.Config.restart
import com.danielpl.glasteroids.util.Config.score
import com.danielpl.glasteroids.util.Jukebox
import com.danielpl.glasteroids.util.RenderHud
import com.danielpl.glasteroids.util.SFX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random


lateinit var engine: Game

@AndroidEntryPoint
class Game(ctx: Context, attrs: AttributeSet? = null) : GLSurfaceView(ctx, attrs),
    GLSurfaceView.Renderer {
    private val TAG = "Game"

    private val _stars = ArrayList<Star>()

    private val assetManager = context.assets

    private val renderHud = RenderHud(ctx)
    private var frameTime = 0f
    private var frameCounter = 0
    private var accumulator2 = 0f
    private var oldAverageFPS = 0f

    var jukebox = Jukebox(ctx)

    @Inject
    lateinit var glManager: GLManager

    private val _enemies = ArrayList<Enemy>()


    private val _asteroids = ArrayList<Asteroid>()
    private val _asteroidsToAdd = ArrayList<Asteroid>()
    private val _texts = ArrayList<Text>()


    var _bulletsPlayer = ArrayList<Bullet>(BULLET_COUNT_PLAYER)
    var _bulletsEnemy = ArrayList<Bullet>(BULLET_COUNT_ENEMY)


    private val BG_COLOR = floatArrayOf(0f / 255f, 0f / 255f, 84f / 255f, 1f) //RGBA
    val _player = Player(WORLD_WIDTH / 2, WORLD_HEIGHT / 2f)
    val _border = Border(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, WORLD_WIDTH, WORLD_HEIGHT)

    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    private val _viewportMatrix = FloatArray(4 * 4) //In essence, it is our our Camera

    init {

        engine = this
        for (i in 0 until STAR_COUNT) {
            val x = Random.nextInt(WORLD_WIDTH.toInt()).toFloat()
            val y = Random.nextInt(WORLD_HEIGHT.toInt()).toFloat()
            _stars.add(Star(x, y))
        }

        for (i in 0 until INITIAL_ASTEROID_COUNT) {
            val x = Random.nextInt(WORLD_WIDTH.toInt()).toFloat()
            val y = Random.nextInt(WORLD_HEIGHT.toInt()).toFloat()
            val asteroid =
                Asteroid(x, y, Random.nextInt(MIN_POINTS_ASTEROIDS, MAX_POINTS_ASTEROIDS))
            _asteroids.add(asteroid)
        }

        for (i in 0 until BULLET_COUNT_PLAYER) {
            _bulletsPlayer.add(Bullet())
        }

        for (i in 0 until BULLET_COUNT_ENEMY) {
            _bulletsEnemy.add(Bullet())
        }


        val x = Random.nextInt(WORLD_WIDTH.toInt()).toFloat()
        val y = Random.nextInt(WORLD_HEIGHT.toInt()).toFloat()
        _enemies.add(Enemy(x,y))


        setEGLContextClientVersion(2)
        setRenderer(this)
    }


    var _inputs = InputManager() //empty but valid default

    fun setControls(input: InputManager) {
        _inputs.onPause()
        _inputs.onStop()
        _inputs = input
        _inputs.onResume()
        _inputs.onStart()
    }

    fun hexColorToFloat(hex: Int) = hex / 255f

    override fun onSurfaceCreated(unused: GL10, p1: EGLConfig) {
        glManager.buildProgram(assetManager) //compile, link and upload our shaders to the GPU
        GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]) //set clear color
        //center the player in the world
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height)
    }

    private val dt = 0.01f
    override fun onDrawFrame(unused: GL10?) {

        if (restart) {
            restart()
        }
        update(dt)
        render()
    }

    private fun restart() {
        restart = false
        if (isLevelSuccessful) {
            level++
            INITIAL_ASTEROID_COUNT += INCREASE_ASTEROIDS_NUMBER
            ASTEROID_COUNT = INITIAL_ASTEROID_COUNT

            jukebox.unloadMusic()
            jukebox.loadMusic()

        } else {
            // show settings of current shaderCode (stored in shaderCode variable)
        }
        if (isGameOver) {
            playerHealth = 3
            score = 0
            // revert shaderCode complexity
            INITIAL_ASTEROID_COUNT -= INCREASE_ASTEROIDS_NUMBER * (level - 1)
            ASTEROID_COUNT = INITIAL_ASTEROID_COUNT


            level = 1
        }


        jukebox.play(SFX.starting)
        destroyedAsteroids = 0
        jukebox.resumeBgMusic()
        isGameOver = false
        isLevelSuccessful = false

        // repopulate asteroids with updated ASTEROID_COUNT
        _asteroids.clear()
        for (i in 0 until INITIAL_ASTEROID_COUNT) {
            val x = Random.nextInt(WORLD_WIDTH.toInt()).toFloat()
            val y = Random.nextInt(WORLD_HEIGHT.toInt()).toFloat()
            val asteroid =
                Asteroid(x, y, Random.nextInt(MIN_POINTS_ASTEROIDS, MAX_POINTS_ASTEROIDS))
            _asteroids.add(asteroid)
        }

    }

    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/Links to an external site.
    var accumulator = 0.0f
    var currentTime = (System.nanoTime() * NANOSECONDS_TO_SECONDS).toFloat()
    private fun update(dt: Float) {
        val newTime = (System.nanoTime() * NANOSECONDS_TO_SECONDS).toFloat()
        frameTime = newTime - currentTime
        currentTime = newTime
        accumulator += frameTime

        // Update Info for FPS implementation
        accumulator2 += frameTime
        frameCounter++


        _asteroids.addAll(_asteroidsToAdd)
        _asteroidsToAdd.clear()

        while (accumulator >= dt) {
            for (a in _asteroids) {
                a.update(dt, jukebox)
            }
            for (b in _bulletsPlayer) {
                if (b.isDead()) {
                    continue //skip
                }
                b.update(dt, jukebox)
            }

            for (b in _bulletsEnemy) {
                if (b.isDead()) {
                    continue //skip
                }
                b.update(dt, jukebox)
            }


            _player.update(dt, jukebox)

            for (e in _enemies){
                e.playerX = _player._x
                e.playerY = _player._y
                e.update(dt, jukebox)
            }


            collisionDetection()
            removeDeadEntities()
            accumulator -= dt
        }

        checkGameOverOrLevelSuccessful()

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

        _border.render(_viewportMatrix, glManager)
        for (s in _stars) {
            s.render(_viewportMatrix, glManager)
        }
        for (a in _asteroids) {
            a.render(_viewportMatrix, glManager)
        }
        _player.render(_viewportMatrix, glManager)

        for (b in _bulletsPlayer) {
            if (b.isDead()) {
                continue
            }
            b.render(_viewportMatrix, glManager)
        }

        for (b in _bulletsEnemy) {
            if (b.isDead()) {
                continue
            }
            b.render(_viewportMatrix, glManager)
        }



        for (e in _enemies){
            e.render(_viewportMatrix,glManager)
        }




        if (frameCounter == DISPLAY_FRAME_COUNTER) {
            val averageFps = accumulator2 / DISPLAY_FRAME_COUNTER
            renderHud.showFps(averageFps, _viewportMatrix,glManager)
            frameCounter = 0
            accumulator2 = 0f
            oldAverageFPS = averageFps
        } else {
            renderHud.showFps(oldAverageFPS, _viewportMatrix,glManager)
        }

        if (!isGameOver && !isLevelSuccessful) {
            renderHud.showPlayerInfo(_viewportMatrix,glManager)
        } else {
            renderHud.gameOverOrLevelSuccessful(_viewportMatrix,glManager)
        }


    }

    fun maybeFireBulletfromPlayer(source: GLEntity): Boolean {
        for (b in _bulletsPlayer) {
            if (b.isDead()) {
                jukebox.play(SFX.shoot)
                b.fireFrom(source)
                return true
            }
        }

        return false
    }

    fun fireBulletfromEnemy(source: GLEntity): Boolean {
        for (b in _bulletsEnemy) {
            if (b.isDead()) {
                jukebox.play(SFX.shoot)
                b.fireFrom(source)
                return true
            }
        }

        return false
    }

    private fun collisionDetection() {
        for (b in _bulletsPlayer) {
            if (b.isDead()) {
                continue
            } //skip dead bullets
            for (a in _asteroids) {
                if (a.isDead()) {
                    continue
                } //skip dead asteroids
                if (b.isColliding(a)) {
                    jukebox.play(SFX.explosion)
                    destroyedAsteroids++

                    // Points per asteroid are proportional to shaderCode and type of asteroid
                    score += when (a.type) {
                        AsteroidType.SMALL -> {
                            level * SMALL_ASTEROID_REWARDING_FACTOR
                        }
                        AsteroidType.MEDIUM -> {
                            level * MEDIUM_ASTEROID_REWARDING_FACTOR
                        }
                        AsteroidType.LARGE -> {
                            level * LARGE_ASTEROID_REWARDING_FACTOR
                        }
                        AsteroidType.NONE -> {
                            0
                        }
                    }
                    when (a.type) {
                        AsteroidType.MEDIUM -> {
                            for (i in 0 until BREAK_APART_MEDIUM_ASTEROID) {
                                _asteroidsToAdd.add(
                                    Asteroid(
                                        a._x,
                                        a._y,
                                        Random.nextInt(MIN_POINTS_ASTEROIDS, MAX_POINTS_ASTEROIDS),
                                        type = AsteroidType.SMALL
                                    )
                                )
                                ASTEROID_COUNT++

                            }
                        }
                        AsteroidType.LARGE -> {
                            for (i in 0 until BREAK_APART_LARGE_ASTEROID) {
                                _asteroidsToAdd.add(
                                    Asteroid(
                                        a._x,
                                        a._y,
                                        Random.nextInt(MIN_POINTS_ASTEROIDS, MAX_POINTS_ASTEROIDS),
                                        type = AsteroidType.MEDIUM
                                    )
                                )
                                ASTEROID_COUNT++
                            }
                        }
                        else -> {}
                    }
                    b.onCollision(a) //notify each entity so they can decide what to do
                    a.onCollision(b)

                }
            }
        }

        for (a in _asteroids) {
            if (a.isDead()) {
                continue
            }
            if (_player.isColliding(a)) {
                jukebox.play(SFX.explosion)
                destroyedAsteroids++
                playerHealth--
                _player.onCollision(a)
                a.onCollision(_player)
            }
        }
    }

    private fun checkGameOverOrLevelSuccessful() {
        if (playerHealth < 1) {
            if (!isGameOver) {
                isGameOver = true
                jukebox.play(SFX.death)
                jukebox.pauseBgMusic()
            }
        }

        if (destroyedAsteroids == ASTEROID_COUNT) {
            if (!isLevelSuccessful) {
                jukebox.pauseBgMusic()
                jukebox.play(SFX.win)

                isLevelSuccessful = true
            }
        }
    }

    fun removeDeadEntities() {
        val count = _asteroids.size
        for (i in count - 1 downTo 0) {
            if (_asteroids[i].isDead()) {
                _asteroids.removeAt(i)
            }
        }
    }

    fun resume() {
        jukebox.play(SFX.starting)
        jukebox.resumeBgMusic()
        _inputs.onResume()
    }

    fun pause() {
        _inputs.onPause()
        jukebox.pauseBgMusic()
    }

}