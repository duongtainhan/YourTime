package com.example.duongtainhan555.yourtime.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.duongtainhan555.yourtime.Interface.StatusFirebase;
import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
import com.example.duongtainhan555.yourtime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private Dialog dialogNotif;
    private Dialog dialogWritten;
    private StatusFirebase statusFirebase;

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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error writing document", e);

                    }
                });
    }

    private void UpdateData(ScheduleItem scheduleItem) {

        Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put("EndTime", scheduleItem.getTimeEnd());
        nestedData.put("Note", scheduleItem.getNote());
        nestedData.put("Status", scheduleItem.getStatus());

        docData.put(scheduleItem.getTimeStart(), nestedData);

        db.collection(idUser).document(scheduleItem.getDate())
                .update(docData)
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error writing document", e);
                    }
                });

    }

    private void InsertData(final ScheduleItem scheduleItem) {
        String date = scheduleItem.getDate();
        DocumentReference docRef = db.collection(idUser).document(date);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UpdateData(scheduleItem);
                    } else {
                        SetData(scheduleItem);
                    }
                } else {
                    Log.d("CHECK", "get failed with ", task.getException());
                }
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
                dialog = new Dialog(Objects.requireNonNull(getContext()));
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

    private void ShowDialogError(String text) {
        dialogNotif = new Dialog(Objects.requireNonNull(getContext()));
        dialogNotif.setContentView(R.layout.dialog_error_time);
        TextView txtNotif = dialogNotif.findViewById(R.id.txtNotif);
        txtNotif.setText(text);
        Objects.requireNonNull(dialogNotif.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNotif.show();
        Button btnOk = dialogNotif.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNotif.cancel();
            }
        });
    }

    private String CheckLogic(ScheduleItem scheduleItem) throws ParseException {
        String startTime = scheduleItem.getTimeStart();
        String endTime = scheduleItem.getTimeEnd();
        String date = scheduleItem.getDate();
        String note = scheduleItem.getNote();
        String error = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        calendar = Calendar.getInstance();
        Date calendarFormatDate = formatDate.parse(formatDate.format(calendar.getTime()));
        Date calendarFormatTime = formatTime.parse(formatTime.format(calendar.getTime()));
        if (note.isEmpty()) {
            error = "Note is empty";
        } else if (startTime == null) {
            error = "Start time is empty";
        } else if (endTime == null) {
            error = "End time is empty";
        } else if (formatDate.parse(date).before(calendarFormatDate)) {
            error = "Date must not be earlier current date";
        } else if (formatDate.parse(date).equals(calendarFormatDate)) {
            if (calendarFormatTime.after(formatTime.parse(startTime))) {
                error = "Start time must be later than current time";
            } else if (formatTime.parse(startTime).after(formatTime.parse(endTime))) {
                error = "Start time must be earlier than end time";
            }
        } else if (formatDate.parse(date).equals(calendarFormatDate)) {
            if (formatTime.parse(startTime).after(formatTime.parse(endTime))) {
                error = "Start time must be earlier than end time";
            }
        }
        return error;
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
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        if (year != 0) {
                            calendar1.set(year, month, dayOfMonth);
                        }
                        String date = formatDate.format(calendar1.getTime());
                        newSchedule.setDate(format.format(calendar1.getTime()));
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
                InitTimePicker(txtTimeStart, true);
            }
        });
        linearEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitTimePicker(txtTimeEnd, false);
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
                if (b) {
                    newSchedule.setTimeStart(time);
                } else {
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
                newSchedule.setNote(edNote.getText().toString());
                Log.d("AAA", "note" + newSchedule.getNote());
                try {
                    String checkLogic = CheckLogic(newSchedule);
                    if (checkLogic == null) {
                        newSchedule.setStatus("Not Ready");
                        InsertData(newSchedule);
                        dialog.cancel();
                    } else {
                        ShowDialogError(checkLogic);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void ShowDialogWritten()
    {
        dialogWritten = new Dialog(Objects.requireNonNull(getContext()));
        dialogWritten.setContentView(R.layout.dialog_written);
        dialogWritten.show();
    }
}
