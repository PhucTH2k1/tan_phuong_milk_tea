package com.tanphuong.milktea.home.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tanphuong.milktea.bill.data.MilkTeaOrderFactory;
import com.tanphuong.milktea.bill.ui.adapter.MilkTeaOrderAdapter;
import com.tanphuong.milktea.databinding.FragmentCartBinding;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;

import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private MilkTeaOrderAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        List<MilkTeaOrder> orders = MilkTeaOrderFactory.showCart();
        adapter = new MilkTeaOrderAdapter(orders, new MilkTeaOrderAdapter.Callback() {
            @Override
            public void onIncreaseQuantity(MilkTeaOrder order) {
                boolean isSuccess = MilkTeaOrderFactory.addOneMoreOrder(order);
                if (isSuccess) {
                    adapter.notifyDataSetChanged();
                    calculateTotalPrice();
                }
            }

            @Override
            public void onDecreaseQuantity(MilkTeaOrder order) {
                boolean isSuccess = MilkTeaOrderFactory.subtractOneOrder(order);
                if (isSuccess) {
                    adapter.notifyDataSetChanged();
                    calculateTotalPrice();
                }
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<MilkTeaOrder> orders = MilkTeaOrderFactory.showCart();
        if (orders.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.clCart.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.clCart.setVisibility(View.VISIBLE);
            binding.rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvCart.setAdapter(adapter);
            calculateTotalPrice();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void calculateTotalPrice() {
        List<MilkTeaOrder> orders = MilkTeaOrderFactory.showCart();
        int total = 0;
        for (MilkTeaOrder order : orders) {
            total += order.getTotalCost();
        }
        binding.btnPay.setText("Thanh toán " + total + " VND");
    }
}