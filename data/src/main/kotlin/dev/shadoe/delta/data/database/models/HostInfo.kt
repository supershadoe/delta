package dev.shadoe.delta.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.data.database.converters.MacAddressConverter

@TypeConverters(MacAddressConverter::class)
@Entity
data class HostInfo(
  @PrimaryKey(autoGenerate = false) val macAddress: MacAddress,
  val hostname: String,
)
