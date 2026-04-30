package com.eunjin.dentaleasy.data;

import com.eunjin.dentaleasy.models.ExplanationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * LocalDentalDataSource manages the local fallback knowledge base.
 * This separates the data layer from the UI/ViewModel logic (Repository pattern).
 */
public class LocalDentalDataSource {

    private static final String SAFETY_NOTE = "This is general information only. Please see a dentist for personal advice.";
    private static final String LOCAL_NO_MATCH_MESSAGE = "Sorry, I could not find local information for this term. Please try another dental term.";

    public ExplanationResult findBestMatch(String term, ExplanationResult.Status status) {
        LocalDentalInfo localMatch = findBestLocalInfo(term);
        if (localMatch != null) {
            return new ExplanationResult(
                    localMatch.description,
                    "Matched locally by title/keyword/description: " + localMatch.title,
                    SAFETY_NOTE,
                    "Local",
                    "Medium",
                    status
            );
        }

        return new ExplanationResult(
                LOCAL_NO_MATCH_MESSAGE,
                "No local fallback match found.",
                SAFETY_NOTE,
                "Local",
                "Medium",
                status
        );
    }

    private LocalDentalInfo findBestLocalInfo(String query) {
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
}
