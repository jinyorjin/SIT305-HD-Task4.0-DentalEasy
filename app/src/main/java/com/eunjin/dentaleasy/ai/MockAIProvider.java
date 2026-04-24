package com.eunjin.dentaleasy.ai;

import com.eunjin.dentaleasy.models.ExplanationResult;
import com.eunjin.dentaleasy.utils.AppConstants;

/**
 * A mock implementation of the AIProvider.
 * In a real app, this would send the term to an LLM.
 * For the prototype, it provides predefined, easy-to-understand responses including abbreviation support.
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

        // 2. Mock AI Responses with abbreviation support
        if (searchName.equals("rct") || searchName.contains("root canal")) {
            return new ExplanationResult(
                    "RCT stands for Root Canal Treatment. It is a procedure to clean out the infected inside of a tooth (the pulp) and seal it up.",
                    "Why it's needed: To save a badly decayed or infected tooth instead of pulling it out. \nHow it works: The dentist numbs the tooth, removes the infection, cleans the inside, and fills it to prevent future issues.",
                    "Your tooth might be sensitive for a few days. Avoid chewing hard foods on that side until a final crown is placed."
            );
        } else if (searchName.equals("opg")) {
            return new ExplanationResult(
                    "OPG stands for Orthopantomogram. It is a wide, panoramic dental X-ray.",
                    "Why it's needed: To give the dentist a full 2D view of all your teeth, jawbones, and joints in one single image. \nHow it works: A machine rotates around your head to capture the entire lower face.",
                    "There is no special care needed after an OPG. It is quick and painless."
            );
        } else if (searchName.equals("pa xray") || searchName.equals("pa x-ray") || searchName.contains("periapical")) {
            return new ExplanationResult(
                    "PA stands for Periapical X-ray. It is a specific X-ray that shows the entire tooth, from the crown down to the root tip.",
                    "Why it's needed: To detect problems below the gum line, such as root infections, abscesses, or bone loss. \nHow it works: A small sensor is placed inside your mouth next to the specific tooth.",
                    "No special care is needed. It is a routine diagnostic tool."
            );
        } else if (searchName.equals("la") || searchName.contains("local anaesthetic") || searchName.contains("anesthetic")) {
            return new ExplanationResult(
                    "LA stands for Local Anaesthetic. It is a medication used to temporarily numb a specific area of your mouth.",
                    "Why it's needed: To ensure you do not feel any pain during procedures like fillings, extractions, or root canals. \nHow it works: It temporarily blocks the nerve signals in that area.",
                    "Be careful not to bite your lip, cheek, or tongue while the area is still numb. Wait until the numbness wears off before eating hot foods."
            );
        } else if (searchName.equals("exo") || searchName.contains("extraction") || searchName.contains("pull")) {
            return new ExplanationResult(
                    "EXO stands for Extraction. It is a minor surgical procedure to pull a tooth out of its socket.",
                    "Why it's needed: When a tooth is too damaged to be saved, severely infected, or overcrowding other teeth (like wisdom teeth). \nHow it works: The dentist numbs the area and gently rocks the tooth loose.",
                    "Bite gently on the gauze provided to stop bleeding. Do not spit forcefully, smoke, or drink through a straw for 24 hours to protect the blood clot."
            );
        } else if (searchName.contains("scaling") || searchName.contains("cleaning")) {
            return new ExplanationResult(
                    "Scaling is a deep clean of your teeth to remove hardened plaque (tartar) that brushing can't get rid of.",
                    "Why it's needed: To keep your gums healthy and prevent gum disease (gingivitis or periodontitis). \nHow it works: The dentist uses special ultrasonic tools to vibrate and wash away the buildup.",
                    "Your gums might feel slightly tender or bleed slightly when brushing for a day or two. Continue to brush gently."
            );
        } else if (searchName.contains("crown") || searchName.contains("cap")) {
            return new ExplanationResult(
                    "A dental crown is a custom-made cap placed completely over a damaged or weak tooth.",
                    "Why it's needed: To protect, reinforce, and restore the shape of a tooth, often after a root canal or a large filling. \nHow it works: The tooth is filed down slightly, and a custom porcelain or metal cap is cemented on top.",
                    "Avoid very sticky foods like chewing gum or hard candy that could accidentally pull the crown off."
            );
        } else if (searchName.contains("implant")) {
            return new ExplanationResult(
                    "A dental implant is a tiny titanium screw placed in your jawbone, topped with an artificial tooth.",
                    "Why it's needed: To permanently replace a missing tooth so it looks, feels, and functions naturally. \nHow it works: The screw acts as an artificial root. Once the bone heals around it, a crown is attached to the top.",
                    "You will need to maintain excellent oral hygiene, including regular brushing and flossing, to keep the gums around the implant healthy."
            );
        } else if (searchName.contains("bone graft")) {
            return new ExplanationResult(
                    "A bone graft is a procedure that adds small bits of bone material to your jaw where the bone is too thin.",
                    "Why it's needed: To strengthen and build up your jawbone so it is thick enough to hold a dental implant securely. \nHow it works: Natural or synthetic bone particles are packed into the area and allowed to heal over several months.",
                    "Follow your dentist's post-surgery instructions carefully. Mild swelling and discomfort are normal. Eat soft foods while healing."
            );
        }

        // Default response for unknown terms
        return new ExplanationResult(
                "Our AI is not sure about this specific term right now.",
                "Why it's needed: It might be a very specialized procedure, an uncommon abbreviation, or a typo. \nHow it works: Try searching for a full term instead of an abbreviation.",
                "Try searching for something like 'RCT', 'Extraction', or 'Crown'."
        );
    }
}
