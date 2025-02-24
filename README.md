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

Get the release APK from GitHub releases and install; also get Shizuku app
from Play Store.

[<img src="https://github.com/ImranR98/Obtainium/blob/main/assets/graphics/badge_obtainium.png"
alt="Get it on Obtainium"
height="80">](https://github.com/ImranR98/Obtainium)

### Features

Check out the roadmap [here](https://github.com/users/supershadoe/projects/6/views/3)
for better representation of the current status of the project.

- [X] Trigger hotspot from in-app
- [X] Connected devices info
- [X] Edit hotspot settings from app
- [X] Turn off automatically
- [ ] Blocklist
- [ ] Timed connections/Usage limits
- [ ] Tidied up UI

Uses Compose for UI rendering and Android's Hotspot API.
