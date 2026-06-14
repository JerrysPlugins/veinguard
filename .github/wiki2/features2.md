# 🛡️ Feature Overview

**VeinGuard** is more than just a simple alert plugin; it is a comprehensive behavioral analysis tool designed to identify and document X-ray abuse while maintaining server performance.

---

## 🔍 Intelligent Detection Model

### 📦 Mining Incident Model
Introduced in v2.0.0, this model moves away from spammy "per-block" alerts to a structured "Incident" system.
*   **Alert Aggregation:** Consecutive breaks of the same material are grouped into a single **Incident**.
*   **Session Tracking:** Records the start and end time of a mining session, including the exact path taken (Coordinates A → Coordinates B).
*   **Absolute Accuracy:** Even blocks broken during an alert cooldown are captured and added to the incident's total count.
*   **Database Efficiency:** Highly optimized storage ensures a clean database even on high-traffic servers.

### 🎯 Core Tracking Features
*   **Real-Time Monitoring:** Tracks every break of your configured materials.
*   **Rolling Time Windows:** Evaluates activity over a customizable window (default: 5 minutes).
*   **Smart Filtering:**
    *   **Y-Level:** Ignore breaks above a certain depth to focus on underground mining.
    *   **Gamemode:** Automatically ignore players in Creative mode.
    *   **World Blacklist:** Disable tracking in specific worlds (e.g., Creative or Resource worlds).
    *   **Tool Exceptions:** Ignore mining done with specific tools (e.g., Silk Touch or hoes).
*   **Granular Bypasses:** Use permissions to exempt staff or trusted players globally or for specific materials.

---

## 📢 Multi-Channel Alert System
Keep your staff informed through their preferred channels.

*   **In-Game Notifications:** Choose between standard **Chat Messages** or non-intrusive **Action Bar** alerts.
*   **Discord Webhooks:** Send detailed alerts (including player name, block type, count, and location) to your Discord staff channels.
*   **Automated Actions:** Execute custom console commands (e.g., `/kick`, `/freeze`, or logging) immediately when a threshold is met.
*   **Violation Level (VL) System:** Assign "suspicion scores" to materials. A player's VL grows as they trigger alerts and decays naturally over time.

---

## 🕵️ Staff & Investigation Tools

### 🚁 Advanced Patrol System
Monitor your players in Spectator mode without the manual effort.
*   **Automated Cycling:** Automatically teleports you to the next online player at a set interval.
*   **Interactive UI:** A Boss Bar shows you who you are watching, who is next, and how much time remains.
*   **Full Control:** Pause the timer to investigate further, skip ahead, or go back to a previous player.

### 📊 Reporting & Analytics
*   **Real-Time Reports (`/vg check`):** Get an instant breakdown of a player's current mining rates.
*   **Historical Database (`/vg history`):** Review past incidents for any player, even if they are offline.
*   **Leaderboards (`/vg top`):** Identify the most frequent violators over any timeframe.
*   **Violation Highlighting:** Reports clearly distinguish between normal activity and suspicious spikes.

---

## ⚙️ Integration & Performance
*   **WorldGuard Support:** Use the `veinguard-check` flag to control tracking in specific regions.
*   **Database Flexibility:** Support for **SQLite** (standard) and **MySQL/MariaDB** (for networks).
*   **Optimized Performance:** All heavy operations (webhooks, database writes, and lookups) are handled asynchronously.
*   **Hot-Reloading:** Change any setting or message in `config.yml` or `lang.yml` and apply it instantly with `/vg reload`.

---

*VeinGuard is continuously evolving. Join our [Discord](https://discord.gg/sW7zu4RXmD) to suggest new features or report bugs!*
