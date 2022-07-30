package com.example.respirho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.CompoundButton;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class PatientData extends AppCompatActivity implements View.OnClickListener {

    public final String LOG_TAG = MainActivity.class.getSimpleName();

    //STORAGE variables
    private StorageReference mStorageRef;
    //recycler view for storage from firebase storage
    public ArrayList<Item_StorageFiles> item_storage_files=null;
    private RecyclerView recyclerview_storage_files;
    private Adapter_PatientData adapter_storage_files;
    private RecyclerView.LayoutManager layoutManager_storage_files;
    //define needed for line separator on storage recycler view
    private static final int HORIZONTAL = 1;
    //dialog storage storage
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialogDownloadStorage;
    private Button update_storage_files,cancel_storage_files;
    private ProgressBar progressbar_storage_files;
    private TextView status_storage_files;

    private TextView id_patient,info_patient;

    GridLayout layout_sensors_button_switch;
    Toast toast;

    private ImageButton telephone, storage,showpopup;

    private Button helpbutton;

    //file variables
    public File fileInt;
    public String intPath,extPath;

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
        setContentView(R.layout.patient_data);

        //STORAGE FILES INITIALIZATION
        //always clear the storage list and reload it
        item_storage_files=new ArrayList<>();

        //FILES INT AND EXT INITIALIZATION
        //path where the txt file is saved internally
        intPath=getApplicationContext().getFilesDir().getAbsolutePath();

        //path where the root of the txt file is located on the smartphone
        extPath=getExternalFilesDir(null).getAbsolutePath();

        fileInt=new File(fileInt,"demo");

        //layout_sensors_button_switch
        layout_sensors_button_switch=(GridLayout) findViewById(R.id.layout_sensors_button_switch);
        onClickSensors(layout_sensors_button_switch);

        helpbutton = (Button) findViewById(R.id.helpbutton);
        helpbutton.setOnClickListener(this);

        telephone = (ImageButton) findViewById(R.id.telephone);
        telephone.setOnClickListener(this);

        storage = (ImageButton) findViewById(R.id.storage);
        storage.setOnClickListener(this);

        showpopup=findViewById(R.id.showpopup);

        //first access to Patient Data from Patients List? save the ID Patient info
        if(GlobalVariables.flag_patientdata=true){
            //get ID Patient from the PatientsList click
            Intent intent = getIntent();
            Items_CardView items_cardView = intent.getParcelableExtra("Get ID Patient");

            GlobalVariables.string_idpatient= items_cardView.getID_patient();
            GlobalVariables.string_telephonepatient = items_cardView.getTelephone_patient();
            GlobalVariables.string_infopatient = items_cardView.getInfo_patient();

            GlobalVariables.flag_patientdata=false;
        }

        id_patient=(TextView) findViewById(R.id.card);
        id_patient.setText(GlobalVariables.string_idpatient);

        info_patient=(TextView) findViewById(R.id.info);
        info_patient.setText("INFO PATIENT: \n" + GlobalVariables.string_infopatient);

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

    private void onClickSensors(GridLayout layout_sensors_button_switch) {
        //loop the sensors
        for(int i=0;i<layout_sensors_button_switch.getChildCount();i++)
        {
            //call view clicked
            View view=layout_sensors_button_switch.getChildAt(i);

            //reassign correct number to the corresponding sensors
            int childnumber=i;
            int newnumber=-1;
            //call the function to associate the child number to the switch and to the sensor number
            newnumber=assignCorrectNumbers(childnumber);

            //look if it's a sensor or a switch investigating the type
            //if it's a sensor
            if(view instanceof GridLayout){

                //cast to gridlayout
                final GridLayout sensor= (GridLayout) view;

                sensor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //TODO--go to the storage demo page
                        redirectActivity(PatientData.this, DemoDownload.class); //DEFAULT, IT WORKS
                    }
                });
            }

            //if it's a switch
            if(view instanceof SwitchCompat){

                final int numberToast=newnumber;
                //number associating the switch number to the corresponding sensor number
                final int numberSensor=childnumber-2;

                //cast the sensor referred to the switch in order to change the background accordingly
                View view_sensor= layout_sensors_button_switch.getChildAt(numberSensor);
                final GridLayout sensor=(GridLayout) view_sensor;

                //cast to switch compat
                SwitchCompat switchCompat= (SwitchCompat) view;

                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton cb, boolean on) {
                        if (on)
                        {
                            //set clickable ON
                            sensor.setClickable(true);
                            //set background of the sensor ON
                            sensor.setBackgroundResource(R.drawable.layoutroundedcornerssensor);
                            //set the status icon of the selected sensor ON
                            for(int j=0;j<sensor.getChildCount();j++) {
                                //call view clicked
                                View child = sensor.getChildAt(j);
                                if (child instanceof ImageButton) {
                                    //make them visible and clickable
                                    child.setVisibility(View.VISIBLE);
                                    child.setClickable(true);
                                    //toast.makeText(getApplicationContext(), "is clickable ", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //toast message that the relative sensor is ON
                            toast.makeText(getApplicationContext(), "Sensor " + numberToast + " is ON", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //set clickable OFF
                            sensor.setClickable(false);
                            //set background of the sensor OFF
                            sensor.setBackgroundResource(R.drawable.layoutroundedcornerssensorfade);
                            //set the status icon of the selected sensor OFF
                            for(int j=0;j<sensor.getChildCount();j++) {
                                //call view clicked
                                View child = sensor.getChildAt(j);
                                if (child instanceof ImageButton) {
                                    //make them NOT visible and NOT clickable (TODO drawable will change their color)
                                    child.setVisibility(View.INVISIBLE);
                                    child.setClickable(false);
                                    //toast.makeText(getApplicationContext(), "is NOT clickable ", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //toast message that the relative sensor is OFF
                            toast.makeText(getApplicationContext(), "Sensor " + numberToast + " is OFF", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    /*
    //return the newnumber containing the correct number for toast message associated to the correct sensor number

    *        i       newnumber     sensorreferredtotheswitch = i-2
    *        0           1               /
    *        1           2               /
    *        2           1               1
    *        3           2               2
    *        4           3               /
    *        5           4               /
    *        6           3               3
    *        7           4               4

    */

    public int assignCorrectNumbers (int childnumber){
        int newnumber= -1;
        switch (childnumber){
            case 0:
                newnumber=1;
                break;
            case 1:
                newnumber=2;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                newnumber=childnumber-1;
                break;
            case 6:
            case 7:
                newnumber=childnumber-3;
                break;
        }
        return newnumber;
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
                redirectActivity(PatientData.this,PatientsList.class);
                break;

            case R.id.drawer_support:
                redirectActivity(PatientData.this, Support.class);
                break;

            case R.id.drawer_logout:
                //call logout function
                logout(this);
                break;

            case R.id.drawer_closeapp:
                closeApp(this);
                break;

            case R.id.helpbutton:
                GlobalVariables.flag_help_patientdata=true;
                MessageDialog messageDialog =new MessageDialog();
                messageDialog.show(getSupportFragmentManager(),"help messageDialog");
                break;

            case R.id.telephone:
                telephone(this);
                break;

            case R.id.storage:
                //call storage function
                dialogDownloadStorage();
                break;

            case R.id.update_storage_files:
                //to update close and reopen it
                dialogDownloadStorage.dismiss();
                dialogDownloadStorage();
                break;

            case R.id.cancel_storage_files:
                dialogDownloadStorage.dismiss();
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
                redirectActivity(PatientData.this, Login.class);
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

    private void telephone(final Activity activity){
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Telephone");

        if(GlobalVariables.string_telephonepatient !=null && !GlobalVariables.string_telephonepatient.isEmpty()){
            builder.setMessage(GlobalVariables.string_telephonepatient);
        }
        else {
            builder.setMessage("Telephone number not added");
        }

        //call button
        builder.setPositiveButton("CALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //OPEN TELEPHONE
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+GlobalVariables.string_telephonepatient));
                startActivity(intent);
            }
        });
        //close button
        builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.show();
    }

    public void dialogDownloadStorage(){
        //initialize alert dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View storage_files=getLayoutInflater().inflate(R.layout.storagefiles_dialog,null);

        //load the list with the files in firebase storage
        updateDownloadStorage();

        //recycler view
        recyclerview_storage_files = (RecyclerView) storage_files.findViewById(R.id.recyclerView_storage_files);

        //add a line separator
        DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), HORIZONTAL);
        recyclerview_storage_files.addItemDecoration(itemDecor);
        recyclerview_storage_files.setHasFixedSize(true);
        layoutManager_storage_files=new LinearLayoutManager(this);
        adapter_storage_files=new Adapter_PatientData(item_storage_files);

        recyclerview_storage_files.setLayoutManager(layoutManager_storage_files);
        recyclerview_storage_files.setAdapter(adapter_storage_files);

        status_storage_files=(TextView) storage_files.findViewById(R.id.status_storage_files);

        progressbar_storage_files=(ProgressBar) storage_files.findViewById(R.id.progressbar_storage_files);
        progressbar_storage_files.setVisibility(View.VISIBLE);

        update_storage_files=(Button) storage_files.findViewById(R.id.update_storage_files);
        update_storage_files.setOnClickListener(this);

        cancel_storage_files=(Button) storage_files.findViewById(R.id.cancel_storage_files);
        cancel_storage_files.setOnClickListener(this);

        dialogBuilder.setView(storage_files);
        dialogDownloadStorage=dialogBuilder.create();
        dialogDownloadStorage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDownloadStorage.show();

        //to change the UI we have to put codes in the runOnUiThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //function for the storage files
                adapter_storage_files.setOnItemClickListener(new Adapter_PatientData.OnItemClickListener() {

                    @Override
                    public void onDownloadFileClick(int position) {

                        downloadFileStorage(position);
                    }

                    @Override
                    public void onDeleteFileClick(int position) {

                        deleteFileStorage(PatientData.this,position);
                    }
                });
            }
        });
    }

    private void updateDownloadStorage(){

        //always clear the list and reload it
        item_storage_files=new ArrayList<>();

        mStorageRef= FirebaseStorage.getInstance().getReference("Patients").
                child(GlobalVariables.string_idpatient);

        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference item : listResult.getItems()) {
                    //save the items in the recycler view
                    item_storage_files.add(new Item_StorageFiles(item.getName()));
                    adapter_storage_files.notifyDataSetChanged();
                }

                if(item_storage_files.isEmpty()){
                    status_storage_files.setVisibility(View.VISIBLE);
                    progressbar_storage_files.setVisibility(View.GONE);
                }
                else{
                    status_storage_files.setVisibility(View.GONE);
                    progressbar_storage_files.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Uh-oh, an error occurred!
                Log.e("demo","error");
                toast.makeText(getApplicationContext(), "Error loading the files\n\nCheck internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadFileStorage(int position) {

        String downloadFilename = item_storage_files.get(position).getFilename();

        mStorageRef = FirebaseStorage.getInstance().getReference("Patients").
                child(GlobalVariables.string_idpatient).child(downloadFilename);

        //ext storage
        File folderExt=createDirectory("respirho","Patients", GlobalVariables.string_idpatient, extPath);
        File fileExt=new File(folderExt,downloadFilename);

        progressbar_storage_files.setVisibility(View.VISIBLE);

        mStorageRef.getFile(fileExt).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                progressbar_storage_files.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "File " + downloadFilename + " saved in:\n" + extPath + "/respirho/Patients", Toast.LENGTH_LONG).show();
                Log.e("demo","success");
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull FileDownloadTask.TaskSnapshot snapshot) {

                Log.e("demo","progress");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(), "An error occurred while downloading the file\n\nCheck internet connection", Toast.LENGTH_SHORT).show();
                Log.e("demo","failure");
            }
        });
    }

    private void deleteFileStorage(Activity activity, int position){
        //AIM: move file to another folder on firebase storage (bin)
        //1. download the file internally
        //2. upload the file in the bin folder on firebase storage
        //3. delete the file on the original storage folder
        //4. delete the file internally
        //5. update the storage dialog

        Log.e("demo","1.");

        String deletedFilename = item_storage_files.get(position).getFilename();

        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Delete file");
        //set message
        builder.setMessage("Are you sure you want to delete the following file?\n\nFile: " + deletedFilename);
        //Positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1. download the file internally
                StorageReference deletedFileStorageRef = FirebaseStorage.getInstance().getReference("Patients").
                        child(GlobalVariables.string_idpatient).child(deletedFilename);

                //path where the root of the txt file will be located internally (NOT VISIBLE ON THE SMARTPHONE)
                File folderInt=new File(intPath);

                if(!folderInt.exists()){
                    folderInt.mkdir();
                }

                File deletedfileInt=new File(folderInt,deletedFilename);

                progressbar_storage_files.setVisibility(View.VISIBLE);

                deletedFileStorageRef.getFile(deletedfileInt).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //2. upload the file in the bin folder on firebase storage
                        Log.e("demo","2.");

                        //get Uri needed to upload file on Firebase storage
                        Uri deletedFileUri=Uri.fromFile(deletedfileInt);

                        if(deletedFileUri!=null){
                            StorageReference binStorageRef=FirebaseStorage.getInstance().getReference("Bin").
                                    child(GlobalVariables.string_idpatient).child(deletedFilename);

                            StorageTask deletedFileUploadTask= binStorageRef.putFile(deletedFileUri);

                            deletedFileUploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Log.e("saveFirebase", "ERROR:file not uploaded on Firebase");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //3. delete the file on the original storage folder
                                    Log.e("demo","3.");

                                    deletedFileStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //4. delete the file internally
                                            Log.e("demo","4.");
                                            deletedfileInt.delete();

                                            progressbar_storage_files.setVisibility(View.GONE);

                                            //5. update the storage dialog
                                            //to update close and reopen it
                                            dialogDownloadStorage.dismiss();
                                            dialogDownloadStorage();

                                            toast.makeText(getApplicationContext(), deletedFilename + " has been deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                        }
                                    });
                                }
                            });
                        }
                        Log.e("demo","success");
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull @NotNull FileDownloadTask.TaskSnapshot snapshot) {
                        Log.e("demo","progress");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("demo","failure");
                    }
                });
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

    private File createDirectory(String dirName, String subDir,String subsubDir, String path){
        File rootfolder,subfolder,subsubfolder;

        rootfolder=new File(path + "/" + dirName);

        if(!rootfolder.exists()){
            rootfolder.mkdir();
        }

        subfolder=new File(rootfolder + "/" + subDir);

        if(!subfolder.exists()){
            subfolder.mkdir();
        }

        subsubfolder=new File(subfolder + "/" + subsubDir);

        if(!subsubfolder.exists()){
            subsubfolder.mkdir();
        }

        return subsubfolder;
    }

}


