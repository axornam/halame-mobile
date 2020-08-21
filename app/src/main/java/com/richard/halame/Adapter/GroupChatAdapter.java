package com.richard.halame.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.richard.halame.Model.Groupchat;
import com.richard.halame.R;



import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder>{
    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;

    private Context m_context;
    private List<Groupchat> m_chat;
    private String imageURL;

    private FirebaseUser f_User;

    public GroupChatAdapter(Context m_context, List<Groupchat> m_chat, String imageURL) {
        this.m_context = m_context;
        this.m_chat = m_chat;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public GroupChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MESSAGE_TYPE_LEFT){
            View view = LayoutInflater.from(m_context).inflate(R.layout.chat_item_left, parent, false);
            return new GroupChatAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(m_context).inflate(R.layout.chat_item_right, parent, false);
            return new GroupChatAdapter.ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull GroupChatAdapter.ViewHolder holder, int position) {

//        Chat chat = m_chat.get(position);
        Groupchat groupchat = m_chat.get(holder.getAdapterPosition());


        holder.m_ShowMessage.setText(groupchat.getMessage());
        Log.e("Message", groupchat.getMessage());
        Log.e("Sender", groupchat.getSender());

        if(imageURL.equals("default")){
            holder.m_ProfileImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(m_context).load(imageURL).into(holder.m_ProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return m_chat.size();
    }

    @Override
    public int getItemViewType(int position) {
        f_User = FirebaseAuth.getInstance().getCurrentUser();

        if(m_chat.get(position).getSender().equals(f_User.getUid())){
            return MESSAGE_TYPE_RIGHT;
        }else{
            return MESSAGE_TYPE_LEFT;
        }
    }

    ////////////////////////////////////////////////
    /// This inner Class extends the view holder for holding the users
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView m_ShowMessage;
        private ImageView m_ProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            m_ProfileImage = itemView.findViewById(R.id.profileImage);
            m_ShowMessage = itemView.findViewById(R.id.showMessage);
        }
    }

}
