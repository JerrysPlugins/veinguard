<p align="center">
  <img src="https://img.shields.io/github/v/release/jerrysplugins/veinguard?display_name=release&label=Latest" alt="Latest Release Badge">
</p>

![Main Banner](https://cdn.modrinth.com/data/cached_images/d8a1778b57bbf5a02072fd5f1b561889a00bb5f1.png)
![Chat Notification](https://cdn.modrinth.com/data/zIQ58sDz/images/207283f03412dfb7523cad97c091542e54e589bc.png)

## VeinGuard

**⚠️ NOTICE: Not all planned features have been implemented yet! Join our [Discord](https://discord.gg/sW7zu4RXmD) for support and announcements.**

**VeinGuard** is a lightweight plugin that helps server staff detect potential x-rayers. It tracks block breaks in real time for blocks defined in the configuration.

When a player exceeds the configured break limits, VeinGuard alerts staff via in-game messages, console logs, or a Discord webhook, enabling quick responses.

VeinGuard is simple, configurable, and designed to moderate X-ray abuse without impacting server performance.


---

## Features

### Core Detection
- Tracks player block-break activity in real time
- Time-based detection windows (e.g., blocks broken within X minutes)
- Supports all Minecraft block types
- Configurable break thresholds per block
- Per-block cooldowns to prevent repeated alerts

### Alerts & Notifications
- In-game staff messages
- Console logging
- Discord webhook notifications
- Configurable in-game sound upon alert trigger
- Execute configurable commands on alerts
- Toggleable alerts for individual staff
- Per-player mute system for suppressing alerts

### Reports
- Detailed player reports with block counts and threshold indicators
- Clear highlighting of violation blocks
- Paginated reports for large datasets

### Configuration & Control
- Ignore players in Creative mode
- Ignore blocks broken above a configurable Y-level
- Ignore breaks with specific tools
- Disable tracking in selected worlds
- Permission-based bypass for trusted players

### Administration
- Hot-reload support for config and language files

---

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

<details>
<summary><strong>Messages</strong></summary>

<br>

```yml
######################################################################################
#                                  PLUGIN MESSAGES                                   #
#               Classic color codes and HEX color codes are accepted.                #
#                       CLASSIC: '&a-f0-9'      HEX: '&#0f0f0f'                      #
######################################################################################

# -----------------------------------------------------------------------------
# Prefix
# -----------------------------------------------------------------------------
plugin-prefix: "&c&lV&b&lG &r&7> &r"

# -----------------------------------------------------------------------------
# General Messages
# -----------------------------------------------------------------------------
no-permission: "&cYou don't have permission!"
player-not-found: "&cPlayer &b{player} &cwas not found!"
in-game-only: "This command can only be used by in-game players!"

# -----------------------------------------------------------------------------
# Staff Messages
# -----------------------------------------------------------------------------
staff-notify: "&b{player} &cbroke &3{count} &b{material} &cin the last &b{time} &cminutes!"
staff-join-notify: "&cThere are &b{count} &cplayers with block violations!"

# -----------------------------------------------------------------------------
# Report Message
# -----------------------------------------------------------------------------
report-header: "&7---- < &c&lVein&b&lGuard &r&7Report / &3{player} &7> ----"
report-material-normal: "&7{material}&f: &8(&b{count}&7/&3{threshold}&8)"
report-material-violation: "&c* {material}&f: &8(&c{count}&7/&3{threshold}&8)"
report-none: "&7No tracked blocks broken in the last &b{time} &7minutes."
report-footer: "&7------------ &8(&7Page: &9{page} &7of &9{totalPages} &8) &7------------"
report-not-tracked: "&b{player} &chas bypass permission and is not tracked!"
report-invalid-page: "&b{page} &cis not a valid page number!"

# -----------------------------------------------------------------------------
# Reload Messages
# -----------------------------------------------------------------------------
reload-failed: "&cPlugin reload failed! Check the console for details."
reload-success: "&aPlugin and configuration files reloaded successfully!"

# -----------------------------------------------------------------------------
# Reset Messages
# -----------------------------------------------------------------------------
reset-player: "&b{player} &ablock break data has been reset!"
reset-all: "&aAll player block break data has been reset!"

# -----------------------------------------------------------------------------
# Mute Messages
# -----------------------------------------------------------------------------
already-muted: "&b{player} &cis already muted!"
mute-player: "&b{player}'s &aalerts are now muted."
already-unmuted: "&b{player} &cis already unmuted!"
unmute-player: "&b{player}'s &aalerts are now unmuted."

# -----------------------------------------------------------------------------
# Toggle Alerts Messages
# -----------------------------------------------------------------------------
toggle-alerts-self-on: "&aYou will now receive VeinGuard alerts."
toggle-alerts-self-off: "&cYou will no longer receive VeinGuard alerts."
toggle-alerts-others-on: "&b{player} &awill now receive VeinGuard alerts."
toggle-alerts-others-off: "&b{player} &cwill no longer receive VeinGuard alerts."
toggle-alerts-self-not-staff: "&cYou do not have permission to receive alerts!"
toggle-alerts-others-not-staff: "&b{player} &cdoesn't have permission to receive alerts!"

# -----------------------------------------------------------------------------
# Help Message
# -----------------------------------------------------------------------------
help:
  - "&7---- < &b&lVein&c&lGuard &r&7Help > &7----"
  - "&b/veinguard &8(&7Alias&f: &7/vg&8) &7- Base command."
  - "&3/vg check <Player> [Page] &7- View a player's block report."
  - "&b/vg help &7- View this command help page."
  - "&3/vg mute <Player> &7- Mute a player's alerts."
  - "&b/vg reload &7- Reload the plugin and configuration files."
  - "&3/vg reset <Player> &7- Reset a player's block history."
  - "&b/vg resetall &7- Reset all players block history."
  - "&3/vg toggle-alerts &7- Toggle alerts for yourself."
  - "&b/vg unmute <Player> &7- Unmute a player's alerts."
  - "&7-----------------------------------"

# -----------------------------------------------------------------------------
# Info Message
# -----------------------------------------------------------------------------
plugin-info:
  - "&7---- < &b&lVein&c&lGuard &r&7Information > &7----"
  - "&7Description&f: &b{description}"
  - "&7Current Version&f: &3{version}"
  - "&7Author&f: &b{author}"
  - "&7Discord&f: &3&n{website}&r"
  - " "
  - "&3Use '&b/vg help&3' for help with commands."
  - "&7---------------------------------"

# -----------------------------------------------------------------------------
# Update Message
# -----------------------------------------------------------------------------
update:
  - "&7---- < &b&lVein&c&lGuard &r&7Update > &7----"
  - "&7A new update for VeinGuard is available!"
  - "&8(&bCurrent&f: &7{oldVersion}&8) &8(&bLatest&f: &6{newVersion}&8)"
  - "&7Download &b@ &7Spigot, Modrinth or Github!"
  - "&7-----------------------------"

# -----------------------------------------------------------------------------
# Usage Message
# -----------------------------------------------------------------------------
command-usage: "&cUsage: /veinguard {usage}"

# DO NOT CHANGE – Used internally to track lang config compatibility.
# Modifying this value may cause the plugin to fail loading or reset your configuration.
lang-version: 1
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