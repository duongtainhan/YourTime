package com.example.duongtainhan555.yourtime.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
import com.example.duongtainhan555.yourtime.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<ScheduleItem> scheduleItems;
    private Context context;

    public ScheduleAdapter(List<ScheduleItem> scheduleItems, Context context) {
        this.scheduleItems = scheduleItems;
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
        ScheduleItem scheduleItem = scheduleItems.get(i);
        viewHolder.txtStartTime.setText(scheduleItem.getTimeStart());
        viewHolder.txtNote.setText(scheduleItem.getNote());
        viewHolder.txtStatus.setText(scheduleItem.getStatus());
        viewHolder.imgOption.setOnClickListener(new View.OnClickListener() {
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
                            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
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
