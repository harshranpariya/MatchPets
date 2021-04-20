package com.example.matchpets.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.matchpets.Chat.ChatAdapter;
import com.example.matchpets.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserId , matchId , chatId;

    DatabaseReference mDatabaseUser , mDatabaseChat;

    private EditText mSendEditText;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Pets").child(currentUserId).child("connections").child("matches").child(matchId).child("chatId");

        mDatabaseChat =  FirebaseDatabase.getInstance().getReference().child("chat");
        getChatId();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDatasetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendButton = (Button) findViewById(R.id.sendMessageButton);
        mSendEditText = (EditText) findViewById(R.id.sendMessage);
        
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String messageText = mSendEditText.getText().toString();
        if(!messageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();
            Map newMessage = new HashMap();

            newMessage.put("createdBy",currentUserId);
            newMessage.put("text",messageText);


            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    chatId = snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    String message = null;
                    String createdBy = null;

                    if(snapshot.child("text").getValue() != null){
                        message = snapshot.child("text").getValue().toString();
                    }

                    if(snapshot.child("createdBy").getValue() != null){
                        createdBy = snapshot.child("createdBy").getValue().toString();
                    }

                    if(message != null && createdBy != null){
                        Boolean currentUserBool = false;
                        if(createdBy.equals(currentUserId)){
                            currentUserBool = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBool);
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();

                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();

    private List<ChatObject> getDatasetChat() {
        return resultChat;
    }
}