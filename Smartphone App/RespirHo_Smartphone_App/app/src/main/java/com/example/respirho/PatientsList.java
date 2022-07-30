package com.example.respirho;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PatientsList extends AppCompatActivity implements View.OnClickListener{

    public ArrayList<Items_CardView> items_cardViews=null;
    private RecyclerView recyclerview;
    private Adapter_PatientsLists adapter;
    private RecyclerView.LayoutManager layoutManager;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button addbuttondialog,editbuttondialog,cancelbuttondialog;
    private TextInputEditText dialog_idpatient,dialog_info,dialog_telephone;
    private ProgressBar progressbar_addnewpatient;

    private ImageButton addbutton,removebutton,showpopup;

    private TextView titleadddialog;

    //STORAGE variables
    private StorageReference mStorageRef;

    //TODO-- drawer
    private ImageButton idicon,arrowback;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawer_home, drawer_support,drawer_logout,drawer_closeapp;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    //TODO-- END drawer

    private Button helpbutton;

    public int position_editPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patients_list);

        //load the patients list
        loadPatientsList();

        //recycler view
        recyclerview = findViewById(R.id.recyclerView);

        recyclerview.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        adapter=new Adapter_PatientsLists(items_cardViews);

        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(adapter);

        helpbutton=findViewById(R.id.helpbutton);
        helpbutton.setOnClickListener(this);

        addbutton=findViewById(R.id.addbuttonlist);
        addbutton.setOnClickListener(this);

        removebutton=findViewById(R.id.removebuttonlist);
        removebutton.setOnClickListener(this);

        showpopup=findViewById(R.id.showpopup);

        adapter.setOnItemClickListener(new Adapter_PatientsLists.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GlobalVariables.flag_patientdata=true;
                Intent intent = new Intent(PatientsList.this,PatientData.class);
                intent.putExtra("Get ID Patient", items_cardViews.get(position));
                startActivity(intent);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onShowPopUpClick(int position,View itemView) {
                //Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();

                PopupMenu popup = new PopupMenu(PatientsList.this,itemView);
                popup.inflate(R.menu.popup_menu_edit_delete);
                popup.setGravity(Gravity.END);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                //save position in a public variable
                                position_editPatient=position;
                                dialogEditPatient(position);
                                break;
                            case R.id.delete:
                                deletePatient(PatientsList.this,position);
                                break;
                        }
                        return true;
                    }
                });
            }
        });

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

    //disable back button in this page to prevent user going back to logo page
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.addbuttonlist:
                //add patient dialog
                dialogAddPatient();
            break;

            case R.id.removebuttonlist:
                //TODO-- remove patient from the list

            break;

            case R.id.idicon:
                openDrawer(drawerLayout);
            break;

            case R.id.arrowback:
                closeDrawer(drawerLayout);
            break;

            case R.id.drawer_home:
                closeDrawer(drawerLayout);
            break;

            case R.id.drawer_support:
                redirectActivity(PatientsList.this, Support.class);
            break;

            case R.id.drawer_logout:
                logout(this);
            break;

            case R.id.drawer_closeapp:
                closeApp(this);
            break;

            case R.id.helpbutton:
                GlobalVariables.flag_help_patientslist=true;
                MessageDialog messageDialog =new MessageDialog();
                messageDialog.show(getSupportFragmentManager(),"help messageDialog");
            break;

            case R.id.addbuttondialog:
                addPatientToTheList();
            break;

            case R.id.editbuttondialog:
                editPatientToTheList();
            break;

            case R.id.cancelbuttondialog:
                dialog.dismiss();
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
                redirectActivity(PatientsList.this,Login.class);
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

    public void dialogAddPatient(){
        //initialize alert dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View addpatient_dialog=getLayoutInflater().inflate(R.layout.addpatient_dialog,null);

        dialog_idpatient=(TextInputEditText) addpatient_dialog.findViewById(R.id.insert_idpatient);
        dialog_info=(TextInputEditText) addpatient_dialog.findViewById(R.id.insert_infopatient);
        dialog_telephone=(TextInputEditText) addpatient_dialog.findViewById(R.id.insert_telephonepatient);

        addbuttondialog=(Button) addpatient_dialog.findViewById(R.id.addbuttondialog);
        cancelbuttondialog=(Button) addpatient_dialog.findViewById(R.id.cancelbuttondialog);
        addbuttondialog.setOnClickListener(this);
        cancelbuttondialog.setOnClickListener(this);

        progressbar_addnewpatient=(ProgressBar) addpatient_dialog.findViewById(R.id.progressbar_addnewpatient);
        progressbar_addnewpatient.setVisibility(View.GONE);

        dialogBuilder.setView(addpatient_dialog);
        dialog=dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dialogEditPatient(int position){
        //save ID Patient,info and telephone of the patient at position
        String id_patienttoedit=items_cardViews.get(position).getID_patient();
        String info_patienttoedit=items_cardViews.get(position).getInfo_patient();
        String telephone_patienttoedit=items_cardViews.get(position).getTelephone_patient();

        //initialize alert dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View addpatient_dialog=getLayoutInflater().inflate(R.layout.addpatient_dialog,null);

        titleadddialog=(TextView) addpatient_dialog.findViewById(R.id.titleadddialog);
        titleadddialog.setText("Edit patient");

        dialog_idpatient=(TextInputEditText) addpatient_dialog.findViewById(R.id.insert_idpatient);
        dialog_idpatient.setText(id_patienttoedit);
        dialog_idpatient.setEnabled(false);

        dialog_info=(TextInputEditText) addpatient_dialog.findViewById(R.id.insert_infopatient);
        dialog_info.setText(info_patienttoedit);
        dialog_telephone=(TextInputEditText) addpatient_dialog.findViewById(R.id.insert_telephonepatient);
        dialog_telephone.setText(telephone_patienttoedit);

        //hide add button
        addbuttondialog=(Button) addpatient_dialog.findViewById(R.id.addbuttondialog);
        addbuttondialog.setVisibility(View.GONE);
        //show edit button and make it clickable
        editbuttondialog=(Button) addpatient_dialog.findViewById(R.id.editbuttondialog);
        editbuttondialog.setVisibility(View.VISIBLE);
        editbuttondialog.setOnClickListener(this);

        cancelbuttondialog=(Button) addpatient_dialog.findViewById(R.id.cancelbuttondialog);
        cancelbuttondialog.setOnClickListener(this);

        progressbar_addnewpatient=(ProgressBar) addpatient_dialog.findViewById(R.id.progressbar_addnewpatient);
        progressbar_addnewpatient.setVisibility(View.GONE);

        dialogBuilder.setView(addpatient_dialog);
        dialog=dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void addPatientToTheList(){

        progressbar_addnewpatient.setVisibility(View.VISIBLE);

        String id_patienttoadd=dialog_idpatient.getText().toString().trim();
        String info_patienttoadd=dialog_info.getText().toString().trim();
        String telephone_patienttoadd=dialog_telephone.getText().toString().trim();

        if(id_patienttoadd.isEmpty()){
            dialog_idpatient.setError("ID Patient is required");
            dialog_idpatient.requestFocus();
            return;
        }

        if(id_patienttoadd.length()<6){
            dialog_idpatient.setError("ID Patient must be 6 digits");
            dialog_idpatient.requestFocus();
            return;
        }

        //check if a patient with the same ID is already present on firebase storage list and prevent to add it in case

        final boolean[] patientAlreadyPresent = {false};

        mStorageRef= FirebaseStorage.getInstance().getReference("Patients");

        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference prefix : listResult.getPrefixes()) {
                    Log.e("demo", "prefix is: " + prefix.getName());

                    if(id_patienttoadd.equals(prefix.getName())){
                        patientAlreadyPresent[0] =true;
                    }
                }

                //if the patient is already present, show error message
                if(patientAlreadyPresent[0]){
                    Log.e("demo", "patient: " + id_patienttoadd + " already present in the database");
                    dialog_idpatient.setError("The ID Patient is already used in the database");
                    dialog_idpatient.requestFocus();
                }
                else{
                    //add the patient
                    items_cardViews.add(new Items_CardView(id_patienttoadd,telephone_patienttoadd,info_patienttoadd));

                    //save the new patient
                    savePatientsList();

                    dialog.dismiss();

                    //TODO - add patients to Firebase in the folder of the user
                    //...
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Uh-oh, an error occurred!
                Log.e("demo","cannot open the firebase storage to check if the ID patient is already present");
                Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void editPatientToTheList() {
        String id_patienttoadd=dialog_idpatient.getText().toString().trim();
        String info_patienttoadd=dialog_info.getText().toString().trim();
        String telephone_patienttoadd=dialog_telephone.getText().toString().trim();

        //edit= remove + add
        //delete patient
        items_cardViews.remove(position_editPatient);
        adapter.notifyItemRemoved(position_editPatient);
        //add patient
        items_cardViews.add(new Items_CardView(id_patienttoadd,telephone_patienttoadd,info_patienttoadd));

        //save the new patient
        savePatientsList();

        dialog.dismiss();

        Toast.makeText(getApplicationContext(), "Patient "+id_patienttoadd+" edited", Toast.LENGTH_SHORT).show();
    }

    private void savePatientsList(){
        SharedPreferences sharedPreferences = getSharedPreferences("save patients list", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items_cardViews);
        editor.putString("save patients list", json);
        editor.apply();
    }

    private void loadPatientsList(){
        SharedPreferences sharedPreferences = getSharedPreferences("save patients list", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("save patients list", null);
        Type type = new TypeToken<ArrayList<Items_CardView>>() {}.getType();
        items_cardViews = gson.fromJson(json, type);

        if (items_cardViews == null || items_cardViews.size()==0) {
            items_cardViews=new ArrayList<>();
            dialogAddPatient();
            Toast.makeText(getApplicationContext(), "Please add a patient", Toast.LENGTH_LONG ).show();
        }
    }

    public void deletePatient(final Activity activity,int position){
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Delete patient");
        //set message
        builder.setMessage("Are you sure you want to delete this patient?");
        //Positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete patient on app
                items_cardViews.remove(position);
                adapter.notifyItemRemoved(position);

                //apply changes to the list
                savePatientsList();

                //delete patient on firebase
                //get the ID patient and then delete the directory
                //String id_patienttodelete=items_cardViews.get(position).getID_patient();
                //FirebaseDatabase.getInstance().getReference().child("Patients").child(id_patienttodelete).removeValue();

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

    private boolean checkIDPatientFirebase(String IDpatient){

        final boolean[] patient = {false};

        mStorageRef= FirebaseStorage.getInstance().getReference("Patients");

        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference prefix : listResult.getPrefixes()) {
                    Log.e("demo", "prefix is: " + prefix.getName());

                    if(IDpatient.equals(prefix.getName())){
                        patient[0] =true;
                    }
                    else{
                        //do nothing and patient will remain false
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Uh-oh, an error occurred!
                Log.e("demo","cannot open the firebase storage to check if the ID patient is already present");
            }
        });

        return patient[0];
    }
}