package com.example.mychat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {
   private DatabaseReference UsersDatabaseReference;
   private ChildEventListener UsersChildEventListener;
   private ArrayList<User> users = new ArrayList<>();
   private RecyclerView userRecyclerView;
   private UserAdapter userAdapter;
   private RecyclerView.LayoutManager userLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        attachUserDatabaseReferenceListener();
        buildRecyclerView();
    }

    private void attachUserDatabaseReferenceListener() {
        UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        if(UsersChildEventListener == null)
            UsersChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User user = dataSnapshot.getValue(User.class);
                    user.SetAvatarResource(R.drawable.ic_insert_photo_black_24dp);
                    users.add(user);
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        UsersDatabaseReference.addChildEventListener(UsersChildEventListener);
    }

    private void buildRecyclerView() {
        userRecyclerView = findViewById(R.id.UserList);
        userRecyclerView.setHasFixedSize(true);
        userLayoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(users);
        userRecyclerView.setLayoutManager(userLayoutManager);
        userRecyclerView.setAdapter(userAdapter);
    }
}
