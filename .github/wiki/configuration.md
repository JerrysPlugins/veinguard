# Configuration

This page explains all configurable options in **VeinGuard's `config.yml`**.  
Proper configuration ensures accurate tracking, alerts, and reporting of suspicious block-breaking activity.

---

# VeinGuard Configuration Options (Top-Level)

| Option                        | Description                                                                                                                    | Default / Example Value                                                       |
|-------------------------------|--------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| config-version                | Internal version of the config file. Do not change.                                                                            | 1                                                                             |
| blocks-broken-in-last-minutes | Time window in minutes to count block breaks for alert thresholds.                                                             | 5                                                                             |
| alert-cooldown-type           | Determines how alert cooldown is applied: `BLOCK` (per block type) or `ALERT` (per player).                                    | BLOCK                                                                         |
| alert-cooldown-seconds        | Cooldown in seconds between alerts, based on alert-cooldown-type.                                                              | 30                                                                            |
| ignore-creative-mode          | Ignore players in CREATIVE mode when tracking block breaks.                                                                    | true                                                                          |
| ignore-above-y-level          | Maximum Y-level to track block breaks; blocks above are ignored.                                                               | 64                                                                            |
| player-report-page-entries    | Max number of entries per page for `/vg check <player> [page]`.                                                                | 7                                                                             |
| tracked-blocks-page-entries   | Max number of entries per page for `/vg tracked-blocks list`.                                                                  | 7                                                                             |
| send-alerts-to-console        | Send alert messages to the console.                                                                                            | true                                                                          |
| alert-delivery-type           | How alerts are delivered to staff: `CHAT`, `ACTION_BAR`, or `NONE`.                                                            | CHAT                                                                          |
| staff-join-violation-alert    | Notify staff of current violations when they join.                                                                             | false                                                                         |
| alert-sound                   | Configures a sound to play for staff when an alert triggers. Includes sub-options for enabling, sound type, volume, and pitch. | `enabled: true, sound: ENTITY_EXPERIENCE_ORB_PICKUP, volume: 1.0, pitch: 1.0` |
| discord-webhook-url           | Optional Discord webhook URL to send alerts; leave blank to disable.                                                           | ""                                                                            |
| ignored-tools                 | List of tools to ignore when tracking block breaks.                                                                            | WOODEN_HOE                                                                    |
| disabled-worlds               | Worlds where block break tracking is disabled.                                                                                 | test_world                                                                    |
| tracked-blocks                | Defines blocks to track and alert thresholds. Format: `MATERIAL:AMOUNT:"Pretty Name"`.                                         | See config list                                                               |
| show-update-notice            | Show message to players with `veinguard.update` when a new version is available.                                               | true                                                                          |
| debug-mode                    | Enable debug logging. Keep false for production.                                                                               | false                                                                         |
| enable-worldguard             | Whether to enable WorldGuard integration. This registers the 'veinguard-check' flag in WorldGuard.                             | true                                                                          |
| patrol-teleport-seconds       | Seconds between each teleport during patrol.                                                                                   | 45                                                                            |
| patrol-finish-action          | Action to take when all players have been visited: `LOOP` (restart) or `STOP` (end patrol).                                    | STOP                                                                          |
| patrol-boss-bar               | Configures the patrol boss bar. Includes sub-options for patrolling color, paused color, and style.                            | `patrolling-color: BLUE, paused-color: YELLOW, style: SOLID`                   |



---

## blocks-broken-in-last-minutes

**Description:**  
This setting defines the **time window (in minutes)** that VeinGuard uses to evaluate how many blocks a player has broken. The plugin counts the number of blocks broken within this period to determine if a player exceeds the alert thresholds defined in `tracked-blocks`.

**Default Value:**
```yaml
blocks-broken-in-last-minutes: 5
```

---

## alert-cooldown-type

**Description:**  
This setting determines **how VeinGuard calculates the cooldown between alerts** for a player. It controls whether cooldowns are tracked **per block type** or **per player across all alerts**.

**Options:**
- `BLOCK` — Each block type has its own separate cooldown. Alerts for different block types are independent.
- `ALERT` — A single cooldown applies to all alerts for a player, regardless of block type.

