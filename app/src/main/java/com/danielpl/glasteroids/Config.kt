package com.danielpl.glasteroids

object Config {
    val WORLD_WIDTH = 160f //all dimensions are in meters
    val WORLD_HEIGHT = 90f
    val STAR_COUNT = 50
    val ASTEROID_COUNT = 15
    val METERS_TO_SHOW_X = 160f //160m x 90m, the entire game world in view
    val METERS_TO_SHOW_Y = 90f //TO DO: calculate to match screen aspect ratio
    var SECOND_IN_NANOSECONDS: Long = 1000000000
    var MILLISECOND_IN_NANOSECONDS: Long = 1000000
    var NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS
    var NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS
}