package com.bcajans.inotech;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText inputValue;
    private Spinner fromUnitSpinner, toUnitSpinner;
    private Button convertBtn, copyBtn, shareBtn;
    private TextView resultText;

    private String[] units = {"KB", "MB", "GB", "TB", "PB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputValue = findViewById(R.id.inputValue);
        fromUnitSpinner = findViewById(R.id.fromUnitSpinner);
        toUnitSpinner = findViewById(R.id.toUnitSpinner);
        convertBtn = findViewById(R.id.convertBtn);
        copyBtn = findViewById(R.id.copyBtn);
        shareBtn = findViewById(R.id.shareBtn);
        resultText = findViewById(R.id.resultText);

        // Spinner adapters
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromUnitSpinner.setAdapter(adapter);
        toUnitSpinner.setAdapter(adapter);

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = inputValue.getText().toString().trim();
                if (inputStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double value = Double.parseDouble(inputStr);
                    String fromUnit = fromUnitSpinner.getSelectedItem().toString();
                    String toUnit = toUnitSpinner.getSelectedItem().toString();

                    double result = convert(value, fromUnit, toUnit);
                    resultText.setText(String.format("%.2f %s", result, toUnit));
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Copy button
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = resultText.getText().toString();
                if (result.isEmpty() || result.equals("Converted value will appear here")) {
                    Toast.makeText(MainActivity.this, "Nothing to copy", Toast.LENGTH_SHORT).show();
                    return;
                }
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Converted Value", result);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        // Share button
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = resultText.getText().toString();
                if (result.isEmpty() || result.equals("Converted value will appear here")) {
                    Toast.makeText(MainActivity.this, "Nothing to share", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, result + "\n\n— ByteBuddy");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }

    // Conversion logic
    private double convert(double value, String fromUnit, String toUnit) {
        // Base unit: KB
        double valueInKB = value;

        switch (fromUnit) {
            case "MB": valueInKB = value * 1024; break;
            case "GB": valueInKB = value * 1024 * 1024; break;
            case "TB": valueInKB = value * 1024 * 1024 * 1024L; break;
            case "PB": valueInKB = value * 1024 * 1024 * 1024 * 1024L; break;
        }

        switch (toUnit) {
            case "KB": return valueInKB;
            case "MB": return valueInKB / 1024;
            case "GB": return valueInKB / (1024 * 1024);
            case "TB": return valueInKB / (1024 * 1024 * 1024L);
            case "PB": return valueInKB / (1024 * 1024 * 1024 * 1024L);
            default: return valueInKB;
        }
    }
}
