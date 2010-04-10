#!/usr/bin/python

"""Bluetooth listener.

This listener listens on and receives notifications with bluetooth RFCOMM
sockets.
"""

__author__ = 'wcauchois@gmail.com (William Cauchois)'

import bluetooth
import gtk
from gobject import GObject
import gobject
import threading
from threading import Thread

class BluetoothListener(GObject, Thread):
    port = 1 # The Bluetooth port for the server to listen on
    service_id = '7674047e-6e47-4bf0-831f-209e3f9dd23f'
    service_classes = [ service_id ]
    # The timeout in seconds for the RFCOMM socket. This affects how
    # frequently the listener thread can monitor the stopping event.
    sock_timeout = 0.5

    def __init__(self):
        GObject.__init__(self)
        Thread.__init__(self)

        # Stop the thread using an event; from
        # <http://stackoverflow.com/questions/323972/is-there-any-way-
        # to-kill-a-thread-in-python>
        self._stop = threading.Event()

    def _publish_service(self):
        self.sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.sock.bind(('', self.port))
        self.sock.listen(bluetooth.PORT_ANY)
        bluetooth.advertise_service(self.sock, 'AndroidNotifierService',
                                    self.service_id, self.service_classes)

    def _unpublish_service(self):
        self.sock.close()

    def run(self):
        self.sock.settimeout(self.sock_timeout)
        while not self._stop.is_set():
            try:
                client_sock, _ = self.sock.accept()
            except bluetooth.BluetoothError:
                continue # The socket timed out
            data = buffer = client_sock.recv(1024)
            while len(buffer) == 1024:
                buffer = client_sock.recv(1024)
                if len(buffer) == 0:
                    break
                data += buffer
            self.emit('android-notify', data)

    def start(self):
        self._publish_service()
        Thread.start(self)

    def stop(self):
        self._stop.set()
        self._unpublish_service()

gobject.type_register(BluetoothListener)
gobject.signal_new('android-notify', BluetoothListener,
    gobject.SIGNAL_RUN_LAST, gobject.TYPE_NONE, (str,))
