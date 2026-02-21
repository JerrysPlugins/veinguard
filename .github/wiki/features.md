# Features

VeinGuard is a comprehensive X-ray detection and player monitoring tool for Minecraft servers. Below is a detailed list of all features provided by the plugin.

## Core Tracking & Detection
- **Real-time Block Monitoring**: Tracks every block break for materials defined in the configuration.
- **Configurable Thresholds**: Define exactly how many blocks of a specific type (e.g., Diamond Ore) a player can break within a time window before staff are alerted.
- **Time Window Evaluation**: Automatically evaluates mining activity over a rolling time window (default 5 minutes).
- **Y-Level Filtering**: Ignore block breaks above a certain Y-level to prevent false positives from surface mining.
- **Creative Mode Filter**: Option to ignore players in Creative Mode.
- **World Blacklisting**: Disable tracking in specific worlds (e.g., creative or resource worlds).
- **Permission-based Bypasses**:
    - **Global Bypass**: Exempt staff or trusted players from all tracking with `veinguard.bypass`.
    - **Per-Material Bypass**: Exempt players from tracking for specific blocks using `veinguard.bypass.<MATERIAL>` (e.g., `veinguard.bypass.DIAMOND_ORE`).
- **Tool Exceptions**: Ignore block breaks when using specific tools (e.g., Wooden Hoe).

## Advanced Alert System
- **Multi-channel Alerts**:
    - **In-game Chat**: Standard alerts sent to staff members.
    - **Action Bar**: Non-intrusive alerts displayed above the hotbar, with an intelligent queuing system to prevent message overlap.
    - **Console Logging**: All alerts can be logged to the server console for audit purposes.
    - **Discord Integration**: Send real-time alerts to a Discord channel via Webhooks, including player name, block type, count, and coordinates.
- **Alert Cooldowns**:
    - **Per-Block Cooldown**: Prevent spam by setting a cooldown for each individual block type per player.
    - **Per-Alert Cooldown**: Apply a global cooldown for all alerts for a player.
- **Staff Join Notifications**: Notify staff members upon joining if any players currently exceed violation thresholds.
- **Customizable Alert Sounds**: Play a configurable Bukkit sound for staff whenever an alert is triggered.
- **Automated Commands**: Execute one or more server commands automatically when a player triggers an alert (e.g., messaging the player, broadcasting a warning, or logging to a file).

## Staff Patrol System
- **Automated Player Cycling**: Teleport through all online players at a configurable interval to monitor their activity in Spectator mode.
- **Boss Bar Interface**: Displays a countdown to the next teleport, the name of the current player being patrolled, and the next player in the queue.
- **Interactive Controls**:
    - **Pause/Resume**: Stop the automated timer to investigate a player further.
    - **Next/Back**: Manually skip to the next player or return to the previous one.
- **Smart Queue Management**: Shuffles the player list and avoids immediate revisits to the same player.
- **Automatic Cleanup**: Restores staff members to their original location and gamemode when the patrol stops or if they disconnect.

## Player Reporting & Data Management
- **Detailed History Reports**: View a comprehensive breakdown of a player's mining history for all tracked blocks using `/vg check <player>`.
- **Violation Highlighting**: Reports clearly mark which blocks have exceeded their thresholds.
- **Paginated Output**: Easy navigation of large reports or tracked block lists.
- **In-game Management**:
    - **Tracked Block Editor**: Add, remove, or modify tracked blocks and their thresholds in real-time via commands.
    - **Data Resets**: Clear mining history for a single player or reset all data globally.
    - **Alert Toggling**: Staff can mute alerts for themselves or for specific "suspect" players to reduce noise.

## Integration & Compatibility
- **Configurable Plugin Hooks**: Enable or disable integrations like **WorldGuard** via the configuration file to suit your server's needs.
- **Cross-Version Support**: Fully compatible with Minecraft 1.18 through 1.21+ (Spigot, Paper, Purpur).
- **Multi-module Architecture**: Decoupled API and Core modules for better maintainability and third-party integration.
- **Lightweight Design**: Built for performance with asynchronous tasks where possible (e.g., Discord webhooks and data lookups).
- **Comprehensive API**: Includes an experimental API for developers to hook into VeinGuard's tracking and alert systems.
- **Localization**: Fully customizable `lang.yml` to translate every message and boss bar title.
- **Update Checker**: Automatically notifies admins when a new version of VeinGuard is available.
