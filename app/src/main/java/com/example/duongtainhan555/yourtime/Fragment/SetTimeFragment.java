package com.example.duongtainhan555.yourtime.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.example.duongtainhan555.yourtime.Adapter.ScheduleAdapter;
import com.example.duongtainhan555.yourtime.Constant;
import com.example.duongtainhan555.yourtime.CustomView.CustomScrollView;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.Model.Report;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class SetTimeFragment extends Fragment {

    //Init view of fragment
    View view;
    //Init object
    private CalendarView calendarView;
    private FloatingActionButton floatingActionButton;
    private Dialog dialog;
    private Calendar calendar;
    private TextView txtDateDialog;
    private Button btnCreate;
    private TextView txtTimeStart;
    private LinearLayout linearStartTime;
    private EditText edNote;
    private Dialog dialogNotif;
    private RecyclerView recyclerView;
    private TextView txtNoSchedule;
    private Button btnCancel;
    private CustomScrollView scrollView;
    private TextView txtDate;
    private Toolbar toolbar;
    private CardView cardViewCalendar;
    //Init variable
    private String idUser;
    private List<ScheduleItem> createNewScheduleItem;
    private ScheduleItem scheduleItem;
    private DataItem createNewData;
    private List<ScheduleItem> arrCreatedSchedule;
    private DataItem createdData;
    private String dateMemory;
    private String dateCalendar;
    private String time;
    private Report report;
    //Init firebase
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settime, container, false);
        //Init View
        InitView();
        CheckReportExists();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Event
        EventCalendar();
        EventFloatingButton();
        EventTouchScrollView();
        super.onViewCreated(view, savedInstanceState);
    }

    private void InitView() {
        //Init View
        calendarView = view.findViewById(R.id.calendar);
        floatingActionButton = view.findViewById(R.id.floatingButton);
        txtDate = view.findViewById(R.id.txtDate);
        toolbar = view.findViewById(R.id.toolBar);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        txtNoSchedule = view.findViewById(R.id.txtNoSchedule);
        scrollView = view.findViewById(R.id.scrollView);
        cardViewCalendar = view.findViewById(R.id.cardViewCalendar);
        //
        SetDate(0, 0, 0);
        GetIdUser();
        GetData();
    }

    private void EventTouchScrollView() {
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    //cardViewCalendar.setVisibility(View.INVISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    txtDate.setText(dateCalendar);
                } else if (scrollY < oldScrollY) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                    cardViewCalendar.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void GetIdUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
            idUser = firebaseUser.getUid();
    }


    private void EventFloatingButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(Objects.requireNonNull(getContext()));
                dialog.setContentView(R.layout.dialog_set_time);
                linearStartTime = dialog.findViewById(R.id.linearStartTime);
                txtDateDialog = dialog.findViewById(R.id.txtDateDialog);
                //set text header Date
                txtDateDialog.setText(dateCalendar);
                //
                txtTimeStart = dialog.findViewById(R.id.txtTimeStart);
                btnCreate = dialog.findViewById(R.id.btnCreate);
                edNote = dialog.findViewById(R.id.edNote);
                btnCancel = dialog.findViewById(R.id.btnCancelCreate);
                //event click item in dialog
                EventClickLinear();
                //event click txtDateDialod --> show Date Picker Dialod
                EventShowDatePicker();
                //event click btnCancel
                EventClickCancel();
                //
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                createNewData = new DataItem();
                createNewScheduleItem = new ArrayList<>();
                scheduleItem = new ScheduleItem();
                EventCreateEvent();
            }
        });
    }

    private void EventClickCancel() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void ShowDialogError(String text) {
        dialogNotif = new Dialog(Objects.requireNonNull(getContext()));
        dialogNotif.setContentView(R.layout.dialog_error_time);
        TextView txtNotif = dialogNotif.findViewById(R.id.txtNotif);
        txtNotif.setText(text);
        //dialogNotif.setCanceledOnTouchOutside(false);
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
                        createNewData.setDate(format.format(calendar1.getTime()));
                        txtDateDialog.setText(formatDate.format(calendar1.getTime()));
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
                InitTimePicker(txtTimeStart);
            }
        });
    }

    private void SetDate(int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("d MMMM yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        if (year != 0) {
            calendar.set(year, month, dayOfMonth);
            dateMemory = format.format(calendar.getTime());
            dateCalendar = formatDate.format(calendar.getTime());
        }
        dateMemory = format.format(calendar.getTime());
        dateCalendar = formatDate.format(calendar.getTime());
    }

    private void EventCalendar() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SetDate(year, month, dayOfMonth);
                GetIdUser();
                GetData();
            }
        });
    }

    private void InitTimePicker(final TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendarTime = Calendar.getInstance();
                calendarTime.set(0, 0, 0, hourOfDay, minute);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                time = formatTime.format(calendarTime.getTime());
                textView.setText(time);
            }
        }, hour, min, true);
        timePickerDialog.show();
    }

    //CheckLogic
    private String CheckLogic(DataItem dataItem) throws ParseException {
        String startTime = dataItem.getScheduleItems().get(0).getTimeStart();
        String date = dataItem.getDate();
        String note = dataItem.getScheduleItems().get(0).getNote();
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
        } else if (formatDate.parse(date).before(calendarFormatDate)) {
            error = "Date must not be earlier current date";
        } else if (formatDate.parse(date).equals(calendarFormatDate)) {
            if (calendarFormatTime.after(formatTime.parse(startTime))) {
                error = "Start time must be later than current time";
            }
        }
        return error;
    }

    private void CheckReportExists() {
        final DocumentReference docRef = db.collection(idUser).document(Constant.report);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    report = new Report();
                    report.setNumberOfWork(snapshot.getData().get(Constant.numberOfWork).toString());
                    Log.d("check_report", snapshot.getData().get(Constant.numberOfWork).toString());
                } else {
                    Log.d("check_report", "NULL");
                    Map<String, String> nestedData = new HashMap<>();
                    nestedData.put(Constant.numberOfWork, "0");
                    db.collection(idUser).document(Constant.report).set(nestedData);
                }

            }
        });
    }

    //Create New Schedule
    private void ShowDialogStatus(int dialog, int textView, int status) {
        final Dialog dialogStatus = new Dialog(Objects.requireNonNull(getContext()));
        dialogStatus.setContentView(dialog);
        Objects.requireNonNull(dialogStatus.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtStatus = dialogStatus.findViewById(textView);
        txtStatus.setText(status);
        //dialogStatus.setCanceledOnTouchOutside(false);
        dialogStatus.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogStatus.cancel();
            }
        }, 1369);

    }

    private void SetData(DataItem dataItem) {
        Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put(Constant.note, dataItem.getScheduleItems().get(0).getNote());
        nestedData.put(Constant.status, dataItem.getScheduleItems().get(0).getStatus());
        nestedData.put(Constant.alarm, dataItem.getScheduleItems().get(0).getAlarm());
        nestedData.put(Constant.requestID,dataItem.getScheduleItems().get(0).getRequestID());

        docData.put(dataItem.getScheduleItems().get(0).getTimeStart(), nestedData);



        db.collection(idUser).document(dataItem.getDate())
                .set(docData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ShowDialogStatus(R.layout.dialog_status, R.id.txtStatusSuccess, R.string.status_success_written);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error writing document", e);
                        ShowDialogStatus(R.layout.dialog_error_status, R.id.txtStatusError, R.string.status_error_write);
                    }
                });

    }

    private void UpdateData(DataItem dataItem) {
        Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put(Constant.note, dataItem.getScheduleItems().get(0).getNote());
        nestedData.put(Constant.status, dataItem.getScheduleItems().get(0).getStatus());
        nestedData.put(Constant.alarm, dataItem.getScheduleItems().get(0).getAlarm());
        nestedData.put(Constant.requestID,dataItem.getScheduleItems().get(0).getRequestID());

        docData.put(dataItem.getScheduleItems().get(0).getTimeStart(), nestedData);

        db.collection(idUser).document(dataItem.getDate())
                .update(docData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ShowDialogStatus(R.layout.dialog_status, R.id.txtStatusSuccess, R.string.status_success_written);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error writing document", e);
                        ShowDialogStatus(R.layout.dialog_error_status, R.id.txtStatusError, R.string.status_error_write);
                    }
                });
    }

    private void InsertData(final DataItem dataItem) {
        String date = dataItem.getDate();
        DocumentReference docRef = db.collection(idUser).document(date);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UpdateData(dataItem);
                        //SetData(dataItem);
                        Log.d("EXISTS", "YES");
                    } else {
                        Log.d("EXISTS", "NO");
                        SetData(dataItem);
                        //UpdateData(dataItem);
                    }
                } else {
                    Log.d("CHECK", "get failed with ", task.getException());
                }
            }
        });
    }
    private void UpdateNumberOfWork()
    {
        int request = Integer.valueOf(report.getNumberOfWork());
        request++;
        String requestID= String.valueOf(request);
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put(Constant.numberOfWork, requestID);

        db.collection(idUser).document(Constant.report).update(nestedData);
    }

    private void EventCreateEvent() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int request = Integer.valueOf(report.getNumberOfWork());
                    request++;
                    String requestID= String.valueOf(request);
                    scheduleItem.setTimeStart(time);
                    scheduleItem.setNote(edNote.getText().toString());
                    scheduleItem.setAlarm(Constant.onAlarm);
                    scheduleItem.setStatus(Constant.notReadyStatus);
                    scheduleItem.setRequestID(requestID);
                    createNewScheduleItem.add(scheduleItem);
                    createNewData.setScheduleItems(createNewScheduleItem);
                    createNewData.setDate(dateMemory);
                    String checkLogic = CheckLogic(createNewData);
                    if (checkLogic == null) {
                        UpdateNumberOfWork();
                        InsertData(createNewData);
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

    private void GetData() {
        final DocumentReference docRef = db.collection(idUser).document(dateMemory);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    txtNoSchedule.setVisibility(View.VISIBLE);
                    txtNoSchedule.setText("ERROR");
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoSchedule.setVisibility(View.INVISIBLE);
                    createdData = new DataItem();
                    createdData.setDate(dateMemory);
                    arrCreatedSchedule = new ArrayList<>();
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(snapshot.getData()).entrySet()) {
                        ScheduleItem scheduleItem = new ScheduleItem();
                        scheduleItem.setTimeStart(entry.getKey());
                        Map<String, String> nestedData = (Map<String, String>) entry.getValue();
                        scheduleItem.setNote(nestedData.get(Constant.note));
                        scheduleItem.setStatus(nestedData.get(Constant.status));
                        scheduleItem.setAlarm(nestedData.get(Constant.alarm));
                        scheduleItem.setRequestID(nestedData.get(Constant.requestID));
                        //if ("Not Ready".equals(nestedData.get("Status"))) { }
                        arrCreatedSchedule.add(scheduleItem);
                    }
                    Collections.sort(arrCreatedSchedule);
                    createdData.setScheduleItems(arrCreatedSchedule);
                    if (arrCreatedSchedule.isEmpty()) {
                        scrollView.setEnableScrolling(false);
                        recyclerView.setVisibility(View.INVISIBLE);
                        txtNoSchedule.setVisibility(View.VISIBLE);
                        DeleteDocIfNull(idUser, dateMemory);
                    } else {
                        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(createdData, getContext(), idUser);
                        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(scheduleAdapter);
                    }

                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    txtNoSchedule.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void DeleteDocIfNull(String id, String date) {
        db.collection(id).document(date)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}
