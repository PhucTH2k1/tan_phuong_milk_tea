package com.tanphuong.milktea.bill.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tanphuong.milktea.databinding.ItemHistoryMilkTeaBinding;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;
import com.tanphuong.milktea.drink.data.model.RealIngredient;

import java.util.List;

public class MilkTeaHistoryAdapter extends RecyclerView.Adapter<MilkTeaHistoryAdapter.ViewHolder> {
    private List<MilkTeaOrder> orders;

    public MilkTeaHistoryAdapter(List<MilkTeaOrder> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryMilkTeaBinding binding = ItemHistoryMilkTeaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        if (order.getToppings().isEmpty()) {
            holder.binding.tvMilkTeaTopping.setText("Không có topping");
        } else {
            StringBuilder sb = new StringBuilder();
            for (RealIngredient topping : order.getToppings()) {
                sb.append(topping.toString()).append(", ");
            }
            holder.binding.tvMilkTeaTopping.setText(sb);
        }
        holder.binding.tvMilkTeaQuantity.setText("x " + order.getQuantity());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryMilkTeaBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHistoryMilkTeaBinding.bind(itemView);
        }
    }
}