**Default Value:**
```yaml
alert-cooldown-type: BLOCK
```

---

## alert-cooldown-seconds

**Description:**  
This setting defines the **cooldown time in seconds between alert notifications** for a player. The cooldown behavior depends on the `alert-cooldown-type` setting:

- If `BLOCK` is selected, the cooldown applies **separately to each block type**.
- If `ALERT` is selected, the cooldown applies **to all alerts for the player** regardless of block type.

**Default Value:**
```yaml
alert-cooldown-seconds: 30
```

---

## ignore-creative-mode

**Description:**  
This setting determines whether VeinGuard **ignores players in CREATIVE mode** when tracking block breaks.
- `true` — Players in CREATIVE mode are not monitored and cannot trigger alerts.
- `false` — Creative mode players are tracked like any other player.

**Default Value:**
```yaml
ignore-creative-mode: true
```

---

## ignore-above-y-level

**Description:**  
This setting defines the **maximum Y-level** at which VeinGuard tracks block breaks.
- Blocks broken **above this level** are ignored and will not contribute to alerts.
- Useful for ignoring surface mining or creative landscaping that could trigger false positives.

**Default Value:**
```yaml
ignore-above-y-level: 64
```

---

## player-report-page-entries

**Description:**  
This setting defines the **maximum number of entries displayed per page** when running the command `/vg check <player> [page]`.
- Helps keep player reports readable by splitting large lists of block breaks across multiple pages.

**Default Value:**
```yaml
player-report-page-entries: 7
```

---

## tracked-blocks-page-entries

**Description:**  
This setting defines the **maximum number of entries displayed per page** when running the command `/vg tracked-blocks list`.
- Useful for keeping the list of tracked blocks readable if your server monitors many block types.

**Default Value:**
```yaml
tracked-blocks-page-entries: 7
```

---

## send-alerts-to-console

**Description:**  
This setting controls whether VeinGuard **sends alert messages to the server console**.
- `true` — All alerts will appear in the console in addition to any staff notifications.
- `false` — Alerts will only be sent to staff according to `alert-delivery-type` and other settings.

**Default Value:**
```yaml
send-alerts-to-console: true
```

---

## alert-delivery-type

**Description:**  
This setting determines **how VeinGuard delivers alert notifications to staff**.

**Options:**
- `CHAT` — Sends alerts as chat messages to staff members.
- `ACTION_BAR` — Displays alerts in the action bar above the player’s hotbar.
    - Action bar messages are **queued**, with each message lasting **5 seconds** before the next one is displayed to prevent overlapping.
- `NONE` — Disables staff notifications entirely.

**Default Value:**
```yaml
alert-delivery-type: CHAT
```

---

## staff-join-violation-alert

**Description:**  
This setting determines whether staff members are **notified of players currently violating block break thresholds** when they join the server.
- `true` — Staff will receive a notification listing the number of players currently in violation.
- `false` — No notifications are sent on staff join.

**Default Value:**
```yaml
staff-join-violation-alert: false
```

---

## alert-sound

**Description:**  
This setting controls the **sound notification** that plays for staff when an alert is triggered.  
It has the following nested sub-options:

- `enabled` — Whether the alert sound is active.
    - `true` to play the sound on alerts.
    - `false` to disable the sound entirely.
- `sound` — The **Bukkit sound** to play.
    - Must be a valid Bukkit sound: [Bukkit Sounds](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html)
- `volume` — The **volume** of the sound (default `1.0`).
- `pitch` — The **pitch** of the sound (default `1.0`).

**Default Values:**
```yaml
alert-sound:
  enabled: true
  sound: ENTITY_EXPERIENCE_ORB_PICKUP
  volume: 1.0
  pitch: 1.0
```

---

## discord-webhook-url

**Description:**  
This setting allows you to **send VeinGuard alert notifications to a Discord channel** via a webhook.
- Enter a valid Discord webhook URL to enable alerts.
- Leave blank (`""`) to disable Discord notifications.

**Default Value:**
```yaml
discord-webhook-url: ""
```

