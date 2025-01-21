package com.example.signalmodulatorapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChart lineChart;
    private SeekBar seekBarAmplitude, seekBarFrequency, seekBarPhase;
    private float amplitude = 10f;
    private float frequency = 1f;
    private float phase = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.chart);
        seekBarAmplitude = findViewById(R.id.seekBarAmplitude);
        seekBarFrequency = findViewById(R.id.seekBarFrequency);
        seekBarPhase = findViewById(R.id.seekBarPhase);

        setupChart();
        setupSeekBars();
    }

    private void setupChart() {
        Description description = new Description();
        description.setText("Señales Moduladas");
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = lineChart.getAxisLeft();
        // Ajustamos los límites para acomodar las tres señales separadas
        yAxis.setAxisMaximum(6f);  // Aumentado para dar espacio a las tres señales
        yAxis.setAxisMinimum(-6f);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // Opcional: Añadir líneas de cuadrícula para mejor visualización
        xAxis.setDrawGridLines(true);
        yAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.LTGRAY);
        yAxis.setGridColor(Color.LTGRAY);
    }

    private void setupSeekBars() {
        seekBarAmplitude.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amplitude = progress;
                updateChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequency = progress / 10f; // Para obtener decimales
                updateChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarPhase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                phase = (float) Math.toRadians(progress); // Convertir grados a radianes
                updateChart();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateChart() {
        List<Entry> amEntries = new ArrayList<>();
        List<Entry> fmEntries = new ArrayList<>();
        List<Entry> pmEntries = new ArrayList<>();

        float carrierFrequency = 10f;
        // Definimos offsets verticales para separar las señales
        float amOffset = 4f;   // AM estará arriba
        float fmOffset = 0f;   // FM en el medio
        float pmOffset = -4f;  // PM abajo

        for (int i = 0; i <= 360; i++) {
            float t = (float) i;
            float time = t / 10f;

            // AM con offset
            float carrier = (float) Math.sin(2 * Math.PI * carrierFrequency * time + phase);
            float modulator = (float) Math.sin(2 * Math.PI * frequency * time);
            float amSignal = (1 + (amplitude/10) * modulator) * carrier + amOffset;

            // FM con offset
            float modulationIndex = amplitude / 2;
            float fmSignal = (float) Math.sin(2 * Math.PI * carrierFrequency * time +
                    modulationIndex * Math.sin(2 * Math.PI * frequency * time)) + fmOffset;

            // PM con offset
            float pmSignal = (float) Math.sin(2 * Math.PI * carrierFrequency * time +
                    phase + (amplitude/5) * Math.sin(2 * Math.PI * frequency * time)) + pmOffset;

            amEntries.add(new Entry(time, amSignal));
            fmEntries.add(new Entry(time, fmSignal));
            pmEntries.add(new Entry(time, pmSignal));
        }

        LineDataSet amDataSet = new LineDataSet(amEntries, "AM (Superior)");
        amDataSet.setColor(Color.RED);
        amDataSet.setDrawCircles(false);
        amDataSet.setLineWidth(1.5f);

        LineDataSet fmDataSet = new LineDataSet(fmEntries, "FM (Centro)");
        fmDataSet.setColor(Color.BLUE);
        fmDataSet.setDrawCircles(false);
        fmDataSet.setLineWidth(1.5f);

        LineDataSet pmDataSet = new LineDataSet(pmEntries, "PM (Inferior)");
        pmDataSet.setColor(Color.GREEN);
        pmDataSet.setDrawCircles(false);
        pmDataSet.setLineWidth(1.5f);

        LineData lineData = new LineData(amDataSet, fmDataSet, pmDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
