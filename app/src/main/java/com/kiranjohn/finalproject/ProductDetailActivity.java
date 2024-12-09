package com.kiranjohn.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDescription, productPrice, quantityTextView;
    private Button addToCartButton, goToCartButton;
    private int quantity = 1;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize views
        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        quantityTextView = findViewById(R.id.quantityTextView);
        addToCartButton = findViewById(R.id.addToCartButton);
        goToCartButton = findViewById(R.id.goToCartButton);

        // Initialize cartItems list
        CartManager cartManager = CartManager.getInstance();

        // Retrieve the product passed from ProductAdapter
        product = getIntent().getParcelableExtra("product");

        if (product != null) {
            productName.setText(product.getName());
            productDescription.setText(product.getDetailDesc());
            productPrice.setText(String.format("$%s", product.getPrice()));

            ImageView cartItemImage = findViewById(R.id.productImage);
            Glide.with(this)
                    .load(product.getImageUrl())
                    .into(productImage);
        } else {
            Toast.makeText(this, "Product data is missing or incomplete", Toast.LENGTH_SHORT).show();
            // Optionally finish the activity if product data is not available
            finish();
        }

        addToCartButton.setOnClickListener(v -> {
            if (product != null) {
                if (product.getId() != null) {
                    // Debugging information to log product details
                    Log.d("ProductDetailActivity", "Product ID: " + product.getId());
                    Log.d("ProductDetailActivity", "Product Name: " + product.getName());
                    Log.d("ProductDetailActivity", "Product Quantity: " + quantity);

                    // Check if quantity is valid (e.g., greater than 0)
                    if (quantity > 0) {
                        CartItem cartItem = new CartItem(product, quantity);
                        cartManager.addCartItem(cartItem);
                        Toast.makeText(this, product.getName() + " added to cart (" + quantity + ")", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Quantity must be greater than zero", Toast.LENGTH_SHORT).show();
                        Log.e("ProductDetailActivity", "Add to cart failed - Quantity must be greater than zero");
                    }
                } else {
                    Toast.makeText(this, "Product ID is missing", Toast.LENGTH_SHORT).show();
                    Log.e("ProductDetailActivity", "Add to cart failed - Product ID is missing");
                }
            } else {
                Toast.makeText(this, "Product data is missing or incomplete", Toast.LENGTH_SHORT).show();
                Log.e("ProductDetailActivity", "Add to cart failed - Product object is null");
            }
        });



        // Go to Cart button logic
        goToCartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Increment quantity button logic
        findViewById(R.id.incrementButton).setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
        });

        // Decrement quantity button logic
        findViewById(R.id.decrementButton).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });
    }
}
