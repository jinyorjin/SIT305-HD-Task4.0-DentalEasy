package com.eunjin.dentaleasy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String PREF_NAME = "dental_prefs";
    private static final String KEY_HISTORY_LIST = "history_list";

    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        rvHistory = findViewById(R.id.rvHistory);
        Button btnClearHistory = findViewById(R.id.btnClearHistory);
        Button btnHistoryBack = findViewById(R.id.btnHistoryBack);

        btnHistoryBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter();
        rvHistory.setAdapter(historyAdapter);

        btnClearHistory.setOnClickListener(v -> {
            clearHistory();
            loadHistory();
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
        });

        loadHistory();
    }

    private void loadHistory() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String historyJson = prefs.getString(KEY_HISTORY_LIST, "[]");
        List<HistoryAdapter.HistoryItem> items = new ArrayList<>();

        try {
            JSONArray historyArray = new JSONArray(historyJson);
            for (int i = historyArray.length() - 1; i >= 0; i--) {
                JSONObject obj = historyArray.getJSONObject(i);
                String type = obj.optString("type", "");
                String title = obj.optString("title", "");
                String description = obj.optString("description", "");
                long timestamp = obj.optLong("timestamp", 0L);
                items.add(new HistoryAdapter.HistoryItem(type, title, description, timestamp));
            }
        } catch (JSONException e) {
            items.clear();
        }

        historyAdapter.submitItems(items);
    }

    private void clearHistory() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_HISTORY_LIST, "[]").apply();
    }
}
