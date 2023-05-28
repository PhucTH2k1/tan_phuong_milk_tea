package com.tanphuong.milktea.home.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
import com.tanphuong.milktea.R;
import com.tanphuong.milktea.bill.data.BillFetcher;
import com.tanphuong.milktea.bill.data.BillUploader;
import com.tanphuong.milktea.bill.data.model.Bill;
import com.tanphuong.milktea.bill.data.model.BillStatus;
import com.tanphuong.milktea.core.ui.map.adapter.StoreInfoWindowAdapter;
import com.tanphuong.milktea.core.util.BitmapUtils;
import com.tanphuong.milktea.databinding.FragmentStoreBinding;
import com.tanphuong.milktea.shipment.ui.ShipmentMapsActivity;
import com.tanphuong.milktea.store.data.StoreFetcher;
import com.tanphuong.milktea.store.data.model.Store;
import com.tanphuong.milktea.store.ui.StoreDetailActivity;

import java.util.ArrayList;
import java.util.List;


public class StoreListFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private FragmentStoreBinding binding;
    private List<Store> loadedStores = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Lấy danh sách store từ Firestore
        binding.pbLoading.setVisibility(View.VISIBLE);
        StoreFetcher.fetchStores(new StoreFetcher.Callback() {
            @Override
            public void onLoaded(List<Store> stores) {
                binding.pbLoading.setVisibility(View.GONE);

                loadedStores.clear();
                loadedStores.addAll(stores);
                if (map != null) {
                    List<LatLng> coordinates = new ArrayList<>();
                    for (int i = 0; i < stores.size(); i++) {
                        Store store = stores.get(i);
                        LatLng position = new LatLng(store.getLatitude(), store.getLongitude());
                        map.addMarker(new MarkerOptions()
                                .position(position)
                                .title(store.getName())
                                .snippet(store.getAddress())
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapUtils.createMaker(getContext(),
                                                R.drawable.img_default_store_cover,
                                                store.getName()))));
                        coordinates.add(position);
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng latLng : coordinates) {
                        builder.include(latLng);
                    }
                    LatLngBounds bounds = builder.build();
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.1);
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, height, width, padding);
                    map.animateCamera(cu);
                }
            }

            @Override
            public void onFailed() {
                binding.pbLoading.setVisibility(View.GONE);
            }
        });

        BillFetcher.fetchBills(new BillFetcher.Callback() {
            @Override
            public void onLoaded(List<Bill> bills) {
                for (Bill ongoingBill : bills) {
                    if (ongoingBill.getStatus() == BillStatus.ON_GOING) {
                        BillUploader.setOnGoingBill(ongoingBill);
                        binding.llOnGoingBill.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .load(ongoingBill.getOrders().get(0).getMilkTea().getCoverImage())
                                .centerCrop()
                                .into(binding.imgMilkTeaCover);
                        binding.tvMilkTeaName.setText(ongoingBill.getSummaryMilkTeas());
                        binding.tvBillPrice.setText(ongoingBill.getTotalPrice() + "đ");
                        binding.llOnGoingBill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ShipmentMapsActivity.class);
                                startActivity(intent);
                            }
                        });
                        return;
                    }
                }
            }

            @Override
            public void onFailed() {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        StoreInfoWindowAdapter markerInfoWindowAdapter = new StoreInfoWindowAdapter(getContext());
        googleMap.setInfoWindowAdapter(markerInfoWindowAdapter);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        for (Store store : loadedStores) {
            if (store.getLatitude() == marker.getPosition().latitude &&
                    store.getLongitude() == marker.getPosition().longitude) {
                // Sang màn hình chi tiết cửa hàng này
                Intent intent = new Intent(getContext(), StoreDetailActivity.class);
                intent.putExtra(StoreDetailActivity.STORE_DATA, store);
                startActivity(intent);
                return true;
            }
        }
        return false;
    }
}