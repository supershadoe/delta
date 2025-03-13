package delta.buildsrc

data class VersionConfig(
  val versionCode: Int,
  val versionName: String,
)

val versionConfig
  get() = VersionConfig(
    versionCode = 13,
    versionName = "2025.03+4",
  )
