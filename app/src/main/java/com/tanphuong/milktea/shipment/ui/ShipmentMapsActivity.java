package com.tanphuong.milktea.shipment.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tanphuong.milktea.R;
import com.tanphuong.milktea.bill.data.BillUploader;
import com.tanphuong.milktea.bill.data.model.BillStatus;
import com.tanphuong.milktea.bill.util.JsonUtils;
import com.tanphuong.milktea.core.util.BitmapUtils;
import com.tanphuong.milktea.databinding.ActivityShipmentMapsBinding;
import com.tanphuong.milktea.shipment.data.DirectionsJSONParser;
import com.tanphuong.milktea.shipment.data.MapConstants;
import com.tanphuong.milktea.shipment.data.ShipperFetcher;
import com.tanphuong.milktea.shipment.data.model.ShipStage;
import com.tanphuong.milktea.shipment.data.model.Shipper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShipmentMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final long SHIPPER_SPEED_IN_MILLIS = 300;
    private GoogleMap map;
    private ActivityShipmentMapsBinding binding;
    private Marker userMarker;
    private Marker storeMarker;
    private Marker shipperMarker;
    private Polyline routePolyline;
    private ShipStage shipStage = ShipStage.FINDING;
    private Handler animationHandler;
    private Runnable animationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShipmentMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animationHandler != null && animationCallback != null) {
            animationHandler.removeCallbacks(animationCallback);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng user = new LatLng(20.97928734467758, 105.7568586083332);
        LatLng store = new LatLng(20.959854779788706, 105.7673035665212);

        userMarker = map.addMarker(new MarkerOptions().position(user).title("An Vượng Villa")
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapUtils.createMaker(this,
                                R.drawable.ic_user_avatar,
                                "PhucTH"))));
        storeMarker = map.addMarker(new MarkerOptions().position(store).title("CS 1")
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapUtils.createMaker(this,
                                R.drawable.img_default_store_cover,
                                "CS 1"))));

        // Hướng màn hình về vị trí cửa hàng
        animateMap(storeMarker);
        updateUIStage(ShipStage.FINDING);
        binding.llFindingShipper.setVisibility(View.VISIBLE);
        binding.llShipperInfo.setVisibility(View.GONE);

        // Lấy dữ liệu Shipper
        ShipperFetcher.fetchShipper(new ShipperFetcher.Callback() {
            @Override
            public void onLoaded(Shipper shipper) {
                shipperMarker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(shipper.getLatitude(), shipper.getLongitude()))
                        .title("Ga Hà Đông")
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                BitmapUtils.createMaker(ShipmentMapsActivity.this,
                                        R.drawable.ic_shipper,
                                        "Shipper"))));
                updateUIStage(ShipStage.ACCEPTED);
                animateMap(shipperMarker);
                moveOnMap(shipperMarker.getPosition(), storeMarker.getPosition());
                BillUploader.uploadShipper(shipper.getId());

                // Cập nhật giao diện
                binding.llFindingShipper.setVisibility(View.GONE);
                binding.llShipperInfo.setVisibility(View.VISIBLE);
                binding.tvShipperName.setText(shipper.getName());
                binding.tvShipperSignal.setText(shipper.getSignal());
                binding.tvShipperPhone.setText(shipper.getPhoneNumber());
                Glide.with(ShipmentMapsActivity.this)
                        .load(shipper.getAvatar())
                        .centerCrop()
                        .into(binding.imgShipperAvatar);
                binding.llShipperPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", shipper.getPhoneNumber(), null));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailed() {
                Toast.makeText(ShipmentMapsActivity.this, "Lấy dữ liệu shipper thất bại!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void moveOnMap(LatLng start, LatLng end) {
        String url = getDirectionsUrl(start, end);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private void updateUIStage(ShipStage stage) {
        Drawable inactiveBg = AppCompatResources.getDrawable(this, R.drawable.bg_corner_stroke);
        int inactiveColor = getColor(R.color.DarkSlateGray);
        Drawable activeBg = AppCompatResources.getDrawable(this, R.drawable.bg_corner_fill);
        int activeColor = getColor(R.color.white);
        shipStage = stage;
        binding.rlAccept.setBackground(inactiveBg);
        binding.tvAccept.setTextColor(inactiveColor);
        binding.rlPicked.setBackground(inactiveBg);
        binding.tvPicked.setTextColor(inactiveColor);
        binding.rlShipped.setBackground(inactiveBg);
        binding.tvShipped.setTextColor(inactiveColor);
        switch (shipStage) {
            case ACCEPTED:
                binding.rlAccept.setBackground(activeBg);
                binding.tvAccept.setTextColor(activeColor);
                Toast.makeText(ShipmentMapsActivity.this, "Có shipper đã nhận đơn hàng của bạn!", Toast.LENGTH_SHORT).show();
                BillUploader.uploadBillStatus(BillStatus.SHIPPER_FINDING);
                break;
            case PICKED:
                binding.rlAccept.setBackground(activeBg);
                binding.tvAccept.setTextColor(activeColor);
                binding.rlPicked.setBackground(activeBg);
                binding.tvPicked.setTextColor(activeColor);
                Toast.makeText(ShipmentMapsActivity.this, "Shipper đã lấy đơn hàng từ cửa hàng!", Toast.LENGTH_SHORT).show();
                BillUploader.uploadBillStatus(BillStatus.ON_GOING);
                break;
            case SHIPPED:
                binding.rlAccept.setBackground(activeBg);
                binding.tvAccept.setTextColor(activeColor);
                binding.rlPicked.setBackground(activeBg);
                binding.tvPicked.setTextColor(activeColor);
                binding.rlShipped.setBackground(activeBg);
                binding.tvShipped.setTextColor(activeColor);
                Toast.makeText(ShipmentMapsActivity.this, "Shipper đã giao hàng thành công!", Toast.LENGTH_SHORT).show();
                BillUploader.uploadBillStatus(BillStatus.COMPLETED);
                break;
        }
    }

    private void animateMap(Marker focusMarker) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(focusMarker.getPosition(), 16.0f);
        map.animateCamera(cu);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
                if (!routes.isEmpty()) {
                    return routes;
                }

                // Trường hợp không lấy được dữ liệu từ Google Dỉrection API, lấy từ file json
                int fileSource = R.raw.shipper_to_store;
                if (shipStage == ShipStage.PICKED) {
                    fileSource = R.raw.store_to_user;
                }
                jObject = JsonUtils.readFromResources(ShipmentMapsActivity.this, fileSource);
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> allPoints = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                ArrayList<LatLng> points = new ArrayList<>();
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                allPoints.addAll(points);
            }

            if (allPoints.isEmpty()) {
                Toast.makeText(ShipmentMapsActivity.this, "Không thể lấy dữ liệu chỉ đường!", Toast.LENGTH_SHORT).show();
                return;
            }

            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(allPoints);
            lineOptions.width(14);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);
            routePolyline = map.addPolyline(lineOptions);

            // Update shipper marker by time interval
            animationHandler = new Handler();
            animationCallback = new Runnable() {
                private int index = 0;

                @Override
                public void run() {
                    shipperMarker.setPosition(allPoints.get(index));
                    routePolyline.setPoints(allPoints.subList(index, allPoints.size() - 1));
                    animateMap(shipperMarker);
                    if (index == allPoints.size() - 1) {
                        if (shipStage == ShipStage.ACCEPTED) {
                            moveOnMap(shipperMarker.getPosition(), userMarker.getPosition());
                            updateUIStage(ShipStage.PICKED);
                        } else {
                            updateUIStage(ShipStage.SHIPPED);
                        }
                        return;
                    }
                    index++;
                    animationHandler.postDelayed(this, SHIPPER_SPEED_IN_MILLIS);
                }
            };
            animationHandler.postDelayed(animationCallback, 3000);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String apiKey = "key=" + MapConstants.MAP_API_KEY;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + apiKey;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}