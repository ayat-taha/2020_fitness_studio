package com.example.fitforlife.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.anychart.palettes.RangeColors;
import com.example.fitforlife.Activities.ProgressActivity;
import com.example.fitforlife.R;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThighsProgress_Fragment extends Fragment {
    AnyChartView anyChartView;


    public ThighsProgress_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_thighs_progress, container, false);
        ProgressActivity activity = (ProgressActivity) getActivity();
        anyChartView = rootView.findViewById(R.id.any_chart_view_thigh);
        anyChartView.setProgressBar(rootView.findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.line();
        RangeColors palette = RangeColors.instantiate();
        palette.items("#0062ff", "#ffbf00");


        cartesian.palette(palette);
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Thighs Measurements");
        cartesian.yAxis(0).title("Cm)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        List<DataEntry> seriesData = activity.getMyData();


        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value7' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value8' }");
        Line series1 = cartesian.line(series1Mapping);
        series1.name("Left Thigh");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Right Thigh");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);


        return rootView;}

}
