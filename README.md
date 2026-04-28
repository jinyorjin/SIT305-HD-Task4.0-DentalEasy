<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/a8848293-3c11-413b-824a-de236ee842f5" />


# DentalEasy: Project Objectives and Technical Approach

## Primary Purpose

The main goal of DentalEasy is to simplify dental communication. Clinical terms and post-operative instructions can often be difficult for patients to understand. This app translates professional dental terminology into plain English to help improve patient understanding and reduce anxiety.

## Technical Scope

This project was developed using Java to provide a stable and reliable Android environment. I used Java’s object-oriented structure to organise the code clearly and make it easier to manage user input and application logic, especially when handling health-related information.

## Architecture (MVVM)

The app follows a basic MVVM (Model-View-ViewModel) structure.

- The UI (Activities) is separated from the logic (ViewModel)
- This prevents the code from becoming difficult to maintain
- ViewModel helps preserve data during configuration changes such as screen rotation

This structure also makes the app easier to extend in the future.

## LLM Integration (Gemini)

This app integrates a Generative AI feature using the Google Gemini API.

When a user searches for a dental term, the input may be sent to the Gemini API, which generates a simplified explanation in plain language. The response is then displayed in the app.

The app uses a hybrid approach:
- Online: Gemini API generates dynamic responses
- Offline / fallback: The app provides local explanations if the API is unavailable

This approach improves flexibility compared to fixed logic-based responses and helps provide more natural explanations to the user.

## Privacy and Data Handling

This app uses the Google Gemini API for AI-generated explanations.

- User search input may be sent to Google's servers for processing
- No personal data is stored permanently within the app
- The API key is stored locally in `local.properties` and is not included in the repository

The app is designed for educational purposes only and does not provide medical diagnosis or treatment advice.

## UI/UX Design

The user interface is built using XML layouts and Material Design components such as CardView.

- Clean and simple layout for readability
- Easy navigation between features (search, category, history)
- Information grouped into sections to reduce cognitive load

The aim was to create a simple and trustworthy interface suitable for a healthcare-related app.

## Development Environment

- Android Studio (latest stable version)
- Java (JDK 17)
- Gradle build system
- Target SDK: 36 (Android 16)

Git was used to track development progress and manage version control.

## Safety Handling

Before sending user input to the Gemini API, the app checks for urgent dental keywords such as severe pain, bleeding, swelling, trauma, fever, infection, or abscess.

If any of these keywords are detected, the app does not send the request to Gemini. Instead, it displays a safety message advising the user to contact a dentist or emergency health service.

This ensures the app remains appropriate for dental literacy use and avoids providing unsafe or misleading advice.

## Android 16 Back Navigation Compatibility

DentalEasy supports modern Android back navigation behaviour.

The app enables `android:enableOnBackInvokedCallback="true"` in the AndroidManifest and uses `OnBackPressedDispatcher` with `OnBackPressedCallback` for secondary screens.

This ensures compatibility with newer Android navigation systems, including predictive back gestures, while maintaining consistent behaviour across all screens.

## Future Improvements

- Improve AI response quality through better prompt design
- Add caching to reduce response time
- Expand the local knowledge base for improved offline support
- Improve accessibility features such as font scaling and contrast
