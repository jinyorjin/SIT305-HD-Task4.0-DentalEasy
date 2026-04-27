package com.eunjin.dentaleasy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.eunjin.dentaleasy.utils.AppConstants;

public class MainActivity extends AppCompatActivity {
    private DentalViewModel viewModel;

    // UI Elements
    private EditText etTerm;
    private Button btnAnalyze;
    private LinearLayout llResultContainer;
    private TextView tvExplanation, tvUsuallyMeansTitle, tvUsuallyMeans, tvAfterCareTitle, tvAfterCare;
    private TextView tvDisclaimer, tvPrivacyNote;
    private TextView tvLoadingMessage;
    private TextView tvEmergencyTitle;
    private TextView tvToothInfo;
    private static final String TOOTH_CLICK_TAG = "TOOTH_CLICK";
    private static final int[] TOOTH_BUTTON_IDS = {
            R.id.btnTooth17, R.id.btnTooth16, R.id.btnTooth15, R.id.btnTooth14, R.id.btnTooth13, R.id.btnTooth12, R.id.btnTooth11,
            R.id.btnTooth21, R.id.btnTooth22, R.id.btnTooth23, R.id.btnTooth24, R.id.btnTooth25, R.id.btnTooth26, R.id.btnTooth27,
            R.id.btnTooth37, R.id.btnTooth36, R.id.btnTooth35, R.id.btnTooth34, R.id.btnTooth33, R.id.btnTooth32, R.id.btnTooth31,
            R.id.btnTooth41, R.id.btnTooth42, R.id.btnTooth43, R.id.btnTooth44, R.id.btnTooth45, R.id.btnTooth46, R.id.btnTooth47
    };

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
        setupCategoryCards();
        setupDentalChart();

        // 4. Set Disclaimers and Privacy Notes
        tvDisclaimer.setText(AppConstants.MEDICAL_DISCLAIMER);
        tvPrivacyNote.setText(AppConstants.PRIVACY_NOTE);

        // 5. Setup Generate Button Click
        btnAnalyze.setOnClickListener(v -> handleGenerateClick());

