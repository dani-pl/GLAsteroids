package com.danielpl.glasteroids.util

import android.opengl.GLES20
import com.danielpl.glasteroids.entity.Mesh
import kotlin.math.PI

object Config {

    // General Dimensions
    const val WORLD_WIDTH = 160f //all dimensions are in meters
    const val WORLD_HEIGHT = 90f
    const val METERS_TO_SHOW_X = 160f //160m x 90m, the entire game world in view
    const val METERS_TO_SHOW_Y = 90f

    // Time related
    var SECOND_IN_NANOSECONDS: Long = 1000000000
    private var MILLISECOND_IN_NANOSECONDS: Long = 1000000
    var NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS
    var NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS
    const val TIME_BETWEEN_SHOTS_PLAYER = 0.25f
    const val TIME_BETWEEN_SHOTS_ENEMY = 2f
    const val INITIAL_BULLET_COOL_DOWN_ENEMY = 5f

    // Speed of entities related
    var MAX_VEL_ASTEROID_SMALL = 20f
    var MIN_VEL_ASTEROID_SMALL = -20f
    var MAX_VEL_ASTEROID_MEDIUM = 15f
    var MIN_VEL_ASTEROID_MEDIUM = -15f
    var MAX_VEL_ASTEROID_LARGE = 11f
    var MIN_VEL_ASTEROID_LARGE = -11f
    const val ROTATION_VELOCITY = 400f
    const val THRUST = 8f
    const val DRAG = 0.1f
    const val SPEED_BULLET = 120f
    const val TIME_TO_LIVE_BULLET = 3.0f //seconds

    // Number of entities related
    const val STAR_COUNT = 50
    var INITIAL_ASTEROID_COUNT = 1
    var ASTEROID_COUNT = INITIAL_ASTEROID_COUNT
    const val INCREASE_ASTEROIDS_NUMBER = 2
    const val BULLET_COUNT_PLAYER = (TIME_TO_LIVE_BULLET / TIME_BETWEEN_SHOTS_PLAYER).toInt() + 1
    const val BULLET_COUNT_ENEMY = 1
    const val BREAK_APART_MEDIUM_ASTEROID = 2
    const val BREAK_APART_LARGE_ASTEROID = 3
    const val ENEMY_APPEARANCE_POSSIBILITIES = 1500

    @Volatile
    var destroyedAsteroids = 0

    @Volatile
    var thereIsAnEnemy = false

    // Position of entities related
    const val PLAYER_INITIAL_COORDINATE_X = WORLD_WIDTH / 2
    const val PLAYER_INITIAL_COORDINATE_Y = WORLD_HEIGHT / 2
    const val BORDER_INITIAL_COORDINATE_X = WORLD_WIDTH / 2
    const val BORDER_INITIAL_COORDINATE_Y = WORLD_HEIGHT / 2
    const val BORDER_FINAL_COORDINATE_X = WORLD_WIDTH
    const val BORDER_FINAL_COORDINATE_Y = WORLD_HEIGHT
    const val ANGLE_DIFFERENCE_WITH_SHOOTING_VERTEX = 3*PI/2


    // Size of entities related
    const val PLAYER_WIDTH = 8f
    const val PLAYER_HEIGHT = 12f
    const val ENEMY_WIDTH = PLAYER_WIDTH
    const val ENEMY_HEIGHT = PLAYER_HEIGHT
    const val RADIUS_BORDER = 10.0f
    const val SMALL_ASTEROID_SIZE = 8f
    const val MEDIUM_ASTEROID_SIZE = 12f
    const val LARGE_ASTEROID_SIZE = 15f

    // shape of entities related
    const val POINTS_BORDER = 4
    const val MAX_POINTS_ASTEROIDS = 8
    const val MIN_POINTS_ASTEROIDS = 3


    // Score related
    const val SMALL_ASTEROID_REWARDING_FACTOR = 3
    const val MEDIUM_ASTEROID_REWARDING_FACTOR = 2
    const val LARGE_ASTEROID_REWARDING_FACTOR = 1
    const val SAUCER_ENEMY_REWARD = 100

    @Volatile
    var score = 0

    // Music related
    const val DEFAULT_MUSIC_VOLUME = 0.6f
    const val MAX_STREAMS = 3
    const val BOOST_SOUND_DELAY = 100000

    // FPS related
    const val DISPLAY_FRAME_COUNTER = 10

    // Angle conversion related
    const val TO_DEGREES = (180.0f / PI).toFloat()
    const val TO_RADIANS = (PI / 180.0f).toFloat()

    // Level related
    const val INITIAL_LEVEL = 1
    var level = INITIAL_LEVEL
    @Volatile
    var isLevelSuccessful = false


    // Lives related
    var playerHealth = 3
    const val PLAYER_INITIAL_LIVES = 3

