package com.tanphuong.milktea.drink.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tanphuong.milktea.databinding.ItemGridMilkTeaBinding;
import com.tanphuong.milktea.drink.data.model.MilkTea;

import java.util.List;

public class GridMilkTeaAdapter extends RecyclerView.Adapter<GridMilkTeaAdapter.ViewHolder> {
    private List<MilkTea> milkTeas;
    private OnClickCallback callback;

    public GridMilkTeaAdapter(List<MilkTea> milkTeas, OnClickCallback callback) {
        this.milkTeas = milkTeas;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGridMilkTeaBinding binding = ItemGridMilkTeaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MilkTea milkTea = milkTeas.get(position);
        Glide.with(holder.binding.getRoot().getContext())
                .load(milkTea.getCoverImage())
                .centerCrop()
                .into(holder.binding.imgMilkTeaCover);
        holder.binding.tvMilkTeaName.setText(milkTea.getName());
        holder.binding.tvMilkTeaPrice.setText(milkTea.getTotalCost() + " VND");
        holder.binding.cvMilkTea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClicked(milkTea);
            }
        });
    }

    @Override
    public int getItemCount() {
        return milkTeas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemGridMilkTeaBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemGridMilkTeaBinding.bind(itemView);
        }
    }

    public interface OnClickCallback {
        void onItemClicked(MilkTea milkTea);
    }
}
