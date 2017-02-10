package quock.randdevelopment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import quock.audio.adapter.MainAdapter;
import quock.audio.receiver.AudioRouter;
import quock.receiver.MyReceiver;
import quock.utils.LogUtils;

/**
 * Created by altaf.h.shaikh on 2/7/2017.
 */


public class BlutoothMainActivity extends FragmentActivity {

    AudioManager audioM = null;
    BluetoothAdapter btAdapter;
    public static Context ctx;
    BluetoothManager bMgr = null;
    private Set<BluetoothDevice> devices;
    private MyReceiver receiver;
    IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
    IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    ToggleButton tb1 = null;

    MediaPlayer mediaPlayer;
    //    String str;
    AudioRouter audioRouter;
    public static final String SPEAKER = "Speaker";
    public static final String BLUTOOTH = "Bluetooth";
    public static final String AUXILARY = "Auxilary";

    HashMap<String, String> listMap;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluttoth_lay);
        tb1 = (ToggleButton) findViewById(R.id.toggleButton1);
        recyclerView = (RecyclerView) findViewById(R.id.rvListitems);
        this.registerReceiver(receiver, filter1);
        this.registerReceiver(receiver, filter2);

        audioRouter = new AudioRouter(this);
        audioRouter.setRouteMode(AudioRouter.AudioRouteMode.SPEAKER);

        HashSet<String> setItems = new HashSet<>();

        listMap = new HashMap<>();

        audioM = (AudioManager) getApplicationContext().
                getSystemService(getApplicationContext().AUDIO_SERVICE);
        AudioDeviceInfo[] adi = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            adi = audioM.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo devices :
                    adi) {
                CharSequence productName = devices.getProductName();
//                setItems.add(productName.toString());
//                listMap.put(productName.toString(), AUXILARY);
                if (devices.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                    LogUtils.d("AudioDeviceInfo.TYPE_AUX_LINE");
                    listMap.put(productName.toString(), AUXILARY);
                }
                if (devices.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                    LogUtils.d("AudioDeviceInfo.TYPE_BLUETOOTH_A2DP");
                    setItems.add("BLUTOOTH ADP");
                    listMap.put(productName.toString(), BLUTOOTH);
                }
                if (devices.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    LogUtils.d("AudioDeviceInfo.TYPE_BUILTIN_SPEAKER");
                    setItems.add("SPEAKER");
                    listMap.put(productName.toString(), SPEAKER);
                }
            }

        }


        bMgr = (BluetoothManager) getApplicationContext().
                getSystemService(getApplicationContext().BLUETOOTH_SERVICE);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = btAdapter.getBondedDevices();


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        MainAdapter mAdapter = new MainAdapter(this, listMap);
        recyclerView.setAdapter(mAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mediaPlayer = MediaPlayer.create(this, R.raw.katy_parry);
        mediaPlayer.start();

    }

    public void onToggleClicked(View view) {

        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            if ((MyReceiver.isBTConnected == true) || (devices.size() > 0)) {
                // TODO Auto-generated method stub
                audioM.setMode(audioM.MODE_IN_COMMUNICATION);
                audioM.setBluetoothScoOn(true);
                audioM.startBluetoothSco();
                audioM.setSpeakerphoneOn(false);
                Log.d("S@ur@v", "Toggle Button On!");
            } else {
                tb1.setChecked(false);
                Toast.makeText(getApplicationContext(), "BT is not connected, Pls pair your device and restart the app again!", Toast.LENGTH_LONG).show();
            }

        } else {
            audioM.setMode(audioM.MODE_NORMAL);
            audioM.setBluetoothScoOn(false);
            audioM.stopBluetoothSco();
            audioM.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioM.setSpeakerphoneOn(true);
            Log.d("S@ur@v", "Toggle Button Off!");

        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        audioM.setMode(AudioManager.MODE_NORMAL);
        audioM.setSpeakerphoneOn(true);
        super.onDestroy();
    }
}
