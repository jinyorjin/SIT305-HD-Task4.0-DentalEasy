package com.eunjin.dentaleasy.ai;

import com.eunjin.dentaleasy.BuildConfig;
import com.eunjin.dentaleasy.models.ExplanationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * GeminiAIProvider handles API calls to Google Gemini.
 * It strictly focuses on network logic and JSON parsing, separating these concerns from ViewModel.
 */
public class GeminiAIProvider implements AIProvider {

    private static final String GEMINI_MODEL = "gemini-3-flash-preview";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String SAFETY_NOTE = "This is general information only. Please see a dentist for personal advice.";

    @Override
    public ExplanationResult explainTerm(String term) {
        String geminiAnswer = requestGeminiExplanation(term);

        if (isUsefulGeminiResponse(geminiAnswer)) {
            return new ExplanationResult(
                    geminiAnswer.trim(),
                    "Source: Gemini AI response.",
                    SAFETY_NOTE,
                    "Gemini",
                    "High",
                    ExplanationResult.Status.AI_SUCCESS
            );
        }
        
        // Return null if API fails or response is not useful,
        // allowing the ViewModel to handle the fallback logic.
        return null;
    }

    private String requestGeminiExplanation(String question) {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return null;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(GEMINI_BASE_URL + GEMINI_MODEL + ":generateContent?key=" + apiKey);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();

            String prompt = "You are a dental learning assistant.\n"
                    + "Explain the following dental topic in simple, beginner-friendly language.\n"
                    + "Do NOT diagnose or provide personal treatment advice.\n"
                    + "Always include:\n"
                    + "'" + SAFETY_NOTE + "'\n\n"
                    + "Topic: " + question;

            parts.put(new JSONObject().put("text", prompt));
            content.put("parts", parts);
            contents.put(content);
            body.put("contents", contents);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                return null;
            }

            StringBuilder responseBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }

            return extractGeminiText(responseBuilder.toString());
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String extractGeminiText(String rawResponse) {
        try {
            JSONObject json = new JSONObject(rawResponse);
            JSONArray candidates = json.optJSONArray("candidates");
            if (candidates == null || candidates.length() == 0) {
                return null;
            }

            JSONObject firstCandidate = candidates.optJSONObject(0);
            if (firstCandidate == null) {
                return null;
            }

            JSONObject content = firstCandidate.optJSONObject("content");
            if (content == null) {
                return null;
            }

            JSONArray parts = content.optJSONArray("parts");
            if (parts == null || parts.length() == 0) {
                return null;
            }

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < parts.length(); i++) {
                JSONObject part = parts.optJSONObject(i);
                if (part != null) {
                    String piece = part.optString("text", "").trim();
                    if (!piece.isEmpty()) {
                        if (text.length() > 0) text.append("\n");
                        text.append(piece);
                    }
                }
            }
            return text.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isUsefulGeminiResponse(String geminiAnswer) {
        if (geminiAnswer == null) return false;
        String cleaned = geminiAnswer.trim();
        if (cleaned.isEmpty()) return false;
        String lower = cleaned.toLowerCase(Locale.US);
        return !lower.contains("i cannot help with that")
                && !lower.contains("i can't help with that")
                && !lower.contains("unable to provide")
                && !lower.equals("null");
    }
}
