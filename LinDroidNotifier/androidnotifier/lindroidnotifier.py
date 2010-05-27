#!/usr/bin/python

"""Entry point for the linux android notifier.

This module starts up the application.
"""

__author__ = 'rodrigo@damazio.org (Rodrigo Damazio Bovendorp)'

import gtk, gtk.glade
import os
import preferences
from manager import NotificationManager

# TODO: Detect the data path (for when this is installed somewhere)
gladefile = gtk.glade.XML('../data/lindroidnotifier.glade')

# TODO: Show the gnome menu icon (applet), open prefs from there
prefs = preferences.Preferences(os.path.expanduser('~/.config/android-notifier'))
prefs_dialog = preferences.PreferencesDialog(gladefile, prefs)
prefs_dialog.show()

# Article about threading in GTK:
# <http://unpythonic.blogspot.com/2007/08/using-threads-in-pygtk.html>
gtk.gdk.threads_init() # This must be called before manager.start()!

manager = NotificationManager(prefs)
manager.start()

# TODO: Remove this test notification once there are listeners
#manager._on_notification(sender=None, raw_data='1234/5678/RING/Mom is calling')

gtk.main()

manager.stop()
