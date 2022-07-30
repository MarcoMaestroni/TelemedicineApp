package com.example.respirho;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


public class MessageDialog extends AppCompatDialogFragment{

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        //TODO-- set a switch case for different dialogs
        //if it's a message with negative and positive button (logout,app,downlaod)...
        if(GlobalVariables.flag_download) {

        }

        if(GlobalVariables.flag_logout) {

        }

        if(GlobalVariables.flag_closeapp) {

        }

        //otherwise if it's a message with only positive button (help)...

        if(GlobalVariables.flag_help_login){
            builder.setTitle("Help");
            builder.setMessage(R.string.help_login);
            GlobalVariables.flag_help_login=false;
        }

        else if(GlobalVariables.flag_help_register){
            builder.setTitle("Help");
            builder.setMessage(R.string.help_register);
            GlobalVariables.flag_help_register=false;
        }

        else if(GlobalVariables.flag_help_patientslist){
            builder.setTitle("Help");
            builder.setMessage(R.string.help_patientslist);
            GlobalVariables.flag_help_patientslist=false;
        }

        else if(GlobalVariables.flag_help_patientdata){
            builder.setTitle("Help");
            builder.setMessage(R.string.help_patientdata);
            GlobalVariables.flag_help_patientdata=false;
        }

        else if(GlobalVariables.flag_help_demodownload){
            builder.setTitle("Help");
            builder.setMessage(R.string.help_demodownload);
            GlobalVariables.flag_help_demodownload =false;
        }

        else if(GlobalVariables.flag_help_support){
            builder.setTitle("Help");
            builder.setMessage(R.string.help_support);
            GlobalVariables.flag_help_support =false;
        }

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing so it closes itself
            }
        });
     return builder.create();
    }

    private static void redirectActivity(Activity activity, Class aClass) {
        //initialize intent
        Intent intent= new Intent (activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Start activity
        activity.startActivity(intent);
    }
}
