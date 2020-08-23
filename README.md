# Hypixel Public API Mod (Forge)
This is a forge mod implementation of the [Hypixel Public API](https://github.com/HypixelDev/PublicAPI)

## Main idea
Some mods might want to use [Hypixel's Public API](https://github.com/HypixelDev/PublicAPI) to add certain features to the game, and, indeed,  the documentation does a good job of explaining how to implement this.

**But** to use the api, you need a key obtainable from hypixel itself (see their documentation). The API also forces the user to limit their queries per minute to 120.

It wouldn't really be user-friendly if every mod handled this part (especially the key part) on their own.

That's the reason this mod was made. It has following features:
- This mod will manage the user's `API key` in one place
- This mod will provide an instance of the API to other mods!
- This mod will check whether an API key is present and whether the amount of queries sent, hasn't exceeded the query limit.

Currently, this check is hard-coded and defaults to 115 queries every 62 seconds. (*115 for the purpose of being 100% certain there will be no query loss, this might change in the future to allow a more flexible way of defining this limit)*

## Getting Started (for Forge users)
Download the latest [release](https://github.com/ThomasVDP/HypixelPublicAPIMod/releases) and put the `HypixelStatsOverlayMod-1.0.jar`-file in your Minecraft mods directory.

That's it, the mod is ready for use!
See [Commands](#Commands) for further information.

## Getting Started (for developers)
This mod's API can be added to your project using JitPack.
[![](https://jitpack.io/v/ThomasVDP/HypixelPublicAPIMod.svg)](https://jitpack.io/#ThomasVDP/HypixelPublicAPIMod)

Subscribe to the `OnHpPublicAPIReadyEvent` event to receive an instance of the `HypixelPublicAPIModLibrary`.

To send a request, simply call `HypixelPublicAPIModLibrary#handleHypixelAPIRequest()`. You will get a `Promise` back to process the result of the query.

"A promise? What's that?!" you might think. Don't worry, it's part of a great library that really improves the `CompletableFuture` class in java. I definitely recommend checking out [their github](https://github.com/vsilaev/tascalate-concurrent).


## Commands
When using the mod you need to tell this mod your `API key` in order for it to work.

#### Only 2 commands are necessary:
- `/hpsetapikey <key>`
Use this command to inform the mod about your key, the `<key>` argument must be replaced by the [`API key`](https://github.com/HypixelDev/PublicAPI#obtaining-an-api-key) you received from hypixel.
- `/hpsaveapikey`
Use this command to save the `API key` in the config files. It will be loaded at startup by default. (*this behavior can be changed in the mod config*)
#### Other commands :
- `/hpunsetapikey`
Use this command to disable the api. This command is necessary to change your `API key`.
- `/hploadapikey`
Use this command to load your `API key` from the config files. This is usually only required if you don't want the mod to automatically load the keys at startup.

> Written with [StackEdit](https://stackedit.io/).
