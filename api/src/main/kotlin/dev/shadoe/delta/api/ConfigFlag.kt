package dev.shadoe.delta.api

/**
 * Holds certain flags used to determine the app behavior.
 *
 * Possible usage: Determining first run, app version migrations, etc.
 */
enum class ConfigFlag {
  /** True if the app has been run before. */
  NOT_FIRST_RUN,

  /**
   * True if the app uses Room for config.
   *
   * This is set either after migrating app from Datastore when upgrading from
   * older versions or when app is installed afresh.
   */
  USES_ROOM_DB,
}
