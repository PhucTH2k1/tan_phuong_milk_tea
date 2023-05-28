package com.tanphuong.milktea.authorization.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tanphuong.milktea.authorization.data.model.User;

public final class UserFetcher {
    private static final String TAG = "UserFetcher";

    public static void fetchUserInfo(@NonNull Callback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                User user = new User();
                                user.setId(currentUser.getUid());
                                user.setName(document.getString("userName"));
                                user.setUserName(document.getString("userName"));
                                user.setPhoneNumber(document.getString("phoneNumber"));
                                user.setEmail(document.getString("email"));
                                user.setAvatar(document.getString("avatar"));
                                user.setAddress(document.getString("address"));
                                callback.onLoaded(user);
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
        void onLoaded(User userInfo);

        void onFailed();
    }
}
