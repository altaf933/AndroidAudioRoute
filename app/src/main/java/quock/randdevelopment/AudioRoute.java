package quock.randdevelopment;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import quock.audio.receiver.AudioRouter;

import static org.apache.cordova.engine.SystemWebViewEngine.TAG;


/**
 * Created by altaf.h.shaikh on 2/7/2017.
 */


public class AudioRoute extends CordovaPlugin {

    AudioManager audioM = null;
    //    private Set<BluetoothDevice> devices;
    //    String str;
    public static final String SPEAKER = "Speaker";
    public static final String BLUTOOTH = "Bluetooth";
    public static final String AUXILARY = "Auxilary";
    public static final String Headphone3 = "Headphone3";
    public static final String SPEAKER_NAME = "Phone";


    HashMap<String, String> hashDevices;
    AudioRouter audioRouter;
    //Register output
    public static final String AUDIO_OUTPUT_CHANGED = "audio_output";
    public static final String CURRENT = "current_output";

    private CallbackContext audioCallbackContext = null;

    //Bluetooth devices connection

    private MusicIntentReceiver myHeadPhonePlugReceiver;
    //Blutoothth adapter class
    private BluetoothAdapter mBtAdapter;
    private BluetoothA2dp mA2dpService;
    //End of bluetooth adapter class
    //End of bluetooth devices connection

