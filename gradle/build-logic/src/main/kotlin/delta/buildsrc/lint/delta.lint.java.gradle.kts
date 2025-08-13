plugins { id("com.diffplug.spotless") }

spotless {
  java {
    importOrder()
    removeUnusedImports()
    googleJavaFormat("1.28.0").aosp().reflowLongStrings()
    formatAnnotations()
    target("src/*/java/**/*.java")
  }
}
