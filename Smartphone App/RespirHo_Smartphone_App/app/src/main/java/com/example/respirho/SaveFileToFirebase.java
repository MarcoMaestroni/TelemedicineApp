package com.example.respirho;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.time.LocalDateTime;

public class SaveFileToFirebase extends DemoDownload {

    //FIREBASE
    private StorageReference mStorageRef;

    private StorageTask mUploadTask;

    private Uri fileUri;

    private long fileIntSizeBytes,fileIntSizeKyloBytes;

    public void mainFirebase(File fileInt){

        //get Uri needed to upload file on Firebase storage
        fileUri=Uri.fromFile(fileInt);

        if(fileUri!=null){
            mStorageRef= FirebaseStorage.getInstance().getReference("Patients").child(GlobalVariables.string_idpatient)
                    .child(GlobalVariables.string_idpatient +" - " + GlobalVariables.setCurrentTimeFile +".txt");

            mUploadTask= mStorageRef.putFile(fileUri);

            mUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e("saveFirebase", "ERROR:file not uploaded on Firebase");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle unsuccessful uploads
                    Log.e("saveFirebase", "OK:file uploaded on Firebase");
                }
            });
        }
        else{
            //error dialog
            Log.e("saveFirebase", "ERROR:file is null");
        }
    }
}
