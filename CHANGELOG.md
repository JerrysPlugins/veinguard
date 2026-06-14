## v2.0.0-PUBLISH
*   Violation Level (VL) System: A new suspicion-based scoring system that tracks player behavior over time and triggers automated actions when thresholds are met.
*   Mining Incident Model: Intelligent alert grouping that combines consecutive breaks into single sessions for cleaner logs and more accurate investigation.
*   New Investigation Commands:
    *   `/vg history <player>`: View a player's past mining sessions from the persistent database.
    *   `/vg top`: See a leaderboard of the most active/suspicious players on your server.
    *   `/vg purge`: Easily manage database size by removing old historical records.
    *   `/vg staffmsg`: Coordinate with other staff members instantly.
*   MySQL & MariaDB Support: Synchronize mining data across your entire network with full MySQL support alongside the standard SQLite.
*   Discord Integration: Improved webhook reliability with secure data escaping and detailed alert formatting.
*   Automated Database Cleanup: New background tasks to keep your database optimized automatically.
*   In-Game Management: Add, remove, or list tracked blocks and manage multipliers directly through commands.
*   Performance Optimizations: Asynchronous logging and database operations ensure zero impact on server TPS.

## v2.0.0
* Added a comprehensive Violation Level (VL) system to quantify the severity of suspicious mining activity.
* Implemented the **Mining Incident Model** for database tracking, grouping consecutive alerts into single incident records for better accuracy and readability.
* Added `last_x`, `last_y`, `last_z`, and `last_timestamp` to the database schema (Schema Version 4) to track the full range and duration of mining incidents.
* Added `violation-settings` in `config.yml` to control VL decay and initial values.
* Added `violation-actions` in `config.yml` to trigger automated console commands at specific VL thresholds.
* Added material weights to `tracked-blocks-violation-multipliers` (moved from `tracked-blocks` for better configuration compatibility).
* Updated `/vg check <player>` to display the player's active Violation Level.
* Updated `/vg tracked-blocks list` to display material weights.
* Updated `/vg tracked-blocks add` to support in-game block tracking management.
* Implemented persistent VL storage in the database (Schema Version 3).
* Added asynchronous VL decay task to gradually reduce player VL over time.
* Added `{vl}` placeholder to alert messages and automated commands.
* Added `actions-enabled` in `violation-settings` to toggle automated actions independently.
* Added detailed instructions to the `violation-actions` section in `config.yml`.
* Updated `/vg history <player> [time] [page]` command to view historical alert data from the database.
* Added `/vg purge <time> [player]` command to manually purge old alert data from the database.
* Added `/vg staffmsg <message>` command to send formatted messages to all online staff.
* Added new configuration options for history command (default time frame, pagination).
* Added automated database cleanup task to periodically purge old alert records.
* Added new configuration options for database cleanup (enabled, interval, retention).
* Implemented a persistent statistics system with support for both SQLite and MySQL/MariaDB.
* Added new configuration options for database connectivity and table prefixes.
* Implemented an automated database schema update system for seamless plugin upgrades.
* Added a database migration utility to facilitate moving data between different SQL backends.
* Implemented asynchronous alert logging to ensure zero impact on server performance.
* Added automatic schema migration to convert existing MySQL timestamps to the new BIGINT format.
* Added `/vg top [time] [page]` command and associated permission `veinguard.command.top` to view a leaderboard of top violators.
* Added config option 'top-alert-report-page-entries' to control pagination for the top command.
* Added database purge functionality to support future data maintenance commands.
* Added utility for parsing complex time strings (e.g., '1m2d3h').
* Added full localization support for the new history command in `lang.yml`.
* Updated configuration to version 9 and language to version 8 to support new history command settings.
* Verified and added standard copyright headers to all Java classes in the project.
* Bumped version to 2.0.0 across all modules.
* Standardized and unified timestamp handling across different database types (SQLite and MySQL) to ensure accurate and consistent time-based filtering.
* Optimized database storage for both SQLite and MySQL by using numeric milliseconds (BIGINT) for timestamps, resolving previous chronological comparison issues.
* Fixed a memory leak in `PlayerTracker` by ensuring empty data structures are removed from memory during cleanup.
* Enhanced thread safety in `PlayerTracker` by migrating to `ConcurrentHashMap` for material tracking, preventing race conditions during asynchronous cleanup.
* Improved task management in `PlayerTracker` by ensuring the background cleanup task is properly canceled on plugin shutdown.
* Standardized data expiration logic in `PlayerTracker` to ensure consistent behavior between real-time and background cleanup.
* Improved reliability of player and staff muting by using thread-safe collections.
* Fixed a bug in the Mining Incident Model where new incidents incorrectly inherited the block count from previous, expired incidents.
* Fixed a bug where Violation Level actions were not triggering because the check was performed before the level was incremented.
* Fixed an inconsistency where alert messages showed an outdated Violation Level compared to the `/vg check` command.
* Fully resolved the issue with Violation Level actions not triggering by implementing a deep-search configuration loading method (`getValues(true)`) that handles nested thresholds caused by Bukkit's path parsing of keys containing dots (e.g., `20.0`).
* Optimized Violation Level actions to only trigger once when a threshold is crossed, preventing repetitive commands on every later alert.
* Added extra debug logging for Violation Level action loading and execution to assist in future troubleshooting.
* Fixed an `SQLException` ("database connection closed") occurring during plugin shutdown by ensuring that the database disconnects only after all other services have finished their operations.
* Improved performance of the Patrol system by implementing name caching, reducing redundant player name lookups during Boss Bar updates.
* Enhanced the `DatabaseMigrator` to include the `violations` table during database transfers, ensuring no data loss when switching SQL backends.
* Refactored database logic to remove code duplication and improved polymorphism by moving dialect-specific queries to subclasses.
* Reverted WorldGuard dependency to 7.0.9 due to incompatibilities with future Java versions.
* Hardened Discord Webhook security and reliability by implementing robust JSON escaping for all alert data.
* Synchronized the internal configuration and language versioning with the default resource files to prevent unnecessary update notifications on fresh installs.
* Refined argument parsing for the `/vg tracked-blocks add` command.
* Cleaned up the project `TODO.md` and removed superseded roadmap items.


