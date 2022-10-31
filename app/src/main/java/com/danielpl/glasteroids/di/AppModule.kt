package com.danielpl.glasteroids.di

import android.app.Application
import com.danielpl.glasteroids.GLManager
import com.danielpl.glasteroids.util.Jukebox
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*
    @Provides
    @Singleton
    fun provideJukebox(
        app: Application
    ): Jukebox {
        return Jukebox(app.applicationContext)
    }

     */


    @Provides
    @Singleton
    fun provideGlManager(): GLManager{
        return GLManager()
    }




}