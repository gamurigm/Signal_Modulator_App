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
        description.setText("Señales");
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaximum(50f);
        yAxis.setAxisMinimum(-50f);

        updateChart();
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

        for (int i = 0; i <= 360; i++) {
            float t = (float) i;
            float time = t / 10f; // Escalar tiempo para más puntos
            float amSignal = amplitude * (float) Math.sin(2 * Math.PI * frequency * time + phase);
            float fmSignal = amplitude * (float) Math.sin(2 * Math.PI * (frequency + Math.sin(2 * Math.PI * 0.1 * time)) * time);
            float pmSignal = amplitude * (float) Math.sin(2 * Math.PI * frequency * time + (float) Math.sin(phase * time));

            amEntries.add(new Entry(time, amSignal));
            fmEntries.add(new Entry(time, fmSignal));
            pmEntries.add(new Entry(time, pmSignal));
        }

        LineDataSet amDataSet = new LineDataSet(amEntries, "AM");
        amDataSet.setColor(Color.RED);
        amDataSet.setDrawCircles(false);

        LineDataSet fmDataSet = new LineDataSet(fmEntries, "FM");
        fmDataSet.setColor(Color.BLUE);
        fmDataSet.setDrawCircles(false);

        LineDataSet pmDataSet = new LineDataSet(pmEntries, "PM");
        pmDataSet.setColor(Color.GREEN);
        pmDataSet.setDrawCircles(false);

        LineData lineData = new LineData(amDataSet, fmDataSet, pmDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
