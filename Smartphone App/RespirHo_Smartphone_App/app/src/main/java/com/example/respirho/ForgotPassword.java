package com.example.respirho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText editTextMail;
    private Button resetpassword;
    private ProgressBar progressbar;
    private TextView gobacktologin;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        resetpassword=(Button) findViewById(R.id.resetpassword);
        resetpassword.setOnClickListener(this);

        editTextMail=(TextInputEditText) findViewById(R.id.mail);

        progressbar=(ProgressBar) findViewById(R.id.progressbar);

        gobacktologin = (TextView) findViewById(R.id.gobacktologin);
        gobacktologin.setOnClickListener(this);

        auth=FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.resetpassword:
                resetPassword();
                break;

            case R.id.gobacktologin:
                onBackPressed();
                break;
        }
    }

    private void resetPassword() {
        String mail=editTextMail.getText().toString().trim();

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

        progressbar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Check your email to reset your password", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Try again, something wrong happen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}