## delta

This is an in-development app to create hotspots on stock Android with
more functionalities like a blocklist, usage limits, etc.

This app can trigger hotspot on devices like Samsung Tabs where tethering is
hidden from Settings app and SystemUI for Wi-Fi only devices.

App was tested on a Pixel 6A with Android 15, compatibility with other devices
is questionable at best as Android's internals change without a stable API.

The app is supposed to work on devices with Android >= 11 (R), feel free to file
an issue if it doesn't work as expected. (time to respond to issues may be high)

### How to use
Get the pre-release APK from GitHub releases and install; also get Shizuku app
from Play Store.

### Features
- [X] Trigger hotspot from in-app
- [X] Connected devices info
- [ ] Edit hotspot settings from app
- [ ] Blocklist
- [ ] Turn off automatically
- [ ] Timed connections/Usage limits
- [ ] Tidied up UI

Uses Compose for UI rendering and Android's Hotspot API.
