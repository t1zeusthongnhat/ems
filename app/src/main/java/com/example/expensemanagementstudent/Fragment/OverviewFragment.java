package com.example.expensemanagementstudent.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.expensemanagementstudent.AllTransactionsActivity;
import com.example.expensemanagementstudent.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    private TextView monthYearDisplay;
    private Calendar calendar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        // Find the greeting TextView
        TextView greetingText = rootView.findViewById(R.id.greetingText);
        // Find the "See All" button
        TextView seeAllButton = rootView.findViewById(R.id.see_all_button);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Username"); // Default is "Username" if not found

        // Set the greeting text with the username
        greetingText.setText("Hi " + username + ",");

        // Initialize views
        monthYearDisplay = rootView.findViewById(R.id.month_year_display);
        ImageView previousMonth = rootView.findViewById(R.id.previous_month);
        ImageView nextMonth = rootView.findViewById(R.id.next_month);

        // Initialize calendar
        calendar = Calendar.getInstance();
        updateMonthYearDisplay();

        // Set up button listeners
        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move calendar to the previous month
                calendar.add(Calendar.MONTH, -1);
                updateMonthYearDisplay();
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move calendar to the next month
                calendar.add(Calendar.MONTH, 1);
                updateMonthYearDisplay();
            }
        });

        // Set click listener for "See All" button
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
        // Format and display the month and year
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        monthYearDisplay.setText(formattedDate);
    }

}