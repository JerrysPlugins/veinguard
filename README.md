<p align="center">
  <img src="https://cdn.modrinth.com/data/zIQ58sDz/images/630507d6fa117c5d38dc15306adcbcc5b4bb3e1b.png" alt="VeinGuard Banner">
</p>

<p align="center">
  <a href="https://modrinth.com/plugin/veinguard">
    <img src="https://raw.githubusercontent.com/vLuckyyy/badges/main/avaiable-on-modrinth.svg" alt="Modrinth" />
  </a>
  <a href="https://www.spigotmc.org/resources/veinguard-antixray-for-1-17-1-21.131871/">
    <img src="https://raw.githubusercontent.com/vLuckyyy/badges/main/available-on-spigotmc.svg" alt="SpigotMC" />
  </a>
  <a href="https://hangar.papermc.io/JerrysPlugins/VeinGuard">
    <img src="https://raw.githubusercontent.com/vLuckyyy/badges/main/avaiable-on-hangar.svg" alt="Hangar" />
  </a>
  <a href="https://bstats.org/plugin/bukkit/VeinGuard/28893">
    <img src="https://raw.githubusercontent.com/vLuckyyy/badges/main/available-on-bstats.svg" alt="bStats" />
  </a>
</p>

<p align="center">
  <img src="https://github.com/jerrysplugins/veinguard/actions/workflows/maven.yml/badge.svg" alt="Passing" />
  <img src="https://img.shields.io/github/v/release/jerrysplugins/veinguard" alt="Latest Release" />
  <img src="https://img.shields.io/discord/1461939188105609298" alt="Discord" />
</p>

