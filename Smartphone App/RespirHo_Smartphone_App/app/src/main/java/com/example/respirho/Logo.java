package com.example.respirho;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class Logo extends AppCompatActivity {

    //ASK PERMISSION TO THE STORAGE
    private int STORAGE_PERMISSION_CODE=1;

    ProgressBar progressBar;

    //TODO- DEMOOOOO to erase
    private static final int REQUEST_SEND_SMS =0 ;
    private String[] PERMISSIONS;

    //file variables
    public File fileInt;
    public String intPath,extPath;


    // TODO- end DEMOOOOO to erase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);

        progressBar=(ProgressBar) findViewById(R.id.progressbar_logo);

        PERMISSIONS = new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,
        };

        if (!hasPermissions(Logo.this,PERMISSIONS)) {

            ActivityCompat.requestPermissions(Logo.this,PERMISSIONS,1);
        }
        else{
            //make a delay and then go to Login page
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    redirectActivity(Logo.this,Login.class);
                }
            }, 500 ); //500
        }

        //TODO- DEMOOOOO to erase

        // TODO- end DEMOOOOO to erase
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {

            for (String permission: PERMISSIONS){

                if (ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            //REDIRECT ACTIVITY
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                redirectActivity(Logo.this, Login.class);
            }
        }
    }

    private static void redirectActivity(Activity activity, Class aClass) {
        //initialize intent
        Intent intent= new Intent (activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Start activity
        activity.startActivity(intent);
    }

    //TODO- DEMOOOOO to erase

    private void initializeNotification(String title) {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel_not= new NotificationChannel(title,title, NotificationManager.IMPORTANCE_DEFAULT);

            //LIGHTS
            channel_not.enableLights(true);
            channel_not.setLightColor(Color.GREEN);

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel_not);
        }
    }

    private void sendNotification(String title, String description){

        initializeNotification(title);

        NotificationCompat.Builder builder_not=new NotificationCompat.Builder(Logo.this,title);
        builder_not.setContentTitle(title);
        builder_not.setContentText(description);
        builder_not.setSmallIcon(R.mipmap.ic_applogo_foreground);
        builder_not.setAutoCancel(true);
        builder_not.setTicker(title);

        //pattern vibration
        long [] patternVibration={0,500, 100, 500};

        Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(patternVibration,-1);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(Logo.this);
        managerCompat.notify(1,builder_not.build());
    }


    public void addInfoRecordingToFile(File file, String text) throws IOException {
        File temp = File.createTempFile("temp-file-name", ".tmp");
        Log.e("demo","filetemp: " + temp);
        String posturesandinfo="Real time postures and extra info recording:";

        BufferedReader br = new BufferedReader(new FileReader( file ));
        PrintWriter pw =  new PrintWriter(new FileWriter( temp ));
        String line;
        int lineCount = 0;
        while ((line = br.readLine()) != null) {
            pw.println(line);
            if(line.equals(posturesandinfo)){
                pw.println(text);
                Log.e("demo","line modified");
            }
            lineCount++;
        }
        br.close();
        pw.close();
        file.delete();
        temp.renameTo(file);
        Log.e("demo","file saved in: " + temp.getAbsolutePath());
    }

    // TODO- end DEMOOOOO to erase
}