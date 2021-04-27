package com.example.fitforlife.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitforlife.Activities.ProgressActivity;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    public BlankFragment(UserInfo currentUser) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_blank, container, false);
        ProgressActivity activity = (ProgressActivity) getActivity();
//        konfettiView = rootView.findViewById(R.id.viewKonfetti);
//        startconffeti();

        activity.showConfetti();
        return rootView;

    }

}
