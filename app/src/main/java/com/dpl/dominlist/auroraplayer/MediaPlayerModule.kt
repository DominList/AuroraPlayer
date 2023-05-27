package com.dpl.dominlist.auroraplayer

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MediaPlayerModule {

    @Provides
    @ViewModelScoped
    fun provideMediaPlayer(app: Application) : Player
    =  ExoPlayer
        .Builder(app)
        .build()

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(app: Application) : MetaDataReader
    =  MediaDataReader(app)
}