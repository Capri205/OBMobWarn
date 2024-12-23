# OBMobWarn
Minecraft bukkit/spigot plugin to give a visual warning to a player when a hostile mob targeting them.<br>
The plugin allows you to get warnings on all hostile mobs, special mobs that the player can anger, such<be>
as polar bears, or bees, as well as configure it to only warn on specific mobs.<br>
There is a configurable cooldown period on which the mob re-targeting the player will not warn.<br>
This is to prevent spamming of the warnings, as mobs frequently lose and re-target players.<br>
You can also configure the warnings to display to the chat or the action bar, or both.<br>

Use the /mobwarn to get the usage message<br>

/mobwarn help - show the usage<br>
/mobwarn settings - will show the current configuration values<br>
/mobwarn show - show mobs that currently have you as a target<br>
/mobwarn list - shows the custom warning list which overrides the allhostile flag<br>
/mobwarn allhostile - toggle off/on warning on all hostile mobs. Off means it will use the custom list if
there are any mobs on there. On means warn on all hostile mobs and do not use the custom list.<br>
/mobwarn add <mob> - adds a mob to the custom list and disabled all hostile warnings<br>
/mobwarn remove <all|mob> - removes a specific mob or all mobs from the custom warn list<br>
/mobwarn quiettime <seconds> - the cooldown time in  to suppress warnings when a mob re-targets a player<br>
/mobwarn cleanupinterval <ticks> - the interval in server ticks between dead/despawned mob checking<br>
/mobwarn actionbarwarn - toggle on/off the warnings going to the action bar<br>
/mobwarn chatwarn - toggle on/off the warnings going to chat<br>

The warning message consists of the mob type or the mob custom name if set, followed by the direction the<br>
mob is relative to the player when it targeted the player. The direction is split into two. The first<br>
part shows the number of blocks horizontally the mob is from the player, followed by a colored arrow<br>
showing the direction - red behind the player, yellow left or right of the player and green in front<br>
of the player. The second part of the direction is the number of blocks vertically with a '+' or '-' sign<br>
and a green arrow for above the player, and red for below the player.<br>

Warnings in action bar:<br>
![alt text](https://ob-mc.net/repo/obmobwarn_actionbar.png)

Warnings in chat:<br>
![alt text](https://ob-mc.net/repo/obmobwarn_chat.png)

Compiled for 1.21 and Java 21.
