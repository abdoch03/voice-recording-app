package com.example.voice_memo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voice_memo.recorderlib.WaveRecorder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public class recording extends AppCompatActivity {

    private FloatingActionButton pauseBtn, doneBtn,deleteBtn;
    private TextView timerText;
    private WaveformView waveformView;

    private WaveRecorder waveRecorder;
    private String audioFilePath;

    private boolean isRecording = false;
    private boolean isPaused = false;

    private long startTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording);

        pauseBtn = findViewById(R.id.pauseBtn);
        doneBtn = findViewById(R.id.doneBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        timerText = findViewById(R.id.timerText);
        waveformView = findViewById(R.id.waveformView);

        audioFilePath = getOutputFilePath();

        setupWaveRecorder();

        pauseBtn.setOnClickListener(v -> {
            if (isRecording && !isPaused) {
                waveRecorder.pauseRecording();
                isPaused = true;
                pauseBtn.setImageResource(android.R.drawable.ic_media_play);
                Toast.makeText(this, "En pause", Toast.LENGTH_SHORT).show();
            } else if (isRecording && isPaused) {
                waveRecorder.resumeRecording();
                isPaused = false;
                pauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                Toast.makeText(this, "Reprise", Toast.LENGTH_SHORT).show();
            }
        });

        deleteBtn.setOnClickListener(v -> {
            if (isRecording) {
                waveRecorder.stopRecording();
                isRecording = false;
            }

            // Supprimer le fichier s’il existe
            File file = new File(audioFilePath);
            if (file.exists()) {
                file.delete();
            }

            Toast.makeText(this, "Enregistrement annulé", Toast.LENGTH_SHORT).show();

            // Retour à MainActivity sans résultat
            setResult(RESULT_CANCELED);
            finish();
        });

        doneBtn.setOnClickListener(v -> {
            stopRecording();
        });

        requestMicrophonePermission();
    }
    private void setupWaveRecorder() {
        waveRecorder = new WaveRecorder(audioFilePath);


        waveRecorder.setOnAmplitudeListener(new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer amplitude) {
                if (!isPaused) {
                    runOnUiThread(() -> waveformView.addAmplitude(amplitude));
                }
                return Unit.INSTANCE;
            }
        });
        waveRecorder.setOnTimeElapsed(new Function1<Long, Unit>() {
            @Override
            public Unit invoke(Long elapsedSeconds) {
                if (!isPaused) {
                    runOnUiThread(() -> {
                        int minutes = (int) (elapsedSeconds / 60);
                        int seconds = (int) (elapsedSeconds % 60);
                        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                    });
                }
                return Unit.INSTANCE;
            }
        });
    }




    private void startRecording() {
        try {
            waveRecorder.startRecording();
            isRecording = true;
            isPaused = false;
            pauseBtn.setImageResource(android.R.drawable.ic_media_pause);
            Toast.makeText(this, "Enregistrement démarré", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur d'enregistrement", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            waveRecorder.stopRecording();
            isRecording = false;
            Toast.makeText(this, "Enregistrement terminé", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("audioFilePath", audioFilePath);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
    private String getOutputFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File outputDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return outputDir.getAbsolutePath() + "/VoiceMemo_" + timeStamp + ".wav";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            waveRecorder.stopRecording();
        }
    }
    private void requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
        } else {
            startRecording();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Microphone permission is required to record", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }






}
