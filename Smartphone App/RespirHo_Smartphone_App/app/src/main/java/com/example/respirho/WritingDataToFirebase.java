package com.example.respirho;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class WritingDataToFirebase extends AppCompatActivity {

    //FIREBASE
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public void mainFirebase(String message, String startrec_time){

        //TODO--try to put these two lines out of the mainFirebase
        database=FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Patients").child(GlobalVariables.string_idpatient).child(startrec_time).push();

        myRef.setValue(message);
    }
}
