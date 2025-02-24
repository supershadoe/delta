package delta.buildsrc

import kotlinx.serialization.Serializable

@Serializable
data class SigningConfig(
    val keyAlias: String,
    val keyPassword: String,
    val storeFile: String,
    val storePassword: String,
)
