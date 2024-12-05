package com.example.expensemanagementstudent.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expensemanagementstudent.AllTransactionsActivity;
import com.example.expensemanagementstudent.CategoryActivity; // Import CategoryActivity
import com.example.expensemanagementstudent.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OverviewFragment extends Fragment {

    private TextView monthYearDisplay;
    private Calendar calendar;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        TextView greetingText = rootView.findViewById(R.id.greetingText);
        TextView seeAllButton = rootView.findViewById(R.id.see_all_button);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LinearLayout btnAddCategory = rootView.findViewById(R.id.btnAddCategory);

        // Thêm Intent chuyển đến CategoryActivity
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển đến CategoryActivity
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Username");

        greetingText.setText("Hi " + username + ",");

        monthYearDisplay = rootView.findViewById(R.id.month_year_display);
        ImageView previousMonth = rootView.findViewById(R.id.previous_month);
        ImageView nextMonth = rootView.findViewById(R.id.next_month);

        calendar = Calendar.getInstance();
        updateMonthYearDisplay();

        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                updateMonthYearDisplay();
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                updateMonthYearDisplay();
            }
        });

        seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllTransactionsActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMonthYearDisplay() {
        // Force the locale to English
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
        String formattedDate = dateFormat.format(calendar.getTime());
        monthYearDisplay.setText(formattedDate);
    }

}