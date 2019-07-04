package com.example.boardtest;

public class MyUsbManager {

    /**
     * Broadcast Action: A sticky broadcast for USB state change events when in device mode.
     * This is a sticky broadcast for clients that includes USB connected/disconnected state,
     * <ul>
     * <li> {@link #USB_CONNECTED} boolean indicating whether USB is connected or disconnected.
     * <li> {@link #USB_CONFIGURED} boolean indicating whether USB is configured. currently zero if not configured, one for configured.
     * <li> {@link #USB_FUNCTION_ADB} boolean extra indicating whether the adb function is enabled
     * <li> {@link #USB_FUNCTION_RNDIS} boolean extra indicating whether the RNDIS ethernet function is enabled
     * <li> {@link #USB_FUNCTION_MTP} boolean extra indicating whether the MTP function is enabled
     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the PTP function is enabled
     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the accessory function is enabled
     * <li> {@link #USB_FUNCTION_AUDIO_SOURCE} boolean extra indicating whether the audio source function is enabled
     * <li> {@link #USB_FUNCTION_MIDI} boolean extra indicating whether the MIDI function is enabled
     * </ul>
     */

    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";

    /**
     * Boolean extra indicating whether USB is connected or disconnected.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast.
     */
    public static final String USB_CONNECTED = "connected";

    /**
     * Boolean extra indicating whether USB is configured.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast.
     */
    public static final String USB_CONFIGURED = "configured";

    /**
     * Boolean extra indicating whether confidential user data, such as photos, should be
     * made available on the USB connection. This variable will only be set when the user
     * has explicitly asked for this data to be unlocked.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast.
     */
    public static final String USB_DATA_UNLOCKED = "unlocked";

    /**
     * A placeholder indicating that no USB function is being specified.
     * Used to distinguish between selecting no function vs. the default function in {@link #setCurrentFunction(String)}.
     */
    public static final String USB_FUNCTION_NONE = "none";

    /**
     * Name of the adb USB function.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast
     */
    public static final String USB_FUNCTION_ADB = "adb";

    /**
     * Name of the RNDIS ethernet USB function.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast
     */
    public static final String USB_FUNCTION_RNDIS = "rndis";

    /**
     * Name of the MTP USB function.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast
     */
    public static final String USB_FUNCTION_MTP = "mtp";

    /**
     * Name of the PTP USB function.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast
     */
    public static final String USB_FUNCTION_PTP = "ptp";

    /**
     * Name of the audio source USB function.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast
     */
    public static final String USB_FUNCTION_AUDIO_SOURCE = "audio_source";

    /**
     * Name of the MIDI USB function.
     * Used in extras for the {@link #ACTION_USB_STATE} broadcast
     */
    public static final String USB_FUNCTION_MIDI = "midi";
}

