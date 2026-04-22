package com.eunjin.dentaleasy.utils;

/**
 * Utility class to hold constant strings like disclaimers and safety warnings.
 * Keeping these here prevents the UI code from becoming cluttered.
 */
public class AppConstants {
    
    // Privacy and Disclaimer notices
    public static final String MEDICAL_DISCLAIMER = "⚠️ Important: This app provides simple explanations for educational purposes only. It is NOT professional medical or dental advice. Always consult a qualified dentist for diagnosis and treatment.";
    public static final String PRIVACY_NOTE = "🔒 Privacy Note: Currently, all processing runs locally or via secure mock services on this device. No personal health data is saved or transmitted.";

    // Safety and Emergency rules
    public static final String[] EMERGENCY_KEYWORDS = {"severe bleeding", "can't breathe", "unconscious", "emergency", "choking", "excessive bleeding"};
    public static final String EMERGENCY_WARNING = "🚨 EMERGENCY WARNING 🚨\nIt appears you may be experiencing a medical emergency. Please call emergency services (e.g., 000, 911) or visit a hospital immediately. Do not rely on this app for urgent care.";

    public static final String[] EXAMPLE_TERMS = {
        "root canal", 
        "scaling and cleaning",
        "dental implant",
        "wisdom tooth removal",
        "crown",
        "filling",
        "gum inflammation",
        "bone graft"
    };
}
