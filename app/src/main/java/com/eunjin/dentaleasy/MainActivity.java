package com.eunjin.dentaleasy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.eunjin.dentaleasy.utils.AppConstants;

public class MainActivity extends AppCompatActivity {
    private DentalViewModel viewModel;

    // UI Elements
    private EditText etTerm;
    private LinearLayout llResultContainer;
    private TextView tvExplanation, tvUsuallyMeansTitle, tvUsuallyMeans, tvAfterCareTitle, tvAfterCare;
    private TextView tvDisclaimer, tvPrivacyNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DentalViewModel.class);

        // 2. Bind UI Elements
        initViews();

        // 3. Setup RecyclerView for Example Terms
        setupRecyclerView();

        // 4. Set Disclaimers and Privacy Notes
        tvDisclaimer.setText(AppConstants.MEDICAL_DISCLAIMER);
        tvPrivacyNote.setText(AppConstants.PRIVACY_NOTE);

        // 5. Setup Generate Button Click
        Button btnAnalyze = findViewById(R.id.btnAnalyze);
        btnAnalyze.setOnClickListener(v -> handleGenerateClick());

        // 6. Observe ViewModel Results
        observeViewModel();
    }

    private void initViews() {
        etTerm = findViewById(R.id.etDentalTerm);
        tvDisclaimer = findViewById(R.id.tvDisclaimer);
        tvPrivacyNote = findViewById(R.id.tvPrivacyNote);

        llResultContainer = findViewById(R.id.llResultContainer);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvUsuallyMeansTitle = findViewById(R.id.tvUsuallyMeansTitle);
        tvUsuallyMeans = findViewById(R.id.tvUsuallyMeans);
        tvAfterCareTitle = findViewById(R.id.tvAfterCareTitle);
        tvAfterCare = findViewById(R.id.tvAfterCare);
    }

    private void setupRecyclerView() {
        RecyclerView rvExampleTerms = findViewById(R.id.rvExampleTerms);
        
        // Pass the example terms from Constants and listen for clicks
        ExampleTermAdapter adapter = new ExampleTermAdapter(AppConstants.EXAMPLE_TERMS, term -> {
            // When an item is clicked, populate the input box and trigger search
            etTerm.setText(term);
            handleGenerateClick();
        });
        
        rvExampleTerms.setAdapter(adapter);
    }

    private void handleGenerateClick() {
        String term = etTerm.getText().toString().trim();
        
        // Input validation
        if (term.isEmpty()) {
            Toast.makeText(this, "Please type a dental term first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hide result container while generating (useful if simulating network delay in future)
        llResultContainer.setVisibility(View.GONE);
        
        // Ask ViewModel to process the term
        viewModel.generateExplanation(term);
    }

    private void observeViewModel() {
        viewModel.getExplanation().observe(this, result -> {
            if (result == null) return;

            // Show the result container
            llResultContainer.setVisibility(View.VISIBLE);

            // Populate text
            tvExplanation.setText(result.getPlainEnglishExplanation());

            // Handle Error / Emergency Warning display logic
            if (result.isError()) {
                tvExplanation.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                // Hide sub-sections
                tvUsuallyMeansTitle.setVisibility(View.GONE);
                tvUsuallyMeans.setVisibility(View.GONE);
                tvAfterCareTitle.setVisibility(View.GONE);
                tvAfterCare.setVisibility(View.GONE);
            } else {
                tvExplanation.setTextColor(getResources().getColor(android.R.color.black));
                
                // Show sub-sections
                tvUsuallyMeansTitle.setVisibility(View.VISIBLE);
                tvUsuallyMeans.setVisibility(View.VISIBLE);
                tvUsuallyMeans.setText(result.getUsuallyMeans());

                tvAfterCareTitle.setVisibility(View.VISIBLE);
                tvAfterCare.setVisibility(View.VISIBLE);
                tvAfterCare.setText(result.getAfterCareTip());
            }
        });
    }
}