        // 6. Observe ViewModel Results
        observeViewModel();
    }

    private void initViews() {
        etTerm = findViewById(R.id.etDentalTerm);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        tvDisclaimer = findViewById(R.id.tvDisclaimer);
        tvPrivacyNote = findViewById(R.id.tvPrivacyNote);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        tvEmergencyTitle = findViewById(R.id.tvEmergencyTitle);

        llResultContainer = findViewById(R.id.llResultContainer);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvUsuallyMeansTitle = findViewById(R.id.tvUsuallyMeansTitle);
        tvUsuallyMeans = findViewById(R.id.tvUsuallyMeans);
        tvAfterCareTitle = findViewById(R.id.tvAfterCareTitle);
        tvAfterCare = findViewById(R.id.tvAfterCare);
        tvToothInfo = findViewById(R.id.tvToothInfo);
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

    private void setupCategoryCards() {
        findViewById(R.id.cardGeneralDentistry).setOnClickListener(v ->
                openCategoryDetail("General Dentistry"));
        findViewById(R.id.cardOrthodontics).setOnClickListener(v ->
                openCategoryDetail("Orthodontics"));
        findViewById(R.id.cardDentalImplants).setOnClickListener(v ->
                openCategoryDetail("Dental Implants"));
    }

    private void setupDentalChart() {
        setupToothClick(R.id.btnTooth17, "Tooth 17: Upper right second molar. Used for grinding food.");
        setupToothClick(R.id.btnTooth16, "Tooth 16: Upper right first molar. A main chewing tooth and commonly checked for decay.");
        setupToothClick(R.id.btnTooth15, "Tooth 15: Upper right second premolar. Helps with chewing and supports the bite.");
        setupToothClick(R.id.btnTooth14, "Tooth 14: Upper right first premolar. Helps tear and grind food.");
        setupToothClick(R.id.btnTooth13, "Tooth 13: Upper right canine. Helps tear food and guides the bite.");
        setupToothClick(R.id.btnTooth12, "Tooth 12: Upper right lateral incisor. Helps with biting and smile appearance.");
        setupToothClick(R.id.btnTooth11, "Tooth 11: Upper right central incisor. Front tooth used for biting and appearance.");

        setupToothClick(R.id.btnTooth21, "Tooth 21: Upper left central incisor. Front tooth used for biting and appearance.");
        setupToothClick(R.id.btnTooth22, "Tooth 22: Upper left lateral incisor. Helps with biting and smile appearance.");
        setupToothClick(R.id.btnTooth23, "Tooth 23: Upper left canine. Helps tear food and guides the bite.");
        setupToothClick(R.id.btnTooth24, "Tooth 24: Upper left first premolar. Helps tear and grind food.");
        setupToothClick(R.id.btnTooth25, "Tooth 25: Upper left second premolar. Helps with chewing and supports the bite.");
        setupToothClick(R.id.btnTooth26, "Tooth 26: Upper left first molar. A main chewing tooth and commonly checked for decay.");
        setupToothClick(R.id.btnTooth27, "Tooth 27: Upper left second molar. Used for grinding food.");

        setupToothClick(R.id.btnTooth37, "Tooth 37: Lower left second molar. Used for grinding food.");
        setupToothClick(R.id.btnTooth36, "Tooth 36: Lower left first molar. One of the main chewing teeth and commonly restored if decayed.");
        setupToothClick(R.id.btnTooth35, "Tooth 35: Lower left second premolar. Helps with chewing and supports the bite.");
        setupToothClick(R.id.btnTooth34, "Tooth 34: Lower left first premolar. Helps tear and grind food.");
        setupToothClick(R.id.btnTooth33, "Tooth 33: Lower left canine. Helps tear food and guides the bite.");
        setupToothClick(R.id.btnTooth32, "Tooth 32: Lower left lateral incisor. Helps with biting.");
        setupToothClick(R.id.btnTooth31, "Tooth 31: Lower left central incisor. Front lower tooth used for biting.");

        setupToothClick(R.id.btnTooth41, "Tooth 41: Lower right central incisor. Front lower tooth used for biting.");
        setupToothClick(R.id.btnTooth42, "Tooth 42: Lower right lateral incisor. Helps with biting.");
        setupToothClick(R.id.btnTooth43, "Tooth 43: Lower right canine. Helps tear food and guides the bite.");
        setupToothClick(R.id.btnTooth44, "Tooth 44: Lower right first premolar. Helps tear and grind food.");
        setupToothClick(R.id.btnTooth45, "Tooth 45: Lower right second premolar. Helps with chewing and supports the bite.");
        setupToothClick(R.id.btnTooth46, "Tooth 46: Lower right first molar. One of the main chewing teeth and commonly checked for decay.");
        setupToothClick(R.id.btnTooth47, "Tooth 47: Lower right second molar. Used for grinding food.");
    }

    private void setupToothClick(int buttonId, String infoText) {
        View toothButton = findViewById(buttonId);
        toothButton.setOnClickListener(v -> {
            resetToothButtonStyles();
            if (v instanceof Button) {
                Button selectedButton = (Button) v;
                selectedButton.setBackgroundResource(R.drawable.bg_tooth_selected);
                selectedButton.setBackgroundTintList(null);
                selectedButton.setTextColor(Color.WHITE);
            }
            tvToothInfo.setText(infoText);
            String toothCode = infoText.split(":")[0].replace("Tooth ", "").trim();
            Log.d(TOOTH_CLICK_TAG, "Tooth " + toothCode + " clicked");
        });
    }

    private void resetToothButtonStyles() {
        for (int buttonId : TOOTH_BUTTON_IDS) {
            View toothButtonView = findViewById(buttonId);
            if (toothButtonView instanceof Button) {
                Button toothButton = (Button) toothButtonView;
                toothButton.setBackgroundResource(R.drawable.bg_tooth_button);
                toothButton.setBackgroundTintList(null);
                toothButton.setTextColor(Color.parseColor("#1565C0"));
            }
        }
    }

    private void openCategoryDetail(String categoryName) {
        Intent intent = new Intent(this, CategoryDetailActivity.class);
        intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY_NAME, categoryName);
        startActivity(intent);
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
        // Observe loading state to update UI elements accordingly
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                // While loading is true: show loading text, disable button and input
                tvLoadingMessage.setVisibility(View.VISIBLE);
                btnAnalyze.setEnabled(false);
                etTerm.setEnabled(false);
            } else {
                // When loading is false: hide loading text, enable button and input
                tvLoadingMessage.setVisibility(View.GONE);
                btnAnalyze.setEnabled(true);
                etTerm.setEnabled(true);
            }
        });

        viewModel.getExplanation().observe(this, result -> {
            if (result == null) return;

            // Show the result container
            llResultContainer.setVisibility(View.VISIBLE);

            // Populate text
            tvExplanation.setText(result.getPlainEnglishExplanation());

            // Handle Error / Emergency Warning display logic
            if (result.isError()) {
                llResultContainer.setBackgroundResource(R.drawable.bg_emergency);
                tvEmergencyTitle.setVisibility(View.VISIBLE);
                tvExplanation.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                // Hide sub-sections
                tvUsuallyMeansTitle.setVisibility(View.GONE);
                tvUsuallyMeans.setVisibility(View.GONE);
                tvAfterCareTitle.setVisibility(View.GONE);
                tvAfterCare.setVisibility(View.GONE);
            } else {
                llResultContainer.setBackgroundResource(R.drawable.bg_card);
                tvEmergencyTitle.setVisibility(View.GONE);
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