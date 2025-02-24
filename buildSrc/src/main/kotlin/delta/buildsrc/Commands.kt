package delta.buildsrc

import kotlinx.serialization.json.Json
import org.gradle.api.Project
import java.io.File
import java.nio.charset.Charset

fun Project.readSigningConfig(file: File) = file
    .takeIf { it.exists() }
    ?.runCatching {
        Json.decodeFromString<SigningConfig>(
            string = readText(charset = Charset.defaultCharset()),
        )
    }?.getOrNull()
