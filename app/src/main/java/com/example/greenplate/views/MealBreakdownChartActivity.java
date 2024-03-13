package com.example.greenplate.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.MealBreakdownViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MealBreakdownChartActivity extends AppCompatActivity {

    // Chart
    private PieChart pieChart;
    private TextView backToHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_meal_breakdown_chart_activity);
        pieChart = findViewById(R.id.mealChart);

        BottomNavigationView btm = findViewById(R.id.bottomNavigationView);
        btm.setOnNavigationItemSelectedListener(item -> {
            Intent intent = new Intent(MealBreakdownChartActivity.this, NavBarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int itemId = item.getItemId();
            intent.putExtra("NAVIGATION_ID", itemId);
            startActivity(intent);
            return true;
        });
        MealBreakdownViewModel viewModel = new ViewModelProvider(this)
                .get(MealBreakdownViewModel.class);

        // Observe the LiveData for pie chart entries
        viewModel.getPieChartEntries().observe(this, pieEntries -> {
            PieDataSet dataSet = new PieDataSet(pieEntries, "Meals Today");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setDrawValues(true);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setData(data);
            pieChart.invalidate(); // Refresh the pie chart with new data
        });

        // Fetch today's meals
        viewModel.fetchMealsForToday();
        backToHome = findViewById(R.id.back_to_home);

        backToHome.setOnClickListener(event -> {
            startActivity(new Intent(MealBreakdownChartActivity.this,
                    HomeFragment.class));
        });
    }

}