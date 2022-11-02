package com.danielpl.glasteroids.util

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.danielpl.glasteroids.R
import com.danielpl.glasteroids.util.Config.DEFAULT_MUSIC_VOLUME
import com.danielpl.glasteroids.util.Config.MAX_STREAMS
import com.danielpl.glasteroids.util.Config.level
import com.danielpl.glasteroids.util.Config.thereIsAnEnemy
import java.io.IOException

object SFX {
    var boost = 0
    var starting = 1
    var death = 2
    var explosion = 3
    var win = 4
    var shoot = 5
    var saucerEnemy = 6
}

class Jukebox(engine: Context) {
    private val assetManager = engine.assets
    private val soundPool: SoundPool
    private var mBgPlayer: MediaPlayer? = null
    private var mMusicEnabled = true


    init {
        val attr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(attr)
            .setMaxStreams(MAX_STREAMS)
            .build()
        Log.d(R.string.jukebox_tag.toString(), "sfx/soundPool created!")
        SFX.boost = loadSound("sfx/boost.wav")
        SFX.starting = loadSound("sfx/starting.wav")
        SFX.death = loadSound("sfx/death.wav")
        SFX.explosion = loadSound("sfx/explosion.wav")
        SFX.shoot = loadSound("sfx/shoot.wav")
        SFX.win = loadSound("sfx/win.wav")
        SFX.saucerEnemy = loadSound("sfx/saucer_enemy.wav")
        loadMusic()
    }

    private fun loadSound(fileName: String): Int {
        try {
            val descriptor: AssetFileDescriptor = assetManager.openFd(fileName)
            return soundPool.load(descriptor, 1)
        } catch (e: IOException) {
            Log.d(
                R.string.jukebox_tag.toString(),
                "Unable to load $fileName! Check the filename, and make sure it's in the assets-folder."
            )
        }
        return 0
    }

    fun play(soundID: Int) {
        val leftVolume = 1f
        val rightVolume = 1f
        val priority = 0
        val loop = 0
        val playbackRate = 1.0f
        if (soundID > 0) {
            soundPool.play(soundID, leftVolume, rightVolume, priority, loop, playbackRate)
        }
    }

    /*
    Function is never used
    fun destroy() {
        soundPool.release()
        //the soundPool can no longer be used! you have to create a new soundPool.
    }
     */

    private fun loadMusic() {
        try {
            mBgPlayer = MediaPlayer()

            var afd = assetManager.openFd("bgm/background_music_1.mp3")
            if (thereIsAnEnemy) {
                afd = assetManager.openFd("bgm/background_music_enemy.mp3")
            } else if (level % 2 == 0) {
                afd = assetManager.openFd("bgm/background_music_2.mp3")
            }

            mBgPlayer!!.setDataSource(
                afd.fileDescriptor,
                afd.startOffset,
                afd.length
            )
            mBgPlayer!!.isLooping = true
            mBgPlayer!!.setVolume(DEFAULT_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME)
            mBgPlayer!!.prepare()
        } catch (e: IOException) {
            //Log.e(TAG, "Unable to create MediaPlayer.", e)
        }
    }

    fun pauseBgMusic() {
        if (!mMusicEnabled) {
            return
        }
        mBgPlayer!!.pause()
    }

    fun resumeBgMusic() {
        if (!mMusicEnabled) {
            return
        }
        mBgPlayer!!.start()
    }


    private fun unloadMusic() {
        if (mBgPlayer == null) {
            return
        }
        mBgPlayer!!.stop()
        mBgPlayer!!.release()
    }

    fun changeBgMusic() {
        unloadMusic()
        loadMusic()
        resumeBgMusic()
    }

}