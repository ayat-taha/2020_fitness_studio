package com.example.fitforlife.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.fitforlife.R;
import com.example.fitforlife.Model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> users, boolean isFragment){
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.username.setText(user.getFullName());
        holder.userType.setText(user.getType());

        Log.d("type",""+user.getType() );

        Glide.with(mContext).load(user.getImageUrl()).into(holder.image_profile);
    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username, userType;
        public CircleImageView image_profile;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            userType = itemView.findViewById(R.id.userType);
            image_profile = itemView.findViewById(R.id.image_profile);

        }
    }

}