    public AudioRoute() {
        hashDevices = new HashMap<String, String>();
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        //Get audio manager class
        audioM = (AudioManager) cordova.getActivity().getApplicationContext().
                getSystemService(cordova.getActivity().getApplicationContext().AUDIO_SERVICE);
        //End of audio manager class

        audioRouter = new AudioRouter(cordova.getActivity());
        //default speaker mode enable
        audioRouter.setRouteMode(AudioRouter.AudioRouteMode.SPEAKER);

        //Register listener for headset plugged in
        myHeadPhonePlugReceiver = new MusicIntentReceiver();
        //End of register plugged in

        //Register bluetooth listener
        cordova.getActivity().registerReceiver(mReceiverBluetooth,
                new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
        cordova.getActivity().registerReceiver(mReceiverBluetooth,
                new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED));
        //End of register listener

        //Headphone plugged in broadcast
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        cordova.getActivity().registerReceiver(myHeadPhonePlugReceiver, filter);
        //End of head phone broadcast

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtAdapter.getProfileProxy(cordova.getActivity(), mA2dpServiceListener, BluetoothProfile.A2DP);
        hashDevices.put("Phone", SPEAKER);

    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        CordovaResourceApi resourceApi = webView.getResourceApi();
        final PluginResult.Status status = PluginResult.Status.OK;
        String result = "";
        if (action.equals("currentOutputs")) {
            HashMap<String, String> devicesList = hashDevices;
            //get json value from the hashmap
            String jsonHashmap = currentOutputs(devicesList);
            callbackContext.sendPluginResult(new PluginResult(status, jsonHashmap));
            return true;
        }
        if (action.equals("overrideOutput")) {
            String typeSpeaker = args.getString(0);
            setAudioRoute(typeSpeaker);
            callbackContext.sendPluginResult(new PluginResult(status, typeSpeaker));
            return true;
        }
        if (action.equals("setRouteChangeCallback")) {
            this.audioCallbackContext = callbackContext;
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getExtras();
                    String str = bundle.getString(CURRENT);
                    setRouteChangeCallback(str);
                    audioCallbackContext.sendPluginResult(new PluginResult(status, str));

//                    webView.sendJavascript("cordova.require('cordova-plugin-headsetdetection.HeadsetDetection').remoteHeadsetRemoved();");
                }
            };
            cordova.getActivity().
                    registerReceiver(receiver, new IntentFilter(AUDIO_OUTPUT_CHANGED));

        }
        callbackContext.sendPluginResult(new PluginResult(status, result));
        return true;
    }

    private void setAudioRoute(String typeSpeaker) {
        if (typeSpeaker.equalsIgnoreCase(Headphone3)) {
            audioRouter.setRouteMode(AudioRouter.AudioRouteMode.WIRED_HEADPHONE);
        } else if (typeSpeaker.equalsIgnoreCase(SPEAKER)) {
            audioRouter.setRouteMode(AudioRouter.AudioRouteMode.SPEAKER);
        } else if (typeSpeaker.equalsIgnoreCase(BLUTOOTH)) {
            audioRouter.setRouteMode(AudioRouter.AudioRouteMode.BLUETOOTH_A2DP);
        }
    }

    private String currentOutputs(HashMap<String, String> devicesList) {
        Set mapSet = (Set) devicesList.entrySet();
        Iterator iterator = mapSet.iterator();
        JSONObject jObject;
        JSONArray jsonArray = new JSONArray();
        while (iterator.hasNext()) {
            //Add json object
            jObject = new JSONObject();

            Map.Entry mapEntry = (Map.Entry) iterator.next();
            // getKey Method of HashMap access a key of map
            String keyValue = (String) mapEntry.getKey();
            //getValue method returns corresponding key's value
            String value = (String) mapEntry.getValue();

            try {
                jObject.put("name", keyValue);
                jObject.put("value", value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jObject);
        }

        return jsonArray.toString();
    }

    private HashMap<String, String> getDevicesList() {
//        hashDevices.put(SPEAKER_NAME, SPEAKER);

//        AudioDeviceInfo[] adi = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            adi = audioM.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
//            for (AudioDeviceInfo devices :
//                    adi) {
//                CharSequence productName = devices.getProductName();
//                if (devices.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
//
//                    hashDevices.put("Headphone1", Headphone3);
//                }
//                if (devices.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
//
//                    hashDevices.put("Headphone3", Headphone3);
//                }
//                if (devices.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
//
//                    hashDevices.put(productName.toString(), BLUTOOTH);
//                }
//
//                if (devices.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
//
//                    hashDevices.put(productName.toString(), SPEAKER);
//                }
//            }
//
//        }
        return hashDevices;

    }

    @Override
    public void onDestroy() {

        audioM.setMode(AudioManager.MODE_NORMAL);
        audioM.setSpeakerphoneOn(true);

        mBtAdapter.closeProfileProxy(BluetoothProfile.A2DP, mA2dpService);
        cordova.getActivity().unregisterReceiver(mReceiverBluetooth);
        super.onDestroy();
    }


    public void setRouteChangeCallback(String typeSpeaker) {
        Toast.makeText(cordova.getActivity(), typeSpeaker, Toast.LENGTH_SHORT).show();
    }


    BroadcastReceiver mReceiverBluetooth = new BroadcastReceiver() {

        @Override
        public void onReceive(Context ctx, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "receive intent for action : " + action);
            if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                if (state == BluetoothA2dp.STATE_CONNECTED) {
                    setIsA2dpReady(true);
//                    playMusic();
                } else if (state == BluetoothA2dp.STATE_DISCONNECTED) {
                    setIsA2dpReady(false);
                }
            } else if (action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING);
                if (state == BluetoothA2dp.STATE_PLAYING) {
                    Log.d(TAG, "A2DP start playing");
//                    Toast.makeText(A2DPActivity.this, "A2dp is playing", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "A2DP stop playing");
//                    Toast.makeText(A2DPActivity.this, "A2dp is stopped", Toast.LENGTH_SHORT).show();
                }
            }
        }

    };
    boolean mIsA2dpReady = false;

    void setIsA2dpReady(boolean ready) {
        mIsA2dpReady = ready;
        Toast.makeText(cordova.getActivity(), "A2DP ready ? " + (ready ? "true" : "false"), Toast.LENGTH_SHORT).show();
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        hashDevices.remove("Headphone");
                        break;
                    case 1:
                        hashDevices.put("Headphone", Headphone3);
                        Log.d(TAG, "Headset is plugged");
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    }

    private BluetoothProfile.ServiceListener mA2dpServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile a2dp) {
            Log.d(TAG, "a2dp service connected. profile = " + profile);
            if (profile == BluetoothProfile.A2DP) {
                mA2dpService = (BluetoothA2dp) a2dp;

                List<BluetoothDevice> bluetoothDevices =
                        a2dp.getConnectedDevices();
                if (bluetoothDevices.size() > 0) {
                    for (BluetoothDevice devices :
                            bluetoothDevices) {
                        hashDevices.put(devices.getName(), BLUTOOTH);
                        audioRouter.connectedBluetoothDevices.add(devices);
                    }

//                    audioRouter.connectedBluetoothDevices.add(bluetoothDevices.get(0));
//                    listMap.put(bluetoothDevices.get(0).getName(), BLUTOOTH);
                }
                if (audioM.isBluetoothA2dpOn()) {
                    setIsA2dpReady(true);
//                    playMusic();
                } else {
                    Log.d(TAG, "bluetooth a2dp is not on while service connected");
                }
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (mA2dpService != null) {
                if (profile == BluetoothProfile.A2DP) {
                    List<BluetoothDevice> bluetoothDevices =
                            mA2dpService.getConnectedDevices();

                    for (BluetoothDevice devices :
                            bluetoothDevices) {
                        hashDevices.remove(devices.getName());
                    }
                }

            }
            setIsA2dpReady(false);
        }

    };

}
