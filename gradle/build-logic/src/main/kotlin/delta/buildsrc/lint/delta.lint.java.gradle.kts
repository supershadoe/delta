plugins { id("com.diffplug.spotless") }

spotless {
  java {
    importOrder()
    removeUnusedImports()
    googleJavaFormat("1.34.1").aosp().reflowLongStrings()
    formatAnnotations()
    target("src/*/java/**/*.java")
  }
}
