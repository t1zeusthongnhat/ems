package com.example.expensemanagementstudent;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // PieChart
        PieChart pieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(50f, "Food"));
        pieEntries.add(new PieEntry(30f, "Shopping"));
        pieEntries.add(new PieEntry(70f, "Travelling"));
        pieEntries.add(new PieEntry(40f, "Entertainment"));
        pieEntries.add(new PieEntry(20f, "Medical"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(android.graphics.Color.BLACK);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK);
        pieChart.invalidate();

        Legend pieLegend = pieChart.getLegend();
        pieLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        // BarChart
        BarChart barChart = findViewById(R.id.barChart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 3000f)); // Income
        barEntries.add(new BarEntry(1, 2500f)); // Expense

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(new int[]{android.graphics.Color.BLUE, android.graphics.Color.RED});
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTextColor(android.graphics.Color.BLACK);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);

        // Chú thích
        Legend barLegend = barChart.getLegend();
        barLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        barLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        barLegend.setForm(Legend.LegendForm.CIRCLE);
        barLegend.setTextColor(android.graphics.Color.BLACK);
        

        barChart.invalidate();
    }
}
