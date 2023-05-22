package com.tanphuong.milktea.bill.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tanphuong.milktea.databinding.ActivityEditCardBinding;

public class EditCardActivity extends AppCompatActivity {
    private ActivityEditCardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}