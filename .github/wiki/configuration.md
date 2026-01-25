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
blocks-broken-in-last-minutes: 5

# Cooldown (in seconds) between alerts for the same player and block type.
alert-cooldown-seconds: 45

# Ignore block breaks by players in Creative mode.
ignore-creative-mode: true

# Blocks broken above this Y-level will not be tracked.
ignore-above-y-level: 64

# Number of tracked block entries to display per page in '/veinguard check <Player>'.
player-report-page-entries: 7

# Alerts configuration
send-alerts-to-console: true
send-alerts-to-staff: true
staff-join-violation-alert: false

# Commands to execute when a player triggers a VeinGuard alert
alert-commands: {}

# Optional Discord webhook for staff alert notifications
discord-webhook-url: ""

# Tools to ignore for block tracking
ignored-tools:
  - WOODEN_HOE

# Worlds where VeinGuard tracking is disabled
disabled-worlds:
  - test_world

# Tracked blocks and alert thresholds
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

# Update notifications
show-update-notice: true

# Internal config version (do not change)
config-version: 1

# Debug mode for troubleshooting
debug-mode: false
