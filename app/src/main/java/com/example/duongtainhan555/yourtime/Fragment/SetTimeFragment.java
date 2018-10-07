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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.example.duongtainhan555.yourtime.Adapter.ScheduleAdapter;
import com.example.duongtainhan555.yourtime.Model.DataItem;
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

    View view;
    private CalendarView calendarView;
    private FloatingActionButton floatingActionButton;
    private Dialog dialog;
    private TextView txtDate;
    private Calendar calendar;
    private TextView txtDateDialog;
    private Button btnCreate;
    private TextView txtTimeStart;
    private FirebaseFirestore db;
    private LinearLayout linearStartTime;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String idUser;
    private DataItem createNewData;
    private ScheduleItem createNewScheduleItem;
    private List<DataItem> arrCreatedData;
    private String dateMemory;
    private EditText edNote;
    private Dialog dialogNotif;
    private RecyclerView recyclerView;
    private TextView txtNoSchedule;
    private Button btnCancel;

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
        EventFloatingButton();
        super.onViewCreated(view, savedInstanceState);
    }

    private void InitView() {

        calendarView = view.findViewById(R.id.calendar);
        floatingActionButton = view.findViewById(R.id.floatingButton);
        txtDate = view.findViewById(R.id.txtDate);
        SetDate(0, 0, 0);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        txtNoSchedule = view.findViewById(R.id.txtNoSchedule);
        GetIdUser();
        GetData();
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
                txtDateDialog.setText(txtDate.getText());
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
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                createNewData = new DataItem();
                createNewScheduleItem = new ScheduleItem();
                createNewData.setIdUser(idUser);
                createNewData.setScheduleItem(createNewScheduleItem);
                createNewData.setDate(dateMemory);
                EventCreateEvent();
            }
        });
    }
    private void EventClickCancel()
    {
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
        dialogNotif.setCanceledOnTouchOutside(false);
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
                        String date = formatDate.format(calendar1.getTime());
                        createNewData.setDate(format.format(calendar1.getTime()));
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
        }
        String date = formatDate.format(calendar.getTime());
        dateMemory = format.format(calendar.getTime());
        txtDate.setText(date);
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
                String time = formatTime.format(calendarTime.getTime());
                createNewScheduleItem.setTimeStart(time);
                textView.setText(time);
            }
        }, hour, min, true);
        timePickerDialog.show();
    }

    //CheckLogic
    private String CheckLogic(DataItem dataItem) throws ParseException {
        String startTime = dataItem.getScheduleItem().getTimeStart();
        String date = dataItem.getDate();
        String note = dataItem.getScheduleItem().getNote();
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
    //Region Function: Create New Schedule

    private void ShowDialogStatus(int dialog, int textView, int status) {
        final Dialog dialogStatus = new Dialog(Objects.requireNonNull(getContext()));
        dialogStatus.setContentView(dialog);
        Objects.requireNonNull(dialogStatus.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtStatus = dialogStatus.findViewById(textView);
        txtStatus.setText(status);
        dialogStatus.setCanceledOnTouchOutside(false);
        dialogStatus.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogStatus.cancel();
            }
        }, 500);

    }

    private void SetData(DataItem dataItem) {

        Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put("Note", dataItem.getScheduleItem().getNote());
        nestedData.put("Status", dataItem.getScheduleItem().getStatus());

        docData.put(dataItem.getScheduleItem().getTimeStart(), nestedData);

        db.collection(dataItem.getIdUser()).document(dataItem.getDate())
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
        nestedData.put("Note", dataItem.getScheduleItem().getNote());
        nestedData.put("Status", dataItem.getScheduleItem().getStatus());

        docData.put(dataItem.getScheduleItem().getTimeStart(), nestedData);

        db.collection(dataItem.getIdUser()).document(dataItem.getDate())
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
                    } else {
                        SetData(dataItem);
                    }
                } else {
                    Log.d("CHECK", "get failed with ", task.getException());
                }
            }
        });
    }

    private void EventCreateEvent() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewScheduleItem.setNote(edNote.getText().toString());
                try {
                    String checkLogic = CheckLogic(createNewData);
                    if (checkLogic == null) {
                        createNewScheduleItem.setStatus("Not Ready");
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

    //EndRegion
    //Realtime
    private void GetData() {
        arrCreatedData = new ArrayList<>();
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
                    arrCreatedData.clear();
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    for (Map.Entry<String, Object> entry : Objects.requireNonNull(snapshot.getData()).entrySet()) {
                        DataItem dataItem = new DataItem();
                        ScheduleItem scheduleItem = new ScheduleItem();
                        scheduleItem.setTimeStart(entry.getKey());
                        Map<String, String> nestedData = (Map<String, String>) entry.getValue();
                        scheduleItem.setNote(nestedData.get("Note"));
                        scheduleItem.setStatus(nestedData.get("Status"));
                        dataItem.setScheduleItem(scheduleItem);
                        dataItem.setIdUser(idUser);
                        dataItem.setDate(dateMemory);
                        arrCreatedData.add(dataItem);
                    }
                    if (arrCreatedData.isEmpty()) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        txtNoSchedule.setVisibility(View.VISIBLE);
                        DeleteDocIfNull(idUser, dateMemory);
                    } else {
                        Collections.sort(arrCreatedData);
                        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(arrCreatedData, getContext());
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
