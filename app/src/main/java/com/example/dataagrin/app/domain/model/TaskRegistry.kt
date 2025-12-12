package com.example.dataagrin.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class TaskRegistry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val area: String,
    val startTime: String,
    val endTime: String,
    val observations: String,
    val isModified: Boolean = false
)
