package com.richard.halame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.richard.halame.Adapter.GroupAdapter;
import com.richard.halame.Adapter.GroupChatAdapter;
import com.richard.halame.Adapter.MessageAdapter;
import com.richard.halame.Model.Chat;
import com.richard.halame.Model.Group;
import com.richard.halame.Model.Groupchat;
import com.richard.halame.Notification.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private CircleImageView m_groupImage;
    private TextView m_groupName;
    private String groupId;

    private FirebaseUser f_User;
    private DatabaseReference ref_db;

    private Intent intent;
    private Toolbar toolbar;
    private String myId, groupName;

    private ImageButton messageSendBtn, mediaSendBtn;
    private EditText messageField;


    private List<Groupchat> m_chat;
    private RecyclerView m_recyclerView;
    private GroupChatAdapter groupAdapter;

    private Calendar calcDate, calcTime;
    private String currentDate, currentTime;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        intent = getIntent();
        groupId = intent.getStringExtra("group_id");
        Log.e("GROUP ID ", groupId);

        initComponents();

        myId = f_User.getUid();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        m_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setReverseLayout(true);
        m_recyclerView.setLayoutManager(linearLayoutManager);


        messageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageField.getText().toString();

                if (!TextUtils.isEmpty(message)) {
                    sendGroupMessage(myId, message);
                }

                messageField.setText("");
            }
        });

        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Group Data Snapshot", dataSnapshot.toString());

                Group group = dataSnapshot.getValue(Group.class);

                m_groupName.setText(group.getGroupName());
                groupName = group.getGroupName();
                Log.e("GROUP NAME ", groupName);

                if (group.getImageURL().equals("default")) {
                    m_groupImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(group.getImageURL()).into(m_groupImage);
                }

                readGroupMessages(myId, group.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /////////////////////////////////////////////////////
    /// This Function is responsible for Reading the group messages
    /////
    private void readGroupMessages(String myId, final String imageurl) {
        m_chat = new ArrayList<>();

        ref_db = FirebaseDatabase.getInstance().getReference("GroupMessages").child(groupId);
        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                m_chat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Groupchat groupchat = snapshot.getValue(Groupchat.class);

                    m_chat.add(groupchat);

                    groupAdapter = new GroupChatAdapter(getApplicationContext(), m_chat, imageurl);
                    m_recyclerView.setAdapter(groupAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // Read Group Messages

    /////////////////////////////////////////////////////
    /// This Function Will Handle the Sending of Groups Messages To Database
    /////////////////////////////////////////////////////
    private void sendGroupMessage(String senderID, String message) {
        currentDate = simpleDateFormat.format(calcDate.getTime());
        currentTime = simpleTimeFormat.format(calcTime.getTime());

//        DatabaseReference reference = FirebaseDatabase.getInstance()
//                .getReference("Groups").child("Group Messages");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderID);
        hashMap.put("message", message);
        hashMap.put("date", currentDate);
        hashMap.put("time", currentTime);

        ref_db = FirebaseDatabase.getInstance().getReference("GroupMessages");
        ref_db.child(groupId).push().setValue(hashMap);
    } //Send Group Messages Function Ends Here


    ///////////////////////////////////////////////////////////////////////
    /// This function is responsible for Initialising the components of the ui
    ///////////////////////////////////////////////////////////////////////
    private void initComponents() {
        toolbar = findViewById(R.id.toolBar);
        m_groupImage = findViewById(R.id.groupImage);
        m_groupName = findViewById(R.id.groupName);

        //        mediaSendBtn = findViewById(R.id.mediaBtn);
        messageSendBtn = findViewById(R.id.sendBtn);
        messageField = findViewById(R.id.messageInputField);

        m_recyclerView = findViewById(R.id.recyclerView);

        calcDate = Calendar.getInstance();
        calcTime = Calendar.getInstance();

        f_User = FirebaseAuth.getInstance().getCurrentUser();
        ref_db = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);

    }// Init Components Function Ends Here;
}