## v1.1.6
* Support for 26.1+
* Updated WorldGuard dependency to v7.0.17.
* Fixed 'veinguard-check' WorldGuard flag not being registered on newer versions of WorldGuard and MC 26.1+.
* Fixed a NoClassDefFoundError console stack trace when starting the plugin on a server without WorldGuard installed.

## v1.1.5
* Added config option 'enable-worldguard' to enable or disable WorldGuard integration.
* Moved some core initialization to onLoad to support config-dependent hook registration.
* Refactored project into multi-module project.
* Implemented an API which is currently experimental.
* Added arg to subcommand '/vg help \[page\]' for pagination of help command messages.
* Added exempt permission 'veinguard.bypass.\<MATERIAL\>' for permission bypass of specific tracked blocks.
* New pagination class CommandHelp for paginating of the '/vg help' command.
* Renamed sub-command classes From 'VeinguardCmd' to 'SubCmd'
* Renamed pagination classes to be 'SubPage' with sub being sub-command name.
* Refactored 'StaffJoinListener' and 'BlockBreakListener' into same class 'VGListener'
* Removed support for Minecraft 1.17 and lower.
* Changed Java version to Java 17.
* New lang version '7'.
* New config version '7'.
* Implemented Player Patrol feature for staff members.
* Added new command '/vg patrol <start|stop|pause|resume|next|back>' for automated player monitoring.
* Added patrol boss bar with countdown timer and current/next player information.
* Added config option 'patrol-teleport-seconds' to change interval between teleports.
* Added config options 'patrolling-color' and 'paused-color' for boss bar customization.
* Added config option 'patrol-finish-action' to configure behavior (LOOP/STOP) when patrol completes.
* Added patrol history tracking to allow going back to previous players with '/vg patrol back'.

## v1.1.4
* Added new command '/vg tracked-blocks <add|list|remove>' for changing tracked blocks in-game.
* Added action-bar alert message, switch to this in config.yml.
* Added config option 'alert-cooldown-type' to switch between alert cooldown types.
* Added config option 'alert-delivery-type' to switch between alert message delivery types.
* Added class 'ActionBarQueue' for handling of queueing action bar message alerts and sending.
* Added class 'VGUtils' for commonly used global methods.
* Added class 'PageHandler' for handling commands that have paginated messages.
* Changed BlockReport to take advantage of PageHandler.
* Added class 'TrackedBlockList' for paginated in-game list of tracked blocks.
* Added enum 'CooldownType' for switching between cooldown types.
* Added enum 'AlertDelivery' for switching between alert delivery types.
* Added new package 'com.jerrysplugins.veinguard.common.alert' for housing of alert handling classes.
* Added new package 'com.jerrysplugins.veinguard.common.pagination' for housing of pagination handlers.
* Added language key 'staff-notify-action-bar' for action bar alert messages.
* Changed language key 'staff-notify' to 'staff-notify-chat'.
* Re-do of config.yml to have smaller description comments and better organization.
* Removed config option 'send-alerts-to-staff' in favor of new 'alert-delivery' option.
* Removed CommandDispatcher class. Refactored method's into AlertManager.
* Fixed HashMap blockCooldowns not clearing past/old entries causing memory usage to go up over time.
* Shortened config.yml option descriptions for better readability. Further instruction can be found in the wiki.
* New config version '6'.
* New lang version '6'.

