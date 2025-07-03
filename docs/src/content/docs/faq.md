---
title: FAQ
description: Frequently Asked Questions about Delta
sidebar:
    order: 2
---

Here are answers to some of the questions that users have asked multiple
times in the past.

## Does this app require root?

**NO!**

This app does not require root access and the development is also focused
on implementing features **without** requiring root access.

But, it does require Shizuku to get ADB privileges.

## Shizuku gets disabled when hotspot is turned on.

If you have USB debugging enabled as well in developer options, Shizuku
continues to operate **even if wireless debugging turns off**.

This happens only in certain models where WiFi sharing is not supported but
I have seen this concern from an overwhelming amount of people.

## Why is Shizuku required?

Android Debug Bridge (ADB) is a tool that developers use to test their apps
on devices before pushing them to markets like Google Play Store. But, ADB
is also used internally by Google to test all the system features while
developing Android itself.

Due to this reason, ADB has certain privileges that a normal app don't have
and Shizuku is an app that safely provides access to ADB by letting the user
be in control by providing permission controls while also letting apps use
system-level APIs more easily.

Hotspot and other network settings are very sensitive and thus, access to those
settings is not provided to any app other than the Settings app. Thus, Delta
uses Shizuku to access and modify hotspot settings.

## Will you add "X" feature to the app?

Create a discussion or issue on the [GitHub repo](https://github.com/supershadoe/delta)
and I'll assess how it can be implemented, whether it can be done and
decide accordingly!

This app is maintained single-handedly by me. So, adding new features or
reviewing large PRs (pull requests) take time given my other responsibilities.
Thus, discuss once in the issues/discussion thread before making a huge change
to the app.

## Will you add root-specific features to the app?

Maintaining a separate implementation only for rooted devices adds more
complexity to the app which requires a lot of maintenance already.

It is already unstable with Android's internals changing with every Android
version and with each OEM implementing networking on their devices differently.

Also, I do not have a rooted device to test with and emulators are flaky
because the same APIs that work on emulators running a specific Android version
do not work on real life devices.

So, nope, I will not add root-specific features to the app.

## Can you make this app work without Shizuku as well?

Nope, because that would require running an ADB client in app for non-rooted
devices and using `su` for rooted devices, both of which provide text shells.

The single advantage that Shizuku has over plain `su` or the adb shell is
the fact that Shizuku lets you perform Remote binder calls by letting a server
process run with ADB privileges and proxying your function calls to the system
through itself.

Due to this functionality, developers like me can call system APIs directly
using code, instead of parsing text data, which is more efficient.

Also, there is no shell command to communicate with tethering API on Android,
so even if we can potentially get root access through Magisk or something else,
we can't use the functionalities directly.

## Some functionality do not work on my device!

Every manufacturer implements the networking stack differently either due to
specific hardware requirements, legacy code that they do not want to touch,
etc.

Samsung is especially notorious because they have already implemented these
functionality years before other OEMs did and thus, the entire codebase and
APIs they use are different.

I only implement features using Android's source code provided by Google as
reference. Most of the OEMs adapt to these APIs over time and these APIs are
the ones I can trust to work on majority of the devices (but even that trust
is broken on Samsung)

I am not planning on supporting every edge case on every model by implementing
device specific API as those API aren't accessible using ADB sometimes.

I will do my best to debug and reverse-engineer every crash/failure and provide
mitigations if possible but I will NOT adding features that work only on certain
models.

## IP addresses for the devices change every time.

This is something that I cannot control. Refer to this [comment](https://github.com/supershadoe/delta/issues/46#issuecomment-2734413820)
for a low-level analysis.
