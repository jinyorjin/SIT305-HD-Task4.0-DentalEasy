package com.eunjin.dentaleasy;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CareTipsActivity extends AppCompatActivity {

    private static final String[][] CARE_TIPS = {
            {
                    "What should I do after a tooth extraction?",
                    "Bite gently on the gauze as instructed. Avoid rinsing strongly, smoking, or drinking through a straw for the first 24 hours."
            },
            {
                    "Can I eat after dental treatment?",
                    "Wait until the numbness wears off before eating. Start with soft foods and avoid very hot, hard, or spicy food."
            },
            {
                    "How should I brush after treatment?",
                    "Brush gently around the treated area. Do not scrub the sore area too hard, especially on the first day."
            },
            {
                    "Is mild pain normal?",
                    "Mild soreness can be normal after treatment. If pain becomes severe or does not improve, contact a dentist."
            },
            {
                    "What should I do if there is bleeding?",
                    "A small amount of bleeding can happen after some procedures. If bleeding is heavy or does not stop, seek urgent dental advice."
            },
            {
                    "Can I exercise after treatment?",
                    "Avoid heavy exercise for the first 24 hours after extraction or surgery because it may increase bleeding or swelling."
            },
            {
                    "How do I reduce swelling?",
                    "Use a cold pack on the outside of the face for short periods if recommended. If swelling gets worse, contact a dentist."
            },
            {
                    "When should I take medicine?",
                    "Take medicine only as directed by your dentist or doctor. Do not take extra doses without professional advice."
            },
            {
                    "When should I call a dentist?",
                    "Contact a dentist if you have severe pain, swelling, fever, pus, heavy bleeding, or symptoms that are getting worse."
            },
            {
                    "How can I protect the treated tooth?",
                    "Avoid chewing hard food on the treated side until it feels comfortable or until your dentist confirms it is safe."
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_tips);

        Button btnBack = findViewById(R.id.btnCareBack);
        btnBack.setOnClickListener(v -> finish());

        LinearLayout llTipsContainer = findViewById(R.id.llCareTipsContainer);
        for (String[] tip : CARE_TIPS) {
            llTipsContainer.addView(createTipCard(tip[0], tip[1]));
        }
    }

    private View createTipCard(String question, String answer) {
        int padding = dpToPx(14);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_surface);
        card.setPadding(padding, padding, padding, padding);
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dpToPx(10);
        card.setLayoutParams(cardParams);

        TextView tvQuestion = new TextView(this);
        tvQuestion.setText(question);
        tvQuestion.setTextSize(15);
        tvQuestion.setTextColor(0xFF1F2D3D);
        tvQuestion.setTypeface(tvQuestion.getTypeface(), android.graphics.Typeface.BOLD);

        TextView tvIndicator = new TextView(this);
        tvIndicator.setText("Tap to view answer");
        tvIndicator.setTextSize(12);
        tvIndicator.setTextColor(0xFF1976D2);
        tvIndicator.setGravity(Gravity.START);
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        indicatorParams.topMargin = dpToPx(6);
        tvIndicator.setLayoutParams(indicatorParams);

        TextView tvAnswer = new TextView(this);
        tvAnswer.setText(answer);
        tvAnswer.setTextSize(14);
        tvAnswer.setTextColor(0xFF425466);
        tvAnswer.setLineSpacing(0f, 1.1f);
        tvAnswer.setVisibility(View.GONE);
        LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        answerParams.topMargin = dpToPx(8);
        tvAnswer.setLayoutParams(answerParams);

        card.addView(tvQuestion);
        card.addView(tvIndicator);
        card.addView(tvAnswer);

        card.setOnClickListener(v -> {
            boolean isOpen = tvAnswer.getVisibility() == View.VISIBLE;
            tvAnswer.setVisibility(isOpen ? View.GONE : View.VISIBLE);
            tvIndicator.setText(isOpen ? "Tap to view answer" : "Tap to hide answer");
        });

        return card;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
