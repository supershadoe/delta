package dev.shadoe.delta.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout
import dev.shadoe.delta.api.SoftApRandomizationSetting
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSpeedType
import dev.shadoe.delta.data.database.converters.PresetConverter

@TypeConverters(PresetConverter::class)
@Entity
data class Preset(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(defaultValue = "") val passphrase: String = "",
  val ssid: String?,
  @ColumnInfo(defaultValue = SoftApSecurityType.SECURITY_TYPE_OPEN.toString())
  @SoftApSecurityType.SecurityType
  val securityType: Int,
  @ColumnInfo(
    defaultValue = SoftApRandomizationSetting.RANDOMIZATION_NONE.toString()
  )
  @SoftApRandomizationSetting.RandomizationType
  val macRandomizationSetting: Int,
  @ColumnInfo(defaultValue = "0") val isHidden: Boolean,
  @ColumnInfo(defaultValue = SoftApSpeedType.BAND_2GHZ.toString())
  @SoftApSpeedType.BandType
  val speedType: Int,
  @ColumnInfo(defaultValue = "") val blockedDevices: List<MacAddress>,
  @ColumnInfo(defaultValue = "") val allowedClients: List<MacAddress>,
  @ColumnInfo(defaultValue = "0") val isAutoShutdownEnabled: Boolean,
  /** Timeout in milliseconds for auto shutdown. */
  @ColumnInfo(defaultValue = SoftApAutoShutdownTimeout.DEFAULT.toString())
  val autoShutdownTimeout: Long,
  @ColumnInfo(defaultValue = "0") val maxClientLimit: Int,
)
