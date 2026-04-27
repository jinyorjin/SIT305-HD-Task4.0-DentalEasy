package com.eunjin.dentaleasy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class CategoryDetailActivity extends AppCompatActivity {
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    private static final String SAFETY_NOTE = "This is general information only. Please see a dentist for personal advice.";

    private EditText etQuestion;
    private TextView tvResult;
    private TextView tvResponseSource;
    private TextView tvCategoryTitle;
    private TextView tvCategoryDescription;
    private LinearLayout llKeyTermsContainer;
    private ProgressBar pbLoading;
    private Button btnAskAi;
    private String currentCategory;
    private DentalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        viewModel = new ViewModelProvider(this).get(DentalViewModel.class);
        initViews();
        setupCategoryContent();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        etQuestion = findViewById(R.id.etCategoryQuestion);
        tvResult = findViewById(R.id.tvCategoryResult);
        tvResponseSource = findViewById(R.id.tvResponseSource);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvCategoryDescription = findViewById(R.id.tvCategoryDescription);
        llKeyTermsContainer = findViewById(R.id.llKeyTermsContainer);
        pbLoading = findViewById(R.id.pbCategoryLoading);
        btnAskAi = findViewById(R.id.btnAskAi);
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnAskAi.setOnClickListener(v -> handleAskAi());
    }

    private void setupCategoryContent() {
        currentCategory = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        if (currentCategory == null || currentCategory.trim().isEmpty()) {
            currentCategory = "General Dentistry";
        }

        tvCategoryTitle.setText(currentCategory);
        tvCategoryDescription.setText(getCategoryDescription(currentCategory));
        populateKeyTerms(getKeyTermsForCategory(currentCategory));
        viewModel.setCurrentCategory(currentCategory);
    }

    private void populateKeyTerms(String[] terms) {
        llKeyTermsContainer.removeAllViews();
        for (String term : terms) {
            Button termButton = new Button(this);
            termButton.setText(term);
            termButton.setAllCaps(false);
            termButton.setBackgroundResource(R.drawable.bg_chip);
            termButton.setTextColor(getResources().getColor(android.R.color.black));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.bottomMargin = dpToPx(8);
            termButton.setLayoutParams(params);

            termButton.setOnClickListener(v -> {
                etQuestion.setText(term);
                tvResult.setText(buildMockExplanation(term));
            });

            llKeyTermsContainer.addView(termButton);
        }
    }

    private void handleAskAi() {
        String question = etQuestion.getText().toString().trim();
        if (question.isEmpty()) {
            Toast.makeText(this, "Please type a question first.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Activity stays UI-only: trigger work in ViewModel.
        tvResult.setText("Generating explanation...");
        viewModel.getExplanation(question);
    }

    private void showLoading(boolean loading) {
        pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAskAi.setEnabled(!loading);
    }

    private void observeViewModel() {
        // Loading state from ViewModel controls spinner and button state.
        viewModel.getIsLoading().observe(this, this::showLoading);

        // Result text comes from Gemini or fallback mock inside ViewModel.
        viewModel.getCategoryExplanation().observe(this, result -> {
            if (result != null && !result.trim().isEmpty()) {
                tvResult.setText(result);
            }
        });

        // If fallback was used, inform user without breaking flow.
        viewModel.getUsedMockFallback().observe(this, usedFallback -> {
            if (Boolean.TRUE.equals(usedFallback)) {
                tvResponseSource.setText("Response source: Mock fallback");
                Toast.makeText(this, "Using mock explanation (API unavailable).", Toast.LENGTH_SHORT).show();
            } else if (Boolean.FALSE.equals(usedFallback)) {
                tvResponseSource.setText("Response source: Gemini API");
            }
        });
    }

    private String getCategoryDescription(String categoryName) {
        switch (categoryName) {
            case "Orthodontics":
                return "Orthodontics focuses on straightening teeth and improving bite alignment using braces or clear aligners.";
            case "Dental Implants":
                return "Dental implants replace missing teeth with strong artificial roots and crowns for long-term function.";
            case "General Dentistry":
            default:
                return "General dentistry covers everyday dental care like check-ups, cleaning, fillings, and prevention.";
        }
    }

    private String[] getKeyTermsForCategory(String categoryName) {
        switch (categoryName) {
            case "Orthodontics":
                return new String[]{
                        "Braces",
                        "Invisalign",
                        "Clear aligners",
                        "Retainers",
                        "Overbite",
                        "Underbite",
                        "Crowding",
                        "Spacing",
                        "Bite correction",
                        "Orthodontic consultation",
                        "Adult orthodontics",
                        "Jaw surgery",
                        "Surgical orthodontics",
                        "Braces food care",
                        "Treatment time"
                };
            case "Dental Implants":
                return new String[]{
                        "Dental implant",
                        "Implant consultation",
                        "Implant treatment steps",
                        "Bone graft",
                        "Who needs bone grafting",
                        "Who may not need bone grafting",
                        "Bone volume",
                        "Osseointegration",
                        "Healing time",
                        "Implant crown",
                        "Implant bridge comparison",
                        "Multiple missing teeth",
                        "Implant surgery",
                        "Implant risks",
                        "Implant maintenance"
                };
            case "General Dentistry":
            default:
                return new String[]{
                        "Dental check-up",
                        "Scaling and cleaning",
                        "Fluoride treatment",
                        "Dental filling",
                        "Tooth decay",
                        "Tooth sensitivity",
                        "Plaque",
                        "Tartar",
                        "Gum disease",
                        "Bad breath",
                        "Root canal treatment",
                        "Dental crown",
                        "Tooth extraction",
                        "Preventive dentistry",
                        "Home oral care"
                };
        }
    }

    // Keeps existing instant mock preview when a key-term chip is tapped.
    private String buildMockExplanation(String question) {
        return question + " is a common topic in " + currentCategory + ". "
                + "This usually means your dentist will check your teeth and gums, explain simple treatment options, and guide daily care to protect your oral health.\n\n"
                + SAFETY_NOTE;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

}
