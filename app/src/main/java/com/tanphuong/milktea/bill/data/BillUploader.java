package com.tanphuong.milktea.bill.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tanphuong.milktea.bill.data.model.Bill;
import com.tanphuong.milktea.bill.data.model.BillStatus;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;
import com.tanphuong.milktea.drink.data.model.RealIngredient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillUploader {
    private static final String TAG = "BillUploader";

    private static Bill currentBill;

    public static void setCurrentBill(Bill currentBill) {
        BillUploader.currentBill = currentBill;
    }

    public static void uploadBillStatus(BillStatus status) {
        if (currentBill.getId() == null) {
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bills")
                .document(currentBill.getId()).update("status", status.name());
    }

    public static void uploadShipper(String shipperId) {
        if (shipperId == null) {
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bills")
                .document(currentBill.getId())
                .update("shipper", db.document("shippers/" + shipperId));
    }

    public static void upload(Bill bill, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> billMap = new HashMap<>();

        db.collection("bills").count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                long count = 0;
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    count = snapshot.getCount();
                    Log.d(TAG, "Count: " + snapshot.getCount());
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
                // Thêm dữ liệu danh sách trà sữa đã order trong bill này
                String billId = bill.getId();
                if (bill.getId() == null) {
                    billId = String.valueOf(count + 1);
                    bill.setId(billId);
                }
                currentBill = bill;
                if (bill.getOrders() != null) {
                    List<Map<String, Object>> orders = new ArrayList<>();
                    for (MilkTeaOrder order : bill.getOrders()) {
                        Map<String, Object> orderMap = new HashMap<>();
                        // Dữ liệu trà
                        orderMap.put("milk_tea", db.document("milk_teas/" + order.getMilkTea().getId()));
                        // Dữ liệu topping
                        List<DocumentReference> toppings = new ArrayList<>();
                        for (RealIngredient topping : order.getToppings()) {
                            toppings.add(db.document("toppings/" + topping.getId()));
                        }
                        orderMap.put("toppings", toppings);
                        // Các dữ liệu còn lại về kích thước, đường, đá, ghi chú, số lượng
                        orderMap.put("size", order.getSize().name());
                        orderMap.put("sugar_gauge", order.getSugarGauge().name());
                        orderMap.put("ice_gauge", order.getIceGauge().name());
                        orderMap.put("note", order.getNote());
                        orderMap.put("quantity", order.getQuantity());
                        orders.add(orderMap);
                    }
                    billMap.put("orders", orders);
                }

                // Thêm dữ liệu người dùng đặt bill này
                if (bill.getUser() != null) {
                    billMap.put("user", db.document("users/" + bill.getUser().getId()));
                }

                // Thêm dữ liệu shipper nhận bill này nếu có
                if (bill.getShipper() != null) {
                    billMap.put("shipper", db.document("shippers/" + bill.getShipper().getId()));
                }

                // Thêm dữ liệu phương thức thanh toán bill này
                if (bill.getPaymentMethod() != null) {
                    billMap.put("payment_method", bill.getPaymentMethod().name());
                }

                // Thêm dữ liệu trạng thái bill này
                if (bill.getStatus() != null) {
                    billMap.put("status", bill.getStatus().name());
                }

                // Thêm dữ liệu ngày tạo bill này
                billMap.put("created_date", new Date());

                // Upload
                db.collection("bills")
                        .document(billId)
                        .set(billMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                if (callback != null) {
                                    callback.onSuccess();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                                if (callback != null) {
                                    callback.onFailure();
                                }
                            }
                        });
            }
        });
    }

    public interface Callback {
        void onSuccess();

        void onFailure();
    }
}
