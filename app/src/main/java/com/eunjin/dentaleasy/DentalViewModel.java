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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DentalViewModel extends ViewModel {
    
    // The provider interface allows us to swap Mock with Real AI later without changing ViewModel
    private final AIProvider aiProvider;
    private static final String SAFETY_NOTE = "This is general information only. Please see a dentist for personal advice.";
    private static final String GEMINI_MODEL = "gemini-3-flash-preview";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String LOCAL_NO_MATCH_MESSAGE = "Sorry, I could not find local information for this term. Please try another dental term.";

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
        isLoading.setValue(true);

        executorService.execute(() -> {
            ExplanationResult result = resolveSearchResult(term);
            new Handler(Looper.getMainLooper()).post(() -> {
                currentExplanation.setValue(result);
                isLoading.setValue(false);
            });
        });
    }

    private ExplanationResult resolveSearchResult(String term) {
        if (term == null || term.trim().isEmpty()) {
            return new ExplanationResult("Please enter a dental term to explain.");
        }

        String geminiAnswer = requestGeminiExplanation(term);
        if (isUsefulGeminiResponse(geminiAnswer)) {
            return new ExplanationResult(
                    geminiAnswer.trim(),
                    "Source: Gemini AI response.",
                    SAFETY_NOTE
            );
        }

        LocalDentalInfo localMatch = findBestLocalMatch(term);
        if (localMatch != null) {
            return new ExplanationResult(
                    localMatch.description,
                    "Matched locally by title/keyword/description: " + localMatch.title,
                    SAFETY_NOTE
            );
        }

        return new ExplanationResult(
                LOCAL_NO_MATCH_MESSAGE,
                "No local fallback match found.",
                SAFETY_NOTE
        );
    }

    private boolean isUsefulGeminiResponse(String geminiAnswer) {
        if (geminiAnswer == null) return false;
        String cleaned = geminiAnswer.trim();
        if (cleaned.isEmpty()) return false;
        String lower = cleaned.toLowerCase(Locale.US);
        return !lower.contains("i cannot help with that")
                && !lower.contains("i can't help with that")
                && !lower.contains("unable to provide")
                && !lower.equals("null");
    }

    private LocalDentalInfo findBestLocalMatch(String query) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isEmpty()) return null;

        LocalDentalInfo best = null;
        int bestScore = 0;

        for (LocalDentalInfo item : getLocalKnowledgeBase()) {
            int score = scoreMatch(normalizedQuery, item);
            if (score > bestScore) {
                bestScore = score;
                best = item;
            }
        }
        return bestScore > 0 ? best : null;
    }

    private int scoreMatch(String query, LocalDentalInfo item) {
        int score = 0;
        String title = normalize(item.title);
        String description = normalize(item.description);

        if (title.equals(query)) score += 120;
        if (title.contains(query)) score += 70;
        if (description.contains(query)) score += 35;

        for (String keyword : item.keywords) {
            String normalizedKeyword = normalize(keyword);
            if (normalizedKeyword.equals(query)) score += 100;
            if (normalizedKeyword.contains(query) || query.contains(normalizedKeyword)) score += 65;
            if (description.contains(normalizedKeyword) && query.contains(normalizedKeyword)) score += 20;
        }

        return score;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.US).trim();
    }

    private List<LocalDentalInfo> getLocalKnowledgeBase() {
        List<LocalDentalInfo> list = new ArrayList<>();

        list.add(new LocalDentalInfo(
                "Root Canal Treatment",
                "RCT stands for Root Canal Treatment. It is a procedure to clean out infected pulp inside a tooth and seal it.",
                "rct", "root canal", "root canal treatment", "endodontic treatment", "pulp infection"
        ));
        list.add(new LocalDentalInfo(
                "Cavity",
                "A cavity is tooth decay caused by acids from plaque. Early treatment helps stop progression and protect tooth structure.",
                "cavity", "caries", "tooth decay", "decay", "hole in tooth"
        ));
        list.add(new LocalDentalInfo(
                "Dental Filling",
                "A filling restores tooth structure after decay is removed and helps prevent further damage.",
                "filling", "fill", "restoration", "composite", "amalgam"
        ));
        list.add(new LocalDentalInfo(
                "Dental Crown",
                "A dental crown is a cap that covers and protects a weak or damaged tooth, often after root canal treatment.",
                "crown", "cap", "tooth cap"
        ));
        list.add(new LocalDentalInfo(
                "Scaling and Cleaning",
                "Scaling removes tartar and plaque from teeth and gumline to maintain gum health.",
                "scaling", "cleaning", "deep clean", "prophylaxis", "gum cleaning"
        ));
        list.add(new LocalDentalInfo(
                "Dental Implant",
                "A dental implant is a titanium root replacement used to support a crown for a missing tooth.",
                "implant", "dental implant", "missing tooth replacement"
        ));
        list.add(new LocalDentalInfo(
                "Tooth 45",
                "Tooth 45: Lower right second premolar. Helps with chewing and supports the bite.",
                "45", "tooth 45", "lower right second premolar", "premolar"
        ));
        list.add(new LocalDentalInfo(
                "Molar Teeth",
                "Molars are major chewing teeth used for grinding food. They include first and second molars in each quadrant.",
                "molar", "molars", "grinding tooth", "chewing tooth", "first molar", "second molar"
        ));
        list.add(new LocalDentalInfo(
                "Tooth 46",
                "Tooth 46: Lower right first molar. One of the main chewing teeth and commonly checked for decay.",
                "46", "tooth 46", "lower right first molar", "molar"
        ));
        list.add(new LocalDentalInfo(
                "Tooth 47",
                "Tooth 47: Lower right second molar. Used for grinding food.",
                "47", "tooth 47", "lower right second molar", "molar"
        ));
        list.add(new LocalDentalInfo(
                "Tooth 36",
                "Tooth 36: Lower left first molar. One of the main chewing teeth and commonly restored if decayed.",
                "36", "tooth 36", "lower left first molar", "molar"
        ));
        list.add(new LocalDentalInfo(
                "Tooth 37",
                "Tooth 37: Lower left second molar. Used for grinding food.",
                "37", "tooth 37", "lower left second molar", "molar"
        ));

        return list;
    }

    private static class LocalDentalInfo {
        final String type = "search";
        final String title;
        final String description;
        final List<String> keywords;

        LocalDentalInfo(String title, String description, String... keywords) {
            this.title = title;
            this.description = description;
            this.keywords = Arrays.asList(keywords);
        }
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