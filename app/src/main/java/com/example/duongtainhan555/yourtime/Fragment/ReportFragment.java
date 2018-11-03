package com.example.duongtainhan555.yourtime.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.duongtainhan555.yourtime.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class ReportFragment extends Fragment {
    BarChart barChart;
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CreateBarChart();
        super.onViewCreated(view, savedInstanceState);
    }

    private void CreateBarChart()
    {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(44f,0));
        barEntries.add(new BarEntry(88f,1));
        barEntries.add(new BarEntry(66f,2));
        barEntries.add(new BarEntry(12f,3));
        barEntries.add(new BarEntry(22f,4));
        barEntries.add(new BarEntry(33f,5));
        barEntries.add(new BarEntry(55f,6));
    }
}
