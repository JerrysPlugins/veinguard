# VeinGuard — Commands & Permissions

This page documents all available **commands** and **permission nodes** provided by **VeinGuard**.  
Each command requires the appropriate permission to execute. Permissions should be managed using a permissions plugin (LuckPerms recommended).

---

## Permissions

### Administrative Permissions

* **`veinguard.admin`**  
  Grants full administrative access to VeinGuard.  
  Includes **all commands**, **all bypasses**, and **all notification permissions**.

---

### Moderator Permissions

* **`veinguard.mod`**  
  Intended for staff members who monitor players but do not manage plugin configuration.  
  This permission grants the following nodes automatically:
    * `veinguard.notify`
    * `veinguard.bypass`
    * `veinguard.command`
    * `veinguard.command.check`
    * `veinguard.command.help`
    * `veinguard.command.toggle-alerts`
    * `veinguard.command.tracked-blocks.list`

---

### Notification & Utility Permissions

* **`veinguard.notify`**  
  Allows the player to receive in-game alerts when VeinGuard detects suspicious block-breaking activity.

* **`veinguard.bypass`**  
  Exempts the player from block-break tracking and violation checks.  
  Intended for staff members or trusted roles.

* **`veinguard.bypass.MATERIAL`**  
  Exempts the player from block-break tracking for a specific material. Material is CASE SENSITIVE!\
  Example: `veinguard.bypass.DIAMOND_ORE`\
  Intended for staff members or trusted roles.

* **`veinguard.update`**  
  Sends an in-game notification to the player when a new version of VeinGuard is available.

---

## Commands

### Base Command

## `/veinguard`
**Aliases:** `/vg`  
**Permission:** `veinguard.command`

**Description:**  
Displays basic plugin information.

---

## Subcommands

### `/veinguard help`
**Permission:** `veinguard.command.help`

**Description:**  
Displays a list of available VeinGuard commands along with brief usage information.

---

### `/veinguard check <player>`
### `/veinguard check <player> <page>`
**Permission:** `veinguard.command.check`

**Description:**  
Displays the specified player’s tracked block-break report.  
Results are paginated to make large reports easier to review.

---

### `/veinguard msg <player> <message>`
**Permission:** `veinguard.command.msg`

**Description:**  
Sends a formatted message to the specified player.  
This command is primarily intended for internal use with the `alert-commands` option in `config.yml`, but can be executed manually if needed.

---

### `/veinguard mute <player>`
**Permission:** `veinguard.command.mute`

**Description:**  
Globally mutes alerts originating from the specified player.  
While muted, **no staff members** with `veinguard.notify` will receive alerts related to that player.

---

### `/veinguard unmute <player>`
**Permission:** `veinguard.command.unmute`

**Description:**  
Removes a global mute from the specified player, allowing alerts related to them to be received again.

---

### `/veinguard patrol <start|stop|pause|resume|next|back>`
**Permission:** `veinguard.command.patrol`

**Description:**  
Patrol through all online players with an automated cycle and boss bar display.  
Staff can skip to the next player, go back to the previous one, or pause/resume the automated cycle.

---

### `/veinguard toggle-alerts`
### `/veinguard toggle-alerts <player>`
**Permissions:**
* `veinguard.command.toggle-alerts`
* `veinguard.command.toggle-alerts-others` (required when targeting other staff)

**Description:**
* Without arguments: Toggles alerts **for yourself only**. When disabled, you will not receive alerts from any player.
* With `<player>`: Toggles alerts for another staff member, provided they have the `veinguard.notify` permission.

---

### `/veinguard tracked-blocks <add|list|remove> (?list [page]) <block> <threshold> <pretty-name>`
**Permissions:** `veinguard.command.tracked-blocks`, `veinguard.command.tracked-blocks.add`,
`veinguard.command.tracked-blocks.list`, `veinguard.command.tracked-blocks.remove`

**Description:**  
Add, list or remove tracked blocks while in-game.

---

### `/veinguard reset <player>`
**Permission:** `veinguard.command.reset`

**Description:**  
Clears the tracked block-break history for the specified player only.

---

### `/veinguard resetall`
**Permission:** `veinguard.command.resetall`

**Description:**  
Resets **all tracked block-break data** for every player currently stored by VeinGuard.  
Use with caution.

---

### `/veinguard reload`
**Permission:** `veinguard.command.reload`

**Description:**  
Reloads VeinGuard and all associated configuration files without requiring a full server restart.

---

## Notes

* Commands and permissions are designed to be modular—grant only what each role requires.
* Using `veinguard.admin` overrides all individual permission checks.
* A permissions plugin is required for proper role management.

---

*Last updated for VeinGuard 1.1.5+*

## Permission Overview

The table below reflects VeinGuard’s actual permission structure as defined in the plugin configuration.  
Regular players do not have access to any VeinGuard functionality unless permissions are explicitly granted.

| Permission Node                           | Description                                     | Admin | Moderator | Player |
|-------------------------------------------|-------------------------------------------------|:-----:|:---------:|:------:|
| `veinguard.admin`                         | Full administrative access to VeinGuard         |   ✔   |     ❌     |   ❌    |
| `veinguard.mod`                           | Grants limited moderation permissions           |   ❌   |     ✔     |   ❌    |
| `veinguard.notify`                        | Receive in-game block break alerts              |   ✔   |     ✔     |   ❌    |
| `veinguard.bypass`                        | Exempt from block-break tracking                |   ✔   |     ✔     |   ❌    |
| `veinguard.bypass.MATERIAL`               | Exempt from specific block-break tracking       |   ❌   |     ❌     |   ❌    |
| `veinguard.update`                        | Receive plugin update notifications             |   ✔   |     ❌     |   ❌    |
| `veinguard.command`                       | Access to base `/veinguard` command             |   ✔   |     ✔     |   ❌    |
| `veinguard.command.help`                  | Access to `/veinguard help`                     |   ✔   |     ✔     |   ❌    |
| `veinguard.command.check`                 | View player block-break reports                 |   ✔   |     ✔     |   ❌    |
| `veinguard.command.msg`                   | Send formatted messages via VeinGuard           |   ✔   |     ❌     |   ❌    |
| `veinguard.command.mute`                  | Globally mute alerts from a player              |   ✔   |     ❌     |   ❌    |
| `veinguard.command.unmute`                | Remove a global alert mute                      |   ✔   |     ❌     |   ❌    |
| `veinguard.command.patrol`                | Automated player patrol for staff               |   ✔   |     ❌     |   ❌    |
| `veinguard.command.toggle-alerts`         | Toggle alerts for yourself                      |   ✔   |     ✔     |   ❌    |
| `veinguard.command.toggle-alerts.others`  | Toggle alerts for other staff members           |   ✔   |     ❌     |   ❌    |
| `veinguard.command.tracked-blocks.add`    | Add a new tracked block                         |   ✔   |     ❌     |   ❌    |
| `veinguard.command.tracked-blocks.list`   | List the current tracked blocks and their data  |   ✔   |     ✔     |   ❌    |
| `veinguard.command.tracked-blocks.remove` | Remove a currently tracked block                |   ✔   |     ❌     |   ❌    |
| `veinguard.command.reset`                 | Reset block-break history for a player          |   ✔   |     ❌     |   ❌    |
| `veinguard.command.resetall`              | Reset block-break history for all players       |   ✔   |     ❌     |   ❌    |
| `veinguard.command.reload`                | Reload VeinGuard plugin and configuration files |   ✔   |     ❌     |   ❌    |
