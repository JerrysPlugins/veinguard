# 🚀 Installation Guide

Getting **VeinGuard** up and running on your server is a straightforward process. Follow these steps to ensure a smooth installation and initial setup.

---

## 📋 Prerequisites

Before you begin, ensure your server meets the following requirements:

*   **Server Platform:** Spigot, Paper, Purpur (1.21+), or any compatible fork.
*   **Minecraft Version:** 1.18 or newer.
*   **Java Version:** Java 17 or newer is required.
*   **Access:** You must have access to the server's `plugins/` directory and the ability to restart the server.

---

## 🛠️ Step-by-Step Installation

### 1. Download the Plugin
Visit one of our official distribution pages and download the latest `.jar` file:
*   [GitHub Releases](https://github.com/JerrysPlugins/veinguard/releases) (Recommended)
*   [Modrinth](https://modrinth.com/plugin/veinguard)
*   [SpigotMC](https://www.spigotmc.org/resources/131871/)
*   [Hangar](https://hangar.papermc.io/JerrysPlugins/VeinGuard)

### 2. Upload to Server
Move the downloaded `VeinGuard-vX.X.X.jar` file into your server's `plugins/` folder.

### 3. Initial Boot
Start or restart your server. VeinGuard will initialize and automatically generate its default configuration files in the `plugins/VeinGuard/` directory.

### 4. Custom Configuration
Open `config.yml` and `lang.yml` to tailor the plugin to your server's needs. Key areas to review include:
*   **Database Settings:** Choose between SQLite or MySQL.
*   **Tracked Blocks:** Verify the alert thresholds for materials like Diamonds and Ancient Debris.
*   **Alert Delivery:** Decide if you want alerts in Chat, Action Bar, or via Discord.

### 5. Apply Changes
If you modified the configuration files, apply the changes instantly by running the command:
` /vg reload `
*(Alternatively, you can restart the server.)*

---

## ✅ Verifying the Installation

To ensure everything is working correctly, check the following:

1.  **Console Logs:** Look for the message `[VeinGuard] Enabling VeinGuard vX.X.X` in your startup logs.
2.  **In-Game Commands:** Type `/vg` or `/vg help` in-game. If you see the plugin information or help menu, the installation was successful.
3.  **Permissions:** If you are not an OP, ensure you have granted yourself the `veinguard.admin` permission via LuckPerms to access all features.

---

## 💡 Pro-Tips

*   **Automate Updates:** Enable `show-update-notice` in the config to receive in-game notifications when a new version is released.
*   **Test Environment:** We highly recommend testing new configurations on a staging server before applying them to your production environment.
*   **WorldGuard:** If you use WorldGuard, remember you can now use the `veinguard-check` flag to toggle tracking in specific regions!

---

*Need help? Join our [Discord community](https://discord.gg/sW7zu4RXmD) for support!*
