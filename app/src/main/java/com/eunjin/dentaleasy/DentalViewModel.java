package com.eunjin.dentaleasy;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eunjin.dentaleasy.ai.AIProvider;
import com.eunjin.dentaleasy.ai.GeminiAIProvider;
import com.eunjin.dentaleasy.data.LocalDentalDataSource;
import com.eunjin.dentaleasy.models.ExplanationResult;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DentalViewModel coordinates UI state and business logic.
 * Refactored to AndroidViewModel to access application context for network state checking.
 * Implements Separation of Concerns: delegates API calls to GeminiAIProvider and 
 * local fallback logic to LocalDentalDataSource.
 */
public class DentalViewModel extends AndroidViewModel {

    public static final String EMERGENCY_LITERACY_REFUSAL_MESSAGE =
            "This app is for dental literacy only and cannot provide emergency advice or diagnosis. "
                    + "If you have severe pain, swelling, bleeding, trauma, fever, infection, or other urgent symptoms, "
                    + "please contact a dentist or emergency health service immediately.";

    private static final String[] EMERGENCY_DENTAL_KEYWORDS = {
            "severe pain", "bleeding", "swelling", "trauma", "knocked out tooth",
            "knocked out", "infection", "pus", "fever", "emergency", "abscess"
    };

    private final AIProvider aiProvider;
    private final LocalDentalDataSource localDataSource;
    
    // LruCache to store recent searches, reducing network overhead
    private final LruCache<String, ExplanationResult> resultCache;

    private final MutableLiveData<ExplanationResult> currentExplanation = new MutableLiveData<>();
    private final MutableLiveData<ExplanationResult> categoryExplanation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private String currentCategory = "General Dentistry";

    public DentalViewModel(@NonNull Application application) {
        super(application);
        this.aiProvider = new GeminiAIProvider();
        this.localDataSource = new LocalDentalDataSource();
        // Cache up to 20 successful results to prevent repeated network calls
        this.resultCache = new LruCache<>(20);
    }

    public LiveData<ExplanationResult> getExplanation() {
        return currentExplanation;
    }

    public LiveData<ExplanationResult> getCategoryExplanation() {
        return categoryExplanation;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setCurrentCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            currentCategory = category;
        } else {
            currentCategory = "General Dentistry";
        }
    }

    /**
     * Used by CategoryDetailActivity
     */
    public void generateCategoryExplanation(String question) {
        isLoading.setValue(true);

        executorService.execute(() -> {
            ExplanationResult result = resolveSearchResult(question);
            new Handler(Looper.getMainLooper()).post(() -> {
                categoryExplanation.setValue(result);
                isLoading.setValue(false);
            });
        });
    }

    /**
     * Used by MainActivity
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

        String normalizedTerm = term.toLowerCase(Locale.US).trim();

        // 1. Safety Check: Intercept emergency queries
        if (containsEmergencyDentalKeywords(normalizedTerm)) {
            return new ExplanationResult(EMERGENCY_LITERACY_REFUSAL_MESSAGE);
        }

        // 2. Cache Check: Return cached result immediately (reduce network cost)
        ExplanationResult cachedResult = resultCache.get(normalizedTerm);
        if (cachedResult != null) {
            // Return a copy with CACHE_HIT status to avoid mutating original
            return new ExplanationResult(
                    cachedResult.getPlainEnglishExplanation(),
                    cachedResult.getUsuallyMeans(),
                    cachedResult.getAfterCareTip(),
                    cachedResult.getSource(),
                    cachedResult.getConfidence(),
                    ExplanationResult.Status.CACHE_HIT
            );
        }

        // 3. Network Pre-check: Avoid API call if device is offline
        if (!isNetworkAvailable()) {
            return localDataSource.findBestMatch(term, ExplanationResult.Status.OFFLINE_FALLBACK);
        }

        // 4. API Call: Request explanation from Gemini
        ExplanationResult aiResult = aiProvider.explainTerm(term);
        
        if (aiResult != null && aiResult.getStatus() == ExplanationResult.Status.AI_SUCCESS) {
            // Cache successful AI response
            resultCache.put(normalizedTerm, aiResult);
            return aiResult;
        }

        // 5. Fallback: If API fails, show local fallback
        return localDataSource.findBestMatch(term, ExplanationResult.Status.API_ERROR_FALLBACK);
    }

    private boolean containsEmergencyDentalKeywords(String normalized) {
        if (normalized.isEmpty()) return false;
        for (String keyword : EMERGENCY_DENTAL_KEYWORDS) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdownNow();
    }
}