package com.droiduino.bluetoothconn.adapters;

import android.content.Intent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.droiduino.bluetoothconn.R;
import com.droiduino.bluetoothconn.activities.MainActivity;
import com.droiduino.bluetoothconn.models.DeviceInfoModel;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context         context;
    private List<Object>    deviceList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView        textName;
        TextView        textAddress;
        LinearLayout    linearLayout;

        public ViewHolder(View v) {
            super(v);
            textName        = v.findViewById(R.id.textViewDeviceName);
            textAddress     = v.findViewById(R.id.textViewDeviceAddress);
            linearLayout    = v.findViewById(R.id.linearLayoutDeviceInfo);
        }
    }

    public DeviceListAdapter(Context context, List<Object> deviceList) {
        this.context    = context;
        this.deviceList = deviceList;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v          = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_info_layout, parent, false);
        ViewHolder vh   = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final DeviceInfoModel deviceInfoModel = (DeviceInfoModel) deviceList.get(position);
        ViewHolder itemHolder                 = (ViewHolder) holder;

        itemHolder.textName.setText(deviceInfoModel.getDeviceName());
        itemHolder.textAddress.setText(deviceInfoModel.getDeviceHardwareAddress());
        itemHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("deviceName", deviceInfoModel.getDeviceName());
                intent.putExtra("deviceAddress",deviceInfoModel.getDeviceHardwareAddress());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int dataCount = deviceList.size();
        return dataCount;
    }
}
