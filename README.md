# Marathon

Misc utilities mod for 1.16 speedrunners.

## Utilities

### Spawner info

![spawner_info](assets/spawner_info.png)

This is useful for testing and evaluating different blaze bedding/mining techniques. The info is calculated and
displayed just _before_ a spawn attempt is made, and is done on a pixel (1/16th of a block) resolution.  
The green text displays the chance of spawning x number of blazes and the average number of blazes that will spawn based
on the configuration of blocks around the spawner. The gold text is this including the impact of existing blazes within
the spawner bounding box and collisions with the hitboxes of any entities and fluids.  
Look at the green info to determine how well your blaze bedding/block breaking is working, look at the gold info to get
a more accurate idea of what exactly happening.

## Setup

Put the `jar` file in your mods folder with Fabric Loader installed. This mod does not require `Fabric API`. This mod is
developed and tested for `1.16.1` but may work in other versions.

## License

This project is available under the `GPL-3.0` license. Feel free to learn from it and incorporate its components in your
own projects, as long as you abide by the terms of the license.

If you fork the project and distribute your version, please change the name and `modid` to a suitable and distinct
alternative to avoid confusion.
