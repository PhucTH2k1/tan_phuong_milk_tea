package com.tanphuong.milktea.bill.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tanphuong.milktea.bill.data.CardFactory;
import com.tanphuong.milktea.bill.data.model.Card;
import com.tanphuong.milktea.databinding.ActivityEditCardBinding;

public class EditCardActivity extends AppCompatActivity {
    private ActivityEditCardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        Card oldCardInfo = CardFactory.getCardInfo();
        if (oldCardInfo != null) {
            binding.edtAddNumberCard.setText(oldCardInfo.getCardNumber());
            binding.tvCardNumber.setText(oldCardInfo.getCardNumber());
            binding.edtCardExpiry.setText(oldCardInfo.getExpireDate());
            binding.tvCardExpiry.setText(oldCardInfo.getExpireDate());
            binding.edtCardHolder.setText(oldCardInfo.getCardHolder());
            binding.tvCardHolder.setText(oldCardInfo.getCardHolder());
            binding.edtSecurityCode.setText(oldCardInfo.getCvv() + "");
        }

        binding.edtAddNumberCard.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(19),
        });
        binding.edtAddNumberCard.addTextChangedListener(new TextWatcher() {
            // Change this to what you want... ' ', '-' etc..
            private static final char space = ' ';

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove spacing char
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
                binding.tvCardNumber.setText(s.toString());
            }
        });
        binding.edtCardHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvCardHolder.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtCardExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvCardExpiry.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnEditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtAddNumberCard.getText().toString().isEmpty()) {
                    Toast.makeText(EditCardActivity.this, "Thẻ không hợp lệ, vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Card newCardInfo = new Card(binding.edtCardHolder.getText().toString(),
                        binding.edtCardExpiry.getText().toString(),
                        binding.edtAddNumberCard.getText().toString(),
                        222);
                CardFactory.changeCardInfo(newCardInfo);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}