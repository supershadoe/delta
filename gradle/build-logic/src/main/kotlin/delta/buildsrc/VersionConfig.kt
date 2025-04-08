package delta.buildsrc

data class VersionConfig(
  val versionCode: Int,
  val versionName: String,
)

val versionConfig
  get() = VersionConfig(
    versionCode = 14,
    versionName = "2025.04+0",
  )
