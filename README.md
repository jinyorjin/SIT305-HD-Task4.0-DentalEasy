<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/a8848293-3c11-413b-824a-de236ee842f5" />


# DentalEasy: Project Objectives and Technical Approach

## Primary Purpose

The main goal of DentalEasy is to simplify dental communication. Clinical terms and post-operative instructions are often too complex for patients to understand. This app translates professional dental terminology into simple, plain English to improve patient understanding and reduce anxiety.

## Technical Scope

This project is developed using Java to provide a stable and reliable Android application environment. Java’s object-oriented structure was used to organise the code clearly and support safe handling of user input and medical-related content.

## Architecture (MVVM)

The app follows a basic MVVM (Model-View-ViewModel) structure.

- The UI (Activities) is separated from logic (ViewModel)
- This helps keep the code clean and easier to maintain
- ViewModel is used to preserve data during configuration changes such as screen rotation

This structure makes it easier to extend the app in the future.

## LLM Integration (Gemini)

This app integrates a Generative AI feature using Google Gemini API.

- When a user searches for a dental term, the input may be sent to the Gemini API
- The API generates a simplified explanation in plain language
- The response is then displayed in the app UI

This is a **hybrid approach**:
- Online: Gemini API generates responses
- Offline / fallback: The app provides local explanations when the API is unavailable

The AI feature improves the user experience by providing more flexible and natural explanations compared to fixed logic-based responses.

## Privacy and Data Handling

This app uses Google Gemini API for AI-generated explanations.

- User search input may be sent to Google's servers for processing
- No personal user data is stored permanently
- The API key is stored locally in `local.properties` and is not included in the repository

This app is designed for educational purposes only and does not provide medical diagnosis or treatment advice.

## UI/UX Design

The user interface is built using XML layouts and Material Design components such as CardView.

- Clean layout structure for readability
- Simple navigation between features (search, category, history)
- Information grouped into clear sections to reduce cognitive load

The goal was to create a simple and trustworthy interface suitable for a health-related app.

## Development Environment

- Android Studio (latest stable version)
- Java (JDK 17)
- Gradle build system
- Target SDK: 36 (Android 16)

The project uses version control (Git) to track iterative development.
## Safety Handling

Before sending user input to Gemini, the app checks for urgent dental keywords such as severe pain, bleeding, swelling, trauma, fever, infection, or abscess.

If urgent keywords are detected, the app does not send the text to Gemini. Instead, it displays a safety message advising the user to contact a dentist or emergency health service.

This is because DentalEasy is designed for dental literacy only and does not provide diagnosis, treatment, or emergency medical advice.

## Future Improvements

- Improve AI response quality using better prompt design
- Add caching for faster responses
- Expand local knowledge base for better offline support
- Improve accessibility (font scaling, contrast)
