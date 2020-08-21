package com.richard.halame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.richard.halame.Adapter.MessageAdapter;
import com.richard.halame.Fragments.APIService;
import com.richard.halame.Model.Chat;
import com.richard.halame.Model.User;
import com.richard.halame.Notification.Client;
import com.richard.halame.Notification.Data;
import com.richard.halame.Notification.MyResponse;
import com.richard.halame.Notification.Sender;
import com.richard.halame.Notification.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    APIService apiService;
    boolean notify = false;
    private CircleImageView m_ProfileImage;
    private TextView m_ProfileName;
    private String userId; // This is the id that will be got from the users activity and passed into the message activity for unique user identification
    private FirebaseUser f_User;
    private DatabaseReference ref_db;
    private Intent intent;
    private Toolbar toolbar;
    private ImageButton mediaSendBtn, messageSendBtn;
    private EditText messageField;
    private MessageAdapter messageAdapter;
    private List<Chat> m_Chat;
    private RecyclerView m_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // This portion gets the user if from chat view using a fragment
        intent = getIntent();
        userId = intent.getStringExtra("userid");
        Log.e("Unique UID ", userId);

        //Initialising Components
        initComponents();


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            public void onClick(View v) {
                notify = true;
                String message = messageField.getText().toString();

                if (!TextUtils.isEmpty(message)) {
                    sendMessage(f_User.getUid(), userId, message);
                }

                messageField.setText("");
            }
        });

        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("DataSnapshot", dataSnapshot.toString());

                User user = dataSnapshot.getValue(User.class);
                m_ProfileName.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    m_ProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(m_ProfileImage);
                }

                readMessage(f_User.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }// Overridden Oncreate methods ends here
    ////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////
    /// This function will be responsible for sending messages to selected users
    ////////////////////////////////////////////////////////////////////////
    private void sendMessage(String s_uid, final String r_uid, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", s_uid);
        hashMap.put("receiver", r_uid);
        hashMap.put("message", message);

        databaseReference.child("Chats").push().setValue(hashMap);


        // this adds a user to the chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(f_User.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /////////////////////////////////////////////////////////////////////////////
        //This is the part that handles Notifications and all the other stuffs
        /////////////////////////////////////////////////////////////////////////
        final String msg = message;

        ref_db = FirebaseDatabase.getInstance().getReference("Users").child(f_User.getUid());
        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(r_uid, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }// Send Message Function Ends Here

    //////////////////////////////////////////////////////////////////////
    //// This Function Will handle Notification Sending Features
    /////////////////////////////////////////////////////////////////////
    private void sendNotification(String reciever, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(f_User.getUid(), R.mipmap.ic_launcher_round, username + ": " + message, "New Message", userId);


                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed To ge Notification", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ///////////////////////////////////////////////////////////////////////
    /// This function will be responsible for reading messages sent to the database
    //////////////////////////////////////////////////////////////////////
    private void readMessage(final String myuid, final String userid, final String imageurl) {
        m_Chat = new ArrayList<>();

        ref_db = FirebaseDatabase.getInstance().getReference("Chats");
        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                m_Chat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(myuid) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myuid)) {

                        m_Chat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, m_Chat, imageurl);
                    m_recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    ///////////////////////////////////////////////////////////////////////
    /// This function is responsible for Initialising the components of the ui
    ///////////////////////////////////////////////////////////////////////
    private void initComponents() {
        toolbar = findViewById(R.id.toolBar);
        m_ProfileImage = findViewById(R.id.profileImage);
        m_ProfileName = findViewById(R.id.profileName);

//        mediaSendBtn = findViewById(R.id.mediaBtn);
        messageSendBtn = findViewById(R.id.sendBtn);
        messageField = findViewById(R.id.messageInputField);

        m_recyclerView = findViewById(R.id.recyclerView);

        // Initing the API Service;
        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);

        f_User = FirebaseAuth.getInstance().getCurrentUser();
        ref_db = FirebaseDatabase.getInstance().getReference("Users").child(userId);
    }// Init Components Ends Here
}
