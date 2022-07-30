package com.example.respirho;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {

    //FIREBASE
    private FirebaseAuth mAuth;

    private TextInputEditText editTextFullname,editTextPhonenumber,editTextMail,editTextPassword,editTextConfirmpassword;
    private ProgressBar progressbar;
    private TextView alreadyamember;
    private Button register,helpbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        editTextFullname=(TextInputEditText) findViewById(R.id.fullname);
        editTextPhonenumber=(TextInputEditText) findViewById(R.id.phonenumber);
        editTextMail=(TextInputEditText) findViewById(R.id.mail);
        editTextPassword=(TextInputEditText) findViewById(R.id.password);
        editTextConfirmpassword=(TextInputEditText) findViewById(R.id.confirmpassword);

        progressbar=(ProgressBar) findViewById(R.id.progressbar);

        alreadyamember = (TextView) findViewById(R.id.alreadyamember);
        alreadyamember.setOnClickListener(this);

        register=(Button) findViewById(R.id.register);
        register.setOnClickListener(this);

        helpbutton=(Button) findViewById(R.id.helpbutton);
        helpbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alreadyamember:
                onBackPressed();
                break;

            case R.id.register:
                registerUser();
                break;

            case R.id.helpbutton:
                GlobalVariables.flag_help_register=true;
                MessageDialog messageDialog =new MessageDialog();
                messageDialog.show(getSupportFragmentManager(),"help messageDialog");
                break;
        }
    }

    private void registerUser() {

        final String fullname=editTextFullname.getText().toString().trim();
        final String phonenumber=editTextPhonenumber.getText().toString().trim();
        final String mail=editTextMail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String confirmpassword=editTextConfirmpassword.getText().toString().trim();

        if(fullname.isEmpty()){
            editTextFullname.setError("Full name is required");
            editTextFullname.requestFocus();
            return;
        }

        if(phonenumber.isEmpty()){
            editTextPhonenumber.setError("Phone number is required");
            editTextPhonenumber.requestFocus();
            return;
        }

        if(mail.isEmpty()){
            editTextMail.setError("Email is required");
            editTextMail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            editTextMail.setError("Please provide valid email");
            editTextMail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Minimum password length should be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if(confirmpassword.isEmpty()){
            editTextConfirmpassword.setError("Confirm password is required");
            editTextConfirmpassword.requestFocus();
            return;
        }

        if(!(password.matches(confirmpassword))){
            editTextConfirmpassword.setError("Confirm password doesn't match the password");
            editTextConfirmpassword.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            User user = new User(fullname,phonenumber,mail);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(), "Registration successful!\nYou can now Login with your credentials", Toast.LENGTH_LONG).show();
                                        progressbar.setVisibility(View.GONE);

                                        redirectActivity(Register.this,Login.class);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Fail to add user", Toast.LENGTH_SHORT).show();
                                        progressbar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "Fail to add user,retry", Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private static void redirectActivity (Activity activity, Class aClass){
        //initialize intent
        Intent intent = new Intent(activity, aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Start activity
        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


