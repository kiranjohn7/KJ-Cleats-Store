package com.kiranjohn.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private TextView totalPrice;
    private TextView subtotalPrice;
    private TextView taxAmount;
    private Button checkoutButton;
    private List<CartItem> cartItemList;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private static final double TAX_RATE = 0.13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize CartManager
        cartManager = CartManager.getInstance();

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPrice = findViewById(R.id.totalPrice);
        subtotalPrice = findViewById(R.id.subtotalPrice);
        taxAmount = findViewById(R.id.taxAmount);
        checkoutButton = findViewById(R.id.checkoutButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve CartItem list from CartManager
        cartItemList = cartManager.getCartItems();

        // Initialize adapter
        cartAdapter = new CartAdapter(cartItemList, this::updateTotalPrice);
        cartRecyclerView.setAdapter(cartAdapter);

        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    // Update total price based on cart items
    private void updateTotalPrice() {
        double subtotal = 0.0;

        // Calculate subtotal without tax
        for (CartItem item : cartItemList) {
            double price = item.getProduct().getPriceAsDouble();
            subtotal += item.getQuantity() * price;
        }

        double tax = subtotal * TAX_RATE;
        double grandTotal = subtotal + tax;

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedSubtotal = decimalFormat.format(subtotal);
        String formattedTax = decimalFormat.format(tax);
        String formattedTotal = decimalFormat.format(grandTotal);

        taxAmount.setText("Tax: $" + formattedTax);
        subtotalPrice.setText("Subtotal: $" + formattedSubtotal);
        totalPrice.setText("Total: $" + formattedTotal);
    }
}
