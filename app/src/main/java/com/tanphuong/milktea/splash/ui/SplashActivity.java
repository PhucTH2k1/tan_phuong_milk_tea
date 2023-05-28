package com.tanphuong.milktea.splash.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tanphuong.milktea.R;
import com.tanphuong.milktea.authorization.data.UserUploader;
import com.tanphuong.milktea.authorization.ui.SignInActivity;
import com.tanphuong.milktea.home.ui.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        // Kiểm tra đăng nhập
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            signInSuccess(currentUser);
        } else {
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void signInSuccess(FirebaseUser userInfo) {
        UserUploader.upload(userInfo, new UserUploader.Callback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure() {
                Toast.makeText(SplashActivity.this, "Có lỗi bất thường xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}