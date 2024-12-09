package com.kiranjohn.finalproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private final Runnable updateTotalPriceCallback;

    public CartAdapter(List<CartItem> cartItems, Runnable updateTotalPriceCallback) {
        this.cartItems = cartItems;
        this.updateTotalPriceCallback = updateTotalPriceCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        if (product != null) {
            holder.productName.setText(product.getName());
            holder.productPrice.setText("$" + product.getPrice());
            holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));

            String imageUrl = product.getImageUrl();

            Log.d("CartAdapter", "Product: " + product);
            Log.d("CartAdapter", "Image URL: " + imageUrl);
            Log.d("CartAdapter", "Product ImageView: " + holder.productImageView);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .into(holder.productImageView);
            }

            // Handle increase and decrease button clicks
            holder.increaseQuantityButton.setOnClickListener(v -> {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                notifyItemChanged(position);
                updateTotalPriceCallback.run();
            });

            holder.decreaseQuantityButton.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    notifyItemChanged(position);
                    updateTotalPriceCallback.run();
                }
            });

            holder.removeItemButton.setOnClickListener(v -> {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                updateTotalPriceCallback.run();
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity, productPrice;
        ImageView productImageView;
        Button removeItemButton, increaseQuantityButton, decreaseQuantityButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productPrice = itemView.findViewById(R.id.productPrice);
            removeItemButton = itemView.findViewById(R.id.removeItemButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
        }
    }
}
