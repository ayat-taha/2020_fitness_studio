package com.example.fitforlife.Fragments;

import com.anychart.core.cartesian.series.Line;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Cartesian;
import com.anychart.graphics.vector.Stroke;
import com.anychart.palettes.RangeColors;
import com.example.fitforlife.Activities.ProgressActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightProgress_Fragment extends Fragment {
    private static final String TAG = "WeightProgress_Fragment";

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    AnyChartView anyChartView;
    UserInfo currentUser;
    private TextView weightPrec;
    private boolean isCoach;
//    KonfettiView konfettiView;

    public WeightProgress_Fragment(UserInfo currentUser, boolean isCoach) {
        this.currentUser = currentUser;
        this.isCoach = isCoach;
        // Required empty public constructor
    }


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_weight_progress, container, false);
        ProgressActivity activity = (ProgressActivity) getActivity();
//        konfettiView = rootView.findViewById(R.id.viewKonfetti);
//        startconffeti();


//        anyChartView.setBackgroundColor();

        weightPrec = rootView.findViewById(R.id.weightPrec);
        anyChartView = rootView.findViewById(R.id.any_chart_view_weight);
        anyChartView.setProgressBar(rootView.findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.line();

        RangeColors palette = RangeColors.instantiate();
        palette.items("#6f00ff", "#cc00ff");
        cartesian.animation(true);
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Weight Progress");

        cartesian.yAxis(0).title("Kg");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        List<DataEntry> seriesData = activity.getMyData();

        //TEST
        if (currentUser.getWeightGoal() != null) {
            Log.d(TAG, "goal " + currentUser.getWeightGoal());
            if (!seriesData.isEmpty()) {
                Log.d(TAG, "last index of list " + seriesData.get(seriesData.size() - 1).getValue("value"));
                Double num = currentUser.getWeightGoal() - Double.parseDouble(((String) seriesData.get(seriesData.size() - 1).getValue("value")));
                if (num < 0) {
                    if (isCoach)
                        weightPrec.setText(currentUser.getFullName() + " " + getContext().getResources().getString(R.string.needtolose) + num + " " + getContext().getResources().getString(R.string.kg) + getContext().getResources().getString(R.string.toReachTheirGoal));

                    else
                        weightPrec.setText("" + num + getContext().getResources().getString(R.string.toReachGoal));

                }
                if (num >= 0) {
                    if (isCoach)
                        weightPrec.setText(currentUser.getFullName() + " " + getContext().getResources().getString(R.string.hasReachedgoal));
                    else {
                        weightPrec.setText("" + getContext().getResources().getString(R.string.reachedGoal));
                        activity.showConfetti();
                        dialogBuilder = new AlertDialog.Builder(getContext());
                        final View congratsPopUpView = getLayoutInflater().inflate(R.layout.congrats_popup, null);
                        TextView start = congratsPopUpView.findViewById(R.id.starting);
                        TextView change = congratsPopUpView.findViewById(R.id.change);
                        TextView current = congratsPopUpView.findViewById(R.id.current);
                        TextView close = congratsPopUpView.findViewById(R.id.txtclose);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Double changeNum = Double.parseDouble(((String) seriesData.get(0).getValue("value"))) - Double.parseDouble((String) seriesData.get(seriesData.size() - 1).getValue("value"));
                        start.setText("" + seriesData.get(0).getValue("value"));
                        change.setText("" + changeNum);
                        current.setText("" + seriesData.get(seriesData.size() - 1).getValue("value"));

                        dialogBuilder.setView(congratsPopUpView);
                        dialog = dialogBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //to round popup corners
                        dialog.show();
                    }


                }
            }
        }


        // TEST

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping seriesMapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(seriesMapping);
        series1.name("Weight");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);
        cartesian.background().fill("#FFFFFFFF");
        anyChartView.setChart(cartesian);
        return rootView;
    }

//    private void startconffeti() {
//        konfettiView.build()
//                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
//                .setDirection(0.0, 359.0)
//                .setSpeed(1f, 5f)
//                .setFadeOutEnabled(true)
//                .setTimeToLive(2000L)
//                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
//                .addSizes(new Size(12, 5f))
//                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
//                .streamFor(300, 5000L);
//    }

}
