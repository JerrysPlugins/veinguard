<p align="center">
  <img src="https://cdn.modrinth.com/data/zIQ58sDz/images/096ab4efe3e526126dfa72571962c58832992498.png" alt="VeinGuard Banner">
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
- Prevents spam alerts by using cooldowns per player and block
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

> Grant the permission `veinguard.mod` to your moderators. This permission grants the following permissions: `veinguard.notify` `veinguard.bypass` `veinguard.command` `veinguard.command.check` `veinguard.command.help` `veinguard.command.toggle-alerts`

You are also free to ignore ^ these permissions and configure them as you please!

- **`veinguard.notify`**  
  Receives in-game alert notifications when suspicious mining activity is detected.

- **`veinguard.bypass`**  
  Exempts the player from all VeinGuard block tracking and detection checks.

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

<details>
<summary><strong>Config</strong></summary>

<br>

```yml
# Time window (in minutes) used to evaluate block break activity.
# VeinGuard will count how many tracked blocks a player breaks within this period.
# Example: A value of 5 means "blocks broken in the last 5 minutes".
blocks-broken-in-last-minutes: 5

# Cooldown (in seconds) between alerts for the same player and block type.
# This prevents alert spam if a player continues mining after an alert is triggered.
alert-cooldown-seconds: 45

# If enabled, block breaks by players in CREATIVE mode will be ignored.
# Recommended to keep enabled to avoid false positives from staff or builders.
ignore-creative-mode: true

# Blocks broken above this Y-level will not be tracked.
# Useful for ignoring surface mining and focusing on underground activity.
# Set to 320 to track blocks at all Y-levels.
ignore-above-y-level: 64

# Number of tracked block entries to display per page when using '/veinguard check <Player>'.
# If the player has more entries than fit on one page, you can view additional pages with:
# '/veinguard check <Player> <PageNumber>'
player-report-page-entries: 7

# If true, staff alert messages for suspicious block breaking
# will also be sent to the server console.
# Set too false to disable console notifications.
send-alerts-to-console: true

# If true, alert messages for suspicious block breaking
# will be sent to in-game staff players with the 'veinguard.notify' permission.
# Set too false to disable in-game staff notifications.
send-alerts-to-staff: true

# If true, sends a notification to staff members when they join the
# server, showing the number of players currently flagged for violations.
# Requires the player to have the 'veinguard.notify' permission.
staff-join-violation-alert: false

# If enabled, sends the configured sound to any player with the
# 'veinguard.notify' permission upon an alert being triggered.

# Sound must be a valid Bukkit sound.
# - Visit this site for a list of valid sounds:
#   https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
#
# volume: Change the volume of the alert sound.
# pitch: Change the pitch of the alert sound.
alert-sound:
  enabled: true
  sound: ENTITY_EXPERIENCE_ORB_PICKUP
  volume: 1.0
  pitch: 1.0

# List of console commands to execute when a player triggers a VeinGuard alert.
# These commands run immediately after an alert is fired.
#
# Commands are executed from the server console.
# Use {player} as a placeholder for the flagged player's name.
#
# Example:
#   alert-commands:
#   - 'vg msg {player} &cYou were fined $500 for possibly x-raying!'
#   - 'eco take {player} 500'
#
# Leave this list empty to disable command execution on alerts.
alert-commands: {}

# Optional Discord webhook embed for staff alert notifications.
# Alerts will only be sent if a valid webhook URL is provided.
# Leave empty ("") to disable.
discord-webhook-url: ""

# Tools that should be ignored by VeinGuard.
# Block breaks performed while holding these items will not be tracked.
# Values must be valid Bukkit material names.
# - Visit this site for a list of valid materials:
#   https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html
ignored-tools:
  - WOODEN_HOE

# List of worlds where VeinGuard tracking should be completely disabled.
# World names are case-sensitive.
disabled-worlds:
  - test_world

# Defines which blocks VeinGuard should track and their alert thresholds.
#
# Format:
#   MATERIAL:AMOUNT:"Pretty Name"
#
# - MATERIAL must be a valid Bukkit material name.
# - AMOUNT is how many times the block can be broken within the configured time window
#   before staff are alerted.
# - Pretty Name is required and is used in alerts and reports for better readability.
#
# - Visit this site for a list of valid materials:
#   https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html
#
# Example:
#   'DIAMOND_ORE:15:"Diamond Ore"'
#   Alerts staff when a player breaks 15 or more Diamond Ore blocks within the time window.
tracked-blocks:
  - DIAMOND_ORE:15:"Diamond Ore"
  - EMERALD_ORE:5:"Emerald Ore"
  - LAPIS_ORE:12:"Lapis Ore"
  - REDSTONE_ORE:40:"Redstone Ore"
  - GOLD_ORE:20:"Gold Ore"
  - IRON_ORE:90:"Iron Ore"
  - COPPER_ORE:100:"Copper Ore"
  - COAL_ORE:120:"Coal Ore"

  - DEEPSLATE_DIAMOND_ORE:15:"Deepslate Diamond Ore"
  - DEEPSLATE_EMERALD_ORE:5:"Deepslate Emerald Ore"
  - DEEPSLATE_LAPIS_ORE:12:"Deepslate Lapis Ore"
  - DEEPSLATE_REDSTONE_ORE:40:"Deepslate Redstone Ore"
  - DEEPSLATE_GOLD_ORE:20:"Deepslate Gold Ore"
  - DEEPSLATE_IRON_ORE:90:"Deepslate Iron Ore"
  - DEEPSLATE_COPPER_ORE:100:"Deepslate Copper Ore"
  - DEEPSLATE_COAL_ORE:120:"Deepslate Coal Ore"

  - ANCIENT_DEBRIS:8:"Ancient Debris"
  - NETHER_GOLD_ORE:40:"Nether Gold Ore"
  - NETHER_QUARTZ_ORE:90:"Nether Quartz Ore"

  - AMETHYST_BLOCK:20:"Amethyst Block"
  - BUDDING_AMETHYST:8:"Budding Amethyst"
  - SPAWNER:2:"Spawner"

  - RAW_IRON_BLOCK:5:"Raw Iron Block"
  - RAW_COPPER_BLOCK:5:"Raw Copper Block"
  - RAW_GOLD_BLOCK:5:"Raw Gold Block"

# Determines whether players with the 'veinguard.update' permission or server operators
# receive an in-game notification upon joining if a newer version of VeinGuard is available.
show-update-notice: true

# DO NOT CHANGE – Used internally to track config compatibility.
# Modifying this value may cause the plugin to fail loading or reset your configuration.
config-version: 1

# Enables additional debug logging for troubleshooting and development purposes.
# Should typically remain disabled on production servers.
debug-mode: false
```
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