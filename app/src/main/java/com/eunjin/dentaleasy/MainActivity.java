package com.eunjin.dentaleasy;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private DentalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(DentalViewModel.class);

        // XML의 ID와 연결 (빨간 줄 해결!)
        EditText etTerm = findViewById(R.id.etDentalTerm);
        Button btnAnalyze = findViewById(R.id.btnAnalyze);
        TextView tvExplanation = findViewById(R.id.tvExplanation);

        // 버튼 클릭 이벤트 처리
        btnAnalyze.setOnClickListener(v -> {
            String term = etTerm.getText().toString();
            String result = viewModel.getAiExplanation(term);
            tvExplanation.setText(result);
        });
    }
}