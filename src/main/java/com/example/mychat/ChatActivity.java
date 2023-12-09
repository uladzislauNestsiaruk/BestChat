package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ListView messageList;
    private MessageAdapter messageAdapter;
    private ImageButton sendImageButton;
    private Button sendMessageButton;
    private EditText messageEditText;
    private static final int requestImageCode = 124;
    public String userName;
    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference, usersDatabaseReference;
    ChildEventListener messagesEventListener, usersDatabaseEventListener;
    FirebaseStorage storage;
    StorageReference ImageStorageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        if(intent != null)
        userName = intent.getStringExtra("UserName");
        else
            userName = "User";
        List<MyChatMessage> messages = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        ImageStorageReference = storage.getReference().child("Images");
        messageList = findViewById(R.id.ListMessages);
        messageAdapter = new MessageAdapter(this, R.layout.message_item, messages);
        messageList.setAdapter(messageAdapter);
        sendImageButton = findViewById(R.id.SentPhotoImageButton);
        sendMessageButton = findViewById(R.id.ButtonSendMessage);
        messageEditText = findViewById(R.id.EditTextMessage);
        database = FirebaseDatabase.getInstance();
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        ///////////////////////////////////////////Message editText
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                   if(s.toString().trim().length() > 0)
                       sendMessageButton.setEnabled(true);
                   else
                       sendMessageButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        ////////////////////////////////////Sent message
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyChatMessage message = new MyChatMessage();
                message.SetText(String.valueOf(messageEditText.getText()));
                message.SetName(userName.toUpperCase());
                message.SetImageUrl(null);
                messagesDatabaseReference.push().setValue(message);
                messageEditText.setText("");
            }
        });
        ///////////////////////////////////////////////Sent picture
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "choose an image"), requestImageCode);
            }
        });
        //////////////////////////////////////////////Messages event listener
        messagesEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MyChatMessage message = dataSnapshot.getValue(MyChatMessage.class);
                messageAdapter.add(message);
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
        };messagesDatabaseReference.addChildEventListener(messagesEventListener);
        /////////////////////////////////////////////Users event listener
        usersDatabaseEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if(user.GetId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                userName = user.name;
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
        }; usersDatabaseReference.addChildEventListener(usersDatabaseEventListener);
    }
        ////////////////////////////////////////////Menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
        ////////////////////////////////////////////Menu options logic
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.SignOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        switch (requestCode){
            case requestImageCode:
             Uri selectedImage = data.getData();
             final StorageReference imageReference = ImageStorageReference.child(selectedImage.getLastPathSegment());
                UploadTask uploadTask = imageReference.putFile(selectedImage);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            MyChatMessage message = new MyChatMessage();
                            message.SetImageUrl(String.valueOf(downloadUri));
                            message.SetName(userName);
                            messagesDatabaseReference.push().setValue(message);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
                break;
        }
    }
}
