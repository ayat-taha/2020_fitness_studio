package com.example.fitforlife.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;

import java.util.List;


public class sessionPopupAdapter extends ArrayAdapter<Session> {

    private List<Session> sessionList;
    private Context context;
    private TextView groupName, sessionDay, sessionDuration, sessionHour, sessionMinute;


    public sessionPopupAdapter(Context context, int resource, List<Session> data) {
        super(context, resource, data);
        this.sessionList = data;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_session_popup, null, false);
        final Session currentItem = getItem(position);

        final String[] weekDays = context.getResources().getStringArray(R.array.weekDays);
        final String[] weekDaysEn = context.getResources().getStringArray(R.array.weekDaysEn);


        sessionDay = rootView.findViewById(R.id.session_day);
        int dayIndex = 0;
        for (int i = 0; i < weekDays.length; i++) {
            if (weekDaysEn[i].equals(currentItem.getDay()))
                dayIndex = i;
        }
        sessionDay.setText(weekDays[dayIndex]);

        sessionDuration = rootView.findViewById(R.id.session_duration);
        sessionDuration.setText(String.valueOf(currentItem.getDuration()));

        sessionHour = rootView.findViewById(R.id.session_hour);
        sessionMinute = rootView.findViewById(R.id.session_minute);
        sessionHour.setText("" + currentItem.getHour());
        sessionMinute.setText("" + currentItem.getMinute());

        groupName = rootView.findViewById(R.id.group_name);
        Group group = FitForLifeDataManager.getInstance().getGroup(currentItem.getGroupId());
        groupName.setText("" + group.getGroupName());
        return rootView;
    }
}
