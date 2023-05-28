package com.tanphuong.milktea.bill.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.tanphuong.milktea.R;
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
    private Size size = Size.MEDIUM;
    private IceGauge iceGauge = IceGauge.NORMAL;
    private SugarGauge sugarGauge = SugarGauge.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderMilkTeaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        references();

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
        binding.tvMilkTeaPrice.setText(milkTea.getTotalCost() + "đ");

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
                order.setSize(size);
                order.setIceGauge(iceGauge);
                order.setSugarGauge(sugarGauge);
                order.setToppings(adapter.getPickedToppings());
                MilkTeaOrderFactory.addOrder(order);
                finish();
            }
        });
    }

    private void references() {
        binding.rlSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSize(Size.SMALL);
            }
        });
        binding.rlMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSize(Size.MEDIUM);
            }
        });
        binding.rlLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSize(Size.LARGE);
            }
        });
        binding.tvSugarGauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sugarGauge = sugarGauge.nextGauge();
                binding.tvSugarGauge.setText(sugarGauge.title());
            }
        });
        binding.tvIceGauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iceGauge = iceGauge.nextGauge();
                binding.tvIceGauge.setText(iceGauge.title());
            }
        });
    }

    private void switchSize(Size newSize) {
        size = newSize;
        binding.rlSmall.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_stroke));
        binding.tvSizeSmall.setTextColor(getColor(R.color.DarkSlateGray));
        binding.rlMedium.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_stroke));
        binding.tvSizeMedium.setTextColor(getColor(R.color.DarkSlateGray));
        binding.rlLarge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_stroke));
        binding.tvSizeLarge.setTextColor(getColor(R.color.DarkSlateGray));

        switch (size) {
            case SMALL:
                binding.rlSmall.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_fill));
                binding.tvSizeSmall.setTextColor(getColor(R.color.White));
                break;
            case MEDIUM:
                binding.rlMedium.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_fill));
                binding.tvSizeMedium.setTextColor(getColor(R.color.White));
                break;
            case LARGE:
                binding.rlLarge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_circle_fill));
                binding.tvSizeLarge.setTextColor(getColor(R.color.White));
                break;
        }
    }
}