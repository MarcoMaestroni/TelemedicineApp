package com.example.respirho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Support extends AppCompatActivity implements View.OnClickListener {

    private Button helpbutton;

    //STORAGE variables
    private StorageReference mStorageRef;
    private String extPath;
    private String path;

    private TextView supportmail;
    private Button userguide,sensorsguide;
    private ProgressBar progressbar_support;

    private final String userGuideFilename="Guida_utente_app";
    private final String sensorsGuideFilename="Manuale_RespirHò_Unito";
    private String fileToDownload="";

    //TODO-- drawer
    private ImageButton idicon,arrowback;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawer_home, drawer_support,drawer_logout,drawer_closeapp;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    //TODO-- END drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support);

        helpbutton = (Button) findViewById(R.id.helpbutton);
        helpbutton.setOnClickListener(this);

        userguide=(Button) findViewById(R.id.userguide);
        userguide.setOnClickListener(this);

        sensorsguide=(Button) findViewById(R.id.sensorsguide);
        sensorsguide.setOnClickListener(this);

        progressbar_support=(ProgressBar) findViewById(R.id.progressbar_support);

        supportmail=(TextView) findViewById(R.id.supportmail);
        supportmail.setOnClickListener(this);

        //TODO-- drawer
        drawerLayout=findViewById(R.id.drawer_layout);

        drawer_home=findViewById(R.id.drawer_home);
        drawer_home.setOnClickListener(this);

        drawer_support =findViewById(R.id.drawer_support);
        drawer_support.setOnClickListener(this);

        drawer_logout=findViewById(R.id.drawer_logout);
        drawer_logout.setOnClickListener(this);

        drawer_closeapp=findViewById(R.id.drawer_closeapp);
        drawer_closeapp.setOnClickListener(this);

        idicon=findViewById(R.id.idicon);
        idicon.setOnClickListener(this);

        arrowback=findViewById(R.id.arrowback);
        arrowback.setOnClickListener(this);

        final TextView drawerMail=(TextView) findViewById(R.id.drawermail);
        final TextView drawerFullname=(TextView) findViewById(R.id.drawerfullname);

        //check the user info to display on drawer ONLY IN PATIENTS LIST
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");

        //if the user is not null
        if(user!=null){
            userID=user.getUid();

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User userDrawer=snapshot.getValue(User.class);

                    if(userDrawer !=null){
                        String mail=userDrawer.mail;
                        String fullname=userDrawer.fullname;
                        drawerMail.setText(mail);
                        drawerFullname.setText(fullname);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Something wrong happen", Toast.LENGTH_LONG).show();
                }
            });
        }
        //TODO-- end drawer
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.idicon:
                openDrawer(drawerLayout);
                break;

            case R.id.arrowback:
                closeDrawer(drawerLayout);
                break;

            case R.id.drawer_home:
                redirectActivity(Support.this,PatientsList.class);
                break;

            case R.id.drawer_support:
                closeDrawer(drawerLayout);
                break;

            case R.id.drawer_logout:
                //call logout function
                logout(this);
                break;

            case R.id.drawer_closeapp:
                closeApp(this);
                break;

            case R.id.helpbutton:
                GlobalVariables.flag_help_support =true;
                MessageDialog messageDialog =new MessageDialog();
                messageDialog.show(getSupportFragmentManager(),"help messageDialog");
                break;

            case R.id.userguide:
                downloadUserGuide(userGuideFilename);
                break;

            case R.id.sensorsguide:
                downloadUserGuide(sensorsGuideFilename);
                break;

            case R.id.supportmail:
                supportMail();
                break;
        }
    }

    //TODO-- drawer
    private static void openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private static void closeDrawer (DrawerLayout drawerLayout){
        //close drawer layout
        //check condition
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            //when drawer is open, close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void logout(final Activity activity) {
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Logout");
        //set message
        builder.setMessage("Are you sure?");
        //Positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //save preferences
                SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                SharedPreferences.Editor editor= preferences.edit();
                editor.putString("keepmeloggedin","false");
                editor.apply();

                //firebase log out
                FirebaseAuth.getInstance().signOut();
                //finish activity
                activity.finishAffinity();
                //go to login page
                redirectActivity(Support.this, Login.class);
            }
        });
        //negative no button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.show();
    }

    private static void redirectActivity(Activity activity,Class aClass) {
        //initialize intent
        Intent intent= new Intent (activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Start activity
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

    private void closeApp (final Activity activity){

        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("RespirHò");
        //set message
        builder.setMessage("Are you sure you want to close this App?");
        //Positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close the app
                activity.finishAffinity();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        //negative no button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.show();
    }

    //TODO-- END drawer

    private void downloadUserGuide(String filename) {
        //list the files in the Documentation folder and track the filename that contains "Guida_utente_app"
        //in this way you will download the "Guida_utente_app_[version number].pdf".
        //IMPORTANT: in firebase storage Documentation keep only one file with the name above
        //so we download the latest version and the older versions remain in the smartphone folder anyway

        progressbar_support.setVisibility(View.VISIBLE);

        mStorageRef= FirebaseStorage.getInstance().getReference("Documentation");

        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference item : listResult.getItems()) {
                    //save the item containing the filename we are looking for
                    if(item.getName().contains(filename)){
                        fileToDownload=item.getName();
                        Log.e("FILEDOWNLOAD","file to download is: " + fileToDownload);

                        //once we got the fileToDownload name, download it
                        if(!TextUtils.isEmpty(fileToDownload)){
                            mStorageRef = FirebaseStorage.getInstance().getReference("Documentation").
                                    child(fileToDownload);

                            //path where the root of the txt file is located on the smartphone
                            extPath = getExternalFilesDir(null).getAbsolutePath();

                            //ext storage
                            File folderExt=createDirectory("respirho","Documentation", extPath);
                            File fileExt=new File(folderExt,fileToDownload);

                            mStorageRef.getFile(fileExt).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    progressbar_support.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), fileToDownload + " saved in:\n" + extPath + "/respirho/Documentation", Toast.LENGTH_LONG).show();
                                    Log.e("demo","Guida utente downloaded correctly");
                                    return;
                                }
                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull @NotNull FileDownloadTask.TaskSnapshot snapshot) {
                                    //progressbar_support.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    progressbar_support.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "An error occurred while downloading the file\n\nCheck internet connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            Log.e("demo","file to download not found");
                            Toast.makeText(getApplicationContext(), "Error downloading the file\n\nThere's no file with the name specified.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Uh-oh, an error occurred!
                Log.e("demo","error");
                Toast.makeText(getApplicationContext(), "Error downloading the files\n\nCheck internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createDirectory(String dirName, String subDir, String path){
        File rootfolder,subfolder;

        rootfolder=new File(path + "/" + dirName);

        if(!rootfolder.exists()){
            rootfolder.mkdir();
        }

        subfolder=new File(rootfolder + "/" + subDir);

        if(!subfolder.exists()){
            subfolder.mkdir();
        }

        return subfolder;
    }

    private void supportMail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"respirhopolimi@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support on RespirHo app");
        emailIntent.setSelector(intent);

        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}