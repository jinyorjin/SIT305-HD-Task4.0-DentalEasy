package com.eunjin.dentaleasy.models;

/**
 * A data model representing the result of an AI explanation.
 * This structure keeps the explanation consistent and easy to display.
 */
public class ExplanationResult {
    public enum Status {
        AI_SUCCESS,
        CACHE_HIT,
        LOCAL_FALLBACK,
        OFFLINE_FALLBACK,
        SAFETY_REFUSAL,
        API_ERROR_FALLBACK
    }

    private String plainEnglishExplanation;
    private String usuallyMeans;
    private String afterCareTip;
    private boolean isError;
    private String source;
    private String confidence;
    private Status status;

    // Standard constructor for successful explanations
    public ExplanationResult(String plainEnglishExplanation, String usuallyMeans, String afterCareTip) {
        this.plainEnglishExplanation = plainEnglishExplanation;
        this.usuallyMeans = usuallyMeans;
        this.afterCareTip = afterCareTip;
        this.isError = false;
        this.source = "Local";
        this.confidence = "Medium";
        this.status = Status.LOCAL_FALLBACK;
    }

    // Constructor for errors or safety warnings
    public ExplanationResult(String errorMessage) {
        this.plainEnglishExplanation = errorMessage;
        this.usuallyMeans = "";
        this.afterCareTip = "";
        this.isError = true;
        this.source = "Safety";
        this.confidence = "Low";
        this.status = Status.SAFETY_REFUSAL;
    }

    // Constructor for successful explanations with explicit source/confidence and status
    public ExplanationResult(
            String plainEnglishExplanation,
            String usuallyMeans,
            String afterCareTip,
            String source,
            String confidence,
            Status status
    ) {
        this.plainEnglishExplanation = plainEnglishExplanation;
        this.usuallyMeans = usuallyMeans;
        this.afterCareTip = afterCareTip;
        this.isError = false;
        this.source = source == null ? "Local" : source;
        this.confidence = confidence == null ? "Medium" : confidence;
        this.status = status == null ? Status.LOCAL_FALLBACK : status;
    }

    public String getPlainEnglishExplanation() {
        return plainEnglishExplanation;
    }

    public String getUsuallyMeans() {
        return usuallyMeans;
    }

    public String getAfterCareTip() {
        return afterCareTip;
    }

    public boolean isError() {
        return isError;
    }

    public String getSource() {
        return source;
    }

    public String getConfidence() {
        return confidence;
    }

    public Status getStatus() {
        return status;
    }
}
