package com.tanphuong.milktea.core.ui.map.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.tanphuong.milktea.R;

public class StoreInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public StoreInfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.map_store_info_window, null);

        TextView tvName = (TextView) v.findViewById(R.id.tv_name);
        tvName.setText(arg0.getTitle());

        TextView tvAddress = (TextView) v.findViewById(R.id.tv_address);
        tvAddress.setText(arg0.getSnippet());

        return v;
    }
}
