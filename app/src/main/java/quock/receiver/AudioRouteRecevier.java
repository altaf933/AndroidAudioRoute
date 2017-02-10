package quock.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by altaf.h.shaikh on 2/8/2017.
 */

public class AudioRouteRecevier extends BroadcastReceiver {

    private Collection<BluetoothDevice> connectedBluetoothDevices = new HashSet<>();
    private Collection<Headset> connectedHeadsets = new HashSet<>();
    private Collection<UsbAudio> connectedUsbAudios = new HashSet<>();

    /**
     * Represents headset connection information
     */
    private final class Headset {
        private String address;
        private String portName;
        private int microphone;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPortName() {
            return portName;
        }

        public void setPortName(String portName) {
            this.portName = portName;
        }

        public int getMicrophone() {
            return microphone;
        }

        public void setMicrophone(int microphone) {
            this.microphone = microphone;
        }

        @Override
        public int hashCode() {
            return (address + portName).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Headset)) {
                return false;
            }
            return this.hashCode() == o.hashCode();
        }
    }

    /**
     * Represents USB Audio connection information
     */
    private final class UsbAudio {
        private String address;
        private String portName;


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPortName() {
            return portName;
        }

        public void setPortName(String portName) {
            this.portName = portName;
        }

        @Override
        public int hashCode() {
            return (address + portName).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Headset)) {
                return false;
            }
            return this.hashCode() == o.hashCode();
        }
    }

    /**
     * Audio route mode
     */
    public enum AudioRouteMode {
        WIRED_HEADPHONE,
        SPEAKER,
        USB_AUDIO,
        BLUETOOTH_A2DP,
        NO_ROUTING
    }

    /**
     * Intent actions
     */
    public static final String INTENT_ACTION_ANALOG_AUDIO_DOCK_PLUG =
            "android.intent.action.ANALOG_AUDIO_DOCK_PLUG";
    public static final String MEDIA_ACTION_ANALOG_AUDIO_DOCK_PLUG =
            "android.media.action.ANALOG_AUDIO_DOCK_PLUG";
    public static final String BLUETOOTH_A2DP_CONNECTION_STATE_CHANGED =
            "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";


    @Override
    public void onReceive(Context context, Intent intent) {
    }
}
