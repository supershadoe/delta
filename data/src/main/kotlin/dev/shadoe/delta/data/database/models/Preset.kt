package dev.shadoe.delta.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class Preset(@PrimaryKey(autoGenerate = true) val id: Int)
