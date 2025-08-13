plugins { id("com.diffplug.spotless") }

spotless {
  format("text") {
    target(
      "*.gradle",
      ".gitattributes",
      ".gitignore",
      "*.txt",
      "*.yml",
      "*.yaml",
      "*.toml",
    )
    trimTrailingWhitespace()
    leadingTabsToSpaces()
    endWithNewline()
  }
  flexmark {
    target("*.md")
    flexmark("0.64.8")
  }
}
