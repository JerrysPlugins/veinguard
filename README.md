![Main Banner](https://cdn.modrinth.com/data/cached_images/d8a1778b57bbf5a02072fd5f1b561889a00bb5f1.png)
![Chat Notification](https://cdn.modrinth.com/data/zIQ58sDz/images/207283f03412dfb7523cad97c091542e54e589bc.png)

## VeinGuard

**⚠️ NOTICE: This plugin is in beta. There may be bugs or issues, and not all planned features have been implemented yet. Join our [Discord](https://discord.gg/sW7zu4RXmD) for support, announcements and to report bugs.**

**VeinGuard** is a lightweight Anti-Xray plugin designed to help server staff identify potential x-rayers by identifying suspicious mining behavior. The plugin monitors block break activity in real time and tracks specific blocks defined in the configuration.

When a player breaks a tracked block, the action is recorded in memory. If the player exceeds the configured number of breaks for that block within the defined parameters, VeinGuard automatically alerts staff members. Alerts can be delivered through in-game notifications, console notifications or a discord webhook, allowing staff to respond quickly.

VeinGuard is designed to be simple and configurable, making it easy for server owners to moderate potential X-ray abuse without impacting server performance.

---

## Features

### Core Detection
- Tracks player block-break activity
- Time-based detection window (e.g. blocks broken within the last X minutes)
- Supports tracking of all Minecraft block types
- Configurable break thresholds per block/material
- Per-block alert cooldowns to prevent repeated notifications

### Alerts & Notifications
- In-game staff alert messages
- Console alert logging
- Discord webhook support for external notifications
- Execute configurable commands when an alert is triggered
- Toggleable alerts for individual staff members
- Per-player mute system to suppress alerts

### Reporting
- Detailed player reports showing block counts and alert thresholds
- Clear visual distinction between normal activity and violations
- Paginated reports for easier viewing of large datasets

### Configuration & Control
- Ignore players in Creative mode
- Ignore block breaks above a configurable Y-level
- Ignore block breaks performed with specific tools
- Disable tracking in selected worlds
- Permission-based bypass for trusted players

### Administration
- Hot-reload support for configuration and language files


---

<details>
<summary><strong>Planned Features</strong></summary>

## Planned Features

VeinGuard is actively developed and currently in **beta**.  
The features listed below are either in progress or planned for future releases.

### Localization & Accessibility
- **Multi-language support** via locale-based language files (e.g. `en_us.yml`)
- Easy switching of plugin language through configuration

### Staff Tools & Patrol System
- **Spectator Patrol Mode** (`/vg patrol <start|stop|pause|resume>`)
    - Automatically places staff into spectator mode
    - Teleports staff between online players at a configurable interval
    - Ensures every online player is visited once per patrol cycle
    - Configurable delay between teleports
    - Boss bar countdown showing time until next teleport
    - Visual paused state when patrol is paused

### Detection Improvements
- **Confidence Scoring System**
    - Calculates how likely a player is to be using X-ray
    - Based on mining patterns, thresholds, and historical data
    - Used to better prioritize alerts and reports
- **Ore Vein Detection**
    - Distinguishes between natural ore veins and suspicious isolated mining
    - Reduces false positives from legitimate mining activity

### GUI & Usability
- **In-game Management GUI**
    - View player block-break reports
    - Teleport directly to players from reports
    - Manage alerts, mutes, and plugin settings
    - Designed for fast staff interaction without commands

### Integrations & Persistence
- **WorldGuard Integration**
    - Custom WorldGuard flag to enable or disable VeinGuard per region
- **Data Persistence**
    - Retain block-break history and mute states across server restarts
- **Discord Enhancements**
    - Rich Discord embed support for webhook alerts

---

### Completed / Already Implemented
- Automatic configuration updates between versions
- Update checker for new plugin releases
- bStats metrics for anonymous usage insights
- Console command functionality

---

</details>


<details>
<summary><strong>Commands & Permissions</strong></summary>

<br>

### Permissions

> Grant the permission `veinguard.admin` to provide access to all staff-related commands and notifications.

> Grant the permission `veinguard.mod` to your moderators. This permission grants the following permissions: `veinguard.notify` `veinguard.bypass` `veinguard.command` `veinguard.command.check` `veinguard.command.help` `veinguard.command.toggle-alerts`

- **`veinguard.command`**  
  Allows access to the `/veinguard` (`/vg`) command.

- **`veinguard.notify`**  
  Receives in-game X-ray alert notifications when suspicious mining activity is detected.

- **`veinguard.bypass`**  
  Exempts the player from all VeinGuard block tracking and detection checks.

---

### Base Command

- **/veinguard** or **/vg**
    - Permission: `veinguard.command`
    - Displays general plugin information and how to view plugin help.

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

- **/vg toggle-alerts or /vg toggle-alerts \<player\>**
    - Permission: `veinguard.command.toggle-alerts` and `veinguard.command.toggle-alerts.others`
    - Toggle all notifications for yourself or a specific player.

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

# If true, staff alert messages for suspicious block breaking
# will also be sent to the server console.
# Set too false to disable console notifications.
send-alerts-to-console: true

# If true, alert messages for suspicious block breaking
# will be sent to in-game staff players with the 'veinguard.notify' permission.
# Set too false to disable in-game staff notifications.
send-alerts-to-staff: true

# List of console commands to execute when a player triggers a VeinGuard alert.
# These commands run immediately after an alert is fired.
#
# Commands are executed from the server console.
# Use {player} as a placeholder for the flagged player's name.
#
# Example:
#   alert-commands:
#     - 'vg msg {player} &cYou were fined $500 for possibly x-raying!'
#     - 'eco take {player} 500'
#
# Leave this list empty to disable command execution on alerts.
alert-commands: {}

# Number of tracked block entries to display per page when using '/veinguard check <Player>'.
# If the player has more entries than fit on one page, you can view additional pages with:
# '/veinguard check <Player> <PageNumber>'
player-report-page-entries: 7

# Tools that should be ignored by VeinGuard.
# Block breaks performed while holding these items will not be tracked.
# Values must be valid Bukkit material names.
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

# DO NOT CHANGE / THIS WILL BREAK YOUR CONFIG
config-version: 1
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
new-version: "&bVein&cGuard &7update available! &8(&7Current&f: &c{oldVersion}&7, &7Latest&f: &b{newVersion}&8)"

# -----------------------------------------------------------------------------
# Staff Notifications
# -----------------------------------------------------------------------------
staff-notify: "&b{player} &cbroke &3{count} &b{material} &cin the last &b{time} &cminutes!"

# -----------------------------------------------------------------------------
# Reports
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
# Reload Messages
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
# Help Messages
# -----------------------------------------------------------------------------
help:
  - "&7---- < &b&lVein&c&lGuard &r&7Help > &7----"
  - "&b/veinguard, /vg &7- Base command."
  - "&3/vg check <Player> &7- View a player's X-ray report."
  - "&b/vg help &7- View this help page."
  - "&b/vg mute <Player> &7- Mute a player's X-ray alerts."
  - "&3/vg reload &7- Reload the plugin and configuration files."
  - "&b/vg reset <Player> &7- Reset a player's X-ray report and block history."
  - "&3/vg resetall &7- Reset all players' X-ray reports and block history."
  - "&b/vg unmute <Player> &7- Unmute a player's X-ray alerts."
  - "&7-----------------------------------"

# -----------------------------------------------------------------------------
# Info Messages
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
# Usage Messages
# -----------------------------------------------------------------------------
veinguard-usage: "&cUsage: /veinguard <check|help|mute|reset|resetall|reload|unmute>"
veinguard-usage-check: "&cUsage: /veinguard check <Player>"
veinguard-usage-help: "&cUsage: /veinguard help"
veinguard-usage-msg: "&cUsage: /veinguard msg <Player> <Message>"
veinguard-usage-mute: "&cUsage: /veinguard mute <Player>"
veinguard-usage-reset: "&cUsage: /veinguard reset <Player>"
veinguard-usage-resetall: "&cUsage: /veinguard resetall"
veinguard-usage-reload: "&cUsage: /veinguard reload"
veinguard-usage-toggle-alerts: "&cUsage: /veinguard toggle-alerts 'or' toggle-alerts <Player>"
veinguard-usage-unmute: "&cUsage: /veinguard unmute <Player>"

# DO NOT CHANGE
lang-version: 1
```
</details>

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