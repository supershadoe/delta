package dev.shadoe.delta.data.database.converters

import androidx.room.TypeConverter
import dev.shadoe.delta.api.MacAddress

class PresetConverter {
  @TypeConverter
  fun macAddressListToString(macAddressList: List<MacAddress>) =
    macAddressList.joinToString(",") { it.macAddress }

  @TypeConverter
  fun stringToMacAddressList(serialized: String) =
    serialized.split(",").map { macAddress -> MacAddress(macAddress) }
}
