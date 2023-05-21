package com.tanphuong.milktea.bill.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.tanphuong.milktea.bill.data.MilkTeaOrderFactory;
import com.tanphuong.milktea.databinding.ActivityOrderMilkTeaBinding;
import com.tanphuong.milktea.drink.data.IngredientFetcher;
import com.tanphuong.milktea.drink.data.ToppingFetcher;
import com.tanphuong.milktea.drink.data.model.IceGauge;
import com.tanphuong.milktea.drink.data.model.Ingredient;
import com.tanphuong.milktea.drink.data.model.MilkTea;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;
import com.tanphuong.milktea.drink.data.model.RealIngredient;
import com.tanphuong.milktea.drink.data.model.Size;
import com.tanphuong.milktea.drink.data.model.SugarGauge;
import com.tanphuong.milktea.drink.ui.adapter.ToppingAdapter;

import java.util.List;

public class OrderMilkTeaActivity extends AppCompatActivity {
    public static final String MILK_TEA_DATA = "MILK_TEA_DATA";
    private ActivityOrderMilkTeaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderMilkTeaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        // Đầu tiên, lấy dữ liệu nguyên liệu
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.pbLoading.setVisibility(View.VISIBLE);
        IngredientFetcher.fetchIngredients(new IngredientFetcher.Callback() {
            @Override
            public void onLoaded(List<Ingredient> ingredients) {
                // Dựa theo dữ liệu nguyên liệu, lấy tiếp dữ liệu về topping
                ToppingFetcher.fetchToppings(ingredients, new ToppingFetcher.Callback() {
                    @Override
                    public void onLoaded(List<RealIngredient> toppings) {
                        binding.pbLoading.setVisibility(View.GONE);
                        showMilkTea(toppings);
                    }

                    @Override
                    public void onFailed() {
                        binding.pbLoading.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailed() {
                binding.pbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void showMilkTea(List<RealIngredient> toppings) {
        // Lấy dữ liệu được gửi từ màn trước qua bundle
        MilkTea milkTea = null;
        if (getIntent() != null) {
            milkTea = (MilkTea) getIntent().getSerializableExtra(MILK_TEA_DATA);
        }
        if (milkTea == null) {
            return;
        }
        Glide.with(this)
                .load(milkTea.getCoverImage())
                .centerCrop()
                .into(binding.imgMilkTeaCover);
        binding.tvMilkTeaName.setText(milkTea.getName());
        binding.tvMilkTeaName2.setText(milkTea.getName());
        binding.tvMilkTeaDes.setText(milkTea.getDescribe());
        binding.tvMilkTeaPrice.setText(milkTea.getTotalCost() + " VND");

        ToppingAdapter adapter = new ToppingAdapter(toppings);
        binding.rvTopping.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTopping.setAdapter(adapter);

        MilkTea finalMilkTea = milkTea;
        binding.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MilkTeaOrder order = new MilkTeaOrder();
                order.setMilkTea(finalMilkTea);
                order.setQuantity(1);
                order.setSize(Size.MEDIUM);
                order.setIceGauge(IceGauge.LESS);
                order.setSugarGauge(SugarGauge.LESS);
                order.setToppings(adapter.getPickedToppings());
                MilkTeaOrderFactory.addOrder(order);
                finish();
            }
        });
    }
}