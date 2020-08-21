package com.richard.halame.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.richard.halame.Adapter.GroupAdapter;
import com.richard.halame.Model.Group;
import com.richard.halame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private FloatingActionButton m_createGroups;
    private DatabaseReference ref_db;

    private RecyclerView m_recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> m_groups;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);


        //initComponents;
        m_recyclerView = view.findViewById(R.id.recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupAdapter = new GroupAdapter(getContext(), m_groups);

        ref_db = FirebaseDatabase.getInstance().getReference("Groups");

        m_groups = new ArrayList<>();

        readGroups(); /// This function will retrieve all the groups from firebase database


        //////////////////////////////////////////////////////////////////////////////////////
        /// This Function Enables the user to click on the fab icon and launch an alert dialog
        /// With an edit text for creating groups
        m_createGroups = view.findViewById(R.id.createGroup);
        m_createGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                /////////////////////////////////////////////////////////////
                //Create An AlertDialog and Set Inflater with a view(xml file)
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View myview = layoutInflater.inflate(R.layout.create_group, null);

                // Pass AlertDialog builder to Alert dialog class
                final AlertDialog dialog = builder.create();
                dialog.setView(myview);

                final EditText m_group = myview.findViewById(R.id.newGroupName);
                Button m_createGroup = myview.findViewById(R.id.createGroupBtn);

                m_createGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String groupName = m_group.getText().toString();

                        if (TextUtils.isEmpty(groupName)) {

                        } else {
                            // Launch The Function that will create The New Group
                            createNewGroup(groupName);
                            dialog.dismiss();
                        }
                    }
                });


//                final EditText editText = new EditText(getContext());
//                editText.setHint("eg. KC Boys..");
//                editText.setPadding(20, 5, 20, 5);
//                editText.setHintTextColor(Color.DKGRAY);
//                builder.setView(editText);

////                 Set what the positive creating button will do
//                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String groupName = editText.getText().toString();
//
//                        if (TextUtils.isEmpty(groupName)) {
//
//                        } else {
//                            // launch the function that will create new groups
//                            createNewGroup(groupName);
//                        }
//                    }
//                });
//
//                // Set What the negative button will do
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });

                dialog.show();

            }
        });///Prompt Launching And Alert Dialog box opening Ends Here
        /////////////////////////////////////////////////////////////


        return view;
    }

    /////////////////////////////////////////////////////////
    /// This Function Will Be to read The groups from firebase database
    ///
    private void readGroups() {
        /// retrieve all groups
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                m_groups.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Group group = snapshot.getValue(Group.class);

                    assert group != null;
                    m_groups.add(group);
                }

                groupAdapter = new GroupAdapter(getContext(), m_groups);
                m_recyclerView.setAdapter(groupAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }///Read Group Functions Ends here


    //////////////////////////////////////////////
    // Create New Group Function Starts here
    private void createNewGroup(final String groupname) {
        // Generate A Unique key for each group
        String key = ref_db.push().getKey();

        //Now Lets Push the user details into firebase realtime database
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", key);
        hashMap.put("imageURL", "default");
        hashMap.put("groupName", groupname);


                ref_db.child(key).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Snackbar.make(getView(), groupname + " Created Successfully", Snackbar.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Failed To Create Group", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }/// Create New Group Function Ends Here;

}
