package com.danielpl.glasteroids.util

import com.danielpl.glasteroids.entity.TIME_TO_LIVE
import kotlin.math.PI

object Config {
    val WORLD_WIDTH = 160f //all dimensions are in meters
    val WORLD_HEIGHT = 90f
    val STAR_COUNT = 50
    var INITIAL_ASTEROID_COUNT = 3
    var ASTEROID_COUNT = INITIAL_ASTEROID_COUNT
    val INCREASE_ASTEROIDS_NUMBER = 2
    val METERS_TO_SHOW_X = 160f //160m x 90m, the entire game world in view
    val METERS_TO_SHOW_Y = 90f //TO DO: calculate to match screen aspect ratio
    var SECOND_IN_NANOSECONDS: Long = 1000000000
    var MILLISECOND_IN_NANOSECONDS: Long = 1000000
    var NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS
    var NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS
    const val TO_DEG = 180.0f/PI.toFloat();
    const val TO_RAD = PI.toFloat()/180.0f;
    const val TO_RADIANS = PI.toFloat() / 180.0f
    const val ROTATION_VELOCITY = 400f
    const val THRUST = 8f
    const val DRAG = 0.1f
    const val TIME_BETWEEN_SHOTS = 0.25f //seconds. TO DO: game play setting!
    const val BULLET_COUNT_PLAYER = (TIME_TO_LIVE / TIME_BETWEEN_SHOTS).toInt()+1
    const val BULLET_COUNT_ENEMY = 1
    const val DEFAULT_MUSIC_VOLUME = 0.6f
    const val DISPLAY_FRAME_COUNTER = 10
    var playerHealth = 3
    var level = 1
    @Volatile
    var score = 0
    var MAX_VEL_ASTEROID_SMALL = 20f
    var MIN_VEL_ASTEROID_SMALL = -20f
    var MAX_VEL_ASTEROID_MEDIUM = 15f
    var MIN_VEL_ASTEROID_MEDIUM = -15f
    var MAX_VEL_ASTEROID_LARGE = 11f
    var MIN_VEL_ASTEROID_LARGE = -11f

    val SMALL_ASTEROID_REWARDING_FACTOR = 3
    val MEDIUM_ASTEROID_REWARDING_FACTOR = 2
    val LARGE_ASTEROID_REWARDING_FACTOR = 1

    val BREAK_APART_MEDIUM_ASTEROID = 2
    val BREAK_APART_LARGE_ASTEROID = 3

    val MAX_POINTS_ASTEROIDS = 8
    val MIN_POINTS_ASTEROIDS = 3

    const val INITIAL_LINE_WIDTH = 10f
    const val INITIAL_POINT_SIZE = 8f

    const val POINTS_ENEMY = 3



    @Volatile
    var isGameOver = false

    @Volatile
    var restart = false

    @Volatile
    var isLevelSuccessful = false


    @Volatile
    var destroyedAsteroids = 0


}