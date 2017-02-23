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
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import quock.audio.adapter.MainAdapter;
import quock.audio.receiver.AudioRouter;

/**
 * Created by altaf.h.shaikh on 2/7/2017.
 */


public class BlutoothMainActivity extends FragmentActivity {

    private static final String TAG = BlutoothMainActivity.class.getName();
    AudioManager audioM = null;
    public static Context ctx;

    HashMap<String, String> listMap;
    AudioRouter audioRouter;
    public static final String SPEAKER = "Speaker";
    public static final String BLUTOOTH = "Bluetooth";
    public static final String AUXILARY = "Auxilary";
    public static final String Headphone3 = "Headphone";

    private MusicIntentReceiver myHeadPhonePlugReceiver;
    //Blutoothth adapter class
    private BluetoothAdapter mBtAdapter;
    private BluetoothA2dp mA2dpService;
    //End of bluetooth adapter class


    private RecyclerView recyclerView;
    MediaPlayer mediaPlayer;
    //Android media route api
    private MediaRouter mMediaRouter;
    ToggleButton tb1 = null;
    public static final String AUDIO_OUTPUT_CHANGED = "audio_output";
    private MainAdapter mAdapter;


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluttoth_lay);


        tb1 = (ToggleButton) findViewById(R.id.toggleButton1);
        recyclerView = (RecyclerView) findViewById(R.id.rvListitems);

//        this.registerReceiver(receiver, filter1);
//        this.registerReceiver(receiver, filter2);

        audioRouter = new AudioRouter(this);
        audioRouter.setRouteMode(AudioRouter.AudioRouteMode.SPEAKER);

        this.registerReceiver(receiver, new IntentFilter(AUDIO_OUTPUT_CHANGED));

        HashSet<String> setItems = new HashSet<>();
        listMap = new HashMap<String, String>();


        audioM = (AudioManager) getApplicationContext().
                getSystemService(getApplicationContext().AUDIO_SERVICE);

        //Register listener for headset plugged in
        myHeadPhonePlugReceiver = new MusicIntentReceiver();
        //End of register plugged in

        //Register bluetooth listener
        registerReceiver(mReceiverBluetooth,
                new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
        registerReceiver(mReceiverBluetooth,
                new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED));
        //End of register listener

        //Headphone plugged in broadcast
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myHeadPhonePlugReceiver, filter);
        //End of head phone broadcast

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtAdapter.getProfileProxy(this, mA2dpServiceListener, BluetoothProfile.A2DP);
        listMap.put("Phone", SPEAKER);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
//
//

        mAdapter = new MainAdapter(this, listMap, audioRouter);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mediaPlayer = MediaPlayer.create(this, R.raw.katy_parry);
        mediaPlayer.start();


        // Create a MediaRouter callback for discovery events
//        mMediaRouterCallback = new MyMediaRouterCallback();
//         mr.addCallback(,mMediaRouterCallback);


        //Media router api
//        mMediaRouter = MediaRouter.getInstance(get/ApplicationContext());

    }


    boolean mIsA2dpReady = false;

    void setIsA2dpReady(boolean ready) {
        mIsA2dpReady = ready;
        Toast.makeText(this, "A2DP ready ? " + (ready ? "true" : "false"), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        audioM.setMode(AudioManager.MODE_NORMAL);
        audioM.setSpeakerphoneOn(true);
        unregisterReceiver(receiver);

        mBtAdapter.closeProfileProxy(BluetoothProfile.A2DP, mA2dpService);
        unregisterReceiver(mReceiverBluetooth);
        super.onDestroy();
    }

    public static final String CURRENT = "current_output";
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String str = bundle.getString(CURRENT);
            setRouteChangeCallback(str);
            Toast.makeText(BlutoothMainActivity.this, str, Toast.LENGTH_SHORT).show();
        }
    };

    public void setRouteChangeCallback(String typeSpeaker) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        listMap.remove("Headphone");
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        listMap.put("Headphone", Headphone3);
                        mAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Headset is plugged");
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myHeadPhonePlugReceiver);
        super.onPause();
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
                        listMap.put(devices.getName(), BLUTOOTH);
                        audioRouter.connectedBluetoothDevices.add(devices);
                    }

//                    audioRouter.connectedBluetoothDevices.add(bluetoothDevices.get(0));
//                    listMap.put(bluetoothDevices.get(0).getName(), BLUTOOTH);
                    mAdapter.notifyDataSetChanged();
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
                        listMap.remove(devices.getName());
                    }
                }

            }
            setIsA2dpReady(false);
        }

    };

}



