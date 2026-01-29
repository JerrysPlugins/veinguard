# Installation

This guide explains how to install **VeinGuard** on **Spigot**, **Paper**, or **Bukkit** servers.

---

## Prerequisites

Before installing VeinGuard, ensure the following:

- Your server is running **Minecraft 1.17 or newer**.
- You have **Spigot**, **Paper**, or **Bukkit** installed.
- You have access to your server’s `plugins` folder and permission to restart the server.

---

## Step-by-Step Installation

1. **Download VeinGuard**
    - Visit the [Releases Page](https://github.com/JerrysPlugins/veinguard/releases) and download the latest `.jar` file.

2. **Place the Plugin**
    - Move the `VeinGuard-x.x.x.jar` file into your server’s `plugins` folder.

3. **Start the Server**
    - Start or restart your server.
    - VeinGuard will generate its configuration files automatically inside `plugins/VeinGuard`.

4. **Configure the Plugin**
    - Open the `config.yml` file inside `plugins/VeinGuard`.
    - Adjust settings such as:
        - Alert options
        - Command behavior
        - Permissions defaults
    - Save your changes when finished.

5. **Restart the Server or Reload (Optional)**
    - If you made changes to `config.yml`, you can use '/vg reload' or restart the server to apply them.

---

## Verifying Installation

After starting the server:

- Check your server console for VeinGuard initialization messages.
- Use `/veinguard` or `/vg help` in-game to verify that commands are working.
- Ensure that no errors appear in the console related to VeinGuard or missing dependencies.

---

## Notes

- VeinGuard works the same way on **Spigot**, **Paper**, and **Bukkit**, so there is no need for separate installation steps.
- Keep your plugin updated by checking the [Releases Page](https://github.com/JerrysPlugins/veinguard/releases) regularly.
- If you encounter issues, refer to the [Bugs & Issues](#bugs--issues) section on the wiki or report them on GitHub.

---

*End of Installation Guide*
