package com.richard.halame.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.richard.halame.MessageActivity;
import com.richard.halame.Model.User;
import com.richard.halame.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context m_context;
    private List<User> m_users;
    private boolean ToF;


    public UserAdapter(Context m_context, List<User> m_users, boolean ToF) {
        this.m_context = m_context;
        this.m_users = m_users;
        this.ToF = false;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(m_context).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User user = m_users.get(position);
        holder.m_ProfileName.setText(user.getUsername());

        if (user.getImageURL().equals("default")) {
            holder.m_ProfileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(m_context).load(user.getImageURL()).into(holder.m_ProfileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(m_context, MessageActivity.class);
                    intent.putExtra("userid", user.getId());
                    m_context.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(m_context, "Failed To Get "+holder.m_ProfileName, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_users.size();
    }


    ////////////////////////////////////////////////
    /// This inner Class extends the view holder for holding the users
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView m_ProfileName;
        private ImageView m_ProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            m_ProfileImage = itemView.findViewById(R.id.profileImage);
            m_ProfileName = itemView.findViewById(R.id.profileName);
        }
    }
}
