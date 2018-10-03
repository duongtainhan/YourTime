package com.example.duongtainhan555.yourtime.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.duongtainhan555.yourtime.R;

public class SetTimeFragment extends Fragment {

    View view;
    private CalendarView calendarView;
    private NewEventFragment newEventFragment;
    private CreatedEventsFragment createdEventsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settime, container, false);
        //Init View
        InitView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Event
        EventCalendar();
        super.onViewCreated(view, savedInstanceState);
    }

    private void EventCalendar() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String time = dayOfMonth + "/" + month + "/" + year;
                Toast.makeText(getContext(), time, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void InitView() {
        calendarView = view.findViewById(R.id.calendar);
        newEventFragment = new NewEventFragment();
        createdEventsFragment = new CreatedEventsFragment();
    }
}