package com.kiranjohn.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView brandRecyclerView, productRecyclerView;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private EditText searchBar;
    private ImageButton goToCartButton, logoutButton;

    private List<String> brandList;
    private BrandAdapter brandAdapter;
    private List<Product> originalProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        originalProductList = new ArrayList<>();
        productList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        searchBar = findViewById(R.id.searchBar);
        goToCartButton = findViewById(R.id.goToCartButton);
        logoutButton = findViewById(R.id.logoutButton);
        brandRecyclerView = findViewById(R.id.brandRecyclerView);
        productRecyclerView = findViewById(R.id.productRecyclerView);

        // Set up brand RecyclerView
        brandList = new ArrayList<>();
        brandAdapter = new BrandAdapter(brandList, this::filterByBrand);
        brandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        brandRecyclerView.setAdapter(brandAdapter);

        // Set up product RecyclerView without OnProductClickListener
        productAdapter = new ProductAdapter(this, productList); // No need for click listener
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columns in the grid
        productRecyclerView.setLayoutManager(gridLayoutManager);
        productRecyclerView.setAdapter(productAdapter);

        fetchBrands();
        fetchProducts();

        goToCartButton.setOnClickListener(v -> {
            Toast.makeText(ProductActivity.this, "Go to Cart clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProductActivity.this, CartActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(ProductActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fetchBrands() {
        // Example brands; you can fetch these from Firestore or another data source
        brandList.add("All");
        brandList.add("Nike");
        brandList.add("Adidas");
        brandList.add("Puma");
        brandList.add("Lotto");
        brandList.add("Under Armour");
        brandList.add("Mizuno");
        brandAdapter.notifyDataSetChanged();
    }

    private void fetchProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                products.add(product);
                            }
                        }
                        originalProductList.clear();
                        originalProductList.addAll(products);
                        productList.clear();
                        productList.addAll(products);
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ProductActivity.this, "Error getting products.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            String productName = product.getName();
            if (productName != null && productName.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }

        // Update the adapter with the filtered list
        productAdapter.updateList(filteredList);
    }

    private void filterByBrand(String brand) {
        Log.d("Filter", "Filtering by brand: " + brand);
        if ("All".equalsIgnoreCase(brand)) {
            productList.clear();
            productList.addAll(originalProductList); // Show all products
        } else {
            productList.clear();
            for (Product product : originalProductList) {
                String productBrand = product.getBrand();
                if (productBrand != null && productBrand.equalsIgnoreCase(brand) && product.getName() != null) {
                    productList.add(product);
                    Log.d("Filter", "Adding product: " + product.getName() + " with brand: " + productBrand);
                }
            }
        }
        Log.d("Filter", "Filtered list size: " + productList.size());
        productAdapter.notifyDataSetChanged();
    }
}
