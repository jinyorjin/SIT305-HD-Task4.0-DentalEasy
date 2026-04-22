package com.eunjin.dentaleasy.ai;

import com.eunjin.dentaleasy.models.ExplanationResult;
import com.eunjin.dentaleasy.utils.AppConstants;

/**
 * A mock implementation of the AIProvider.
 * In a real app, this would send the term to an LLM.
 * For the prototype, it provides predefined, easy-to-understand responses.
 */
public class MockAIProvider implements AIProvider {

    @Override
    public ExplanationResult explainTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            return new ExplanationResult("Please enter a dental term to explain.");
        }

        String searchName = term.toLowerCase().trim();

        // 1. Safety Check: Intercept emergency keywords
        for (String keyword : AppConstants.EMERGENCY_KEYWORDS) {
            if (searchName.contains(keyword)) {
                return new ExplanationResult(AppConstants.EMERGENCY_WARNING);
            }
        }

        // 2. Mock AI Responses
        if (searchName.contains("root canal")) {
            return new ExplanationResult(
                    "A procedure to clean out the infected inside of a tooth (the pulp) and seal it up.",
                    "It means saving a badly decayed tooth instead of pulling it out.",
                    "Your tooth might be sensitive for a few days. Avoid chewing hard foods on that side."
            );
        } else if (searchName.contains("scaling") || searchName.contains("cleaning")) {
            return new ExplanationResult(
                    "A deep clean of your teeth to remove hardened plaque (tartar) that brushing can't get rid of.",
                    "It means keeping your gums healthy and preventing gum disease.",
                    "Your gums might bleed slightly when brushing for a day or two."
            );
        } else if (searchName.contains("implant")) {
            return new ExplanationResult(
                    "A tiny metal screw placed in your jawbone, topped with a fake tooth.",
                    "It means permanently replacing a missing tooth so it looks and feels natural.",
                    "You will need to maintain excellent oral hygiene to keep the implant healthy."
            );
        } else if (searchName.contains("wisdom tooth") || searchName.contains("extraction")) {
            return new ExplanationResult(
                    "A minor surgery to pull out the large teeth at the very back of your mouth.",
                    "It means removing a tooth that is stuck, infected, or crowding other teeth.",
                    "Bite gently on the gauze provided, apply an ice pack to your cheek, and eat soft foods."
            );
        } else if (searchName.contains("crown")) {
            return new ExplanationResult(
                    "A custom-made cap placed over a damaged tooth to cover, protect, and restore its shape.",
                    "It means reinforcing a weak tooth so you don't lose it.",
                    "Avoid sticky chewing gum or hard candy that could pull the crown off."
            );
        } else if (searchName.contains("filling")) {
            return new ExplanationResult(
                    "Removing decayed parts of a tooth (cavity) and filling the hole with a strong material.",
                    "It means fixing a small hole in your tooth to stop the decay from spreading.",
                    "Depending on the material, you might need to wait a few hours before eating."
            );
        } else if (searchName.contains("gum inflammation") || searchName.contains("gingivitis")) {
            return new ExplanationResult(
                    "Swollen, red, and irritated gums, often caused by plaque buildup.",
                    "It means you need to improve your brushing and flossing to stop gum disease early.",
                    "Floss daily and use a soft-bristled toothbrush. See a dentist for a professional clean."
            );
        } else if (searchName.contains("bone graft")) {
            return new ExplanationResult(
                    "Adding small bits of bone material to your jaw where the bone is too thin.",
                    "It means strengthening your jawbone so it can hold a dental implant.",
                    "Follow your dentist's instructions carefully. Swelling and minor pain are normal."
            );
        }

        // Default response for unknown terms
        return new ExplanationResult(
                "Our AI is not sure about this specific term right now.",
                "It might be a very specialized procedure or a typo.",
                "Try searching for something like 'root canal' or 'crown'."
        );
    }
}
