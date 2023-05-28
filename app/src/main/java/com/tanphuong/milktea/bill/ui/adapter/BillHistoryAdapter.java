package com.tanphuong.milktea.bill.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tanphuong.milktea.bill.data.model.Bill;
import com.tanphuong.milktea.databinding.ItemHistoryBillBinding;

import java.text.SimpleDateFormat;
import java.util.List;

public class BillHistoryAdapter extends RecyclerView.Adapter<BillHistoryAdapter.ViewHolder> {
    private List<Bill> bills;

    public BillHistoryAdapter(List<Bill> bills) {
        this.bills = bills;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBillBinding binding = ItemHistoryBillBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        Bill bill = bills.get(position);
        MilkTeaHistoryAdapter adapter = new MilkTeaHistoryAdapter(bill.getOrders());
        holder.binding.rlMilkTea.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.rlMilkTea.setAdapter(adapter);
        holder.binding.tvOrderId.setText("#" + bill.getId());
        holder.binding.tvOrderDate.setText(new SimpleDateFormat("HH:mm dd/MM/yyyy").format(bill.getDate()));
        holder.binding.tvOrderStatus.setText(bill.getStatus().title());
        holder.binding.tvOrderStatus.setTextColor(context.getColor(bill.getStatus().colorRes()));
        holder.binding.tvOrderPrice.setText(bill.getTotalPrice() + "đ");
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemHistoryBillBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHistoryBillBinding.bind(itemView);
        }
    }
}
