# Configuration

This page explains all configurable options in **VeinGuard's `config.yml`**.  
Proper configuration ensures accurate tracking, alerts, and reporting of suspicious block-breaking activity.

---

## General Settings

| Option | Description | Default |
|--------|-------------|---------|
| `blocks-broken-in-last-minutes` | Time window (in minutes) to evaluate block break activity. Example: 5 means "blocks broken in the last 5 minutes". | `5` |
| `alert-cooldown-seconds` | Cooldown (in seconds) between alerts for the same player and block type. Prevents alert spam. | `45` |
| `ignore-creative-mode` | If `true`, block breaks by players in **Creative mode** are ignored. Recommended to avoid false positives. | `true` |
| `ignore-above-y-level` | Blocks broken above this Y-level are ignored. Useful for focusing on underground mining. | `64` |
| `player-report-page-entries` | Number of tracked block entries displayed per page in `/veinguard check <player>`. | `7` |
| `send-alerts-to-console` | If `true`, alert messages are sent to the server console as well as in-game staff. | `true` |
| `send-alerts-to-staff` | If `true`, in-game staff with `veinguard.notify` will receive alerts. | `true` |
| `staff-join-violation-alert` | If `true`, staff will see a notification on join showing the number of flagged players. Requires `veinguard.notify`. | `false` |

---

## Alert Sound

VeinGuard can optionally play a configurable sound to staff members when an alert is triggered.  
The sound is only played for players with the `veinguard.notify` permission and if staff alerts are enabled.

### Configuration Example

    alert-sound:
      enabled: true
      sound: ENTITY_EXPERIENCE_ORB_PICKUP
      volume: 1.0
      pitch: 1.0

### Options

| Option | Description | Default |
|--------|-------------|---------|
| `enabled` | Enables or disables the alert sound. | `true` |
| `sound` | Bukkit sound to play when an alert is triggered. Must be a valid `Sound` enum. | `ENTITY_EXPERIENCE_ORB_PICKUP` |
| `volume` | Volume of the alert sound (0.0 – 1.0+). | `1.0` |
| `pitch` | Pitch of the alert sound (0.5 – 2.0). | `1.0` |

### Notes

- Sound values are case-insensitive.
- Invalid or missing sounds fall back to `ENTITY_EXPERIENCE_ORB_PICKUP`.

---

## Alert Commands

| Option | Description | Default |
|--------|-------------|---------|
| `alert-commands` | List of console commands executed when a player triggers an alert. Use `{player}` as a placeholder. Example: `vg msg {player} &cWarning!` | `{}` |
| `discord-webhook-url` | Optional Discord webhook for sending staff alert notifications. Leave empty (`""`) to disable. | `""` |

---

## Tools & Worlds

| Option | Description | Default |
|--------|-------------|---------|
| `ignored-tools` | Tools that VeinGuard ignores when tracking blocks. Values must be valid Bukkit material names. | `[WOODEN_HOE]` |
| `disabled-worlds` | Worlds where VeinGuard tracking is completely disabled. World names are case-sensitive. | `[test_world]` |

---

## Tracked Blocks

VeinGuard tracks specific blocks and triggers alerts when a player breaks more than a set amount within the configured time window.

### Config Option

| Option | Description | Default |
|--------|-------------|---------|
| `tracked-blocks` | List of blocks VeinGuard monitors. Staff are alerted when a player breaks more than the configured amount within the time window. | Predefined list of ores, ancient debris, spawners, and raw metal blocks (see example below) |

---

### Value Format

| Field | Description | Example |
|-------|-------------|---------|
| `MATERIAL` | Bukkit material name of the block to track. Must match exactly. | `DIAMOND_ORE` |
| `AMOUNT` | Maximum number of blocks that can be broken within the configured time window before triggering an alert. | `15` |
| `Pretty Name` | Friendly name shown in staff alerts and reports for readability. | `"Diamond Ore"` |

### How to Add Entries

Each entry in `tracked-blocks` follows this **placeholder format**:

| Config Entry | Description |
|-------------|-------------|
| `MATERIAL:AMOUNT:"Pretty Name"` | Replace `MATERIAL` with a valid Bukkit material name, `AMOUNT` with the number of blocks that triggers an alert, and `"Pretty Name"` with the friendly name shown in alerts and reports. |

**Example Entry:**

```yaml
tracked-blocks:
  - DIAMOND_ORE:15:"Diamond Ore"
  - EMERALD_ORE:5:"Emerald Ore"
```

---

## Full Config File

Below is the **complete default `config.yml`** for VeinGuard version 1.1.2.  
You can copy this as a reference when configuring your server.  
All options are explained in the sections above.

```yaml
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