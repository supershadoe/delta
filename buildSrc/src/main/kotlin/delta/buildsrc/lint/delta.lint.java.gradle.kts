plugins {
    id("com.diffplug.spotless")
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp().reflowLongStrings()
        formatAnnotations()
        target("**/*.java")
        targetExclude("build/**")
    }
}
