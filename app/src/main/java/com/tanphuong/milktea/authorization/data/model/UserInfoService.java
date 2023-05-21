package com.tanphuong.milktea.authorization.data.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public final class UserInfoService {
    private static final String TAG = "UserInfoService";

    public static void upload(FirebaseUser userInfo, OnUploadUserCallback callback) {
        String userId = userInfo.getUid();
        if (userId.isEmpty()) {
            if (callback != null) {
                callback.onFailure();
            }
            return;
        }
        User user = new User();
        user.setId(userId);
        if (userInfo.getPhoneNumber() != null) {
            user.setPhoneNumber(userInfo.getPhoneNumber());
        }
        if (userInfo.getDisplayName() != null) {
            user.setUserName(userInfo.getDisplayName());
        }
        if (userInfo.getPhotoUrl() != null) {
            user.setAvatar(userInfo.getPhotoUrl().getPath());
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .set(user)
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

    public interface OnUploadUserCallback {
        void onSuccess();

        void onFailure();
    }
}
