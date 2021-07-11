package com.droiduino.bluetoothconn;

public class Utils {
    public final static int CONNECTING_STATUS  = 1; // used in bluetooth handler to identify message status
    public final static int MESSAGE_READ       = 2; // used in bluetooth handler to identify message update

    public final static String MESSAGE_FORWARD     = "F"; // used in bluetooth handler to identify command
    public final static String MESSAGE_BACKWARD    = "B"; // used in bluetooth handler to identify command
    public final static String MESSAGE_LEFT        = "L"; // used in bluetooth handler to identify command
    public final static String MESSAGE_RIGHT       = "R"; // used in bluetooth handler to identify command
    public final static String MESSAGE_STOP        = "S"; // used in bluetooth handler to identify command
    public final static String MESSAGE_AUTONOMOUS  = "A"; // used in bluetooth handler to identify command
    public final static String MESSAGE_MANUAL      = "M"; // used in bluetooth handler to identify command
}
