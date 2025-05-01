package dev.shadoe.delta.data.database.converters

import androidx.room.TypeConverter
import dev.shadoe.delta.api.MacAddress

class MacAddressConverter {
  @TypeConverter
  fun macAddressListToString(macAddressList: List<MacAddress>) =
    macAddressList.joinToString(",") { macAddressToString(it) }

  @TypeConverter
  fun stringToMacAddressList(serialized: String) =
    serialized.split(",").map { stringToMacAddress(it) }

  @TypeConverter
  fun macAddressToString(macAddress: MacAddress) = macAddress.macAddress

  @TypeConverter
  fun stringToMacAddress(serialized: String) = MacAddress(serialized)
}
