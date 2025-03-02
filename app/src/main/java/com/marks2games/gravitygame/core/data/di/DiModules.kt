package com.marks2games.gravitygame.core.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.marks2games.gravitygame.battle_game.data.BattleGameRepositoryFactory
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.core.data.SharedPreferencesRepository
import com.marks2games.gravitygame.core.domain.authentication.GoogleSign
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
        auth: FirebaseAuth,
        sharedPreferences: SharedPreferencesRepository
    ): GoogleSign {
        return GoogleSign(
            context = context,
            auth = auth,
            sharedPreferences = sharedPreferences
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object BattleModule {

    @Provides
    @Singleton
    fun provideBattleGameRepositoryFactory(auth: FirebaseAuth): BattleGameRepositoryFactory {
        return BattleGameRepositoryFactory(auth)
    }
}