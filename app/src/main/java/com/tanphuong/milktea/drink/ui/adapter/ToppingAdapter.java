package com.tanphuong.milktea.drink.ui.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanphuong.milktea.databinding.ItemToppingBinding;
import com.tanphuong.milktea.drink.data.model.RealIngredient;

import java.util.ArrayList;
import java.util.List;

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ViewHolder> {
    private List<RealIngredient> toppings;
    private SparseBooleanArray pickedToppings;

    public ToppingAdapter(List<RealIngredient> toppings) {
        this.toppings = toppings;
        pickedToppings = new SparseBooleanArray();
        for (int i = 0; i < toppings.size(); i++) {
            pickedToppings.put(i, false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemToppingBinding binding = ItemToppingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RealIngredient topping = toppings.get(position);
        holder.binding.tvToppingName.setText(topping.getName());
        holder.binding.tvToppingPrice.setText(topping.calculateCost() + " VND");
        holder.binding.cbTopping.setChecked(pickedToppings.get(position));
        holder.binding.cbTopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    pickedToppings.put(position, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return toppings.size();
    }

    public List<RealIngredient> getPickedToppings() {
        List<RealIngredient> result = new ArrayList<>();
        for (int i = 0; i < toppings.size(); i++) {
            if (pickedToppings.get(i)) {
                result.add(toppings.get(i));
            }
        }
        return result;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemToppingBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemToppingBinding.bind(itemView);
        }
    }
}
