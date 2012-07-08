
This directory contains a pre-built jar which implements the
XQuery plugin.

The jar here will need to be loaded into IntelliJ as a plugin.
The easiest way to to this is via the Plugin Settings.

### Before installing this plugin

If you have an XQuery.xml in the filetypes directory of
your IntelliJ preferences, you will need to move it out of the
way, because it will interfere with the plugin code.

### To Install this plugin

* Click the Settings button in the icon bar or select Preferences from
menu bar.

* Select Plugins under IDE Settings

* Check that there is not a previous version of this plugin already
installed (type xquery in filter box at upper right).  If so, delete
it and restart IntelliJ.

* Click the "Install plugin from disk" button below the list of plugins.

* Navigate to the xquery-plugin.jar you downloaded from here and select it.

* You should see the info about the plugin in the panel on the right.

* Click OK, and restart IntelliJ

The plugin will be available when you restart and will be registered
for files whose names end in .xqy.

This is temporary.  Once the code is ready for public consumption,
the compiled plugin will be pushed to the IntelliJ community site
so that it can be downloaded directly from within IntelliJ.

***Use at your own risk***

Ron Hitchens
July 2012
