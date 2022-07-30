package com.example.respirho;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity implements View.OnClickListener {

    //FIREBASE
    private FirebaseAuth mAuth;

    private TextInputEditText editTextMail,editTextPassword;
    private TextView donthaveanaccount,forgotpassword;
    private Button login,helpbutton;
    private ProgressBar progressbar;

    private Button signinwithgoogle;
    private GoogleSignInClient googleSignInClient;

    private CheckBox keepmeloggedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth=FirebaseAuth.getInstance();

        editTextMail=(TextInputEditText) findViewById(R.id.mail);
        editTextPassword=(TextInputEditText) findViewById(R.id.password);

        donthaveanaccount = findViewById(R.id.donthaveanaccount);
        donthaveanaccount.setOnClickListener(this);

        login=findViewById(R.id.login);
        login.setOnClickListener(this);

        keepmeloggedin=(CheckBox) findViewById(R.id.keepmeloggedin);
        keepmeloggedin.setOnClickListener(this);

        forgotpassword=findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(this);

        helpbutton=findViewById(R.id.helpbutton);
        helpbutton.setOnClickListener(this);

        progressbar=findViewById(R.id.progressbar);

        SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox=preferences.getString("keepmeloggedin","");

        if(checkbox.equals("true")){
            redirectActivity(Login.this,PatientsList.class);
        }
        else if(checkbox.equals("false")){
            Toast.makeText(getApplicationContext(), "Please sign in", Toast.LENGTH_SHORT).show();
        }

        //Sign in with google
        //implements the "sign me with google" functionality here
        signinwithgoogle=(Button) findViewById(R.id.signinwithgoogle);

        //).requestIdToken(getString(R.string.default_web_client_id))
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("107390019239-v46lmffb5f974m8h7r8385eoirl4kcm4.apps.googleusercontent.com")
                .requestEmail().build();

        //initialize sign in client
        googleSignInClient= GoogleSignIn.getClient(Login.this,googleSignInOptions);

        signinwithgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize sig n intent
                Intent intent=googleSignInClient.getSignInIntent();
                //start activity for result
                startActivityForResult(intent,100); //check onActivityResult function later
            }
        });

        //implements the "keep me logged in" functionality here
        keepmeloggedin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor= preferences.edit();
                    editor.putString("keepmeloggedin","true");
                    editor.apply();
                }
                else if(!compoundButton.isChecked()){
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor= preferences.edit();
                    editor.putString("keepmeloggedin","false");
                    editor.apply();
                }
            }
        });
    }

    //disable back button in this page to prevent user going back to logo page
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.donthaveanaccount:
                redirectActivity(Login.this,Register.class);
            break;

            case R.id.login:
                loginUser();
            break;

            case R.id.forgotpassword:
                redirectActivity(Login.this,ForgotPassword.class);
            break;

            case R.id.helpbutton:
                GlobalVariables.flag_help_login=true;
                MessageDialog messageDialog =new MessageDialog();
                messageDialog.show(getSupportFragmentManager(),"help messageDialog");
            break;
        }
    }

    private void loginUser() {
        String mail=editTextMail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

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

        progressbar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //verify email
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        //redirect user profile
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        redirectActivity(Login.this,PatientsList.class);
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(getApplicationContext(), "Check email to verify account and then click 'LOG IN' button again", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Login fail,retry", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //check condition
        if(requestCode == 100)
        {
            //initialize task
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            //check condition
            if(signInAccountTask.isSuccessful())
            {
                Toast.makeText(getApplicationContext(), "Google sign in successful", Toast.LENGTH_SHORT).show();

                try
                {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);

                    if(googleSignInAccount!= null) {
                        //initialize auth credentials
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        , null);
                        //check credentials
                        mAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                        //check condition
                                        if (task.isSuccessful()) {
                                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                            if(acct!=null){

                                                String fullname_google=acct.getDisplayName();
                                                String mail_google=acct.getEmail();

                                                UserWithGoogle userWithGoogle=new UserWithGoogle(fullname_google,mail_google);

                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userWithGoogle)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    //Toast.makeText(getApplicationContext(), "New google user added", Toast.LENGTH_SHORT).show();
                                                                    //progressbar.setVisibility(View.GONE);

                                                                }else{
                                                                    //Toast.makeText(getApplicationContext(), "Fail to add google user", Toast.LENGTH_SHORT).show();
                                                                    //progressbar.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                            }

                                            redirectActivity(Login.this, PatientsList.class);
                                        }
                                    }
                                });
                    }
                } catch (ApiException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
