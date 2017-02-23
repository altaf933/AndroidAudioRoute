package quock.audio.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import quock.audio.receiver.AudioRouter;
import quock.randdevelopment.R;

import static quock.randdevelopment.BlutoothMainActivity.BLUTOOTH;
import static quock.randdevelopment.BlutoothMainActivity.Headphone3;
import static quock.randdevelopment.BlutoothMainActivity.SPEAKER;

/**
 * Created by altaf.h.shaikh on 2/8/2017.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private final AudioManager audioM;
    private HashMap<String, String> modelList;
    private LayoutInflater inflater;
    private Context context;
    //    String str;
    private AudioRouter audioRouter;

    public MainAdapter(Context context, HashMap<String, String> modelList, AudioRouter audioRouter) {
        this.inflater = LayoutInflater.from(context);
        this.modelList = modelList;
        this.context = context;
        this.audioRouter = audioRouter;
//        this.audioRouter = new AudioRouter(context);
        audioM = (AudioManager) context.getApplicationContext().
                getSystemService(context.getApplicationContext().AUDIO_SERVICE);
//        audioM.setMode(AudioManager.STREAM_MUSIC);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        String strItem = modelList.get(position);
        String value = (new ArrayList<String>(modelList.values())).get(position);
        String key = (new ArrayList<String>(modelList.keySet())).get(position);
        holder.mainText.setText(key);
        holder.subText.setText(value);
//        holder.subText.setText(modelList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mainText, subText;

        public MainViewHolder(View itemView) {
            super(itemView);
            mainText = (TextView) itemView.findViewById(R.id.mainText);
            subText = (TextView) itemView.findViewById(R.id.subText);
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // We can access the data within the views
            Toast.makeText(context, mainText.getText(), Toast.LENGTH_SHORT).show();
            if (subText.getText().toString().equalsIgnoreCase(BLUTOOTH)) {
//                audioM.setMode(AudioManager.STREAM_MUSIC);
//                audioM.setBluetoothA2dpOn(true);
                audioRouter.setRouteMode(AudioRouter.AudioRouteMode.BLUETOOTH_A2DP);

            }
            if (subText.getText().toString().equalsIgnoreCase(SPEAKER)) {
//                audioRouter.setDeviceConnectionState(DEVICE_IN_WIRED_HEADSET, DEVICE_STATE_UNAVAILABLE, "");
//                audioRouter.setDeviceConnectionState(DEVICE_OUT_WIRED_HEADSET, DEVICE_STATE_UNAVAILABLE, "");
//                audioRouter.setDeviceConnectionState(DEVICE_OUT_EARPIECE, DEVICE_STATE_UNAVAILABLE, "");
//                audioRouter.setDeviceConnectionState(DEVICE_OUT_EARPIECE, DEVICE_STATE_UNAVAILABLE, "");
//                AudioRouter.setDeviceConnectionState();
                audioRouter.setRouteMode(AudioRouter.AudioRouteMode.SPEAKER);
//                audioM.setSpeakerphoneOn(true);
            }
            if (subText.getText().toString().equalsIgnoreCase(Headphone3)) {
//                audioM.setWiredHeadsetOn(true);
                audioRouter.setRouteMode(AudioRouter.AudioRouteMode.WIRED_HEADPHONE);
                //                audioRouter.setRouteMode(AudioRouter.AudioRouteMode.BLUETOOTH_A2DP);

//                audioRouter.setDeviceConnectionState(DEVICE_IN_WIRED_HEADSET, DEVICE_STATE_AVAILABLE, "");
//                audioRouter.setDeviceConnectionState(DEVICE_OUT_WIRED_HEADSET, DEVICE_STATE_AVAILABLE, "");
//                audioRouter.setDeviceConnectionState(DEVICE_OUT_EARPIECE, DEVICE_STATE_AVAILABLE, "");


            }

        }
    }
}