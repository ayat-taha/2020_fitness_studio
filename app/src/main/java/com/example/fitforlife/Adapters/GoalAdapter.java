package com.example.fitforlife.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.fitforlife.R;
import java.util.List;

public class GoalAdapter extends ArrayAdapter<String> {


    private List<String> goalsOptions;
    private Context context;
    private int pos;



    public GoalAdapter(Context context, int resource, List<String> data) {
        super(context, resource, data);
        this.goalsOptions = data;
        this.context = context;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_goal, null, false);

        // final int pos = position;
        final String currentItem = getItem(position);

        TextView item=rootView.findViewById(R.id.goalText);
        item.setText(currentItem);
        ImageView TraineeName = rootView.findViewById(R.id.goalCheckIcon);
        // in getView do
        if(pos == position)

        {
//Show image
            TraineeName.setVisibility(View.VISIBLE);
        }
        else
        {
TraineeName.setVisibility(View.INVISIBLE);        }


        return rootView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    public void SetSelectedPos(int position) {
        pos=position;
    }
}
