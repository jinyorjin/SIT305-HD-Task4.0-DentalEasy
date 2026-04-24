package com.eunjin.dentaleasy;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eunjin.dentaleasy.ai.AIProvider;
import com.eunjin.dentaleasy.ai.MockAIProvider;
import com.eunjin.dentaleasy.models.ExplanationResult;

public class DentalViewModel extends ViewModel {
    
    // The provider interface allows us to swap Mock with Real AI later without changing ViewModel
    private final AIProvider aiProvider;

    // LiveData ensures the UI updates only when data changes
    private final MutableLiveData<ExplanationResult> currentExplanation = new MutableLiveData<>();
    
    // LiveData to track the loading state (true when analyzing, false when done)
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public DentalViewModel() {
        this.aiProvider = new MockAIProvider();
    }

    public LiveData<ExplanationResult> getExplanation() {
        return currentExplanation;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
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
}