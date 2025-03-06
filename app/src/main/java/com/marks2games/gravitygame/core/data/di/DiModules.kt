package com.marks2games.gravitygame.core.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.marks2games.gravitygame.battle_game.data.BattleGameRepositoryFactory
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.building_game.data.repository.EmpireRepositoryImpl
import com.marks2games.gravitygame.building_game.data.repository.PlanetRepositoryImpl
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import com.marks2games.gravitygame.core.data.RealTimeProvider
import com.marks2games.gravitygame.core.domain.TimeProvider
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

    @Provides
    fun provideFirebaseUser(auth: FirebaseAuth): FirebaseUser {
        return auth.currentUser ?: throw IllegalStateException("User is not logged in")
    }

    @Provides
    @Singleton
    fun provideEmpireRepository(
        firestore: FirebaseFirestore,
        user: FirebaseUser
    ): EmpireRepository {
        return EmpireRepositoryImpl(firestore, user)
    }

    @Provides
    @Singleton
    fun providePlanetRepository(
        firestore: FirebaseFirestore,
        user: FirebaseUser
    ): PlanetRepository {
        return PlanetRepositoryImpl(firestore, user)
    }

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider {
        return RealTimeProvider()
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