package com.tanphuong.milktea.shipment.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.tanphuong.milktea.core.data.model.Person;

import java.util.List;

public class Shipper extends Person {
    private List<LatLng> coordinates;

    public Shipper() {
    }

    public Shipper(String id, String phoneNumber, String avatar, List<LatLng> coordinates) {
        super(id, phoneNumber, avatar);
        this.coordinates = coordinates;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }
}
