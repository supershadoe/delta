### delta
This is an in-development app to let some apps use mobile data always
regardless of being connected to a WiFi network — to go over firewalls more
conveniently.

This app is intended for devices which don't have a
"Mobile data only apps" setting like Samsung.

Uses Flutter for UI rendering and Android's VPN service for the
_split-tunneling_ (using the word loosely here) functionality.

Although this app doesn't need root to run, you can't run this app
simultaneously with another VPN app.
