package com.tanphuong.milktea.authorization.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tanphuong.milktea.authorization.data.model.User;

public final class UserUploader {
    private static final String TAG = "UserUploader";

    public static void upload(FirebaseUser userInfo, Callback callback) {
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
        if (userInfo.getEmail() != null) {
            user.setEmail(userInfo.getEmail());
        }
        if (userInfo.getDisplayName() != null) {
            user.setName(userInfo.getDisplayName());
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
                            UserFactory.signIn(user);
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

    public interface Callback {
        void onSuccess();

        void onFailure();
    }
}
