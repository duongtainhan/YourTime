package com.example.duongtainhan555.yourtime.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.duongtainhan555.yourtime.Adapter.ScheduleAdapter;
import com.example.duongtainhan555.yourtime.Constant;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ReportFragment extends Fragment {
    BarChart barChart;
    PieChart pieChart;
    View view;
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String idUser, toDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);
        //InitView
        InitView();
        return view;
    }

    private void InitView() {
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);
        //Init Firebase
        db = FirebaseFirestore.getInstance();
        GetIdUser();
        //Get calendar today
        GetCalendarToday();
        GetCountTimeOfToday();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CreateChart();
        super.onViewCreated(view, savedInstanceState);
    }

    private void GetIdUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
            idUser = firebaseUser.getUid();
    }

    private void CreateChart() {
        CreateBarChart();
        CreatePieChart();
    }

    private void CreateBarChart() {

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

    private void CreatePieChart() {
        //pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        //set animation
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(8, "Working time"));
        entries.add(new PieEntry(16, "Break time"));

        PieDataSet set = new PieDataSet(entries, "");
        set.setSliceSpace(3f);
        set.setSelectionShift(5f);
        set.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(set);
        data.setValueTextColor(R.color.text_pie_chart);
        data.setValueTextSize(25f);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void GetCalendarToday() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        toDay = format.format(calendar.getTime());
        Log.d("DATA_TODAY", toDay);
    }

    private void GetCountTimeOfToday() {

        ArrayList<Integer> timeOfToday = new ArrayList<>();
        final DocumentReference docRef = db.collection(idUser).document(toDay);


        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                int hour = 0;
                int min = 0;
                int sec = 0;
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(snapshot.getData()).entrySet()) {
                        Map<String, String> nestedData = (Map<String, String>) entry.getValue();
                        String status = nestedData.get(Constant.status);
                        assert status != null;
                        if (!status.isEmpty() && !status.equals(Constant.missedStatus) && !status.equals(Constant.notReadyStatus)) {
                            String[] separated = status.split(":");
                            int hourOfStatus = Integer.valueOf(separated[0]);
                            int minOfStatus = Integer.valueOf(separated[1]);
                            int secOfStatus = Integer.valueOf(separated[2]);
                            hour = hour + hourOfStatus;
                            min = min + minOfStatus;
                            sec = sec + secOfStatus;
                        }

                    }

                } else {
                    //Null
                    Log.d("DATA_TODAY", "NULL_DATA");
                }
                Log.d("DATA_TODAY",hour+":"+min+":"+sec);

            }
        });
        //timeOfToday.add(1);
        //return timeOfToday;
    }
}
