package com.marks2games.gravitygame.core.data.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.marks2games.gravitygame.battle_game.data.BattleGameRepositoryFactory
import com.marks2games.gravitygame.battle_game.data.SharedPlayerDataRepository
import com.marks2games.gravitygame.building_game.data.repository.EmpireRepositoryImpl
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import com.marks2games.gravitygame.core.data.RealTimeProvider
import com.marks2games.gravitygame.core.domain.TimeProvider
import com.marks2games.gravitygame.core.data.datasource.GoogleAuthHelper
import com.marks2games.gravitygame.core.data.repository.AuthRepositoryImpl
import com.marks2games.gravitygame.core.data.repository.SharedPreferencesRepositoryImpl
import com.marks2games.gravitygame.core.domain.repository.AuthRepository
import com.marks2games.gravitygame.core.domain.repository.SharedPreferencesRepository
import com.marks2games.gravitygame.core.domain.usecases.sharedRepository.SetHasSignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGoogleAuthHelper(): GoogleAuthHelper {
        return GoogleAuthHelper()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideFirebaseUser(auth: FirebaseAuth): FirebaseUser? {
        return auth.currentUser
    }


    @Provides
    @Singleton
    fun provideEmpireRepository(
        firestore: FirebaseFirestore,
        user: FirebaseUser?
    ): EmpireRepository {
        return EmpireRepositoryImpl(firestore, user)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        setHasSignInUseCase: SetHasSignInUseCase,
        googleAuthHelper: GoogleAuthHelper
    ): AuthRepository {
        return AuthRepositoryImpl(
            auth = auth,
            setHasSignInUseCase = setHasSignInUseCase,
            googleAuthHelper = googleAuthHelper
        )
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesRepository(
        sharedPreferences: SharedPreferences
    ): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider {
        return RealTimeProvider()
    }

    @Provides
    @Singleton
    fun provideBattleGameRepositoryFactory(
        auth: FirebaseAuth,
        sharedPlayerDataRepository: SharedPlayerDataRepository
    ): BattleGameRepositoryFactory {
        return BattleGameRepositoryFactory(
            sharedPlayerDataRepository = sharedPlayerDataRepository,
            auth = auth
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideSharedPlayerDataRepository(): SharedPlayerDataRepository {
        return SharedPlayerDataRepository()
    }
}