package com.estudo.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;
import java.util.List;

public class OcrActivity extends Activity {

    Button btnCapture, btnGenerate;
    TextView txtResult, txtQuestions;
    EditText edtLevel;

    Bitmap bitmap;
    TessBaseAPI tessBaseAPI;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        btnCapture = findViewById(R.id.btnCapture);
        btnGenerate = findViewById(R.id.btnGenerate);
        txtResult = findViewById(R.id.txtResult);
        txtQuestions = findViewById(R.id.txtQuestions);
        edtLevel = findViewById(R.id.edtLevel);

        // Configurar Tesseract
        tessBaseAPI = new TessBaseAPI();
        String datapath = getFilesDir() + "/tesseract/";
        tessBaseAPI.init(datapath, "eng"); // usa inglês, pode trocar para "por" (português)

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String level = edtLevel.getText().toString().toLowerCase();
                String recognizedText = txtResult.getText().toString();
                List<String> questions = generateQuestions(recognizedText, level);
                txtQuestions.setText("");
                for (String q : questions) {
                    txtQuestions.append("- " + q + "\n");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            tessBaseAPI.setImage(bitmap);
            String recognizedText = tessBaseAPI.getUTF8Text();
            txtResult.setText(recognizedText);
        }
    }

    private List<String> generateQuestions(String text, String level) {
        List<String> questions = new ArrayList<>();
        String[] words = text.split(" ");

        if (level.equals("fácil")) {
            for (String w : words) {
                questions.add("O que significa: " + w + "?");
            }
        } else if (level.equals("médio")) {
            for (int i = 0; i < words.length - 1; i++) {
                questions.add("Explique a relação entre: " + words[i] + " e " + words[i + 1]);
            }
        } else if (level.equals("avançado")) {
            questions.add("Resuma o texto em suas próprias palavras.");
            questions.add("Quais ideias principais podem ser tiradas desse texto?");
            questions.add("Elabore 3 perguntas críticas sobre o tema do texto.");
        }

        return questions;
    }
                                }
