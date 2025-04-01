package dev.shadoe.delta.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HostInfo(
  @PrimaryKey(autoGenerate = false) val macAddress: String,
  val hostName: String,
)
