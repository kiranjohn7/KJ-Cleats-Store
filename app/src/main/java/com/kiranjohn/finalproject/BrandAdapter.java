package com.kiranjohn.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private List<String> brandList;
    private OnBrandClickListener onBrandClickListener;
    private int selectedPosition = -1;

    public BrandAdapter(List<String> brandList, OnBrandClickListener onBrandClickListener) {
        this.brandList = brandList;
        this.onBrandClickListener = onBrandClickListener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        String brand = brandList.get(position);
        holder.brandName.setText(brand);

        // Update the color based on the selected position
        if (position == selectedPosition) {
            holder.brandName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.accent_color));
        } else {
            holder.brandName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(v -> {
            // Update the selected position
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            onBrandClickListener.onBrandClick(brand);
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        public TextView brandName;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brandName);
        }
    }

    public interface OnBrandClickListener {
        void onBrandClick(String brand);
    }
}
