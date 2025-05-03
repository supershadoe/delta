package delta.buildsrc

data class VersionConfig(
  val versionCode: Int,
  val versionName: String,
)

val versionConfig
  get() = VersionConfig(
    versionCode = 16,
    versionName = "2025.05+1",
  )
