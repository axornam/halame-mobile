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
import com.google.firebase.auth.FirebaseUser;
import com.richard.halame.GroupActivity;
import com.richard.halame.Model.Group;
import com.richard.halame.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 0;

    private Context m_context;
    private List<Group> m_groupchat;

    private FirebaseUser f_user;

    public GroupAdapter(Context m_context, List<Group> m_groupchat) {
        this.m_context = m_context;
        this.m_groupchat = m_groupchat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(m_context).inflate(R.layout.groups_item, parent, false);

        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupAdapter.ViewHolder holder, int position) {

        final Group group = m_groupchat.get(holder.getAdapterPosition());
        holder.m_GroupName.setText(group.getGroupName());

        if(group.getImageURL().equals("default")){
            holder.m_GroupImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(m_context).load(group.getImageURL()).into(holder.m_GroupImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(m_context, GroupActivity.class);
                    intent.putExtra("group_id", group.getId());
                    m_context.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(m_context, "Failed to Get "+holder.m_GroupName, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return m_groupchat.size();
    }


    //////////////////////////////////////////////////////////////
    /// This is the custom inner class which extends the Recycler view holder
    //// That holds the messages
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView m_GroupName;
        private ImageView m_GroupImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            m_GroupImage = itemView.findViewById(R.id.groupImage);
            m_GroupName = itemView.findViewById(R.id.groupName);
        }
    }
}
