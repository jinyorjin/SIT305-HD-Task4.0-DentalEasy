package com.eunjin.dentaleasy;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private DentalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(DentalViewModel.class);

        EditText etTerm = findViewById(R.id.etDentalTerm);
        Button btnExplain = findViewById(R.id.btnExplain);
        TextView tvResult = findViewById(R.id.tvExplanation);

        btnExplain.setOnClickListener(v -> {
            String term = etTerm.getText().toString();
            String explanation = viewModel.getEasyExplanation(term);
            tvResult.setText(explanation);
        });
    }
}