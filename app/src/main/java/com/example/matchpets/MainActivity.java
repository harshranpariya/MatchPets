package com.example.matchpets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private  Cards cardsData[];
    private arrayAdapter arrayAdapter;
    private int i;

    //this variable store all the info about logged in user
    private FirebaseAuth myAuth;

    private String currentUId;
    private DatabaseReference petsDb;



    ListView listView;
    List<Cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        petsDb = FirebaseDatabase.getInstance().getReference().child("Pets");
        myAuth = FirebaseAuth.getInstance();
        currentUId = myAuth.getCurrentUser().getUid();

        checkPetType();

        //add is for name of the card
        rowItems = new ArrayList<Cards>();
        getSupportActionBar().setTitle("Home");

        //here layout is textview for cards' color and text
        arrayAdapter = new arrayAdapter(this, R.layout.item,rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);


        flingContainer.setAdapter(arrayAdapter);

        //flingListener is for click and move the cards
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                Cards obj = (Cards) dataObject;
                String petId = obj.getUserId();
                petsDb.child(petType).child(petId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Not Intrested", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String petId = obj.getUserId();
                petsDb.child(petType).child(petId).child("connections").child("yes").child(currentUId).setValue(true);
                isConnectionMatch(petId);
                Toast.makeText(MainActivity.this, "Intrested", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Click", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(String petId) {
        DatabaseReference currentPetConnectionDb = petsDb.child(petType).child(currentUId).child("connections").child("yes").child(petId);
        currentPetConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this , "new Connection",Toast.LENGTH_LONG).show();
                    petsDb.child(petType).child(snapshot.getKey()).child("connections").child("matches").child(currentUId).setValue(true);
                    petsDb.child(petType).child(currentUId).child("connections").child("matches").child(snapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String petType;
    private String otherPetType;

    public void checkPetType(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference dogDb = FirebaseDatabase.getInstance().getReference().child("Pets").child("Dog");
        dogDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(user.getUid())){
                    petType = "Dog";
                    otherPetType = "Cat";
                    getSameTypePets();
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

        DatabaseReference catDb = FirebaseDatabase.getInstance().getReference().child("Pets").child("Cat");
        catDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(user.getUid())){
                    petType = "Cat";
                    otherPetType = "Dog";
                    getSameTypePets();
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

    public void getSameTypePets(){
        DatabaseReference sameTypeDb = FirebaseDatabase.getInstance().getReference().child("Pets").child(petType);
        sameTypeDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    if (snapshot.exists() && !snapshot.child("connections").child("nope").hasChild(currentUId) && !snapshot.child("connections").child("yes").hasChild(currentUId)) {

                        String profileImageUrl = "default";
                        if(!snapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                        }

                        Cards item = new Cards(snapshot.getKey(), snapshot.child("Name").getValue().toString(), profileImageUrl);

                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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


    public void logoutUser(View view) {
        myAuth.signOut();
        Intent intent = new Intent(MainActivity.this,ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);

        intent.putExtra("petType", petType);
        startActivity(intent);
        return;
    }
}