---

## ignored-tools

**Description:**  
This setting defines a **list of tools that VeinGuard will ignore** when tracking block breaks.
- Any block broken with a tool listed here will **not contribute to alert thresholds**.
- Tools must use **valid Bukkit material names**.

**Default Value:**
```yaml
ignored-tools:
  - WOODEN_HOE
```

---

## disabled-worlds

**Description:**  
This setting defines a **list of worlds in which block break tracking is disabled**.
- VeinGuard will **ignore all block break activity** in the worlds listed here.
- Useful for test worlds, creative builds, or areas where monitoring is not needed.

**Default Value:**
```yaml
disabled-worlds:
  - test_world
```

---

## tracked-blocks

**Description:**  
This setting defines **which blocks VeinGuard tracks** and the **alert thresholds** for each.  
Each entry consists of three parts: the block type, the alert threshold, and a readable name for reports.

**Parts Explained:**
- `MATERIAL` — The **exact Bukkit material name** of the block to track.
    - Must be valid: see [Bukkit Materials](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)
    - Example: `DIAMOND_ORE`, `NETHER_QUARTZ_ORE`, `AMETHYST_BLOCK`

- `AMOUNT` — The **number of blocks a player can break** within the time window (`blocks-broken-in-last-minutes`) **before an alert is triggered**.
    - Example: `15` means that breaking 15 or more blocks of this type within the configured time triggers an alert.

- `"Pretty Name"` — A **human-readable name** used in staff alerts and reports.
    - Example: `"Diamond Ore"` or `"Ancient Debris"`
    - Quotes are required if the name contains spaces.

### Example Entry
```yaml
DIAMOND_ORE:15:"Diamond Ore"
```

---

## show-update-notice

**Description:**  
This setting controls whether VeinGuard **notifies players with the `veinguard.update` permission** when a new plugin version is available.
- `true` — Players with the permission will see an update message on join.
- `false` — No update messages are shown.

**Default Value:**
```yaml
show-update-notice: true
```

---

## debug-mode

**Description:**  
This setting enables **debug logging** for the plugin.
- `true` — Detailed debug information will be printed to the console.
- `false` — Only standard informational and error messages will be logged.
- **Note:** Keep this disabled for production unless troubleshooting.

**Default Value:**
```yaml
debug-mode: false
```

---

## enable-worldguard

**Description:**
This setting determines whether VeinGuard **integrates with WorldGuard**.
- `true` — VeinGuard will register a custom boolean flag `veinguard-check` in WorldGuard. This flag can be used to enable/disable VeinGuard tracking on a per-region basis.
- `false` — WorldGuard integration is disabled entirely, and the custom flag will not be registered.
- **Note:** This option is loaded during the plugin's `onLoad` phase. Changing it requires a full server restart to take effect if the flag was already registered.

**Default Value:**
```yaml
enable-worldguard: true
```

---

## patrol-teleport-seconds

**Description:**  
This setting defines the **time interval in seconds** between automatic teleports during a staff patrol.
- Every time the countdown reaches zero, the staff member is teleported to the next player in the queue.

**Default Value:**
```yaml
patrol-teleport-seconds: 45
```

---

## patrol-finish-action

**Description:**  
This setting determines **what happens when a staff member has visited all online players** during a patrol.

**Options:**
- `LOOP` — Restarts the patrol from the beginning, shuffling the player list again.
- `STOP` — Automatically ends the patrol and returns the staff member to their original location and gamemode.

**Default Value:**
```yaml
patrol-finish-action: STOP
```

---

## patrol-boss-bar

**Description:**  
This setting configures the **appearance of the boss bar** shown to staff members during a patrol.  
It has the following nested sub-options:

- `patrolling-color` — The color of the boss bar while the patrol is active.
- `paused-color` — The color of the boss bar when the patrol is paused.
- `style` — The style of the boss bar (e.g., `SOLID`, `SEGMENTED_6`, etc.).

**Default Values:**
```yaml
patrol-boss-bar:
  patrolling-color: BLUE
  paused-color: YELLOW
  style: SOLID
```

---

## Full Config File

