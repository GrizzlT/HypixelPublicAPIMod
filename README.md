# Hypixel Public API Mod (Forge 1.8.9)
This is a forge mod implementation of the [Hypixel Public API](https://github.com/HypixelDev/PublicAPI)

## Main idea
Some mods might want to use Hypixel's Public API (this project uses [my fork](https://github.com/GrizzlT/PublicAPI/tree/4.0.0-reactive) to add certain features to the game, and, indeed, the documentation does a good job of explaining how to implement this.

**But** to use the api, you need a key obtainable from hypixel itself (see their documentation). The API also forces the user to limit their queries per minute (120/min default).

It wouldn't really be user-friendly if every mod handled this api part (especially the key part) on their own.

That's the reason why this mod came around. Features include:
- This mod manages the user's `API key` in one place
- This mod provides an instance of the API to any other mod
- This mod checks whether an `API key` is present and enforces the request limit whilst not losing any requests.

## Getting Started (for Forge users)
Download the latest [release](https://github.com/GrizzlT/HypixelPublicAPIMod/releases) and put the `HypixelPublicAPIMod-*.*.*.jar`-file in your Minecraft mods directory.

That's it, the mod is ready for use!
See [Commands](#Commands) for more information.

## Getting Started (for developers)
This mod's API can be added to your project using JitPack (use latest release tag).
[![](https://jitpack.io/v/GrizzlT/HypixelPublicAPIMod.svg)](https://jitpack.io/#GrizzlT/HypixelPublicAPIMod)

Subscribe to the `OnHpPublicAPIReadyEvent` event to receive an instance of the `HypixelPublicAPIModLibrary`.

To send a request, simply call `HypixelPublicAPIModLibrary#handleHypixelAPIRequest()`. You will get a `Mono` back to process the result of the query.

For more information on Monos and project reactor, check out their [website](https://projectreactor.io).


## Commands
When using the mod you need to give this mod your `API key` in order for it to work.

### All you need is one command!
- `/hpapiquickstart`

*Run this command on hypixel.net for it to work!*

#### For finer control:
- `/hpsetapikey <key>`

Use this command to inform the mod about your key, the `<key>` argument must be replaced by the [`API key`](https://github.com/HypixelDev/PublicAPI#obtaining-an-api-key) you received from hypixel.
- `/hpsaveapikey`

Use this command to save the `API key` in the config files. It will be loaded at startup by default. (*this behavior can be changed in the mod config*)
#### Other commands:
- `/hpunsetapikey`

Use this command to disable the api. This command is necessary to change your `API key`.
- `/hploadapikey`

Use this command to load your `API key` from the config files. This is usually only required if you don't want the mod to automatically load the keys at startup.

> Written with [StackEdit](https://stackedit.io/).
