package com.example.fitforlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitforlife.Model.Payment;
import com.example.fitforlife.R;

import java.util.List;

public class PaymentsListAdapter extends ArrayAdapter<Payment> {


    private List<Payment> paymentsList;
    private Context context;
    private Activity ctx;
    private Button button;

    public PaymentsListAdapter(Context context, int resource, List<Payment> data) {
        super(context, resource, data);
        this.paymentsList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_payment, null, false);

        final Payment currentPayment = getItem(position);

        TextView month = v.findViewById(R.id.paymentMonth);
        TextView year = v.findViewById(R.id.paymentYear);
        ImageView isPaid = v.findViewById(R.id.paidOrNot);

        month.setText(currentPayment.getMonth());
        year.setText(currentPayment.getYear());

      //  if (currentPayment.isPaid())
            isPaid.setImageResource(R.drawable.ic_paid);
    //    else
     //       isPaid.setImageResource(R.drawable.ic_error);

        return v;
    }


}