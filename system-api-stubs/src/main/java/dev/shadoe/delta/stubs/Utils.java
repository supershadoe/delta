package dev.shadoe.delta.stubs;

public class Utils {
    /** Polyfills for old compileSdk till Android 16 SDK is compatible with a stable AGP version. */
    public static class VERSION_CODES {
        private VERSION_CODES() {}

        public static final int BAKLAVA = 36;
    }

    private Utils() {}
}