![About](https://cdn.modrinth.com/data/zIQ58sDz/images/c095ffca2188e39e835a7e4713815c9592883f7b.png)

**VeinGuard** is a lightweight plugin that helps server staff detect potential x-rayers. It tracks block breaks in real time for blocks defined in the configuration.

When a player exceeds the configured break limits, VeinGuard alerts staff via in-game messages, console logs, or a Discord webhook, enabling quick responses.

VeinGuard is simple, configurable, and designed to moderate X-ray abuse without impacting server performance.

**⚠️ NOTICE: Not all planned features have been implemented yet! Join our [Discord](https://discord.gg/sW7zu4RXmD) for support and announcements.**

---

![Features](https://cdn.modrinth.com/data/zIQ58sDz/images/a86aad567393c808c4eeb3b0f1a17655a075af64.png)

- Tracks how many tracked blocks players break in a configured time (minutes)
- Supports all block types with configurable limits
- Prevents spam alerts by using cooldowns per tracked block or per alert
- Sends alerts to staff in-game, console, and Discord webhook
- Runs configurable commands when an alert is triggered
- Allows staff to mute or toggle alerts
- Generates clear player reports with block counts and violations
- Supports large reports with pagination
- Ignores creative mode players, certain tools, worlds, and above certain Y-levels
- Permission-based bypasses
- Reload configs and language files without restarting the server

---

![Installation](https://cdn.modrinth.com/data/zIQ58sDz/images/f30c3ef2dd3d1f636c594cca3f0cc505dde3505c.png)

## Requirements

Before installing **VeinGuard**, make sure your server meets the following requirements:

- **Java 16 or newer**
- One of the following server types: Spigot, Paper, Purpur(1.21+) or any compatible fork.
- Minecraft 1.17+ if using Spigot or Paper.
---

## Installation

1. Download the latest **VeinGuard** `.jar` file from: Spigot, Modrinth, Hangar or Github.

2. Place the VeinGuard `.jar` file into your server’s `plugins` folder.

3. Start the server once to generate VeinGuard’s configuration files.

4. Edit the configuration files to your liking, then apply changes by:
  - Running `/vg reload`, **or**
  - Restarting the server


![Resources](https://cdn.modrinth.com/data/zIQ58sDz/images/70a436bbde193caa82ff2234ee78395bd5254303.png)

<details>
<summary><strong>Commands & Permissions</strong></summary>

<br>

### Permissions

> Grant the permission `veinguard.admin` to provide access to all features.

> Grant the permission `veinguard.mod` to your moderators. This permission grants the following permissions: `veinguard.notify` `veinguard.bypass` `veinguard.command` `veinguard.command.check` `veinguard.command.help` `veinguard.command.toggle-alerts` `veinguard.command.tracked-blocks.list`

You are also free to ignore ^ these permissions and configure them as you please!

- **`veinguard.notify`**  
  Receives in-game alert notifications when suspicious mining activity is detected.

- **`veinguard.bypass`**
  Exempts the player from all VeinGuard block tracking and detection checks.

- **`veinguard.bypass.MATERIAL`**  
  Exempts the player from block-break tracking for a specific material. Material is CASE SENSITIVE!\
  Example: `veinguard.bypass.DIAMOND_ORE`\
  Intended for staff members or trusted roles.

- **`veinguard.update`**  
  Allows the player to receive VeinGuard update notifications.

---

### Base Command

- **/veinguard** or **/vg**
  - Permission: `veinguard.command`
  - Displays general plugin information and how to view plugin help.

- **/veinguard help**
  - Permission: `veinguard.help`
  - Displays help message for plugin commands and features.

### Player Commands

- **/vg check \<player\> or /vg check \<player\> \<page\>**
  - Permission: `veinguard.command.check`
  - View a detailed report of a player’s tracked block break history.
  - Paginated, limiting 7 tracked-blocks per page, configurable in config.yml.

- **/vg msg \<player\> \<message\>**
  - Permission: `veinguard.command.msg`
  - Sends a formatted message to the targeted player, with no prefix, used in the config for 'alert-commands'.

- **/vg mute \<player\>**
  - Permission: `veinguard.command.mute`
  - Temporarily mute alert notifications for a specific suspect player. This will mute notifications for players with the permission `veinguard.notify`

- **/vg toggle-alerts**
  - Permission: `veinguard.command.toggle-alerts`
  - Toggle all alert notifications for yourself.

- **/vg tracked-blocks <add|list|remove> <?list \[page\]> \<material\> \<threshold\> \<pretty-name\>**
  - Permissions: `veinguard.command.tracked-blocks.add` `veinguard.command.tracked-blocks.list` `veinguard.command.tracked-blocks.remove`
  - Add, list or remove tracked blocks in real time without updating the config.yml manually.

- **/vg unmute \<player\>**
  - Permission: `veinguard.command.unmute`
  - Re-enable alert notifications for a previously muted player. This will unmute notifications for players with the permission `veinguard.notify`

### Data Management

- **/vg reset \<player\>**
  - Permission: `veinguard.command.reset`
  - Reset a specific player’s tracked block break history.

- **/vg resetall**
  - Permission: `veinguard.command.resetall`
  - Reset tracked block break history for all players.

### Administration

- **/vg reload**
  - Permission: `veinguard.command.reload`
  - Reload the plugin and all configuration files without restarting the server.

</details>

## VeinGuard Plugin Wiki

Explore the full **VeinGuard Wiki** for detailed information about the plugin, including commands, permissions, configuration options, installation guides, and more.

[Visit the VeinGuard Wiki](https://github.com/JerrysPlugins/veinguard/wiki)

## Report Issues/Bugs

Find a bug or an error? Report it here on our issue tracker:

[Issue Tracker](https://github.com/JerrysPlugins/veinguard/issues)

## Join Our Discord

Get support and stay updated on VeinGuard updates:

[Join the VeinGuard Discord](https://discord.gg/sW7zu4RXmD)

## Plugin Metrics

VeinGuard uses bStats to collect anonymous usage data. Click to view plugin metrics:

[View Metrics](https://bstats.org/plugin/bukkit/VeinGuard/28893)
<img src="https://bstats.org/signatures/bukkit/VeinGuard.svg" alt="ModPanel"/>