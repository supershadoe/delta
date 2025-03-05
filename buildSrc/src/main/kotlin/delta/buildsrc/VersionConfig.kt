package delta.buildsrc

data class VersionConfig(
  val versionCode: Int,
  val versionName: String,
)

val versionConfig
  get() = VersionConfig(
    versionCode = 11,
    versionName = "2025.03+2",
  )
