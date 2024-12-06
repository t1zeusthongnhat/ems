package com.example.expensemanagementstudent.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensemanagementstudent.R;
import com.example.expensemanagementstudent.db.ExpenseDB;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class InsightFragment extends Fragment {

    private PieChart pieChart, halfDonutChart;
    private LinearLayout categoryOverviewLayout;
    private ExpenseDB expenseDB;
    private int userId; // Lấy userId động
    private Spinner monthSpinner, yearSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Kết nối PieChart, Spinner và Layout
        pieChart = view.findViewById(R.id.pieChart);
        halfDonutChart = view.findViewById(R.id.halfDonutChart);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        categoryOverviewLayout = view.findViewById(R.id.categoryOverviewLayout);
        expenseDB = new ExpenseDB(requireContext());

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Kiểm tra nếu userId không có thì không làm gì thêm
        if (userId == -1) {
            // Xử lý userId invalid (ví dụ: show error)
            return;
        }

        // Thiết lập Adapter cho Spinner
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getMonths()
        );
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getYears()
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Lắng nghe sự kiện chọn tháng và năm
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = String.format("%02d", monthSpinner.getSelectedItemPosition() + 1); // Tháng dạng "01", "02", ...
                String selectedYear = yearSpinner.getSelectedItem().toString();
                updateChartAndOverview(selectedMonth, selectedYear, getMonths()[monthSpinner.getSelectedItemPosition()]);
                updateHalfDonutChart(selectedMonth, selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần xử lý
            }
        };
        monthSpinner.setOnItemSelectedListener(onItemSelectedListener);
        yearSpinner.setOnItemSelectedListener(onItemSelectedListener);

        // Khởi tạo dữ liệu cho tháng và năm đầu tiên
        monthSpinner.setSelection(11); // Mặc định tháng 1
        yearSpinner.setSelection(getCurrentYearPosition()); // Mặc định năm hiện tại
    }

    private void updateChartAndOverview(String month, String year, String monthName) {
        pieChart.setCenterText("Expenses for " + monthName + " " + year);
        pieChart.setCenterTextSize(18f);


        // Lấy dữ liệu từ database
        Cursor cursor = expenseDB.getExpenseByCategoryAndMonth(userId, month, year);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        categoryOverviewLayout.removeAllViews(); // Xóa dữ liệu cũ

        // Tiêu đề "Category Overview"
        TextView overviewTitle = new TextView(requireContext());
        overviewTitle.setText("Category Overview");
        overviewTitle.setTextColor(Color.BLACK);
        overviewTitle.setPadding(0, 16, 0, 8);
        categoryOverviewLayout.addView(overviewTitle);

        if (cursor.moveToFirst()) {
            float totalAmount = 0;
            do {
                totalAmount += cursor.getFloat(1);
            } while (cursor.moveToNext());

            cursor.moveToFirst(); // Reset lại cursor để sử dụng lại

            int[] availableColors = ColorTemplate.MATERIAL_COLORS; // Danh sách màu mặc định
            int colorIndex = 0;

            do {
                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);
                float percentage = (amount / totalAmount) * 100;

                // Thêm dữ liệu vào PieChart
                pieEntries.add(new PieEntry(percentage, category));
                colors.add(availableColors[colorIndex % availableColors.length]);
                colorIndex++;

                // Hiển thị danh mục và số tiền trong giao diện
                LinearLayout itemLayout = new LinearLayout(requireContext());
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setPadding(0, 8, 0, 8);

                View colorIndicator = new View(requireContext());
                colorIndicator.setLayoutParams(new LinearLayout.LayoutParams(24, 24));
                colorIndicator.setBackgroundColor(colors.get(colors.size() - 1)); // Màu tương ứng

                TextView categoryText = new TextView(requireContext());
                categoryText.setText(category);
                categoryText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                categoryText.setTextSize(14f);
                categoryText.setTextColor(Color.BLACK);

                TextView amountText = new TextView(requireContext());
                amountText.setText(formatCurrency(amount));
                amountText.setTextSize(14f);
                amountText.setTextColor(Color.BLACK);
                amountText.setGravity(View.TEXT_ALIGNMENT_VIEW_END);

                itemLayout.addView(colorIndicator);
                itemLayout.addView(categoryText);
                itemLayout.addView(amountText);
                categoryOverviewLayout.addView(itemLayout);
            } while (cursor.moveToNext());
        } else {
            TextView emptyText = new TextView(requireContext());
            emptyText.setText("No data available for this month.");
            emptyText.setTextSize(14f);
            emptyText.setTextColor(Color.GRAY);
            categoryOverviewLayout.addView(emptyText);
        }
        cursor.close();

        // Cập nhật PieChart
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false); // Ẩn nhãn trong biểu đồ
        pieChart.invalidate();
    }


    private void updateHalfDonutChart(String month, String year) {
        Cursor cursorIncome = expenseDB.getTotalByType(userId, month, year, 1);  // 0: income
        Cursor cursorExpense = expenseDB.getTotalByType(userId, month, year, 0);  // 1: expense

        float totalIncome = 0;
        float totalExpense = 0;

        if (cursorIncome.moveToFirst()) {
            totalIncome = cursorIncome.getFloat(0);
        }
        if (cursorExpense.moveToFirst()) {
            totalExpense = cursorExpense.getFloat(0);
        }

        cursorIncome.close();
        cursorExpense.close();

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalIncome, "Income"));
        entries.add(new PieEntry(totalExpense, "Expense"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        halfDonutChart.setData(data);

        halfDonutChart.setUsePercentValues(false);
        halfDonutChart.setDrawHoleEnabled(true);
        halfDonutChart.setHoleRadius(50f);
        halfDonutChart.setTransparentCircleRadius(55f);

        // Customize to make it half donut
        halfDonutChart.setMaxAngle(180f);  // HALF CHART
        halfDonutChart.setRotationAngle(180f);
        halfDonutChart.setCenterText("Income vs Expense");

        halfDonutChart.invalidate(); // refresh

        // Add comment logic
        TextView comparisonCommentText = requireView().findViewById(R.id.comparisonCommentText);
        String comment;
        if (totalIncome > totalExpense) {
            float savings = totalIncome - totalExpense;
            comment = "Great job! You saved " + formatCurrency(savings) + " this month.";
        } else if (totalIncome == totalExpense) {
            comment = "You broke even this month. Try to save more next time!";
        } else {
            float overspend = totalExpense - totalIncome;
            comment = "Careful! You overspent by " + formatCurrency(overspend) + " this month.";
        }
        comparisonCommentText.setText(comment);
    }


    private String formatCurrency(float amount) {
        // Định dạng số tiền với dấu phân cách hàng nghìn
        NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
        return formatter.format(amount);
    }

    private String[] getMonths() {
        return new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
    }

    private String[] getYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = currentYear - 10; // 10 năm trước
        String[] years = new String[11];
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(startYear + i);
        }
        return years;
    }

    private int getCurrentYearPosition() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = getYears();
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals(String.valueOf(currentYear))) {
                return i;
            }
        }
        return 0; // Mặc định là năm đầu tiên nếu không tìm thấy
    }
}