package com.tanphuong.milktea.store.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.tanphuong.milktea.bill.ui.OrderMilkTeaActivity;
import com.tanphuong.milktea.databinding.ActivityStoreDetailBinding;
import com.tanphuong.milktea.drink.data.IngredientFetcher;
import com.tanphuong.milktea.drink.data.MilkTeaFetcher;
import com.tanphuong.milktea.drink.data.model.Ingredient;
import com.tanphuong.milktea.drink.data.model.MilkTea;
import com.tanphuong.milktea.drink.ui.adapter.GridMilkTeaAdapter;
import com.tanphuong.milktea.store.data.model.Store;

import java.util.List;

public class StoreDetailActivity extends FragmentActivity {
    public static final String STORE_DATA = "STORE_DATA";

    private ActivityStoreDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoreDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lấy dữ liệu được gửi từ màn trước qua bundle
        Store store = null;
        if (getIntent() != null) {
            store = (Store) getIntent().getSerializableExtra(STORE_DATA);
        }
        if (store == null) {
            return;
        }
        Glide.with(this)
                .load(store.getCoverImage())
                .centerCrop()
                .into(binding.imgStoreCover);
        binding.tvName.setText(store.getName());
        binding.tvAddress.setText(store.getAddress());

        // Đầu tiên, lấy dữ liệu nguyên liệu
        binding.pbLoading.setVisibility(View.VISIBLE);
        IngredientFetcher.fetchIngredients(new IngredientFetcher.Callback() {
            @Override
            public void onLoaded(List<Ingredient> ingredients) {
                // Dựa theo dữ liệu nguyên liệu, lấy tiếp dữ liệu về trà sữa
                MilkTeaFetcher.fetchMilkTeas(ingredients, new MilkTeaFetcher.Callback() {
                    @Override
                    public void onLoaded(List<MilkTea> milkTeas) {
                        binding.pbLoading.setVisibility(View.GONE);
                        showMilkTeas(milkTeas);
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

    private void showMilkTeas(List<MilkTea> milkTeas) {
        GridMilkTeaAdapter adapter = new GridMilkTeaAdapter(milkTeas, new GridMilkTeaAdapter.OnClickCallback() {
            @Override
            public void onItemClicked(MilkTea milkTea) {
                Intent intent = new Intent(StoreDetailActivity.this, OrderMilkTeaActivity.class);
                intent.putExtra(OrderMilkTeaActivity.MILK_TEA_DATA, milkTea);
                startActivity(intent);
            }
        });
        binding.rvMilkTea.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvMilkTea.setAdapter(adapter);
    }
}