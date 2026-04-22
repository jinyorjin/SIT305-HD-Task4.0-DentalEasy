package com.eunjin.dentaleasy.models;

/**
 * A data model representing the result of an AI explanation.
 * This structure keeps the explanation consistent and easy to display.
 */
public class ExplanationResult {
    private String plainEnglishExplanation;
    private String usuallyMeans;
    private String afterCareTip;
    private boolean isError;

    // Standard constructor for successful explanations
    public ExplanationResult(String plainEnglishExplanation, String usuallyMeans, String afterCareTip) {
        this.plainEnglishExplanation = plainEnglishExplanation;
        this.usuallyMeans = usuallyMeans;
        this.afterCareTip = afterCareTip;
        this.isError = false;
    }

    // Constructor for errors or safety warnings
    public ExplanationResult(String errorMessage) {
        this.plainEnglishExplanation = errorMessage;
        this.usuallyMeans = "";
        this.afterCareTip = "";
        this.isError = true;
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
}
