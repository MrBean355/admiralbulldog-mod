# AdmiralBulldog Dota 2 Mod

This is a mod for Dota 2 which changes some in-game sounds to achieve similar results to AdmiralBulldog's game.

## Installation
1. Download the `pak01_dir.vpk` file of the [latest release](https://github.com/MrBean355/admiralbulldog-mod/releases/latest) (under the "Assets" section).
2. Locate your Dota 2 installation folder, e.g. `C:\Program Files (x86)\Steam\steamapps\common\dota 2 beta`.
3. Within there, open the `game` folder.
4. Create a new folder called `admiralbulldog`.
5. Place the downloaded file in this folder.
6. Within the `game` folder, open the `dota` folder.
7. Copy the `gameinfo.gi` file to your desktop (i.e. make a backup).
8. Open the original `gameinfo.gi` file in a text editor (e.g. Notepad).
9. Don't make any changes other than the one below! You could break your game.
10. Find a line that looks like:
<pre>Game    dota</pre>
11. ABOVE this line, add a new line:
<pre>Game    admiralbulldog</pre>
12. Save the file.
13. You're good to go! Launch Dota and test some of the sounds.

## Problem?
If you're experiencing issues with your game, you can deactivate the mod:
1. Open the `gameinfo.gi` file again and remove the line you previously added.
2. Delete the `admiralbulldog` folder from your Dota folder.

## Implemented Sounds
- Beastmaster - [Primal Roar](original%20samples/awoo.wav)
- Crystal Maiden - [Freezing Field](original%20samples/Raaaaahh.wav)
- Earthshaker - [Echo Slam](original%20samples/skadoosh.wav)
- Kunkka - [X Marks the Spot](original%20samples/sike.wav) & [Ghost Ship](original%20samples/aaah%20x3.wav)
- Naga Siren - [Song of the Siren](original%20samples/naga%20song.wav)
- Nature's Prophet - [Teleportation](original%20samples/seeya.wav)
- Witch Doctor - [Death Ward](original%20samples/Witch%20Doctor%20Song%20Short.wav)

## To Be Done
- Enigma - Black Hole (where to find the original?)
- Juggernaut - Blade Fury (where to find the original?)
- Queen of Pain - Sonic Wave (where to find the original?)
- Chat Wheel (I don't have Dota Plus):
  - BabyRage
  - Bruh
  - Oh no no 
