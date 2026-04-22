package com.eunjin.dentaleasy.ai;

import com.eunjin.dentaleasy.models.ExplanationResult;

/**
 * Interface representing an AI provider capable of explaining dental terms.
 * Using an interface allows us to easily swap MockAIProvider with a real
 * on-device or cloud-based LLM engine in the future without changing the rest of the app.
 */
public interface AIProvider {
    /**
     * Generates a simple, patient-friendly explanation for a given dental term.
     * 
     * @param term The medical term to explain.
     * @return An ExplanationResult containing the simplified text and tips.
     */
    ExplanationResult explainTerm(String term);
}
