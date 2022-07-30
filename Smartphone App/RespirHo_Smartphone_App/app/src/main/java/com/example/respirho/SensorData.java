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
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class SensorData extends AppCompatActivity implements View.OnClickListener {

    private ImageButton telephone,download;
    private Button helpbutton;

    //TODO-- drawer
    private ImageButton idicon,arrowback;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawer_home, drawer_aboutus,drawer_logout,drawer_closeapp;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    //TODO-- END drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_data);

        helpbutton = (Button) findViewById(R.id.helpbutton);
        helpbutton.setOnClickListener(this);

        telephone = (ImageButton) findViewById(R.id.mail);
        telephone.setOnClickListener(this);

        download = (ImageButton) findViewById(R.id.storage);
        download.setOnClickListener(this);

        //TODO-- drawer
        drawerLayout=findViewById(R.id.drawer_layout);

        drawer_home=findViewById(R.id.drawer_home);
        drawer_home.setOnClickListener(this);

        drawer_aboutus=findViewById(R.id.drawer_support);
        drawer_aboutus.setOnClickListener(this);

        drawer_logout=findViewById(R.id.drawer_logout);
        drawer_logout.setOnClickListener(this);

        drawer_closeapp=findViewById(R.id.drawer_closeapp);
        drawer_closeapp.setOnClickListener(this);

        idicon=findViewById(R.id.idicon);
        idicon.setOnClickListener(this);

        arrowback=findViewById(R.id.arrowback);
        arrowback.setOnClickListener(this);

        //TODO--GRAPH
        GraphView graph = (GraphView) findViewById(R.id.graph);

        //graph demo
        //TODO-- dates as labels
        //generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d6 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d7 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d8 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d9 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d10 = calendar.getTime();

        // you can directly pass Date objects to DataPoint-Constructor
        // this will convert the Date to double via Date#getTime()
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 5),
                new DataPoint(d3, 3),
                new DataPoint(d4, 2),
                new DataPoint(d5, 9),
                new DataPoint(d6, 8),
                new DataPoint(d7, 3),
                new DataPoint(d8, 30),
                new DataPoint(d9, 10),
                new DataPoint(d10, 20)
        });

        //TODO-- styling series
        //parameters
        series.setTitle("Random Curve 1");
        series.setColor(Color.parseColor("#664C8CBF"));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        /* custom paint to make a dotted line
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        series.setCustomPaint(paint);
        */

        //TODO-- scrolling and zooming
        //set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        //set info of the labels
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Unit");
        //graph.getGridLabelRenderer().setVerticalAxisTitleColor();
        //graph.getGridLabelRenderer().setTextSize(10);

        // set manual x bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(d1.getTime()); //first date
        graph.getViewport().setMaxX(d10.getTime()); //end date

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(40);

        // enables zooming and scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling

        // as we use dates as labels, the human rounding to nice readable numbers is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false,true);

        /*parameters to switch on and off the tools
        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalableX(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        */

        //TODO-- tap listener on data points (TOAST MESSAGES)
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getApplicationContext(), "Value: "+dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });

        //TODO-- display graph
        graph.addSeries(series);

        //show login information in drawer
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");

        //if the user is not null, so it's logged in
        if(user!=null){
            userID=user.getUid();

            final TextView drawerMail=(TextView) findViewById(R.id.drawermail);
            final TextView drawerFullname=(TextView) findViewById(R.id.drawerfullname);

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
    }

    //WARNINGS
    //ERROR
    public void onClickError(View view){
        Toast.makeText(getApplicationContext(), "ERROR: \nOne or more sensors don't work correctly\n\nGo to patient page", Toast.LENGTH_LONG).show();
    }

    //EXCLAMATION
    public void onClickExclamationPoint(View view){
        Toast.makeText(getApplicationContext(), "WARNING: \nOne or more parameters are above the threshold\n\nGo to patient page", Toast.LENGTH_LONG).show();
    }

    //CHECKMARK
    public void onClickCheckmark(View view){
        Toast.makeText(getApplicationContext(), "All sensors work great!", Toast.LENGTH_LONG).show();
    }

    //EDIT
    public void onClickEdit(View view){
        Toast.makeText(getApplicationContext(), "edit", Toast.LENGTH_LONG).show();
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
                redirectActivity(SensorData.this,PatientsList.class);
                break;

            case R.id.drawer_support:
                redirectActivity(SensorData.this, Support.class);
                break;

            case R.id.drawer_logout:
                //call logout function
                logout(this);
                break;

            case R.id.drawer_closeapp:
                closeApp(this);
                break;

            case R.id.helpbutton:
                GlobalVariables.flag_help_demodownload =true;
                MessageDialog messageDialog =new MessageDialog();
                messageDialog.show(getSupportFragmentManager(),"help messageDialog");
                break;

            case R.id.telephone:
                break;

            case R.id.storage:
                //call storage function
                download(this);
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
                redirectActivity(SensorData.this, Login.class);
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

    private void download(final Activity activity){
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Download");
        //set message
        builder.setMessage("Are you sure you want to storage the file?");
        //Positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO-- implement storage of the file

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
}