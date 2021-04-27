package com.example.fitforlife.Adapters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitforlife.Fragments.PostDetails_Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Model.Post;
import com.example.fitforlife.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SquaresAdapter extends RecyclerView.Adapter<SquaresAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public SquaresAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public SquaresAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return new SquaresAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SquaresAdapter.ImageViewHolder holder, final int position) {

        final Post post = mPosts.get(position);

        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetails_Fragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image;


        public ImageViewHolder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);

        }
    }
}
