package com.android.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.expensetracker.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set current date in the TextView
        String formattedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
        binding.dateTextView.setText(formattedDate);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.more) {
                View menuView = findViewById(R.id.more);
                showPopupMenu(menuView);
                return true;
            }
            return false;
        });

        // Set up FloatingActionButton to show the Add Transaction dialog
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> showAddTransactionDialog());
    }

    // PopupMenu code (for bottom navigation "more" button)
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        // Force icons to show using reflection
        try {
            Field mPopupField = PopupMenu.class.getDeclaredField("mPopup");
            mPopupField.setAccessible(true);
            Object mPopup = mPopupField.get(popup);
            Method setForceShowIcon = mPopup.getClass().getDeclaredMethod("setForceShowIcon", boolean.class);
            setForceShowIcon.invoke(mPopup, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.more_search) {
                Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.more_favorite) {
                Toast.makeText(this, "Favorites Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.more_settings) {
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    // Method to show the Add Transaction Dialog when FAB is clicked
    private void showAddTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_transaction, null);
        builder.setView(dialogView);

        // Initialize dialog views
        RadioButton rbIncome = dialogView.findViewById(R.id.rb_income);
        RadioButton rbExpense = dialogView.findViewById(R.id.rb_expense);
        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etAmount = dialogView.findViewById(R.id.et_amount);
        ImageButton btnSubmit = dialogView.findViewById(R.id.btn_submit);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle Submit button click
        btnSubmit.setOnClickListener(v -> {
            String type = rbIncome.isChecked() ? "Income" : "Expense";
            String title = etTitle.getText().toString().trim();
            String amount = etAmount.getText().toString().trim();

            if (!title.isEmpty() && !amount.isEmpty()) {
                saveTransaction(type, title, Double.parseDouble(amount));
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save transaction (placeholder implementation)
    private void saveTransaction(String type, String title, double amount) {
        // TODO: Save data to your database or list
        Toast.makeText(this, type + ": " + title + " - ₹" + amount, Toast.LENGTH_SHORT).show();
    }
}
