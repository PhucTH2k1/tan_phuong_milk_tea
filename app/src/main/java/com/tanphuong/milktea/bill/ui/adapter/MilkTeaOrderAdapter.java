package com.tanphuong.milktea.bill.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tanphuong.milktea.databinding.ItemMilkTeaOrderBinding;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;
import com.tanphuong.milktea.drink.data.model.RealIngredient;

import java.util.List;

public class MilkTeaOrderAdapter extends RecyclerView.Adapter<MilkTeaOrderAdapter.ViewHolder> {
    private List<MilkTeaOrder> orders;
    private Callback callback;

    public MilkTeaOrderAdapter(List<MilkTeaOrder> orders, Callback callback) {
        this.orders = orders;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMilkTeaOrderBinding binding = ItemMilkTeaOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MilkTeaOrder order = orders.get(position);
        Glide.with(holder.binding.getRoot().getContext())
                .load(order.getMilkTea().getCoverImage())
                .centerCrop()
                .into(holder.binding.imgMilkTeaCover);
        holder.binding.tvMilkTeaName.setText(order.getMilkTea().getName());
        holder.binding.tvMilkTeaPrice.setText(order.getMilkTea().getTotalCost() + " VND");
        holder.binding.tvTotalPrice.setText(order.getTotalCost() + " VND");
        StringBuilder sb = new StringBuilder();
        for (RealIngredient topping : order.getToppings()) {
            sb.append(topping.getName()).append(": ").append(topping.calculateCost()).append(" VND").append("\n");
        }
        holder.binding.tvTopping.setText(sb.toString());
        holder.binding.rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onDelete(order);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public List<MilkTeaOrder> getOrders() {
        return orders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMilkTeaOrderBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMilkTeaOrderBinding.bind(itemView);
        }
    }

    public interface Callback {
        void onDelete(MilkTeaOrder order);
    }
}
