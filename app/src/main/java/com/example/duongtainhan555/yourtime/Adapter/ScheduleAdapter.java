package com.example.duongtainhan555.yourtime.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duongtainhan555.yourtime.Interface.SendStatus;
import com.example.duongtainhan555.yourtime.Model.DataItem;
import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
import com.example.duongtainhan555.yourtime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<DataItem> dataItems;
    private Context context;
    private Dialog dialogDelete;
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
                            Toast.makeText(context, "Update", Toast.LENGTH_SHORT).show();
                        } else if (item.getItemId() == R.id.itemDelete) {
                            String id = dataItem.getIdUser();
                            String date = dataItem.getDate();
                            String time = dataItem.getScheduleItem().getTimeStart();
                            ShowDialogDelete(id,date,time);
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
    private void ShowDialogDelete(final String id, final String date, final String time)
    {
        dialogDelete = new Dialog(context);
        dialogDelete.setContentView(R.layout.dialog_delete);
        Button btnCancel= dialogDelete.findViewById(R.id.btnCancel);
        Button btnDelete = dialogDelete.findViewById(R.id.btnDelete);
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
                DocumentReference docRef = db.collection(id).document(date);
                Map<String,Object> updates = new HashMap<>();
                updates.put(time, FieldValue.delete());

                docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
                dialogDelete.cancel();
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
