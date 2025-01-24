package com.example.signalmodulatorapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class AudioActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_AUDIO = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private LineChart chartAM, chartFM, chartPM;
    private SeekBar seekBarAmplitude, seekBarFrequency, seekBarPhase;
    private Button btnLoadAudio;
    private float amplitude = 1.0f;
    private float frequency = 2.0f;
    private float phase = 0f;

    private float[] audioData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);


        // Referencias a las gráficas
        chartAM = findViewById(R.id.chartAM);
        chartFM = findViewById(R.id.chartFM);
        chartPM = findViewById(R.id.chartPM);

        seekBarAmplitude = findViewById(R.id.seekBarAmplitudeAudio);
        seekBarFrequency = findViewById(R.id.seekBarFrequencyAudio);
        seekBarPhase = findViewById(R.id.seekBarPhaseAudio);
        btnLoadAudio = findViewById(R.id.btnLoadAudio);

        setupCharts();
        setupSeekBars();
        btnLoadAudio.setOnClickListener(v -> {
            // Verificación de permisos para Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE_PERMISSION);
                } else {
                    openAudioPicker();
                }
            } else {
                // Código existente para versiones anteriores
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                } else {
                    openAudioPicker();
                }
            }
        });


        Button btnBackToMain = findViewById(R.id.btnBackToMain);

        // Configurar el clic del botón
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la MainActivity
                Intent intent = new Intent(AudioActivity.this, MainActivity.class);
                startActivity(intent);

                // Finalizar la actividad actual para no regresar con "atrás"
                finish();
            }
        });
    }

    private void setupCharts() {
        setupChart(chartAM, "Señal AM");
        setupChart(chartFM, "Señal FM");
        setupChart(chartPM, "Señal PM");
    }

    private void setupChart(LineChart chart, String descriptionText) {
        Description description = new Description();
        description.setText(descriptionText);
        chart.setDescription(description);
        chart.setNoDataText("No hay datos para mostrar");
    }

    private void setupSeekBars() {
        seekBarAmplitude.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amplitude = progress / 10f;
                updateCharts();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequency = progress / 10f;
                updateCharts();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarPhase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                phase = (float) Math.toRadians(progress);
                updateCharts();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            try {
                processAudioFile(audioUri);
                Toast.makeText(this, "Audio cargado correctamente", Toast.LENGTH_SHORT).show();
                updateCharts();
            } catch (IOException e) {
                Toast.makeText(this, "Error al cargar el audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processAudioFile(Uri audioUri) throws IOException {
        audioData = new float[360];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = (float) Math.sin(2 * Math.PI * i / 360);
        }
    }

    private void updateCharts() {
        if (audioData == null) return;

        List<Entry> amEntries = new ArrayList<>();
        List<Entry> fmEntries = new ArrayList<>();
        List<Entry> pmEntries = new ArrayList<>();

        for (int i = 0; i < audioData.length; i++) {
            float t = (float) i / 100;
            float modulator = audioData[i];

            amEntries.add(new Entry(t, amplitude * (1 + modulator) * (float) Math.cos(2 * Math.PI * frequency * t)));
            fmEntries.add(new Entry(t, (float) Math.cos(2 * Math.PI * frequency * t + modulator)));
            pmEntries.add(new Entry(t, (float) Math.cos(2 * Math.PI * frequency * t + phase * modulator)));
        }

        setChartData(chartAM, amEntries, "AM", Color.RED);
        setChartData(chartFM, fmEntries, "FM", Color.BLUE);
        setChartData(chartPM, pmEntries, "PM", Color.GREEN);
    }

    private void setChartData(LineChart chart, List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setDrawCircles(false);
        dataSet.setColor(color);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue concedido
                openAudioPicker();
            } else {
                // El permiso fue denegado
                Toast.makeText(this, "Permiso denegado. No se puede cargar el archivo de audio.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
