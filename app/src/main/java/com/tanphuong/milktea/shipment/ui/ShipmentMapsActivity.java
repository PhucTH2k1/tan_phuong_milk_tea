package com.tanphuong.milktea.shipment.ui;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tanphuong.milktea.R;
import com.tanphuong.milktea.core.util.BitmapUtils;
import com.tanphuong.milktea.databinding.ActivityShipmentMapsBinding;
import com.tanphuong.milktea.shipment.data.DirectionsJSONParser;

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

    private GoogleMap map;
    private ActivityShipmentMapsBinding binding;
    private Marker userMarker;
    private Marker storeMarker;
    private Marker shipperMarker;
    private Polyline routePolyline;
    private boolean isPickedProduct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShipmentMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng shipper = new LatLng(20.971683812044024, 105.77646170549578);
        LatLng store = new LatLng(20.959854779788706, 105.7673035665212);

        // Add a marker in Sydney and move the camera
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

        // Focus to the store first
        animateMap(storeMarker);

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                shipperMarker = map.addMarker(new MarkerOptions().position(shipper).title("Ga Hà Đông")
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                BitmapUtils.createMaker(ShipmentMapsActivity.this,
                                        R.drawable.ic_shipper,
                                        "Shipper"))));
                Toast.makeText(ShipmentMapsActivity.this, "A shipper has accepted your order!", Toast.LENGTH_SHORT).show();
                // Focus to the accepted shipper
                animateMap(shipperMarker);
                moveOnMap(shipperMarker.getPosition(), storeMarker.getPosition());
            }
        }, 5000);
    }

    private void moveOnMap(LatLng start, LatLng end) {
        String url = getDirectionsUrl(start, end);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
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

            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(allPoints);
            lineOptions.width(14);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);
            routePolyline = map.addPolyline(lineOptions);

            // Update shipper marker by time interval
            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                private int index = 0;

                @Override
                public void run() {
                    // Update shipper marker
                    shipperMarker.setPosition(allPoints.get(index));
                    if (index == allPoints.size() - 1) {
                        if (!isPickedProduct) {
                            isPickedProduct = true;
                            // Then get to user
                            moveOnMap(shipperMarker.getPosition(), userMarker.getPosition());
                        } else {
                            Toast.makeText(ShipmentMapsActivity.this, "Shipment done!", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    // Redraw route
                    routePolyline.setPoints(allPoints.subList(index, allPoints.size() - 1));
                    animateMap(shipperMarker);
                    index++;
                    h.postDelayed(this, 500);
                }
            }, 5000);
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
        String apiKey = "key=AIzaSyCaTHXaVs22_Qh406E9SyYvmUUW_NcCjoU";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + apiKey;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
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