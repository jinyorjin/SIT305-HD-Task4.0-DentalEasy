package com.eunjin.dentaleasy;

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

    public DentalViewModel() {
        this.aiProvider = new MockAIProvider();
    }

    public LiveData<ExplanationResult> getExplanation() {
        return currentExplanation;
    }

    /**
     * Request an explanation for the given term.
     */
    public void generateExplanation(String term) {
        // Fetch the result from the AI provider
        ExplanationResult result = aiProvider.explainTerm(term);
        // Post the result to the UI safely
        currentExplanation.setValue(result);
    }
}