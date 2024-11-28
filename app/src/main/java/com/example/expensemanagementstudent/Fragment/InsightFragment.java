package com.example.expensemanagementstudent.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensemanagementstudent.R;
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

public class InsightFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // PieChart setup
        PieChart pieChart = view.findViewById(R.id.pieChart);

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

        // BarChart setup
        BarChart barChart = view.findViewById(R.id.barChart);

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

        // Legend for BarChart
        Legend barLegend = barChart.getLegend();
        barLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        barLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        barLegend.setForm(Legend.LegendForm.CIRCLE);
        barLegend.setTextColor(android.graphics.Color.BLACK);

        barChart.invalidate();
    }
}