Below is the **complete default `config.yml`** for VeinGuard version 1.1.5.  
You can copy this as a reference when configuring your server.  
All options are explained in the sections above.

```yaml
#########################################################
# VeinGuard - Configuration File & Options
# Version: 1.1.5
#
# Useful Links:
#   Wiki:       https://github.com/JerrysPlugins/veinguard/wiki
#   Discord:    https://discord.com/invite/sW7zu4RXmD
#   Issues:     https://github.com/JerrysPlugins/veinguard/issues
#   GitHub:     https://github.com/JerrysPlugins/veinguard
#   Modrinth:   https://modrinth.com/plugin/veinguard
#   Spigot:     https://www.spigotmc.org/resources/veinguard-antixray-for-1-17-1-21.131871/
#   Hangar:     https://hangar.papermc.io/JerrysPlugins/VeinGuard
#########################################################
# DO NOT CHANGE
config-version: 8

# ===========================
# Tracking Settings
# ===========================

# Time window in minutes to count block break thresholds for alerting.
blocks-broken-in-last-minutes: 5

# Determines how the alert cooldown is calculated:
#   BLOCK - separate cooldown for each block type
#   ALERT - single cooldown for all alerts per player
alert-cooldown-type: BLOCK

# Cooldown in seconds between alerts, based on the alert-cooldown-type setting.
alert-cooldown-seconds: 30

# Whether to ignore players in CREATIVE mode when tracking block breaks.
ignore-creative-mode: true

# Maximum Y-level to track block breaks. Blocks above this level are ignored.
ignore-above-y-level: 64

# ===========================
# Command Pagination
# ===========================

# Maximum number of entries per page when running '/vg check <player> [page]'.
player-report-page-entries: 7

# Maximum number of entries per page in the '/vg tracked-blocks list' command.
tracked-blocks-page-entries: 7

# ===========================
# Alert Settings
# ===========================

# Determines if alert messages are sent to the console.
send-alerts-to-console: true

# Method to deliver alerts to staff:
#   CHAT - sends a chat message
#   ACTION_BAR - displays in the action bar
#   NONE - disables staff alerts
alert-delivery-type: CHAT

# Notify staff with the total number of players currently violating thresholds when they join.
staff-join-violation-alert: false

# Sound settings for alert notifications.
# Requires a valid Bukkit sound: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
alert-sound:
  enabled: true
  sound: ENTITY_EXPERIENCE_ORB_PICKUP
  volume: 1.0
  pitch: 1.0

# Commands executed when an alert is triggered.
# Placeholders:
#   {player} - player name
#   {block}  - block type
#   {count}  - total broken blocks
#   {world}, {x}, {y}, {z} - block coordinates
alert-commands:
  - 'examplecmd msg {player} &bStop xraying!'
  - 'examplecmd broadcast &b{player} might be xraying!'

# Optional Discord webhook to send alerts to. Leave blank to disable.
discord-webhook-url: ""

# ===========================
# Patrol Settings
# ===========================

# Seconds between each teleport during patrol.
patrol-teleport-seconds: 45

# What to do when all players have been visited.
#   LOOP - Starts the patrol over from the beginning.
#   STOP - Stops the patrol and returns the staff member to their original location.
patrol-finish-action: STOP

# Boss bar settings for patrol.
patrol-boss-bar:
  patrolling-color: BLUE
  paused-color: YELLOW
  style: SOLID

# ===========================
# Tracking Exceptions
# ===========================

# List of tools that are ignored when tracking block breaks.
# Must be a valid Bukkit material: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html
ignored-tools:
  - WOODEN_HOE

# Worlds in which block break tracking is disabled.
disabled-worlds:
  - test_world

# ===========================
# Tracked Blocks & Thresholds
# ===========================

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

# ===========================
# Integrations
# ===========================

# Whether to enable WorldGuard integration.
# This registers the 'veinguard-check' flag in WorldGuard.
enable-worldguard: true

# ===========================
# Update & Debug
# ===========================

# Show a message to players with 'veinguard.update' permission when a new version is available.
show-update-notice: true

# Leave this disabled.
debug-mode: false
```