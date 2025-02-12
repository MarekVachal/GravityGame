package com.marks2games.gravitygame.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.marks2games.gravitygame.models.SharedPlayerDataRepository
import com.marks2games.gravitygame.signIn.GoogleSign
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPlayerDataRepository(): SharedPlayerDataRepository {
        return SharedPlayerDataRepository()
    }

    @Provides
    @Singleton
    fun provideMySignIn(
        context: Context,
        auth: FirebaseAuth
    ): GoogleSign {
        return GoogleSign(
            context = context,
            auth = auth
        )
    }
}