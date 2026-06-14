# 🔑 Commands & Permissions

This guide provides a comprehensive overview of all **VeinGuard** commands and their associated permissions. Use this information to configure your staff ranks and manage plugin access effectively.

---

## 🛡️ Permissions Overview

VeinGuard uses a granular permission system. For most servers, we recommend using [LuckPerms](https://luckperms.net/) to manage these nodes.

### 👑 Core Ranks
| Node | Description | Includes |
| :--- | :--- | :--- |
| `veinguard.admin` | **Full access** to all features, commands, and bypasses. | Everything. |
| `veinguard.mod` | **Moderator access** for monitoring and investigating. | `notify`, `bypass`, `check`, `history`, `top`, `patrol`, `toggle-alerts`. |

### 🛠️ Utility Permissions
*   `veinguard.notify` — Allows receiving real-time in-game alerts.
*   `veinguard.bypass` — Complete exemption from all block tracking.
*   `veinguard.bypass.[MATERIAL]` — Exemption for a specific block (e.g., `veinguard.bypass.DIAMOND_ORE`). *Note: Material names are case-sensitive.*
*   `veinguard.update` — Receive notifications when a new version of VeinGuard is available.

---

## 💻 Commands Reference

The base command for the plugin is `/veinguard`, with the alias `/vg`.

### 🔍 Investigation Commands

#### `/vg check <player> [page]`
*   **Permission:** `veinguard.command.check`
*   **Description:** Displays a real-time report of a player's recent mining activity, showing exact block counts and highlighting violations.

#### `/vg history <player> [time] [page]`
*   **Permission:** `veinguard.command.history`
*   **Description:** Retrieves historical mining data from the database.
*   **Arguments:** `[time]` can be formatted like `1h`, `2d`, or `7d`.

#### `/vg top [time] [page]`
*   **Permission:** `veinguard.command.top`
*   **Description:** Displays a leaderboard of players with the highest violation levels, block counts, and total alerts triggered over a specific period.

#### `/vg patrol <action>`
*   **Permission:** `veinguard.command.patrol`
*   **Description:** Manage the automated staff patrol.
*   **Actions:** `start`, `stop`, `pause`, `resume`, `next`, `back`.

---

### 👮 Staff Utility

#### `/vg staffmsg <message>`
*   **Permission:** `veinguard.command.staffmsg`
*   **Description:** Sends a formatted message to all online staff members with notification permissions.

#### `/vg msg <player> <message>`
*   **Permission:** `veinguard.command.msg`
*   **Description:** Sends a private plugin-formatted message to a player. Ideal for automated violation actions.

#### `/vg toggle-alerts [player]`
*   **Permission:** `veinguard.command.toggle-alerts`
*   **Extra Permission:** `veinguard.command.toggle-alerts.others` (required to target other staff)
*   **Description:** Toggles your own alert visibility or manages it for another staff member.

#### `/vg help [page]`
*   **Permission:** `veinguard.command.help`
*   **Description:** Displays a list of available VeinGuard commands with usage instructions.

#### `/vg mute <player>`
*   **Permission:** `veinguard.command.mute`
*   **Description:** Globally silences alerts originating from a specific player for all staff.

#### `/vg unmute <player>`
*   **Permission:** `veinguard.command.unmute`
*   **Description:** Restores alerts for a previously muted player.

---

### ⚙️ Administrative Commands

#### `/vg reload`
*   **Permission:** `veinguard.command.reload`
*   **Description:** Instantly reloads all configuration and language files without a server restart.

#### `/vg tracked-blocks <add|list|remove>`
*   **Permission:** `veinguard.command.tracked-blocks.[add|list|remove]`
*   **Description:** Manage which blocks are monitored, their thresholds, and their pretty names in real-time.
*   **Usage Example:** `/vg tracked-blocks add DIAMOND_ORE 15 Diamond Ore`

#### `/vg reset <player>`
*   **Permission:** `veinguard.command.reset`
*   **Description:** Clears active tracking data and VL scores for a specific player.

#### `/vg resetall`
*   **Permission:** `veinguard.command.resetall`
*   **Description:** Wipes active tracking data and VL scores for everyone currently being tracked.

#### `/vg purge <time> [player]`
*   **Permission:** `veinguard.command.purge`
*   **Description:** Manually deletes old historical data from the database. In-game staff are restricted to purging data older than 15 days.

---

## 📊 Permission Matrix

| Node | Description | Admin | Mod |
| :--- | :--- | :---: | :---: |
| `veinguard.admin` | Full Control | ✅ | ❌ |
| `veinguard.mod` | Standard Moderation | ❌ | ✅ |
| `veinguard.notify` | Receive Alerts | ✅ | ✅ |
| `veinguard.bypass` | Ignore Tracking | ✅ | ✅ |
| `veinguard.command.check` | View Active Stats | ✅ | ✅ |
| `veinguard.command.history` | View Database History | ✅ | ✅ |
| `veinguard.command.patrol` | Use Patrol System | ✅ | ✅ |
| `veinguard.command.top` | View Leaderboard | ✅ | ✅ |
| `veinguard.command.help` | View Help Menu | ✅ | ✅ |
| `veinguard.command.msg` | Send Plugin Msg | ✅ | ❌ |
| `veinguard.command.mute` | Mute a Player | ✅ | ❌ |
| `veinguard.command.unmute` | Unmute a Player | ✅ | ❌ |
| `veinguard.command.staffmsg` | Msg to Staff | ✅ | ✅ |
| `veinguard.command.reset` | Reset a Player | ✅ | ❌ |
| `veinguard.command.resetall` | Wipe All Data | ✅ | ❌ |
| `veinguard.command.purge` | Purge Database | ✅ | ❌ |
| `veinguard.command.reload` | Reload Plugin | ✅ | ❌ |
| `veinguard.command.toggle-alerts` | Toggle Alerts | ✅ | ✅ |
| `veinguard.command.toggle-alerts.others` | Toggle Alerts (Others) | ✅ | ❌ |
| `veinguard.command.tracked-blocks.add` | Add Tracked Block | ✅ | ❌ |
| `veinguard.command.tracked-blocks.list` | List Tracked Blocks | ✅ | ✅ |
| `veinguard.command.tracked-blocks.remove` | Remove Tracked Block | ✅ | ❌ |

---

*Last updated for VeinGuard v2.0.0*
