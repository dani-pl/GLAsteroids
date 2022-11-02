package com.danielpl.glasteroids.util

import android.content.Context
import com.danielpl.glasteroids.R
import com.danielpl.glasteroids.entity.Text
import com.danielpl.glasteroids.GLManager
import com.danielpl.glasteroids.util.Config.FPS_TEXT_X
import com.danielpl.glasteroids.util.Config.FPS_TEXT_Y
import com.danielpl.glasteroids.util.Config.GAME_OVER_TEXT_INSTRUCTIONS_X
import com.danielpl.glasteroids.util.Config.GAME_OVER_TEXT_INSTRUCTIONS_Y
import com.danielpl.glasteroids.util.Config.GAME_OVER_TEXT_X
import com.danielpl.glasteroids.util.Config.GAME_OVER_TEXT_Y
import com.danielpl.glasteroids.util.Config.LEVEL_COUNTER_TEXT_X
import com.danielpl.glasteroids.util.Config.LEVEL_COUNTER_TEXT_Y
import com.danielpl.glasteroids.util.Config.LEVEL_SUCCESSFUL_TEXT_INSTRUCTIONS_X
import com.danielpl.glasteroids.util.Config.LEVEL_SUCCESSFUL_TEXT_INSTRUCTIONS_Y
import com.danielpl.glasteroids.util.Config.LEVEL_SUCCESSFUL_TEXT_X
import com.danielpl.glasteroids.util.Config.LEVEL_SUCCESSFUL_TEXT_Y
import com.danielpl.glasteroids.util.Config.LIVES_COUNTER_TEXT_X
import com.danielpl.glasteroids.util.Config.LIVES_COUNTER_TEXT_Y
import com.danielpl.glasteroids.util.Config.SCORE_COUNTER_TEXT_X
import com.danielpl.glasteroids.util.Config.SCORE_COUNTER_TEXT_Y
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
        val millsPerFrame =
            (frameTime * Config.SECOND_IN_NANOSECONDS * Config.NANOSECONDS_TO_MILLISECONDS).toString()
        val fpsX = FPS_TEXT_X
        val fpsY = FPS_TEXT_Y
        fpsCounter.add(Text(context.getString(R.string.fps_counter, millsPerFrame), fpsX, fpsY))

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
                LIVES_COUNTER_TEXT_X,
                LIVES_COUNTER_TEXT_Y
            )
        )
        levelCounter.add(
            Text(
                context.getString(R.string.level_counter, level.toString()),
                LEVEL_COUNTER_TEXT_X,
                LEVEL_COUNTER_TEXT_Y
            )
        )
        scoreCounter.add(
            Text(
                context.getString(R.string.score_counter, score.toString()),
                SCORE_COUNTER_TEXT_X,
                SCORE_COUNTER_TEXT_Y
            )
        )

        for (t in livesCounter) {
            t.render(viewportMatrix, glManager)
        }

        for (t in scoreCounter) {
            t.render(viewportMatrix, glManager)
        }

        for (t in levelCounter) {
            t.render(viewportMatrix, glManager)
        }

    }

    fun gameOverOrLevelSuccessful(viewportMatrix: FloatArray, glManager: GLManager) {

        if (isGameOver) {

            val gameOver = ArrayList<Text>()
            val gameOverInstructions = ArrayList<Text>()

            gameOver.add(
                Text(
                    context.getString(R.string.game_over),
                    GAME_OVER_TEXT_X,
                    GAME_OVER_TEXT_Y
                )
            )

            gameOverInstructions.add(
                Text(
                    context.getString(R.string.game_over_instructions),
                    GAME_OVER_TEXT_INSTRUCTIONS_X,
                    GAME_OVER_TEXT_INSTRUCTIONS_Y
                )
            )

            for (t in gameOver) {
                t.render(viewportMatrix, glManager)
            }

            for (t in gameOverInstructions) {
                t.render(viewportMatrix, glManager)
            }

        } else if (isLevelSuccessful) {

            val levelSuccessful = ArrayList<Text>()
            val levelSuccessfulInstructions = ArrayList<Text>()

            levelSuccessful.add(
                Text(
                    context.getString(R.string.level_successful),
                    LEVEL_SUCCESSFUL_TEXT_X,
                    LEVEL_SUCCESSFUL_TEXT_Y
                )
            )

            levelSuccessfulInstructions.add(
                Text(
                    context.getString(R.string.level_successful_instructions),
                    LEVEL_SUCCESSFUL_TEXT_INSTRUCTIONS_X,
                    LEVEL_SUCCESSFUL_TEXT_INSTRUCTIONS_Y
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