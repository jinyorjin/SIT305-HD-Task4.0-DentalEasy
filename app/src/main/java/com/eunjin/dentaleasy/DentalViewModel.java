package com.eunjin.dentaleasy;

import androidx.lifecycle.ViewModel;

public class DentalViewModel extends ViewModel {
    public String getAiExplanation(String term) {
        if (term.toLowerCase().contains("bone grafting")) {
            return "🦷 Bone Grafting Explained\n\n" +
                    "What is it? New bone material is added to your jaw.\n\n" +
                    "Why is it needed? To strengthen the jaw for an implant.\n\n" +
                    "Checklist:\n- Apply ice pack (20 mins)\n- Avoid spicy food\n- Take pain relief";
        } else if (term.toLowerCase().contains("root canal")) {
            return "🦷 Root Canal Analysis\n\n" +
                    "A procedure to remove infected pulp and seal the area to stop pain.";
        }
        return "AI is analyzing: " + term + "... (More details coming in Task 4.2!)";
    }
}