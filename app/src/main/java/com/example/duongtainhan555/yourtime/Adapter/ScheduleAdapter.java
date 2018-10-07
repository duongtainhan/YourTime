package com.example.duongtainhan555.yourtime.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.duongtainhan555.yourtime.Interface.SendStatus;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
import com.example.duongtainhan555.yourtime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<DataItem> dataItems;
    private Context context;
    private Dialog dialogDelete;
    private Dialog dialogUpdate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ScheduleAdapter(List<DataItem> dataItems, Context context) {
        this.dataItems = dataItems;
        this.context = context;
    }

    @NonNull
    @Override

    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_schedule_item, viewGroup, false);
        return new ScheduleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final DataItem dataItem = dataItems.get(i);
        viewHolder.txtStartTime.setText(dataItem.getScheduleItem().getTimeStart());
        viewHolder.txtNote.setText(dataItem.getScheduleItem().getNote());
        viewHolder.txtStatus.setText(dataItem.getScheduleItem().getStatus());
        viewHolder.imgOption.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.itemUpdate) {
                            ShowDialogUpdate(dataItem);
                        } else if (item.getItemId() == R.id.itemDelete) {
                            ShowDialogDelete(dataItem);
                        }
                        return true;
                    }
                });
                MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popupMenu.getMenu(), v);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
                //popupMenu.show();
            }
        });
    }

    private void ShowTimePicker(final TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendarTime = Calendar.getInstance();
                calendarTime.set(0, 0, 0, hourOfDay, minute);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                String time = formatTime.format(calendarTime.getTime());
                textView.setText(time);
            }
        }, hour, min, true);
        timePickerDialog.show();
    }

    private String CheckLogic(DataItem dataItem, String startTime, String note) throws ParseException {
        String date = dataItem.getDate();
        String error = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
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

    private void ShowDialogUpdate(final DataItem dataItem) {
        dialogUpdate = new Dialog(context);
        dialogUpdate.setContentView(R.layout.dialog_update);
        Objects.requireNonNull(dialogUpdate.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnUpdate = dialogUpdate.findViewById(R.id.btnUpdate);
        Button btnCancel = dialogUpdate.findViewById(R.id.btnCancelUpdate);
        LinearLayout linearStartTime = dialogUpdate.findViewById(R.id.linearStartTimeUpdate);
        final TextView txtStartTime = dialogUpdate.findViewById(R.id.txtStartTimeUpdate);
        final EditText edNoteUpdate = dialogUpdate.findViewById(R.id.edNoteUpdate);
        dialogUpdate.setCanceledOnTouchOutside(false);
        txtStartTime.setText(dataItem.getScheduleItem().getTimeStart());
        edNoteUpdate.setText(dataItem.getScheduleItem().getNote());
        dialogUpdate.show();
        //Event
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String error = CheckLogic(dataItem, txtStartTime.getText().toString(), edNoteUpdate.getText().toString());
                    if (error == null) {
                        DeleteData(dataItem,1);
                        UpdateData(dataItem, txtStartTime.getText().toString(), edNoteUpdate.getText().toString());
                        dialogUpdate.cancel();
                    } else {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_error_time);
                        TextView txtNotif = dialog.findViewById(R.id.txtNotif);
                        Button btnOk = dialog.findViewById(R.id.btnOk);
                        txtNotif.setText(error);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        //Event
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        linearStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTimePicker(txtStartTime);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUpdate.cancel();
            }
        });
    }

    private void ShowDialogDelete(final DataItem dataItem) {
        dialogDelete = new Dialog(context);
        dialogDelete.setContentView(R.layout.dialog_delete);
        Button btnCancel = dialogDelete.findViewById(R.id.btnCancel);
        Button btnDelete = dialogDelete.findViewById(R.id.btnDelete);
        Objects.requireNonNull(dialogDelete.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.setCanceledOnTouchOutside(false);
        dialogDelete.show();
        //Event
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDelete.cancel();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteData(dataItem,0);
                dialogDelete.cancel();
            }
        });
    }

    private void ShowDialogStatus(int dialog, int textView, int status, int count) {
        count++;
        if(count == 1)
        {
            final Dialog dialogStatus = new Dialog(context);
            dialogStatus.setContentView(dialog);
            dialogStatus.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialogStatus.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txtStatus = dialogStatus.findViewById(textView);
            dialogStatus.setCanceledOnTouchOutside(false);
            txtStatus.setText(status);
            dialogStatus.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialogStatus.cancel();
                }
            }, 500);
        }
    }

    private void UpdateData(DataItem dataItem, String startTime, String note) {
        final Map<String, Object> docData = new HashMap<>();
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put("Note", note);
        nestedData.put("Status", "Not Ready");

        docData.put(startTime, nestedData);

        db.collection(dataItem.getIdUser()).document(dataItem.getDate())
                .update(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ShowDialogStatus(R.layout.dialog_status, R.id.txtStatusSuccess, R.string.status_success_update,0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ShowDialogStatus(R.layout.dialog_status, R.id.txtStatusError, R.string.status_error_update,0);
                    }
                });
    }

    private void DeleteData(DataItem dataItem, final int count) {
        DocumentReference docRef = db.collection(dataItem.getIdUser()).document(dataItem.getDate());
        Map<String, Object> updates = new HashMap<>();
        updates.put(dataItem.getScheduleItem().getTimeStart(), FieldValue.delete());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ShowDialogStatus(R.layout.dialog_status, R.id.txtStatusSuccess, R.string.status_success_delete,count);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ShowDialogStatus(R.layout.dialog_status, R.id.txtStatusError, R.string.status_error_delete,count);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStartTime;
        TextView txtNote;
        TextView txtStatus;
        ImageView imgOption;
        CardView cardViewSchedule;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSchedule = itemView.findViewById(R.id.cardViewSchedule);
            txtStartTime = itemView.findViewById(R.id.txtStartSchedule);
            txtNote = itemView.findViewById(R.id.txtNoteSchedule);
            imgOption = itemView.findViewById(R.id.imgOptionSchedule);
            txtStatus = itemView.findViewById(R.id.txtStatusSchedule);
        }
    }
}
