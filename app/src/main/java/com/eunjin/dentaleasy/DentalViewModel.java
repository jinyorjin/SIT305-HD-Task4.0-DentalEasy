package com.eunjin.dentaleasy;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eunjin.dentaleasy.ai.AIProvider;
import com.eunjin.dentaleasy.ai.MockAIProvider;
import com.eunjin.dentaleasy.models.ExplanationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DentalViewModel extends ViewModel {
    
    // The provider interface allows us to swap Mock with Real AI later without changing ViewModel
    private final AIProvider aiProvider;
    private static final String SAFETY_NOTE = "This is general information only. Please see a dentist for personal advice.";
    private static final String GEMINI_MODEL = "gemini-3-flash-preview";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    // LiveData ensures the UI updates only when data changes
    private final MutableLiveData<ExplanationResult> currentExplanation = new MutableLiveData<>();
    private final MutableLiveData<String> categoryExplanation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> usedMockFallback = new MutableLiveData<>(false);
    
    // LiveData to track the loading state (true when analyzing, false when done)
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String currentCategory = "General Dentistry";

    public DentalViewModel() {
        this.aiProvider = new MockAIProvider();
    }

    public LiveData<ExplanationResult> getExplanation() {
        return currentExplanation;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Exposes whether latest category result came from fallback mock text.
    public LiveData<Boolean> getUsedMockFallback() {
        return usedMockFallback;
    }

    // UI can update this so the fallback still matches selected category text.
    public void setCurrentCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            currentCategory = category;
        } else {
            currentCategory = "General Dentistry";
        }
    }

    /**
     * Category screen API:
     * Triggers Gemini call and returns LiveData to observe result text.
     */
    public LiveData<String> getExplanation(String question) {
        isLoading.setValue(true);

        executorService.execute(() -> {
            String geminiAnswer = requestGeminiExplanation(question);
            if (geminiAnswer == null || geminiAnswer.trim().isEmpty()) {
                categoryExplanation.postValue(buildMockExplanation(question));
                usedMockFallback.postValue(true);
            } else {
                categoryExplanation.postValue(geminiAnswer);
                usedMockFallback.postValue(false);
            }
            isLoading.postValue(false);
        });

        return categoryExplanation;
    }

    public LiveData<String> getCategoryExplanation() {
        return categoryExplanation;
    }

    /**
     * Request an explanation for the given term.
     */
    public void generateExplanation(String term) {
        // Set loading state to true before starting to generate the response
        isLoading.setValue(true);

        // We use a Handler to simulate a network delay so the loading UI is visible.
        // Once the real AI API is integrated, it will naturally have a delay and 
        // run on a background thread, so this structure prepares us for that.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Fetch the result from the AI provider
            ExplanationResult result = aiProvider.explainTerm(term);
            
            // Post the result to the UI safely
            currentExplanation.setValue(result);
            
            // Set loading state to false after the response is ready
            isLoading.setValue(false);
        }, 1500); // 1.5 seconds simulated delay
    }

    private String requestGeminiExplanation(String question) {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return null;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(GEMINI_BASE_URL + GEMINI_MODEL + ":generateContent?key=" + apiKey);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();

            String prompt = "You are a dental learning assistant.\n"
                    + "Explain the following dental topic in simple, beginner-friendly language.\n"
                    + "Do NOT diagnose or provide personal treatment advice.\n"
                    + "Always include:\n"
                    + "'" + SAFETY_NOTE + "'\n\n"
                    + "Topic: " + question;

            parts.put(new JSONObject().put("text", prompt));
            content.put("parts", parts);
            contents.put(content);
            body.put("contents", contents);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                return null;
            }

            StringBuilder responseBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }

            return extractGeminiText(responseBuilder.toString());
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String extractGeminiText(String rawResponse) {
        try {
            JSONObject json = new JSONObject(rawResponse);
            JSONArray candidates = json.optJSONArray("candidates");
            if (candidates == null || candidates.length() == 0) {
                return null;
            }

            JSONObject firstCandidate = candidates.optJSONObject(0);
            if (firstCandidate == null) {
                return null;
            }

            JSONObject content = firstCandidate.optJSONObject("content");
            if (content == null) {
                return null;
            }

            JSONArray parts = content.optJSONArray("parts");
            if (parts == null || parts.length() == 0) {
                return null;
            }

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < parts.length(); i++) {
                JSONObject part = parts.optJSONObject(i);
                if (part != null) {
                    String piece = part.optString("text", "").trim();
                    if (!piece.isEmpty()) {
                        if (text.length() > 0) text.append("\n");
                        text.append(piece);
                    }
                }
            }
            return text.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private String buildMockExplanation(String question) {
        return question + " is a common topic in " + currentCategory + ". "
                + "This usually means your dentist will check your teeth and gums, explain simple treatment options, and guide daily care to protect your oral health.\n\n"
                + SAFETY_NOTE;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdownNow();
    }
}