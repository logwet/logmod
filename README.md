# LogMod

[![Build and Release Artifacts](https://github.com/logwet/logmod/actions/workflows/build.yml/badge.svg)](https://github.com/logwet/logmod/actions/workflows/build.yml)

Misc utilities mod for 1.16 speedrunners created in collaboration with k4yfour. (I didn't choose the
name btw 👀). Code and algorithm contributions by al (immigrant) and Sharpieman20.

## Utilities

### Savestates

Implemented through my mod [DeLorean](https://github.com/logwet/delorean) and bundled into LogMod.
Lets you save the current state of the game and load it again at any point. Default hotkeys
are `Page Up` to create a savestate and `Page Down` to load the latest savestate. Use
the `/delorean` command for more options.

### Spawner Statistics

![spawner_info](assets/spawner/info.png)

A near completely accurate simulation of in game spawner behaviour and spawn statistics.

This is useful for testing and evaluating different blaze bedding/mining techniques. The info is
calculated and displayed just _before_ a spawn attempt is made, and simulations are done on a
pixel (1/16th of a block) resolution.

The green text displays the chance of spawning x number of blazes and the average number of blazes
that will spawn based on the configuration of blocks, entities, fluid and lighting around the
spawner.

Note that this accounts for everything, including the hitboxes of entities (including the player's
hitbox) and the presence of nearby blazes preventing spawn attempts. It also accounts for the impact
of multiple spawns in one cycle taking away potential spawning spaces thanks to a clever forecasting
algorithm by Sharpieman20.

![blocked_spawner_point_cloud](assets/spawner/blocked_point_cloud.png)

The mod will render every spawn position blocked by a block, liquid, entity hitbox, light etc. with
a red dot.

![full_spawner_point_cloud](assets/spawner/full_point_cloud.png)

If you enable entity hitboxes (F3 + B) you will see a full point cloud. Green represents the highest
likelihood of spawning a blaze, blue the lowest. (Note that at the darkest blue at the edge of the
point cloud is functionally identical to red in terms of spawn chance (ie. none), but is coloured
differently nonetheless.)

Control with `/spawner` and `/rods` (to change rod statistics variables).

### HUD

Renders the player's current absolute speed and speed on each axis in the top left corner. Also
renders the player's exact health, hunger and saturation in the top right. Toggled with `/hud`

### Pearl/Potion Trajectories

Calculates and renders the trajectory of thrown projectiles and the block they will eventually hit.
Calculates up to 60 seconds in the future. Control with `/projectiles`

### Thrown Item Trajectories

Will render the trajectory of a thrown item. Currently enabled for gold ingots to be used in
bastions. Control with `/projectiles`

### Piglins

Looking at a block protected by piglins (eg. a chest or gold block) will highlight all nearby
Piglins that will be aggroed if you break or open that block. Looking at a piglin will highlight the
piglins around it that will be aggroed if you hit it. Control with `/piglins`

### Paths

Create paths. Use a hotkey (by default bound to `Insert`) to add a node to a path while holding a
wool block of the corresponding colour. Control with `/paths`

### And more

Sometimes I forget to document stuff on here. Check the release notes!

## Setup

Put the `jar` file in your mods folder with Fabric Loader installed. This mod does not
require `Fabric API`. This mod is developed and tested for `1.16.1` but may work in other versions.

## License

This project is available under the `GPL-3.0` license. Feel free to learn from it and incorporate
its components in your own projects, as long as you abide by the terms of the license.

If you fork the project and distribute your version, please change the name and `modid` to a
suitable and distinct alternative to avoid confusion.
