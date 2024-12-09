package com.kiranjohn.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    private TextView orderSummaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        orderSummaryTextView = findViewById(R.id.orderSummary);

        // Retrieve order details from Intent extras
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("order_summary")) {
            String orderSummary = intent.getStringExtra("order_summary");
            orderSummaryTextView.setText(orderSummary);
        }

        Button backToProductsButton = findViewById(R.id.backToProductsButton);
        backToProductsButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ThankYouActivity.this, ProductActivity.class);
            startActivity(backIntent);
            finish();
        });
    }
}
