package com.tanphuong.milktea.store.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tanphuong.milktea.store.data.model.Store;

import java.util.ArrayList;
import java.util.List;

public final class StoreFetcher {
    private static final String TAG = "StoreFetcher";

    public static void fetchStores(@NonNull Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stores").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Store> stores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        stores.add(new Store(
                                document.getId(),
                                document.getString("name"),
                                document.getString("address"),
                                document.getString("phone_number"),
                                document.getString("cover_image"),
                                document.getDouble("latitude"),
                                document.getDouble("longitude")
                        ));
                    }
                    callback.onLoaded(stores);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    callback.onFailed();
                }
            }
        });
    }

    public interface Callback {
        void onLoaded(List<Store> stores);

        void onFailed();
    }
}
