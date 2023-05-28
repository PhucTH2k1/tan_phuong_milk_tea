package com.tanphuong.milktea.shipment.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tanphuong.milktea.shipment.data.model.Shipper;

public final class ShipperFetcher {
    private static final String TAG = "ShipperFetcher";

    public static void fetchShipper(@NonNull Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String shipperId = "1";
        db.collection("shippers")
                .document(shipperId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                Shipper shipper = new Shipper();
                                shipper.setId(shipperId);
                                shipper.setName(document.getString("name"));
                                shipper.setPhoneNumber(document.getString("phone_number"));
                                shipper.setAvatar(document.getString("avatar"));
                                shipper.setLatitude(document.getDouble("latitude"));
                                shipper.setLongitude(document.getDouble("longitude"));
                                shipper.setSignal(document.getString("signal"));
                                callback.onLoaded(shipper);
                            } else {
                                Log.d(TAG, "No such document");
                                callback.onFailed();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            callback.onFailed();
                        }
                    }
                });
    }

    public interface Callback {
        void onLoaded(Shipper shipper);

        void onFailed();
    }
}
