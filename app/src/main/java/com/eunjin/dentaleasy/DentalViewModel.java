package com.eunjin.dentaleasy;

import androidx.lifecycle.ViewModel;

public class DentalViewModel extends ViewModel {
    public String getEasyExplanation(String term) {
        if (term.toLowerCase().contains("root canal")) {
            return "Easy Explain: It's like cleaning the inside of a tiny straw inside your tooth to stop the pain.";
        }
        return "AI Analysis for: " + term + "\n\nThis procedure involves treating the inside of the tooth...";
    }
}