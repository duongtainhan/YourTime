package com.example.duongtainhan555.yourtime.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.duongtainhan555.yourtime.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {
    BarChart barChart;
    PieChart pieChart;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report,container,false);
        //InitView
        InitView();
        return view;
    }
    private void InitView()
    {
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CreateChart();
        super.onViewCreated(view, savedInstanceState);
    }

    private void CreateChart()
    {
        CreateBarChart();
        CreatePieChart();
    }
    private void CreateBarChart()
    {

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        // gap of 2f
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(6f, 60f));
        BarDataSet set = new BarDataSet(entries, "");
        //
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); // refresh
    }
    private void CreatePieChart()
    {
        //pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        //set animation
        pieChart.animateY(1000,Easing.EasingOption.EaseInOutCubic);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(8f, "Working time"));
        entries.add(new PieEntry(16f, "Break time"));

        PieDataSet set = new PieDataSet(entries,"");
        set.setSliceSpace(3f);
        set.setSelectionShift(5f);
        set.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(set);
        data.setValueTextColor(R.color.text_pie_chart);
        data.setValueTextSize(25f);
        pieChart.setData(data);
        pieChart.invalidate();
    }
}
