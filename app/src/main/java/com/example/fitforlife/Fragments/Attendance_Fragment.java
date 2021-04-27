package com.example.fitforlife.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.fitforlife.Adapters.AttendanceAdapter;
import com.example.fitforlife.R;

import java.util.ArrayList;
import java.util.List;


public class Attendance_Fragment extends Fragment {


    private ListView traineeList;
    private AttendanceAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_attendence_, container, false);

        Context context = getActivity();
         Button save = rootView.findViewById(R.id.save_attendence);
        //save.setOnClickListener(SaveAttendenceOnClick);
        traineeList =   rootView.findViewById(R.id.listForAttendence);

        List<String> TraineeNames=new ArrayList<>();
        TraineeNames.add("Karla Haddad");
        TraineeNames.add("Karin Nijim");
        TraineeNames.add("Ayat Taha");
        TraineeNames.add("Karin Nijim");
        TraineeNames.add("Ayat Taha");
        TraineeNames.add("Karla Haddad");

        adapter = new AttendanceAdapter(context, R.layout.card_attendence, TraineeNames);
        traineeList.setAdapter(adapter);

        //  traineeList = rootView.findViewById(R.id.list);
        return rootView;
    }
}