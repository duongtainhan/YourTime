package com.example.duongtainhan555.yourtime.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duongtainhan555.yourtime.Model.ScheduleItem;
import com.example.duongtainhan555.yourtime.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    List<ScheduleItem> scheduleItems;
    Context context;

    public ScheduleAdapter(List<ScheduleItem> scheduleItems, Context context) {
        this.scheduleItems = scheduleItems;
        this.context = context;
    }

    @NonNull
    @Override

    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_schedule_item,viewGroup,false);
        return new ScheduleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ScheduleItem scheduleItem = scheduleItems.get(i);
        viewHolder.txtStartTime.setText(scheduleItem.getTimeStart());
        viewHolder.txtEndTime.setText(scheduleItem.getTimeEnd());
        viewHolder.txtNote.setText(scheduleItem.getNote());
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStartTime;
        TextView txtEndTime;
        TextView txtNote;
        ImageView imgOption;
        CardView cardViewSchedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSchedule = itemView.findViewById(R.id.cardViewSchedule);
            txtStartTime = itemView.findViewById(R.id.txtStartSchedule);
            txtEndTime = itemView.findViewById(R.id.txtEndSchedule);
            txtNote = itemView.findViewById(R.id.txtNoteSchedule);
            imgOption = itemView.findViewById(R.id.imgOptionSchedule);
        }
    }
}
