package quock.randdevelopment;

import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import quock.utils.LogUtils;

/**
 * Created by altaf.h.shaikh on 2/7/2017.
 */


public class AudioOutputDevices extends CordovaPlugin {

    AudioManager audioM = null;
    //    private Set<BluetoothDevice> devices;
    IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
    IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    //    String str;
    public static final String SPEAKER = "Speaker";
    public static final String BLUTOOTH = "Bluetooth";
    public static final String AUXILARY = "Auxilary";
    public static final String Headphone3 = "Headphone3";

    HashMap<String, String> hashDevices;

    public AudioOutputDevices() {
        hashDevices = new HashMap<>();
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
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";
        if (action.equals("currentOutputs")) {
            HashMap<String, String> devicesList = this.getDevicesList();

            //get json value from the hashmap
            String jsonHashmap = currentOutputs(devicesList);
            callbackContext.sendPluginResult(new PluginResult(status, jsonHashmap));
            return true;
        }
        callbackContext.sendPluginResult(new PluginResult(status, result));
        return true;
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
                jObject.put("Name", keyValue);
                jObject.put("Value", value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jObject);
        }

        return jsonArray.toString();
    }

    private HashMap<String, String> getDevicesList() {
        audioM = (AudioManager) cordova.getActivity().getApplicationContext().
                getSystemService(cordova.getActivity().getApplicationContext().AUDIO_SERVICE);

        AudioDeviceInfo[] adi = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            adi = audioM.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo devices :
                    adi) {
                CharSequence productName = devices.getProductName();
                if (devices.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                    LogUtils.d("AudioDeviceInfo.TYPE_AUX_LINE");
                    hashDevices.put("Headphone1", Headphone3);
                }
                if (devices.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                    LogUtils.d("AudioDeviceInfo.TYPE_WIRED_HEADSET");
                    hashDevices.put("Headphone3", Headphone3);
                }
                if (devices.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                    LogUtils.d("AudioDeviceInfo.TYPE_BLUETOOTH_A2DP");
                    hashDevices.put(productName.toString(), BLUTOOTH);
                }

                if (devices.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    LogUtils.d("AudioDeviceInfo.TYPE_BUILTIN_SPEAKER");
                    hashDevices.put(productName.toString(), SPEAKER);
                }
            }

        }
        return hashDevices;

    }

}
