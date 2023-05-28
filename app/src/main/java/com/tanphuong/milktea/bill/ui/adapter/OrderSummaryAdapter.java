package com.tanphuong.milktea.bill.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanphuong.milktea.databinding.ItemOrderSummaryBinding;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;

import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {
    private List<MilkTeaOrder> orders;

    public OrderSummaryAdapter(List<MilkTeaOrder> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderSummaryBinding binding = ItemOrderSummaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MilkTeaOrder order = orders.get(position);
        String sb = order.getQuantity() + " x " + order.getMilkTea().getName();
        holder.binding.tvOrderSummary.setText(sb);
        holder.binding.tvOrderPrice.setText(order.getTotalCost() + "đ");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderSummaryBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrderSummaryBinding.bind(itemView);
        }
    }
}
