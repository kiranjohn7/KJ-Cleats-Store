package com.kiranjohn.finalproject;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems; // Return a copy to prevent external modification
    }

    public void clearCart() {
        cartItems.clear();
    }

    public void addCartItem(CartItem item) {
        if (item == null || item.getProduct() == null || item.getProduct().getId() == null) {
            return; // Handle the case where the item or product is null
        }

        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(item.getProduct().getId())) {
                // Item exists, update quantity
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        // Item does not exist, add new
        cartItems.add(item);
    }

    public void updateCartItem(CartItem item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(item.getProduct().getId())) {
                cartItem.setQuantity(item.getQuantity());
                return;
            }
        }
        // If item doesn't exist, add it
        cartItems.add(item);
    }

    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
    }
}
