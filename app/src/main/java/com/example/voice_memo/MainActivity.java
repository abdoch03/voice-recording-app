package com.example.voice_memo;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voice_memo.R;
import com.example.voice_memo.VoiceMemo;
import com.example.voice_memo.VoiceMemoAdapter;
import com.example.voice_memo.VoiceMemoDBHelper;
import com.example.voice_memo.recording;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int RECORD_REQUEST_CODE = 1;
    FloatingActionButton startrecord;
    ListView recordingsList;

    VoiceMemoAdapter adapter;
    ArrayList<VoiceMemo> recordings;
    VoiceMemoDBHelper dbHelper;
    EditText searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startrecord = findViewById(R.id.floatingActionButton);
        recordingsList = findViewById(R.id.recordingsList);


        dbHelper = new VoiceMemoDBHelper(this);
        recordings = dbHelper.getAllMemos();

        adapter = new VoiceMemoAdapter(this, recordings);
        recordingsList.setAdapter(adapter);

        startrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, recording.class);
                startActivityForResult(intent, RECORD_REQUEST_CODE);
            }
        });
        recordingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VoiceMemo selectedMemo = recordings.get(position);
                showAudioPlayerDialog(selectedMemo.getPath(), selectedMemo.getName());
            }
        });
        adapter.setOnMemoActionListener(new VoiceMemoAdapter.OnMemoActionListener() {
            @Override
            public void onRename(VoiceMemo memo) {
                showRenameDialog(memo);
            }
            @Override
            public void onDelete(VoiceMemo memo) {
                confirmDeleteDialog(memo);
            }
        });
        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<VoiceMemo> filtered = new ArrayList<>();
               for(VoiceMemo memo:dbHelper.getAllMemos()){
                   if(memo.getName().toLowerCase().contains(s.toString().toLowerCase())){
                       filtered.add(memo);
                   }
               }
                adapter.clear();
                adapter.addAll(filtered);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void showRenameDialog(final VoiceMemo memo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Renommer le mémo");

        final EditText input = new EditText(MainActivity.this);
        input.setText(memo.getName());
        builder.setView(input);

        builder.setPositiveButton("Renommer", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                dbHelper.updateMemoName(memo.getName(), newName);
                memo.setName(newName);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Mémo renommé", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void confirmDeleteDialog(VoiceMemo memo) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le mémo")
                .setMessage("Voulez-vous vraiment supprimer ce mémo ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    dbHelper.deleteMemo(memo.getName());
                    recordings.remove(memo);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Mémo supprimé", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private MediaPlayer mediaPlayer;

    private void playAudio(String filePath) {
        // Stop any existing playback
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Playing: " + filePath, Toast.LENGTH_SHORT).show();

            // Release media player once playback is complete
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Toast.makeText(MainActivity.this, "Playback finished", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not play audio", Toast.LENGTH_SHORT).show();
        }
    }
    private void showAudioPlayerDialog(String filePath, String fileName) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_audio_player, null);
        WaveformView waveformView = dialogView.findViewById(R.id.waveformViewDialog);
        TextView audioFileName = dialogView.findViewById(R.id.audioFileName);
        TextView currentTimeText = dialogView.findViewById(R.id.currentTime);
        TextView totalTimeText = dialogView.findViewById(R.id.totalTime);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        TextView statusText = dialogView.findViewById(R.id.audioStatusText);
        final boolean[] isPaused = {false};

        audioFileName.setText(fileName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialog.show();

        MediaPlayer dialogPlayer = new MediaPlayer();
        Handler handler = new Handler();
        boolean[] isReleased = {false};

        try {
            dialogPlayer.setDataSource(filePath);
            dialogPlayer.prepare();

            int totalDuration = dialogPlayer.getDuration();
            progressBar.setMax(totalDuration);

            int totalMinutes = (totalDuration / 1000) / 60;
            int totalSeconds = (totalDuration / 1000) % 60;
            totalTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d", totalMinutes, totalSeconds));

            dialogPlayer.start();
            statusText.setText("LECTURE EN COURS");

            statusText.setOnClickListener(v -> {
                if (dialogPlayer.isPlaying()) {
                    dialogPlayer.pause();
                    isPaused[0] = true;
                    statusText.setText("EN PAUSE");
                } else {
                    dialogPlayer.start();
                    isPaused[0] = false;
                    statusText.setText("LECTURE EN COURS");
                }
            });

            Thread waveformThread = new Thread(() -> {
                while (!isReleased[0]) {
                    if (!isPaused[0] && dialogPlayer.isPlaying()) {
                        int fakeAmplitude = (int) (Math.random() * 10000);
                        runOnUiThread(() -> waveformView.addAmplitude(fakeAmplitude));
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

            });
            waveformThread.start();

            Runnable updateUI = new Runnable() {
                @Override
                public void run() {
                    if (!isReleased[0]) {
                        if (dialogPlayer != null && dialogPlayer.isPlaying() && !isPaused[0]) {
                            int current = dialogPlayer.getCurrentPosition();
                            progressBar.setProgress(current);

                            int minutes = (current / 1000) / 60;
                            int seconds = (current / 1000) % 60;
                            currentTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                        }
                        handler.postDelayed(this, 1000);
                    }
                }
            };

            handler.post(updateUI);

            dialogPlayer.setOnCompletionListener(mp -> {
                if (!isReleased[0]) {
                    isReleased[0] = true;
                    mp.release();
                }
                handler.removeCallbacks(updateUI);
                dialog.dismiss();
            });

            dialog.setOnDismissListener(d -> {
                if (!isReleased[0]) {
                    isReleased[0] = true;
                    if (dialogPlayer.isPlaying()) {
                        dialogPlayer.stop();
                    }
                    dialogPlayer.release();
                }
                handler.removeCallbacks(updateUI);
                if (waveformThread.isAlive()) {
                    waveformThread.interrupt();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la lecture", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            String newFile = data.getStringExtra("audioFilePath");
            if (newFile != null) {
                File file = new File(newFile);
                long duration = 0;
                try {
                    MediaPlayer tempPlayer = new MediaPlayer();
                    tempPlayer.setDataSource(file.getAbsolutePath());
                    tempPlayer.prepare();
                    duration = tempPlayer.getDuration();
                    tempPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                VoiceMemo memo = new VoiceMemo(file.getName(), file.getAbsolutePath(), duration, date);
                recordings.add(memo);
                dbHelper.insertMemo(memo.getName(), memo.getPath(), memo.getDuration(), memo.getDate());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}