    @Volatile
    var isGameOver = false

    @Volatile
    var restart = false

    // Color related
    val BACKGROUND_COLOR = floatArrayOf(0f / 255f, 0f / 255f, 84f / 255f, 1f)
    val ENEMY_COLOR = floatArrayOf(255f / 255f, 0f / 255f, 0f / 255f, 1f)
    val BORDER_COLOR = floatArrayOf(255f / 255f, 0f / 255f, 0f / 255f, 1f)
    val SHOOTING_COLOR = floatArrayOf(255f / 255f, 0f / 255f, 1f / 255f, 1f)
    val PLAYER_COLOR = floatArrayOf(255f / 255f, 255f / 255f, 255f / 255f, 1f)


    // Other settings
    const val INITIAL_LINE_WIDTH = 10f
    const val INITIAL_POINT_SIZE = 8f
    const val TEXT_SCALING = 0.75f
    const val OFFSET = 0 //just to have a name for the parameter
    const val dt = 0.01f
    const val EGL_CONTEXT_CLIENT_VERSION = 2
    const val SIZE_OF_FLOAT = java.lang.Float.SIZE / java.lang.Byte.SIZE //32bit/8bit = 4 bytes
    const val COORDINATES_PER_VERTEX = 3 //X, Y, Z
    const val VERTEX_STRIDE = COORDINATES_PER_VERTEX * SIZE_OF_FLOAT

    // Render Hud positions
    const val GAME_OVER_TEXT_X = METERS_TO_SHOW_X * 0.35f
    const val GAME_OVER_TEXT_Y = METERS_TO_SHOW_Y * 0.4f
    const val GAME_OVER_TEXT_INSTRUCTIONS_X = METERS_TO_SHOW_X * 0.30f
    const val GAME_OVER_TEXT_INSTRUCTIONS_Y = METERS_TO_SHOW_Y * 0.55f
    const val LEVEL_SUCCESSFUL_TEXT_X = METERS_TO_SHOW_X * 0.35f
    const val LEVEL_SUCCESSFUL_TEXT_Y = METERS_TO_SHOW_Y * 0.4f
    const val LEVEL_SUCCESSFUL_TEXT_INSTRUCTIONS_X = METERS_TO_SHOW_X * 0.15f
    const val LEVEL_SUCCESSFUL_TEXT_INSTRUCTIONS_Y = METERS_TO_SHOW_Y * 0.55f
    const val FPS_TEXT_X = METERS_TO_SHOW_X * 0.1f
    const val FPS_TEXT_Y = METERS_TO_SHOW_Y * 0.9f
    const val LIVES_COUNTER_TEXT_X = METERS_TO_SHOW_X * 0.05f
    const val LIVES_COUNTER_TEXT_Y = METERS_TO_SHOW_Y * 0.05f
    const val LEVEL_COUNTER_TEXT_X = METERS_TO_SHOW_X * 0.30f
    const val LEVEL_COUNTER_TEXT_Y = METERS_TO_SHOW_Y * 0.05f
    const val SCORE_COUNTER_TEXT_X = METERS_TO_SHOW_X * 0.55f
    const val SCORE_COUNTER_TEXT_Y = METERS_TO_SHOW_Y * 0.05f


