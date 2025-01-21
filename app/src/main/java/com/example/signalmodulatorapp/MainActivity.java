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
import android.os.Handler;
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
    private float amplitude = 1.0f;
    private float frequency = 2.0f;
    private float phase = 0f;
    private Handler handler = new Handler();
    private float timeOffset = 0f;
    private static final float TIME_INCREMENT = 0.05f;

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
        startSignalAnimation();
    }

    private void setupChart() {
        Description description = new Description();
        description.setText("Señales Moduladas");
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaximum(6f);
        yAxis.setAxisMinimum(-6f);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        xAxis.setDrawGridLines(true);
        yAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.LTGRAY);
        yAxis.setGridColor(Color.LTGRAY);
    }

    private void setupSeekBars() {
        seekBarAmplitude.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amplitude = progress / 10f;
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
                frequency = progress / 10f;
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
                phase = (float) Math.toRadians(progress);
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
        float amOffset = 4f;
        float fmOffset = 0f;
        float pmOffset = -4f;
        float modulationIndexFM = 2.0f;
        float modulationIndexPM = (float) Math.PI / 4;
        float integralModulator = 0f;
        float dt = 0.01f;

        for (int i = 0; i <= 360; i++) {
            float t = (float) i;
            float time = t / 100f;
            float currentTime = time + timeOffset;

            float modulator = (float) Math.sin(2 * Math.PI * frequency * currentTime);
            integralModulator += modulator * dt;

            float carrier = (float) Math.cos(2 * Math.PI * carrierFrequency * currentTime);

            float amSignal = amplitude * (1 + modulator) * carrier + amOffset;

            float fmSignal = (float) Math.cos(2 * Math.PI * carrierFrequency * currentTime +
                    modulationIndexFM * integralModulator) + fmOffset;

            float pmSignal = (float) Math.sin(2 * Math.PI * carrierFrequency * currentTime +
                    modulationIndexPM * modulator) + pmOffset;

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

    private void startSignalAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeOffset += TIME_INCREMENT;
                updateChart();
                handler.postDelayed(this, 350);
            }
        }, 50);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
