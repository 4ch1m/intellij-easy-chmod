# ![IntelliJ Easy Chmod](src/main/resources/META-INF/pluginIcon.png#gh-light-mode-only) ![IntelliJ Easy Chmod](src/main/resources/META-INF/pluginIcon_dark.png#gh-dark-mode-only) &nbsp; IntelliJ Easy Chmod

> Change file permissions from within your IDE.

## Table of Contents

* [Screenshots](#screenshots)
* [Installation](#installation)
* [Notes](#notes)
* [Credits](#credits)
* [License](#license)
* [Donate](#donate) :heart:

## Screenshots

#### Statusbar Widget

The new statusbar widget shows the permissions of the currently edited file:

![statusbar](screenshots/statusbar.png)

(Clicking on it will open the edit dialog.)

#### Edit Dialog 

Manually set all permissions; or apply an existing preset:

![dialog](screenshots/dialog.png)

![dialog_presets](screenshots/dialog_presets.png)

#### Context Menu Entry

A dedicated context menu entry is also available, which can also be used to alter permissions of folders:

![contextmenu](screenshots/contextmenu.png)

#### Settings

The plugin's settings-dialog:

![settings](screenshots/settings.png)

## Installation

Use the IDE's built-in plugin system:

* `File` --> `Settings...` --> `Plugins` --> `Marketplace`
* search for: `Easy Chmod`
* click the `Install`-button

Or go to the [plugin page](https://plugins.jetbrains.com/plugin/27492-easy-chmod) on the [JetBrains](https://www.jetbrains.com)-website, download the archive-file and install manually.

## Notes

* This plugin deliberately doesn't operate in bulk but only changes file permissions for one file/directory at a time. 
* Each file/directory permission preset (configured in the settings-dialog) can have a regex filter.  
  If the regex matches with the chosen file/directory (upon opening the edit dialog), then this preset entry will automatically get selected for ease-of-use.  
  The order of all presets can be important, because once a regex matches, all further evaluation of regexes will be skipped.

## Credits

Plugin icon based on [file-csv](https://fontawesome.com/icons/file-csv?f=classic&s=solid) / [Font Awesome](https://fontawesome.com)

## License

Please read the [license](LICENSE) file.

## Donate

If you like this plugin, please consider a [donation](https://paypal.me/AchimSeufert). Thank you!
