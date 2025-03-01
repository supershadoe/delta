import delta.buildsrc.versionConfig

tasks.register("getAppVersionCode") {
  group = "Help"
  description = "Prints the app version code"
  doLast { println(versionConfig.versionCode) }
}

tasks.register("getAppVersionName") {
  group = "Help"
  description = "Prints the app version name"
  doLast { println(versionConfig.versionName) }
}

tasks.register("getAppVersion") {
  group = "Help"
  description = "Prints the app version config"
  doLast { println(versionConfig) }
}
