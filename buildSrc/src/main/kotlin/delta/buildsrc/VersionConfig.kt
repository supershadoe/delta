package delta.buildsrc

data class VersionConfig(
  val versionCode: Int,
  val versionName: String,
)

val versionConfig
  get() = VersionConfig(
    versionCode = 8,
    versionName = "0.2.3",
  )
