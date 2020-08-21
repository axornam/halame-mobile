package com.richard.halame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.richard.halame.Fragments.ChatsFragment;
import com.richard.halame.Fragments.GroupsFragment;
import com.richard.halame.Fragments.UsersFragment;
import com.richard.halame.Model.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CircleImageView m_ProfileImage;
    private TextView m_ProfileName;
    private Toolbar toolbar;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;

    private FirebaseUser f_User;
    private DatabaseReference ref_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initcomponents
        initComponents();

        //Handle Creation of Support ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

//        Add Value Event Listener to the firebase database reference
        ref_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                    m_ProfileName.setText(user.getUsername());

                if(user.getImageURL().equals("default")){
                    //once the image url is default lets set the imageIcon to
                    //android mipmap icon
                    m_ProfileImage.setImageResource(R.mipmap.ic_launcher);

                }else{
                    //Use Glide Library to load Image URl into the Image view
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(m_ProfileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Add More Fragments to the View pager
        viewPageAdapter.addFragments(new UsersFragment(), "USERS");
//        viewPageAdapter.addFragments(new ChatsFragment(), "CHATS");
        viewPageAdapter.addFragments(new GroupsFragment(), "GROUPS");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////
    /// This is an Inner Class that is supposed to hold the view pages
    ////
    class ViewPageAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPageAdapter(@NonNull FragmentManager fm) {
            super(fm);

            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
        //////////////////////////////////////////////
        /// The Inner Class Ends Here
    }////////////////////////////////////////////////////////////




    //////////////////////////////////////////////////////////////////
    //// This function is supposed to initialise the components of the xml file
    ////////////
    private void initComponents() {
        //Init UI components
        m_ProfileImage = findViewById(R.id.profileImage);
        m_ProfileName = findViewById(R.id.profileName);
        toolbar = findViewById(R.id.toolBar);

        //Init Fragment items
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        //Init FireBase components
        f_User = FirebaseAuth.getInstance().getCurrentUser();
        ref_db = FirebaseDatabase.getInstance().getReference("Users").child(f_User.getUid());
        Log.e("Value", "User id is " + f_User.getUid());

    }
}
