package com.marks2games.gravitygame.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository: BattleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}