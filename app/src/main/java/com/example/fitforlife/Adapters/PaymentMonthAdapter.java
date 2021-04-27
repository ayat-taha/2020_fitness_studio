package com.example.fitforlife.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Payment;

import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class PaymentMonthAdapter extends RecyclerView.Adapter<PaymentMonthAdapter.CardViewHolder> {

    private final String[] months;
    private List<Integer> usersPaymentMoths;
    private final int currentYear;

    FirebaseFirestore fStore;
    private final Context context;
    private BottomSheetDialog btm;
    private static final String TAG = "paymentAdapter";
    private UserInfo currentUser;
    private boolean isCoach;
    private CoachInfo currentCoach;

    private NumberPicker methodPicker;
    private TextView paymentMonth;
    private TextView paymentYear;
    private EditText amount;
    private TextView amountTxt;
    private TextView methodTxt;
    private TextView savePayment;
    private TextView close;
    private ImageView delete_payment;
    private ImageView payment_reminder;
    private View btsv;
    String[] paymentMethods;
    String[] paymentMethodsEn;


    public PaymentMonthAdapter(Context context, String[] data, int year, UserInfo currentUser, boolean isCoach) {
        this.months = data;
        this.context = context;
        this.currentYear = year;
        this.currentUser = currentUser;
        Log.d("userTrans", "added  user in constructor : " + this.currentUser.toString());
        this.isCoach = isCoach;
        if (isCoach) {
            if (FitForLifeDataManager.getInstance().getCurrentCoach() != null)
                currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
            if (FitForLifeDataManager.getInstance().getCurrentManager() != null)
                currentCoach = FitForLifeDataManager.getInstance().getCurrentManager();
        }
    }

    @NonNull
    public PaymentMonthAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.month_item, parent, false);
        return new CardViewHolder(view);
    }

    public void onBindViewHolder(@NonNull final PaymentMonthAdapter.CardViewHolder holder, final int position) {

        fStore = FirebaseFirestore.getInstance();

        currentUser.setUserPayments(FitForLifeDataManager.getInstance().getAllUserPayments(currentUser));

        usersPaymentMoths = FitForLifeDataManager.getInstance().getAllPaidPayments(currentUser, currentYear);

        Log.d(TAG, "months " + FitForLifeDataManager.getInstance().getAllPaidPayments(currentUser, currentYear));

        // change layout of paid months
        for (Integer month : usersPaymentMoths) {
            if (month == position + 1) {
                holder.monthLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.paid_frame));
            }
        }

        //set image of month
        final int[] imageArray = {R.drawable.jan, R.drawable.feb, R.drawable.mar, R.drawable.apr, R.drawable.may, R.drawable.june, R.drawable.july, R.drawable.aug, R.drawable.sep, R.drawable.oct, R.drawable.nov, R.drawable.dec};
        holder.image_month.setImageResource(imageArray[position]);

        if (isCoach) {
            holder.monthLayout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                @Override
                public void onClick(View view) {

                    btm = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                    btsv = LayoutInflater.from(context.getApplicationContext())
                            .inflate(R.layout.card_edit_payment, (RelativeLayout) view.findViewById(R.id.paymentSheetContainer));
                    btm.setContentView(btsv);
                    btm.show();

                    paymentMonth = btsv.findViewById(R.id.paymentMonth2);
                    paymentMonth.setText(months[position]);

                    paymentYear = btsv.findViewById(R.id.paymentYear2);
                    paymentYear.setText(currentYear + "");


                    amount = btsv.findViewById(R.id.paymentAmount);
                    amount.setText("0");

                    methodPicker = btsv.findViewById(R.id.methodPicker);
                    methodPicker.setMaxValue(3);
                    methodPicker.setMinValue(0);
                    paymentMethods = context.getResources().getStringArray(R.array.payment_methods);
                    paymentMethodsEn = context.getResources().getStringArray(R.array.payment_methodsEn);
                    methodPicker.setDisplayedValues(paymentMethods);


                    delete_payment = btsv.findViewById(R.id.delete_payment);
                    delete_payment.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            deleteOnClick();
                        }
                    });
                    payment_reminder = btsv.findViewById(R.id.payment_bell);
                    payment_reminder.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            reminderOnClick(position + 1, currentYear);
                        }
                    });

                    if (usersPaymentMoths.contains(position + 1)) {
                        Payment currentPayment = FitForLifeDataManager.getInstance().getPayment(currentUser, position + 1, currentYear);
                        amount.setText(String.format("%d", currentPayment.getAmount()));
                        for (int i = 0; i < paymentMethods.length; i++) {
                            if (paymentMethods[i].equals(currentPayment.getMethod()))
                                methodPicker.setValue(i);
                        }
                        payment_reminder.setVisibility(View.GONE);
                    } else {
                        delete_payment.setVisibility(View.GONE);
                    }
                    savePayment = btsv.findViewById(R.id.save_payment);
                    savePayment.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            setOnClick();
                        }
                    });


                }

                private void reminderOnClick(final int month, final int currentYear) {
                    final String title;
                    final String msg;

                    title = context.getResources().getString(R.string.sendReminder);
                    msg = context.getResources().getString(R.string.sendReminderMsg);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title);
                    builder.setMessage(msg);
                    builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {


                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUser.getId());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userid", currentCoach.getId());
                            hashMap.put("text", "Payment reminder for");
                            hashMap.put("text2", "");
                            hashMap.put("postid", month + " " + currentYear);
                            hashMap.put("isEventRes", false);
                            hashMap.put("isEventCanc", false);
                            hashMap.put("ispost", false);
                            hashMap.put("isEventNotGoing", false);
                            hashMap.put("isEventGoing", false);
                            hashMap.put("isProgress", false);
                            hashMap.put("isPayment", true);
                            reference.push().setValue(hashMap);

                        }
                    }); // end " yes "

                    builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                protected void setOnClick() {

                    String method = methodPicker.getDisplayedValues()[methodPicker.getValue()];
                    int methodIndex = 0;
                    for (int i = 0; i < paymentMethods.length; i++) {
                        if (paymentMethods[i].equals(method)) {
                            methodIndex = i;
                        }
                    }
                    final Payment newPayment = new Payment(currentUser.getEmail(), position + 1, Integer.parseInt(paymentYear.getText().toString()), Integer.parseInt(amount.getText().toString()), paymentMethodsEn[methodIndex]);
                    if (currentUser.getUserPayments().contains(newPayment)) {
                        int payIndex = currentUser.getUserPayments().indexOf(newPayment);

                        //set number of payment
                        int payNumber = currentUser.getUserPayments().get(payIndex).getNumber();
                        newPayment.setNumber(payNumber);

                        currentUser.getUserPayments().remove(payIndex);//remove payment
                        currentUser.getUserPayments().add(newPayment);//add updated payment
                        FitForLifeDataManager.getInstance().updatePayment(newPayment);//update sqlLite
                    } else {
                        currentUser.getUserPayments().add(newPayment);//add new payment
                        FitForLifeDataManager.getInstance().createPayment(newPayment);//add to sqlLite
                    }
                    //update firebase
                    List<Payment> updatedPayments = FitForLifeDataManager.getInstance().getAllUserPayments(currentUser);

                    final DocumentReference documentReference = fStore.collection("users").document(currentUser.getEmail());
                    for (final Payment tmp : updatedPayments) {
                        fStore.collection("users").document(documentReference.getId()).collection("payments").
                                document(tmp.getNumber() + "").set(tmp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "added  payments to user : " + tmp.toString());
                                addNotification(currentUser.getId(), currentCoach, newPayment);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                    notifyDataSetChanged();
                    btm.dismiss();
                    //end add payment
                }

                protected void deleteOnClick() {

                    final String title;
                    final String msg;

                    title = context.getResources().getString(R.string.deletePayment);
                    msg = context.getResources().getString(R.string.deletePaymentMsg);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title);
                    builder.setMessage(msg);
                    builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {


                            final Payment deletedPayment = new Payment(currentUser.getEmail(), position + 1, Integer.parseInt(paymentYear.getText().toString()), Integer.parseInt(amount.getText().toString()), methodPicker.getDisplayedValues()[methodPicker.getValue()]);
                            if (currentUser.getUserPayments().contains(deletedPayment)) {
                                int payIndex = currentUser.getUserPayments().indexOf(deletedPayment);

                                //set number of payment
                                int payNumber = currentUser.getUserPayments().get(payIndex).getNumber();
                                deletedPayment.setNumber(payNumber);

                                currentUser.getUserPayments().remove(payIndex);//remove payment
                                //     currentUser.getUserPayments().r(deletedPayment);//add updated payment
                                FitForLifeDataManager.getInstance().deletePayment(deletedPayment);//delete from sqlLite
                            }
                            //update firebase
                            List<Payment> updatedPayments = FitForLifeDataManager.getInstance().getAllUserPayments(currentUser);

                            final DocumentReference documentReference = fStore.collection("users").document(currentUser.getEmail());
                            fStore.collection("users").document(documentReference.getId()).collection("payments").
                                    document(deletedPayment.getNumber() + "").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "deleted  payment to user : " + deletedPayment.toString());
                                    // addNotification(currentUser.getId(), FitForLifeDataManager.getInstance().getCurrentCoach(), newPayment);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                            notifyDataSetChanged();
                            btm.dismiss();
                            //end delete payment
                        }
                    }); // end " yes "

                    builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {//user
            holder.monthLayout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                public void onClick(View view) {

                    btm = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                    btsv = LayoutInflater.from(context.getApplicationContext())
                            .inflate(R.layout.card_show_payment, (RelativeLayout) view.findViewById(R.id.paymentSheetContainer));
                    btm.setContentView(btsv);
                    btm.show();

                    paymentMonth = btsv.findViewById(R.id.paymentMonth3);
                    paymentMonth.setText(months[position]);

                    paymentYear = btsv.findViewById(R.id.paymentYear3);
                    paymentYear.setText(currentYear + "");

                    amountTxt = btsv.findViewById(R.id.showPaymentAmount);
                    methodTxt = btsv.findViewById(R.id.method);

                    if (usersPaymentMoths.contains(position + 1)) {
                        Payment currentPayment = FitForLifeDataManager.getInstance().getPayment(currentUser, position + 1, currentYear);
                        amountTxt.setText(String.format("%d", currentPayment.getAmount()));
                        methodTxt.setText(currentPayment.getMethod());
                    } else {
                        amountTxt.setText("0");
                        methodTxt.setText(context.getResources().getString(R.string.noMethod));
                    }

                    close = btsv.findViewById(R.id.close_payment);
                    close.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            btm.dismiss();
                        }
                    });
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return months.length;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout monthLayout;
        public ImageView image_month;
        public TextView month;

        public CardViewHolder(View itemView) {
            super(itemView);

            image_month = itemView.findViewById(R.id.Image_month);
            month = itemView.findViewById(R.id.paymentMonth);
            monthLayout = itemView.findViewById(R.id.monthLayout);
        }
    }

    private void addNotification(String userid, CoachInfo currentCoach, Payment payment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", currentCoach.getId());
        hashMap.put("text", "Updated a new payment");
        hashMap.put("text2", "");
        hashMap.put("postid", " " + payment.getMonth() + " " + payment.getYear());
        hashMap.put("isEventRes", false);
        hashMap.put("isEventCanc", false);
        hashMap.put("ispost", false);
        hashMap.put("isEventNotGoing", false);
        hashMap.put("isEventGoing", false);
        hashMap.put("isProgress", false);
        hashMap.put("isPayment", true);
        reference.push().setValue(hashMap);
    }
}


