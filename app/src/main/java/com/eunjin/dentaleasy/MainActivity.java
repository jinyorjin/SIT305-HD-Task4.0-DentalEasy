package com.eunjin.dentaleasy;

import android.app.AlertDialog;
import android.content.SharedPreferences;
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
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.eunjin.dentaleasy.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_NAME = "dental_prefs";
    private static final String KEY_HISTORY_LIST = "history_list";
    private static final String KEY_SAVED_TOOTH_CODE = "saved_tooth_code";
    private static final String KEY_SAVED_TOOTH_INFO = "saved_tooth_info";
    private DentalViewModel viewModel;

    // UI Elements
    private EditText etTerm;
    private Button btnAnalyze;
    private Button btnViewHistory;
    private Button btnToggleDentalChart;
    private LinearLayout llResultContainer;
    private LinearLayout chartExpandableContainer;
    private TextView tvExplanation, tvUsuallyMeansTitle, tvUsuallyMeans, tvAfterCareTitle, tvAfterCare;
    private TextView tvDisclaimer, tvPrivacyNote;
    private TextView tvLoadingMessage;
    private TextView tvEmergencyTitle;
    private TextView tvToothInfo;
    private TextView tvResponseMeta;
    private static final String TOOTH_CLICK_TAG = "TOOTH_CLICK";
    private static final String[] TOOTH_CODES = {
            "17", "16", "15", "14", "13", "12", "11",
            "21", "22", "23", "24", "25", "26", "27",
            "37", "36", "35", "34", "33", "32", "31",
            "41", "42", "43", "44", "45", "46", "47"
    };
    private static final int TOOTH_TEXT_SIZE_SP = 16;
    private final Map<Integer, String> toothInfoByButtonId = new LinkedHashMap<>();

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
        findViewById(android.R.id.content).post(this::refreshToothLabels);
        loadSavedToothInfo();

        // 4. Set Disclaimers and Privacy Notes
        tvDisclaimer.setText(AppConstants.MEDICAL_DISCLAIMER);
        tvPrivacyNote.setText(AppConstants.PRIVACY_NOTE);

        // 5. Setup Generate Button Click
        btnAnalyze.setOnClickListener(v -> handleGenerateClick());
        btnViewHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnToggleDentalChart.setOnClickListener(v -> toggleDentalChartVisibility());
        findViewById(R.id.btnPostCare).setOnClickListener(v ->
                startActivity(new Intent(this, CareTipsActivity.class)));
        findViewById(R.id.btnXrayConcept).setOnClickListener(v ->
                findViewById(R.id.tvXrayConcept).setVisibility(View.VISIBLE));

        // 6. Observe ViewModel Results
        observeViewModel();
    }

    private void initViews() {
        etTerm = findViewById(R.id.etDentalTerm);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        tvDisclaimer = findViewById(R.id.tvDisclaimer);
        tvPrivacyNote = findViewById(R.id.tvPrivacyNote);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        tvEmergencyTitle = findViewById(R.id.tvEmergencyTitle);
        btnToggleDentalChart = findViewById(R.id.btnToggleDentalChart);
        chartExpandableContainer = findViewById(R.id.chartExpandableContainer);

        llResultContainer = findViewById(R.id.llResultContainer);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvUsuallyMeansTitle = findViewById(R.id.tvUsuallyMeansTitle);
        tvUsuallyMeans = findViewById(R.id.tvUsuallyMeans);
        tvAfterCareTitle = findViewById(R.id.tvAfterCareTitle);
        tvAfterCare = findViewById(R.id.tvAfterCare);
        tvToothInfo = findViewById(R.id.tvToothInfo);
        tvResponseMeta = findViewById(R.id.tvResponseMeta);
    }

    private void toggleDentalChartVisibility() {
        if (chartExpandableContainer.getVisibility() == View.VISIBLE) {
            chartExpandableContainer.setVisibility(View.GONE);
            btnToggleDentalChart.setText("Open Dental Chart");
        } else {
            chartExpandableContainer.setVisibility(View.VISIBLE);
            btnToggleDentalChart.setText("Hide Dental Chart");
        }
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
        toothInfoByButtonId.clear();
        toothInfoByButtonId.put(R.id.btnTooth17, "Tooth 17: Upper right second molar. Used for grinding food.");
        toothInfoByButtonId.put(R.id.btnTooth16, "Tooth 16: Upper right first molar. A main chewing tooth and commonly checked for decay.");
        toothInfoByButtonId.put(R.id.btnTooth15, "Tooth 15: Upper right second premolar. Helps with chewing and supports the bite.");
        toothInfoByButtonId.put(R.id.btnTooth14, "Tooth 14: Upper right first premolar. Helps tear and grind food.");
        toothInfoByButtonId.put(R.id.btnTooth13, "Tooth 13: Upper right canine. Helps tear food and guides the bite.");
        toothInfoByButtonId.put(R.id.btnTooth12, "Tooth 12: Upper right lateral incisor. Helps with biting and smile appearance.");
        toothInfoByButtonId.put(R.id.btnTooth11, "Tooth 11: Upper right central incisor. Front tooth used for biting and appearance.");

        toothInfoByButtonId.put(R.id.btnTooth21, "Tooth 21: Upper left central incisor. Front tooth used for biting and appearance.");
        toothInfoByButtonId.put(R.id.btnTooth22, "Tooth 22: Upper left lateral incisor. Helps with biting and smile appearance.");
        toothInfoByButtonId.put(R.id.btnTooth23, "Tooth 23: Upper left canine. Helps tear food and guides the bite.");
        toothInfoByButtonId.put(R.id.btnTooth24, "Tooth 24: Upper left first premolar. Helps tear and grind food.");
        toothInfoByButtonId.put(R.id.btnTooth25, "Tooth 25: Upper left second premolar. Helps with chewing and supports the bite.");
        toothInfoByButtonId.put(R.id.btnTooth26, "Tooth 26: Upper left first molar. A main chewing tooth and commonly checked for decay.");
        toothInfoByButtonId.put(R.id.btnTooth27, "Tooth 27: Upper left second molar. Used for grinding food.");

        toothInfoByButtonId.put(R.id.btnTooth37, "Tooth 37: Lower left second molar. Used for grinding food.");
        toothInfoByButtonId.put(R.id.btnTooth36, "Tooth 36: Lower left first molar. One of the main chewing teeth and commonly restored if decayed.");
        toothInfoByButtonId.put(R.id.btnTooth35, "Tooth 35: Lower left second premolar. Helps with chewing and supports the bite.");
        toothInfoByButtonId.put(R.id.btnTooth34, "Tooth 34: Lower left first premolar. Helps tear and grind food.");
        toothInfoByButtonId.put(R.id.btnTooth33, "Tooth 33: Lower left canine. Helps tear food and guides the bite.");
        toothInfoByButtonId.put(R.id.btnTooth32, "Tooth 32: Lower left lateral incisor. Helps with biting.");
        toothInfoByButtonId.put(R.id.btnTooth31, "Tooth 31: Lower left central incisor. Front lower tooth used for biting.");

        toothInfoByButtonId.put(R.id.btnTooth41, "Tooth 41: Lower right central incisor. Front lower tooth used for biting.");
        toothInfoByButtonId.put(R.id.btnTooth42, "Tooth 42: Lower right lateral incisor. Helps with biting.");
        toothInfoByButtonId.put(R.id.btnTooth43, "Tooth 43: Lower right canine. Helps tear food and guides the bite.");
        toothInfoByButtonId.put(R.id.btnTooth44, "Tooth 44: Lower right first premolar. Helps tear and grind food.");
        toothInfoByButtonId.put(R.id.btnTooth45, "Tooth 45: Lower right second premolar. Helps with chewing and supports the bite.");
        toothInfoByButtonId.put(R.id.btnTooth46, "Tooth 46: Lower right first molar. One of the main chewing teeth and commonly checked for decay.");
        toothInfoByButtonId.put(R.id.btnTooth47, "Tooth 47: Lower right second molar. Used for grinding food.");

        int index = 0;
        for (Map.Entry<Integer, String> entry : toothInfoByButtonId.entrySet()) {
            int buttonId = entry.getKey();
            String toothCode = TOOTH_CODES[index++];
            setupToothButton(buttonId, toothCode, entry.getValue());
        }
    }

    private void setupToothButton(int buttonId, String toothCode, String infoText) {
        View toothButton = findViewById(buttonId);
        if (toothButton == null) {
            Log.e(TOOTH_CLICK_TAG, "Missing tooth button ID: " + buttonId);
            return;
        }

        if (toothButton instanceof Button) {
            Button button = (Button) toothButton;
            button.setText(toothCode);
            button.setGravity(android.view.Gravity.CENTER);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, TOOTH_TEXT_SIZE_SP);
            button.setAllCaps(false);
            button.setTextColor(Color.parseColor("#1565C0"));
            button.setTypeface(Typeface.DEFAULT_BOLD);
            button.setPadding(0, 0, 0, 0);
            button.setContentDescription("Tooth " + toothCode);
        } else {
            Log.e(TOOTH_CLICK_TAG, "Tooth view is not a Button for ID: " + buttonId);
        }

        toothButton.setOnClickListener(v -> {
            resetToothButtonStyles();
            if (v instanceof Button) {
                Button selectedButton = (Button) v;
                selectedButton.setBackgroundResource(R.drawable.bg_tooth_selected);
                selectedButton.setBackgroundTintList(null);
                selectedButton.setTextColor(Color.WHITE);
                selectedButton.setTypeface(Typeface.DEFAULT_BOLD);
            }
            tvToothInfo.setText(infoText);
            showSaveToothDialog(toothCode, infoText);
            Log.d(TOOTH_CLICK_TAG, "Tooth " + toothCode + " clicked");
        });
    }

    private void refreshToothLabels() {
        for (Map.Entry<Integer, String> entry : toothInfoByButtonId.entrySet()) {
            View toothButtonView = findViewById(entry.getKey());
            if (toothButtonView instanceof Button) {
                Button button = (Button) toothButtonView;
                String infoText = entry.getValue();
                String toothCode = infoText.split(":")[0].replace("Tooth ", "").trim();
                button.setText(toothCode);
                button.setTextColor(Color.parseColor("#1565C0"));
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, TOOTH_TEXT_SIZE_SP);
                button.setAllCaps(false);
                button.setTypeface(Typeface.DEFAULT_BOLD);
                button.setPadding(0, 0, 0, 0);
            }
        }
    }

    private void showSaveToothDialog(String toothCode, String infoText) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to save this information?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    saveToothInfo(toothCode, infoText);
                    saveHistoryItem("tooth", "Tooth " + toothCode, infoText);
                    Toast.makeText(this, "Tooth " + toothCode + " saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveToothInfo(String toothCode, String infoText) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_SAVED_TOOTH_CODE, toothCode)
                .putString(KEY_SAVED_TOOTH_INFO, infoText)
                .apply();
    }

    private void loadSavedToothInfo() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedInfo = prefs.getString(KEY_SAVED_TOOTH_INFO, null);
        if (savedInfo != null && !savedInfo.trim().isEmpty()) {
            tvToothInfo.setText(savedInfo);
        }
    }

    private void showSaveSearchDialog(String query, String explanation) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to save this search?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    saveHistoryItem("search", query, explanation);
                    Toast.makeText(this, "Search saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveHistoryItem(String type, String title, String description) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String historyJson = prefs.getString(KEY_HISTORY_LIST, "[]");
        JSONArray historyArray;

        try {
            historyArray = new JSONArray(historyJson);
        } catch (JSONException e) {
            historyArray = new JSONArray();
        }

        JSONObject historyItem = new JSONObject();
        try {
            historyItem.put("type", type);
            historyItem.put("title", title);
            historyItem.put("description", description);
            historyItem.put("timestamp", System.currentTimeMillis());
            historyArray.put(historyItem);
        } catch (JSONException e) {
            Log.e("HISTORY_SAVE", "Failed to build history item", e);
            return;
        }

        prefs.edit().putString(KEY_HISTORY_LIST, historyArray.toString()).apply();
    }

    private void resetToothButtonStyles() {
        for (int buttonId : toothInfoByButtonId.keySet()) {
            View toothButtonView = findViewById(buttonId);
            if (toothButtonView instanceof Button) {
                Button toothButton = (Button) toothButtonView;
                toothButton.setBackgroundResource(R.drawable.bg_tooth_button);
                toothButton.setBackgroundTintList(null);
                toothButton.setTextColor(Color.parseColor("#1565C0"));
                toothButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, TOOTH_TEXT_SIZE_SP);
                toothButton.setTypeface(Typeface.DEFAULT_BOLD);
                toothButton.setPadding(0, 0, 0, 0);
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
            tvResponseMeta.setText("Source: " + result.getSource() + " | Confidence: " + result.getConfidence());

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

            String query = etTerm.getText().toString().trim();
            if (!query.isEmpty()) {
                showSaveSearchDialog(query, result.getPlainEnglishExplanation());
            }
        });
    }
}