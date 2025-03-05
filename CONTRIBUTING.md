## Contribution guide

Thank you for taking the time to contribute to Delta! :sparkles:

### Steps to follow while reporting a bug

> [!WARNING]
> Follow the steps in [security policy](https://github.com/supershadoe/delta/security/policy)
> if it is a security vulnerability.

- Check if a relevant issue exists in the [issue tracker](https://github.com/supershadoe/delta/issues).
- If an issue doesn't exist, choose the relevant [issue form](https://github.com/supershadoe/delta/issues/new/choose)
  and open a new issue.

### Developer guide

The project has the following modules.
- `:api` - Contains all the classes and interfaces used by all modules in the
app.
- `:app` - Contains all the UI components and the ViewModels used by the app.
- `:data` - Contains the core logic of the app that interfaces with the hidden
Android API. This is what you want to modify if you want to fix something in
the backend of the app.
- `:system-api-stubs` - These are only to be modified when adding new
functionality by using any other hidden API.
- `:buildSrc` - Contains the shared convention plugins and functions to be
reused in other build scripts.

### New contributors

If you do not have an idea where to start, ping `@supershadoe` on Discord or
mail me at [shadoe@shadoe.dev](mailto:shadoe@shadoe.dev)

This is only for getting to know what's the need of the hour and what to get
started with, not for asking to be coached through the process of developing.

Some good resources to start with the project are as follows.
(the links might be broken as Android docs changes up the links often)

- [About app architecture | Android docs](https://developer.android.com/topic/architecture)
- [Localization | Android docs](https://developer.android.com/guide/topics/resources/localization#creating-alternatives)
- [Flows | Kotlin docs](https://kotlinlang.org/docs/flow.html)
- [Flow | Android docs](https://developer.android.com/kotlin/flow)
- [StateFlow and SharedFlow | Android docs](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Jetpack compose](https://developer.android.com/compose)
- [Shizuku-API](https://github.com/RikkaApps/Shizuku-API/)

If you want to utilize some other hidden API for a new feature in the app, make
sure you know what you are doing because there are no specific guides to help
you out with that; the only sources of truth are [Android Code Search](https://cs.android.com/)
and your device.

### Steps to send in a patch for an issue

> [!WARNING]
> Do NOT create a pull request without creating an issue first.
>
> [!NOTE]
> Do NOT make a cosmetic change alone as the formatter setup in the repo will
> take of enforcing formatting and other standards.

- Create a pull request after pushing your patch to GitHub.
- Mark the PR as ready for review (if opened as draft) to start the automated
  testing.
- If all the tests do not pass, fix whatever broke the tests and push again.
- PR will not be accepted without the tests passing.

