package com.richard.halame.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.richard.halame.Adapter.UserAdapter;
import com.richard.halame.Model.Chat;
import com.richard.halame.Model.Chatlist;
import com.richard.halame.Model.User;
import com.richard.halame.Notification.Token;
import com.richard.halame.R;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView m_recyclerView;
    private UserAdapter m_userAdapter;
    private List<User> m_users;

    private FirebaseUser f_User;
    private DatabaseReference ref_db;

    private List<Chatlist> m_Userlist;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        m_recyclerView = view.findViewById(R.id.recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        f_User = FirebaseAuth.getInstance().getCurrentUser();
        m_Userlist = new ArrayList<>();


        ref_db = FirebaseDatabase.getInstance().getReference("Chatlist").child(f_User.getUid());
        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                m_Userlist.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);

                    m_Userlist.add(chatlist);
                }

                chatList();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    //////////////////////////////////////////////////////////////////////////////////
    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(f_User.getUid()).setValue(token1);
    }


    //////////////////////////////////////////////////////////////////////////////////
    private void chatList() {
        m_users = new ArrayList<>();
        ref_db = FirebaseDatabase.getInstance().getReference("Users");
        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                m_users.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (Chatlist chatlist : m_Userlist) {
                        if (user.getId().equals(chatlist.getId())) {
                            m_users.add(user);
                        }
                    }
                }

                m_userAdapter = new UserAdapter(getContext(), m_users, true);
                m_recyclerView.setAdapter(m_userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
