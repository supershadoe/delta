package dev.shadoe.delta.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Flag(
  @PrimaryKey(autoGenerate = false) val flag: Int,
  val value: Boolean,
)
