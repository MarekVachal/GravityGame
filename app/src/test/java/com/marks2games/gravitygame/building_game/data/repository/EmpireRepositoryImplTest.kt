package com.marks2games.gravitygame.building_game.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.SmallPlanet
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test

class EmpireRepositoryImplTest {

    @Test
    fun `planet writes follow sign-in, account switch and sign-out on the same repository`() = runBlocking {
        var currentUser: FirebaseUser? = null
        val auth = mockk<FirebaseAuth>()
        every { auth.currentUser } answers { currentUser }
        val firestore = mockk<FirebaseFirestore>()
        val repository = EmpireRepositoryImpl(firestore, auth, mockk())
        val planet = Planet(id = 1, type = SmallPlanet)
        val firstPlanetDoc = mockk<DocumentReference>()
        val secondPlanetDoc = mockk<DocumentReference>()
        val writeTask = mockk<Task<Void>> {
            every { isComplete } returns true
            every { isCanceled } returns false
            every { exception } returns null
            every { result } returns null
        }
        every {
            firestore.collection("empires").document("first").collection("planets").document("1")
        } returns firstPlanetDoc
        every {
            firestore.collection("empires").document("second").collection("planets").document("1")
        } returns secondPlanetDoc
        every { firstPlanetDoc.set(planet) } returns writeTask
        every { secondPlanetDoc.set(planet) } returns writeTask

        repository.updatePlanet(planet)
        verify(exactly = 0) { firestore.collection("empires") }

        currentUser = mockk { every { uid } returns "first" }
        repository.updatePlanet(planet)
        verify(exactly = 1) { firstPlanetDoc.set(planet) }

        currentUser = mockk { every { uid } returns "second" }
        repository.updatePlanet(planet)
        verify(exactly = 1) { secondPlanetDoc.set(planet) }

        currentUser = null
        repository.updatePlanet(planet)
        verify(exactly = 2) { firestore.collection("empires") }
    }
}
