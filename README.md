# Hypixel Public API Mod (Forge 1.8.9)
This is a forge mod implementation of the [Hypixel Public API](https://github.com/HypixelDev/PublicAPI)

## Main idea
Some mod developers might want to use Hypixel's Public API to add certain features to the game, and their documentation does a good job of explaining how to implement this.

**But** the API is for most things only interesting when you use an `API-Key` (see Hypixel's documentation). The API also enforces throttling of the requests (with a default rate of 120 req/min).

It wouldn't really be user-friendly if every mod handled this API part (especially the key part) on their own.

So we came up with this thing!
Features include:
- This mod manages the user's `API-key` in one place
- This mod provides a shared instance of the API to other mods
- This mod checks whether an `API-key` is present and automatically prevents mod from going over the user's current max request rate.

## Getting Started (for Forge users)
Download the latest [release](https://github.com/GrizzlT/HypixelPublicAPIMod/releases) and put the `HypixelPublicAPIMod-*.*.*.jar`-file in your Minecraft mods directory.

That's it, the mod is ready to use!
See [Commands](#Commands) for more information.

## Getting Started (for developers)
This dev-api is available on jitpack (as `com.github.GrizzlT:HypixelPublicAPIMod:<version>`)

Use `HypixelAPIReadyEvent#subscribeToEvent()` to add a listener that will receive the api instance when ready.

Sending a request is as easy as calling `HypixelPublicAPIModApi#handleHypixelAPIRequest` with the necessary request logic. This will return a `CompletableFuture` containing the response.

## Commands
When using this mod you will need to provide your `API-key`. There are several commands to deal with this.

### All you (effectively) need is one command!
- `/hpapiquickstart`

(*Run this command on hypixel.net for it to work!*)

#### For finer control:
- `/hpsetapikey <key>`

Use this command to inform the mod about your `API-key`, the `<key>` argument must be replaced by the [`API-key`](https://github.com/HypixelDev/PublicAPI#obtaining-an-api-key) you got from hypixel.
- `/hpsaveapikey`

Use this command to save the `API-key` to the config file. It will be loaded at startup by default. (*this behavior can be changed in the mod's config*)
#### Other commands:
- `/hpunsetapikey`

Use this command to disable the api. This command is necessary to change your `API-key`.
- `/hploadapikey`

Use this command to load your `API-key` from the config file. This is usually only required if you don't allow the mod to automatically load the key at startup.
