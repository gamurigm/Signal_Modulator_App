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
    private List<String> xValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.chart);
        Description description = new Description();
        description.setText("Seniales");
        description.setPosition(150f, 15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);
        xValues = Arrays.asList("0", "1", "2", "3");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(4);
        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaximum(50f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(Color.BLACK);

        List<Entry> entries1 = new ArrayList<>();

        entries1.add(new Entry(0, 10f));
        entries1.add(new Entry(1, 10f));
        entries1.add(new Entry(2, 15f));
        entries1.add(new Entry(3, 45f));

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(0, 5f));
        entries2.add(new Entry(1, 15f));
        entries2.add(new Entry(2, 25f));
        entries2.add(new Entry(3, 30f));


        LineDataSet dataSet1 = new LineDataSet(entries1, "FM");
        dataSet1.setColor(Color.BLUE);


        LineDataSet dataSet2 = new LineDataSet(entries2, "AM");
        dataSet2.setColor(Color.BLACK);


        LineData lineData = new LineData(dataSet1,dataSet2);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}