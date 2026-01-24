## v1.1.2 (Stable Release)
* This version marks the end of BETA, release is considered stable.
* Complete re-write of plugin Logger.
* Separated command dispatching into new class CommandDispatcher.
* Separated suspect report creation into new class BlockReport.
* Separated alert logic out of all other classes, all alert functionality now lies in AlertManager.
* Command overhauled into CommandManager with sub command class for each sub command.
* Removed all original 'veinguard-usage-command' messages and replaced with 'veinguard-command-usage' that is completed via '{usage}' variable.
* New config option 'staff-join-violation-alert' that notifies staff upon joining the server with how many players currently have a block violation. Requires permission 'veinguard.notify'
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