## v1.1.3
* Downgraded Java version to Java 16.
* Downgraded Spigot API version to 1.17.
* VeinGuard has been tested and is confirmed to work with the following versions: 1.17.x, 1.18.x, 1.19.x, 1.20.x, 1.21.x
* Added new configurable alert sound that will play a sound for any player with permission 'veinguard.notify' when an alert is triggered.
* Added new class VersionComparison, that compares semantical versions.
* Added new enum VersionStatus that returns UP_TO_DATE, AHEAD or BEHIND depending on if version is ahead, behind or equal to the latest version.
* Added new enum BuildType to determine what build type a version is.
* Added new logging in the UpdateChecker.
* Cleaned up code in UpdateChecker.
* Cleaned up code in AlertManager.
* Moved .yml configuration file creation from onEnable to onLoad for logger to check if 'debug-mode' is true in config.
* Removed 'com.jerrysplugins.veinguard.common.' package, all files now nested in 'com.jerrysplugins.veinguard'
* Renamed 'com.jerrysplugins.veinguard.common' to 'com.jerrysplugins.veinguard.common'
* Removed 'new-version' message from lang. Replaced with message list 'update'.
* New lang version '5.
* New config version '5'.

## v1.1.2 (Stable Release)
* This version marks the end of BETA, release is considered stable.
* Complete re-write of plugin Logger.
* Separated command dispatching into new class CommandDispatcher.
* Separated suspect report creation into new class BlockReport.
* Separated alert logic out of all other classes, all alert functionality now lies in AlertManager.
* Command overhauled into CommandManager with sub command class for each sub command.
* Removed all original 'veinguard-usage-command' messages and replaced with 'command-usage' that is completed via '{usage}' variable.
* New config option 'staff-join-violation-alert' that notifies staff upon joining the server with how many players currently have a block violation. Requires permission 'veinguard.notify'
* Fix for Discord webhook not being hot reloadable.
* Implemented asynchronous cleanup task that removes any block break entries older than the check interval + 2 minutes. Runs every 15 minutes.
* Multiple small bug fixes.
* Major clean up of code.

## v1.1.1-BETA
* UpdateChecker will now read from GitHub and only call update available if latest version is greater than current version.
* Discontinued Spigot update checker due to inconsistent API responses.
* Cleaned up.

## v1.1.0-BETA
* Added config option 'show-update-notice' to enable and disable in-game chat version notice.
* Fix for update checker not returning the actual latest version.
* New config version '3'

## v1.0.9-BETA
* Added ability to send an alert embed to discord with a discord webhook.
* Added full console command functionality for all sub commands.
* Added new config option 'alert-commands' as a list of commands to dispatch when an alert is fired.
* Added new sub command '/vg msg <Player> <Args>' as to send messages from the config 'alert-commands'. This sub command is not tab completed.
* Overhaul of command class for better readability and functionality.
* Methods now refer to target player as suspect or staff depending on method context.
* Fix/upgrade for base command '/veinguard' will no longer require base permission 'veinguard.command' if player has permission for a subcommand 'veinguard.command.<subcommand>'
* Changed some logging levels to DEBUG instead of continual logging.
* Fix for '/vg toggle-alerts <Player>' unmuting alerts for the executor instead of the targeted player.
* New config version '2'
* New lang version '3'

## v1.0.8-BETA
* Added Spigot update check that notify the console and onPlayerJoin for players with permission 'veinguard.update'
* Fix for config updater not initially saving 'config-version' when config.yml is first generated.
* Fix for lang updater not initially saving 'config-version' when lang.yml is first generated.

## v1.0.7-BETA
* Added automatic config.yml & lang.yml updater.

## v1.0.6-BETA
* Refactor of handler class to clean up code and separate logic.
* Added config option 'send-alerts-to-console' to enable/disable console alerts.
* Added config option 'send-alerts-to-staff' to enable/disable in-game alerts.
* Added information message to base command '/veinguard' if not sub command is provided.
* Added pagination to the '/veinguard check <Player>' command with '/veinguard check <Player> <Page>' for page browsing.
* Added tab completion of amount of page numbers a players report has.
* Added new config option 'player-report-page-entries' to configure how many entries to show per report page.
* Added config option 'ignore-tools' to list tools to ignore when a tracked block is broken.
* Added new subcommand 'toggle-alerts' to mute alerts for yourself only or a targeted player.
* Added new permission 'veinguard.admin' which grants all permissions.
* Added new permission 'veinguard.mod' which grants limited permissions.
* Removed permission 'veinguard.staff'
* Fixed some lang.yml message formatting and incorrect color codes.
* Fixed updated config.yml 'tracked-blocks' not updating upon using command /vg reload.

## v1.0.5-BETA
* Added bStats metrics.

## v1.0.4-BETA
* Fixed missing check for config option 'ignore-above-y-level'.

## v1.0.3-BETA
* Fixed some lang.yml message formatting and incorrect color codes.

## v1.0.2-BETA:
* Fixed chat notification still being sent even when suspect player is muted.

## v1.0.1-BETA:
* Fixed {player} variable not being replaced in mute/unmute command messages.

## v1.0.0-BETA:
* Base plugin created.