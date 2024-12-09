package com.kiranjohn.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {
    private EditText firstName, lastName, emailAddress, phoneNumber, mailingAddress;
    private RadioGroup paymentOptions;
    private LinearLayout cardDetailsContainer; // Add this line
    private Button submitOrderButton;
    private static final double TAX_RATE = 0.13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize Views
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailAddress = findViewById(R.id.emailAddress);
        phoneNumber = findViewById(R.id.phoneNumber);
        mailingAddress = findViewById(R.id.mailingAddress);
        paymentOptions = findViewById(R.id.paymentOptions);
        cardDetailsContainer = findViewById(R.id.cardDetailsContainer);
        submitOrderButton = findViewById(R.id.submitOrderButton);

        paymentOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.paymentCreditCard || checkedId == R.id.paymentDebitCard) {
                cardDetailsContainer.setVisibility(View.VISIBLE); // Show card details
            } else {
                cardDetailsContainer.setVisibility(View.GONE); // Hide card details
            }
        });

        submitOrderButton.setOnClickListener(v -> {
            String fName = sanitizeInput(firstName.getText().toString());
            String lName = sanitizeInput(lastName.getText().toString());
            String email = sanitizeInput(emailAddress.getText().toString());
            String phone = sanitizeInput(phoneNumber.getText().toString());
            String address = sanitizeInput(mailingAddress.getText().toString());

            int selectedPaymentId = paymentOptions.getCheckedRadioButtonId();

            Log.d("CheckoutActivity", "fName: " + fName);
            Log.d("CheckoutActivity", "lName: " + lName);
            Log.d("CheckoutActivity", "email: " + email);
            Log.d("CheckoutActivity", "phone: " + phone);
            Log.d("CheckoutActivity", "address: " + address);
            Log.d("CheckoutActivity", "selectedPaymentId: " + selectedPaymentId);

            // Validate and process the order
            if (validateInputs(fName, lName, email, phone, address, selectedPaymentId)) {
                handleSubmitOrder();
            } else {
                Toast.makeText(this, "Validation failed. Please check your inputs.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String sanitizeInput(String input) {
        return input.trim();  // Removes leading and trailing whitespace
    }

    private boolean validateInputs(String fName, String lName, String email, String phone, String address, int paymentId) {
        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || paymentId == -1) {
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPostalCode(address)) {
            Toast.makeText(this, "Invalid postal code", Toast.LENGTH_SHORT).show();
            return false;
        }
        // If Credit or Debit Card is selected, validate card details
        if (paymentId == R.id.paymentCreditCard || paymentId == R.id.paymentDebitCard) {
            if (!validateCardDetails()) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.length() == 10; // Example: 10-digit phone number validation
    }

    private boolean isValidPostalCode(String postalCode) {
        return postalCode.length() >= 5; // Example: Postal code should have at least 5 characters
    }

    private boolean validateCardDetails() {
        EditText cardNumber = findViewById(R.id.cardNumber);
        EditText cardHolderName = findViewById(R.id.cardHolderName);
        EditText cardCVV = findViewById(R.id.cardCVV);
        EditText cardExpiryDate = findViewById(R.id.cardExpiryDate);

        String cardNum = sanitizeInput(cardNumber.getText().toString());
        String cardName = sanitizeInput(cardHolderName.getText().toString());
        String cardCvv = sanitizeInput(cardCVV.getText().toString());
        String cardExpDate = sanitizeInput(cardExpiryDate.getText().toString());

        if (cardNum.isEmpty() || cardName.isEmpty() || cardCvv.isEmpty() || cardExpDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all card details", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidCardNumber(cardNum)) {
            Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidCVV(cardCvv)) {
            Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidExpiryDate(cardExpDate)) {
            Toast.makeText(this, "Invalid expiry date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.length() == 16; // Example: 16-digit card number validation
    }

    private boolean isValidCVV(String cvv) {
        return cvv.length() == 3; // Example: 3-digit CVV validation
    }

    private boolean isValidExpiryDate(String expiryDate) {
        // Example: Simple validation for MM/YY format
        Pattern pattern = Pattern.compile("^(0[1-9]|1[0-2])/[0-9]{2}$");
        return pattern.matcher(expiryDate).matches();
    }

    private void handleSubmitOrder() {
        // Prepare order summary
        StringBuilder orderSummary = new StringBuilder("Order Summary:\n\n");
        List<CartItem> cartItemList = CartManager.getInstance().getCartItems();
        double subtotal = 0.0;

        for (CartItem item : cartItemList) {
            double price = item.getProduct().getPriceAsDouble();
            subtotal += item.getQuantity() * price;
            orderSummary.append(item.getProduct().getName())
                    .append(" - Quantity: ")
                    .append(item.getQuantity())
                    .append(" - Price: $")
                    .append(price)
                    .append("\n");
        }

        double tax = subtotal * TAX_RATE;
        double grandTotal = subtotal + tax;

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedSubtotal = decimalFormat.format(subtotal);
        String formattedTax = decimalFormat.format(tax);
        String formattedTotal = decimalFormat.format(grandTotal);

        orderSummary.append("\nSubtotal: $")
                .append(formattedSubtotal)
                .append("\nTax: $")
                .append(formattedTax)
                .append("\nTotal: $")
                .append(formattedTotal);

        // Clear cart items using CartManager
        CartManager.getInstance().clearCart();

        // Redirect to Thank You page
        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("order_summary", orderSummary.toString());
        startActivity(intent);
        finish(); // Optional: Finish this activity to remove it from the back stack
    }

}
