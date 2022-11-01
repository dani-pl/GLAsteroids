package com.danielpl.glasteroids.util

import android.content.Context
import com.danielpl.glasteroids.R
import com.danielpl.glasteroids.entity.Text
import com.danielpl.glasteroids.GLManager
import com.danielpl.glasteroids.util.Config.METERS_TO_SHOW_X
import com.danielpl.glasteroids.util.Config.METERS_TO_SHOW_Y
import com.danielpl.glasteroids.util.Config.isGameOver
import com.danielpl.glasteroids.util.Config.isLevelSuccessful
import com.danielpl.glasteroids.util.Config.level
import com.danielpl.glasteroids.util.Config.playerHealth
import com.danielpl.glasteroids.util.Config.score

class RenderHud(
    private val context: Context
) {

    fun showFps(frameTime: Float, viewportMatrix: FloatArray, glManager: GLManager) {
        val fpsCounter = ArrayList<Text>()
        val mill_per_frame =
            (frameTime * Config.SECOND_IN_NANOSECONDS * Config.NANOSECONDS_TO_MILLISECONDS).toString()
        val fps_x = METERS_TO_SHOW_X * 0.1f
        val fps_y = METERS_TO_SHOW_Y * 0.9f
        fpsCounter.add(Text(context.getString(R.string.fps_counter, mill_per_frame), fps_x, fps_y))

        for (t in fpsCounter) {
            t.render(viewportMatrix, glManager)
        }
    }

    fun showPlayerInfo(viewportMatrix: FloatArray, glManager: GLManager) {
        val livesCounter = ArrayList<Text>()
        val levelCounter = ArrayList<Text>()
        val scoreCounter = ArrayList<Text>()

        livesCounter.add(
            Text(
                context.getString(R.string.lives_counter, playerHealth.toString()),
                METERS_TO_SHOW_X * 0.05f,
                METERS_TO_SHOW_Y * 0.05f
            )
        )
        levelCounter.add(
            Text(
                context.getString(R.string.level_counter, level.toString()),
                METERS_TO_SHOW_X * 0.30f,
                METERS_TO_SHOW_Y * 0.05f
            )
        )
        scoreCounter.add(
            Text(
                context.getString(R.string.score_counter, score.toString()),
                METERS_TO_SHOW_X * 0.55f,
                METERS_TO_SHOW_Y * 0.05f
            )
        )

        for (t in livesCounter){
            t.render(viewportMatrix, glManager)
        }

        for (t in scoreCounter){
            t.render(viewportMatrix, glManager)
        }

        for (t in levelCounter){
            t.render(viewportMatrix, glManager)
        }

    }

    fun gameOverOrLevelSuccessful(viewportMatrix: FloatArray, glManager: GLManager) {

        if(isGameOver) {

            val gameOver = ArrayList<Text>()
            val gameOverInstructions = ArrayList<Text>()

            gameOver.add(
                Text(
                    context.getString(R.string.game_over),
                    METERS_TO_SHOW_X * 0.35f,
                    METERS_TO_SHOW_Y * 0.4f
                )
            )

            gameOverInstructions.add(
                Text(
                    context.getString(R.string.game_over_instructions),
                    METERS_TO_SHOW_X * 0.30f,
                    METERS_TO_SHOW_Y * 0.55f
                )
            )

            for (t in gameOver) {
                t.render(viewportMatrix, glManager)
            }

            for (t in gameOverInstructions) {
                t.render(viewportMatrix, glManager)
            }

        } else if(isLevelSuccessful){

            val levelSuccessful = ArrayList<Text>()
            val levelSuccessfulInstructions = ArrayList<Text>()

            levelSuccessful.add(
                Text(
                    context.getString(R.string.level_successful),
                    METERS_TO_SHOW_X * 0.35f,
                    METERS_TO_SHOW_Y * 0.4f
                )
            )

            levelSuccessfulInstructions.add(
                Text(
                    context.getString(R.string.level_successful_instructions),
                    METERS_TO_SHOW_X * 0.15f,
                    METERS_TO_SHOW_Y * 0.55f
                )
            )

            for (t in levelSuccessful) {
                t.render(viewportMatrix, glManager)
            }

            for (t in levelSuccessfulInstructions) {
                t.render(viewportMatrix, glManager)
            }


        }


    }

}