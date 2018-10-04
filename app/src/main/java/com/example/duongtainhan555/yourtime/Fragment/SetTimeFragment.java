package com.example.duongtainhan555.yourtime.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
import com.example.duongtainhan555.yourtime.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetTimeFragment extends Fragment {

    View view;
    private CalendarView calendarView;
    private FloatingActionButton floatingActionButton;
    private Dialog dialog;
    private LinearLayout linearCreate;
    private TextView txtDate;
    private Calendar calendar;
    private TextView txtDateDialog;
    private LinearLayout linearStartTime;
    private LinearLayout linearEndTime;
    private TextView txtTimeStart;
    private TextView txtTimeEnd;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String idUser;
    private ScheduleItem newSchedule;
    private String dateMemory;
    private EditText edNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settime, container, false);

        //Init View
        InitView();
        GetIdUser();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Event
        EventCalendar();
        EventFloatingButton();
        super.onViewCreated(view, savedInstanceState);
    }

    private void InitView() {
        calendarView = view.findViewById(R.id.calendar);
        floatingActionButton = view.findViewById(R.id.floatingButton);
        txtDate = view.findViewById(R.id.txtDate);
        SetDate(0, 0, 0);
        db = FirebaseFirestore.getInstance();
    }

    private void GetIdUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
            idUser = firebaseUser.getUid();
    }

    private void SetData(ScheduleItem scheduleItem) {

        Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put("EndTime", scheduleItem.getTimeEnd());
        nestedData.put("Note", scheduleItem.getNote());
        nestedData.put("Status", scheduleItem.getStatus());

        docData.put(scheduleItem.getTimeStart(), nestedData);

        db.collection(idUser).document(scheduleItem.getDate())
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AAA", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error writing document", e);
                    }
                });

    }
    private void InsertData(ScheduleItem scheduleItem) {

        Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put("EndTime", scheduleItem.getTimeEnd());
        nestedData.put("Note", scheduleItem.getNote());
        nestedData.put("Status", scheduleItem.getStatus());

        docData.put(scheduleItem.getTimeStart(), nestedData);

        db.collection(idUser).document(scheduleItem.getDate())
                .update(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AAA", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error writing document", e);
                    }
                });

    }

    private void EventCalendar() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SetDate(year, month, dayOfMonth);
            }
        });
    }

    private void SetDate(int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("d MMMM yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        if (year != 0) {
            calendar.set(year, month, dayOfMonth);
        }
        String date = formatDate.format(calendar.getTime());
        dateMemory = format.format(calendar.getTime());
        txtDate.setText(date);
    }

    private void EventFloatingButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_set_time);
                linearCreate = dialog.findViewById(R.id.linearCreate);
                txtDateDialog = dialog.findViewById(R.id.txtDateDialog);
                //set text header Date
                txtDateDialog.setText(txtDate.getText());
                //
                txtTimeStart = dialog.findViewById(R.id.txtTimeStart);
                txtTimeEnd = dialog.findViewById(R.id.txtTimeEnd);
                linearStartTime = dialog.findViewById(R.id.linearStartTime);
                linearEndTime = dialog.findViewById(R.id.linearEndTime);
                edNote = dialog.findViewById(R.id.edNote);
                //event click item in dialog
                EventClickLinear();
                //event click txtDateDialod --> show Date Picker Dialod
                EventShowDatePicker();
                //
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                newSchedule = new ScheduleItem();
                newSchedule.setDate(dateMemory);
                EventCreateEvent();
            }
        });
    }

    private void EventShowDatePicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int date = calendar.get(Calendar.DATE);
        txtDateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar1 = Calendar.getInstance();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("d MMMM yyyy");
                        if (year != 0) {
                            calendar1.set(year, month, dayOfMonth);
                        }
                        String date = formatDate.format(calendar1.getTime());
                        txtDateDialog.setText(date);
                    }
                }, year, month, date);
                datePickerDialog.show();
            }
        });
    }

    private void EventClickLinear() {
        linearStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitTimePicker(txtTimeStart,true);
            }
        });
        linearEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitTimePicker(txtTimeEnd,false);
            }
        });
    }

    private void InitTimePicker(final TextView textView, final boolean b) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendarTime = Calendar.getInstance();
                calendarTime.set(0, 0, 0, hourOfDay, minute);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                String time = formatTime.format(calendarTime.getTime());
                if(b)
                {
                    newSchedule.setTimeStart(time);
                }
                else
                {
                    newSchedule.setTimeEnd(time);
                }
                textView.setText(time);
            }
        }, hour, min, true);
        timePickerDialog.show();
    }

    private void EventCreateEvent() {
        linearCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSchedule.setStatus("Not Ready");
                newSchedule.setNote(edNote.getText().toString());
                InsertData(newSchedule);
                dialog.cancel();
            }
        });
    }
}
