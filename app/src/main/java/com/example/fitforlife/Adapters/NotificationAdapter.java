package com.example.fitforlife.Adapters;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.fitforlife.Fragments.PostDetails_Fragment;
import com.example.fitforlife.Model.Notification;
import com.example.fitforlife.Model.Post;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context context, List<Notification> notification) {
        mContext = context;
        mNotification = notification;
    }

    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ImageViewHolder holder, final int position) {

        final Notification notification = mNotification.get(position);

        Log.d("noti", "noti:" + notification);
        switch (notification.getText()) {
            case "Updated a new payment":
                holder.text.setText(mContext.getResources().getString(R.string.payment_message));
                break;
            case "Added new measurements on":
                holder.text.setText(mContext.getResources().getString(R.string.measurment_message));
                break;
            case "Canceled session of":
                holder.text.setText(mContext.getResources().getString(R.string.cancel_message));
                break;
            case "is not attending on":
                holder.text.setText(mContext.getResources().getString(R.string.not_attending_message));
                break;
            case "is attending on":
                holder.text.setText(mContext.getResources().getString(R.string.attending_message));
                break;
            case "updated weight goal to":
                holder.text.setText(mContext.getResources().getString(R.string.updatedGoal_message));
                break;
            case "Rescheduled canceled session for":
                holder.text.setText(mContext.getResources().getString(R.string.reschedule_message));
                break;
            case "liked your post":
                holder.text.setText(mContext.getResources().getString(R.string.like_message));
                break;
            case "Payment reminder for":
                holder.text.setText(mContext.getResources().getString(R.string.payment_reminder_message));
                break;
            default://"commented: "
                String arr[] = notification.getText().split(" ", 2);
                String theComment = arr[1];     //the comment
                holder.text.setText(mContext.getResources().getString(R.string.comment_message) + " " + theComment);
                break;
        }

        holder.text2.setText(notification.getText2());

        getUserInfo(holder.image_profile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            holder.text2.setVisibility(View.GONE);
            getPostImage(holder.post_image, notification.getPostid());
        } else if (notification.isIsEventRes()) {
            Glide.with(mContext).load("https://firebasestorage.googleapis.com/v0/b/fitforlife-a4308.appspot.com/o/posts%2Fres.jpg?alt=media&token=d95e3c3c-3480-4f3c-af01-20baeb3da83b").into(holder.post_image);
            holder.date.setText(notification.getPostid());
            holder.text2.setVisibility(View.GONE);
        } else if (notification.isIsEventCanc()) {
            Glide.with(mContext).load("https://firebasestorage.googleapis.com/v0/b/fitforlife-a4308.appspot.com/o/posts%2Fcancel.png?alt=media&token=ccf2ec71-e811-4741-8484-b2ed8cea36bf").into(holder.post_image);
            holder.date.setText(notification.getPostid());
            holder.text2.setVisibility(View.GONE);
        } else if (notification.isIsEventNotGoing()) {
            Glide.with(mContext).load("https://firebasestorage.googleapis.com/v0/b/fitforlife-a4308.appspot.com/o/posts%2Fcancel.png?alt=media&token=eb5c8e40-20f6-4efc-91aa-c7d6048bacf1").into(holder.post_image);
            holder.date.setText(notification.getPostid());
        } else if (notification.isIsEventGoing()) {
            holder.post_image.setVisibility(View.GONE);
            holder.date.setText(notification.getPostid());
        } else if (notification.isIsProgress()) {
            Glide.with(mContext).load("https://firebasestorage.googleapis.com/v0/b/fitforlife-a4308.appspot.com/o/posts%2Fmeasurement.png?alt=media&token=07e918d6-2217-49ac-aec7-0e314b6e1571").into(holder.post_image);
            holder.date.setText(notification.getPostid());
        } else if (notification.isIsPayment()) {
            Glide.with(mContext).load("https://firebasestorage.googleapis.com/v0/b/fitforlife-a4308.appspot.com/o/posts%2Fpayment..png?alt=media&token=f7d1a462-fa76-4744-954a-7ddbc626316c").into(holder.post_image);
            holder.date.setText(notification.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIspost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetails_Fragment()).commit();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new ProfileActivity()).commit();
                }
            }
        });


    }

    //
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text, text2, date;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.notiText);
            text2 = itemView.findViewById(R.id.notiText2);
            date = itemView.findViewById(R.id.date);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageUrl()).into(imageView);
                username.setText(user.getFullName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView post_image, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