    // Graphics related
    const val GLYPH_SPACING = 1f
    const val GLYPH_WIDTH = 5 //characters are 5 units wide
    const val GLYPH_HEIGHT = 7 //characters are 7 units tall
    const val CHAR_COUNT = 46 //the font definition contains 45 entries
    const val CHAR_OFFSET = 45 //it start at ASCII code 45 "-", and ends at 90 "Z".
    //FONT_DEFINITION contains most of basic ASCII characters 45-90:
    //Specifically: [0,9], uppercase [A,Z] and - . : = ?
    const val FONT_DEFINITION =
            /*[ind asc    sym]*/
        /*[0   45 '-']*/     "00000" + "00000" + "00000" + "11111" + "00000" + "00000" + "00000" + //-
            /*[1   46 '.']*/     "00000" + "00000" + "00000" + "00000" + "00000" + "01100" + "01100" + //.
            /*[2   47 '/']*/     "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[3   48 '0']*/     "01110" + "10001" + "10011" + "10101" + "11001" + "10001" + "01110" + //0
            /*[4   49 '1']*/     "00100" + "01100" + "00100" + "00100" + "00100" + "00100" + "01110" + //1
            /*[5   50 '2']*/     "01110" + "10001" + "00001" + "00010" + "00100" + "01000" + "11111" + //2
            /*[6   51 '3']*/     "01110" + "10001" + "00001" + "00110" + "00001" + "10001" + "01110" + //3
            /*[7   52 '4']*/     "00010" + "00110" + "01010" + "10010" + "11111" + "00010" + "00111" + //4
            /*[8   53 '5']*/     "11111" + "10000" + "11110" + "00001" + "00001" + "10001" + "01110" + //5
            /*[9   54 '6']*/     "01110" + "10001" + "10000" + "11110" + "10001" + "10001" + "01110" + //6
            /*[10  55 '7']*/     "11111" + "10001" + "00010" + "00010" + "00100" + "00100" + "00100" + //7
            /*[11  56 '8']*/     "01110" + "10001" + "10001" + "01110" + "10001" + "10001" + "01110" + //8
            /*[12  57 '9']*/     "01110" + "10001" + "10001" + "01111" + "00001" + "00001" + "01110" + //9
            /*[13  58 ':']*/     "00000" + "01100" + "01100" + "00000" + "01100" + "01100" + "00000" + //:
            /*[14  59 ';']*/     "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[15  60 '<']*/     "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[16  61 '=']*/     "00000" + "00000" + "11111" + "00000" + "11111" + "00000" + "00000" + //=
            /*[17  62 '>']*/     "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[18  63 '?']*/     "01110" + "10001" + "10001" + "00010" + "00100" + "00000" + "00100" + //?
            /*[19  64 '@']*/     "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + "11111" + //
            /*[20  65 'A']*/     "01110" + "10001" + "10001" + "11111" + "10001" + "10001" + "10001" + //A
            /*[21  66 'B']*/     "11110" + "10001" + "10001" + "11110" + "10001" + "10001" + "11110" + //B
            /*[22  67 'C']*/     "01110" + "10001" + "10000" + "10000" + "10000" + "10001" + "01110" + //C
            /*[23  68 'D']*/     "11110" + "10001" + "10001" + "10001" + "10001" + "10001" + "11110" + //D
            /*[24  69 'E']*/     "11111" + "10000" + "10000" + "11110" + "10000" + "10000" + "11111" + //E
            /*[25  70 'F']*/     "11111" + "10000" + "10000" + "11110" + "10000" + "10000" + "10000" + //F
            /*[26  71 'G']*/     "01110" + "10001" + "10000" + "10111" + "10001" + "10001" + "01110" + //G
            /*[27  72 'H']*/     "10001" + "10001" + "10001" + "11111" + "10001" + "10001" + "10001" + //H
            /*[28  73 'I']*/     "01110" + "00100" + "00100" + "00100" + "00100" + "00100" + "01110" + //I
            /*[29  74 'J']*/     "00001" + "00001" + "00001" + "00001" + "10001" + "10001" + "01110" + //J
            /*[30  75 'K']*/     "10001" + "10010" + "10100" + "11000" + "10100" + "10010" + "10001" + //K
            /*[31  76 'L']*/     "10000" + "10000" + "10000" + "10000" + "10000" + "10000" + "11111" + //L
            /*[32  77 'M']*/     "10001" + "11011" + "10101" + "10101" + "10001" + "10001" + "10001" + //M
            /*[33  78 'N']*/     "10001" + "10001" + "11001" + "10101" + "10011" + "10001" + "10001" + //N
            /*[34  79 'O']*/     "01110" + "10001" + "10001" + "10001" + "10001" + "10001" + "01110" + //O
            /*[35  80 'P']*/     "11110" + "10001" + "10001" + "11110" + "10000" + "10000" + "10000" + //P
            /*[36  81 'Q']*/     "01110" + "10001" + "10001" + "10001" + "10101" + "10010" + "01101" + //Q
            /*[37  82 'R']*/     "11110" + "10001" + "10001" + "11110" + "10100" + "10010" + "10001" + //R
            /*[38  83 'S']*/     "01111" + "10000" + "10000" + "01110" + "00001" + "00001" + "11110" + //S
            /*[39  84 'T']*/     "11111" + "00100" + "00100" + "00100" + "00100" + "00100" + "00100" + //T
            /*[40  85 'U']*/     "10001" + "10001" + "10001" + "10001" + "10001" + "10001" + "01110" + //U
            /*[41  86 'V']*/     "10001" + "10001" + "10001" + "10001" + "10001" + "01010" + "00100" + //V
            /*[42  87 'W']*/     "10001" + "10001" + "10001" + "10101" + "10101" + "10101" + "01010" + //W
            /*[43  88 'X']*/     "10001" + "10001" + "01010" + "00100" + "01010" + "10001" + "10001" + //X
            /*[44  89 'Y']*/     "10001" + "10001" + "10001" + "01010" + "00100" + "00100" + "00100" + //Y
            /*[45  90 'Z']*/     "11111" + "00001" + "00010" + "00100" + "01000" + "10000" + "11111"  //Z
    val BLANK_SPACE = Mesh(floatArrayOf(0.0f), GLES20.GL_POINTS)
}