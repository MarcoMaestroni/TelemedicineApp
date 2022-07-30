package com.example.respirho;

// TODO Trovare un modo per riconnettere le unità se si disconnettono durante l'acquisizione

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dsi.ant.AntService;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntChannelProvider;
import com.dsi.ant.channel.AntCommandFailedException;
import com.dsi.ant.channel.ChannelNotAvailableException;
import com.dsi.ant.channel.IAntChannelEventHandler;
import com.dsi.ant.channel.PredefinedNetwork;
import com.dsi.ant.message.AntMessage;
import com.dsi.ant.message.ChannelId;
import com.dsi.ant.message.ChannelType;
import com.dsi.ant.message.fromant.AntMessageFromAnt;
import com.dsi.ant.message.fromant.BroadcastDataMessage;
import com.dsi.ant.message.fromant.ChannelEventMessage;
import com.dsi.ant.message.fromant.MessageFromAntType;
import com.dsi.ant.message.ipc.AntMessageParcel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.DatabaseReference; //default ON
//import com.google.firebase.database.FirebaseDatabase; //default ON

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReadingAntSensors extends AppCompatActivity implements View.OnClickListener {
    public final String LOG_TAG = MainActivity.class.getSimpleName();

    // GESTIONE ANT
    private static final int USER_PERIOD_IMUS = 1092; // 1092; 30 Hz
    private static final int USER_RADIOFREQUENCY = 66; //66, so 2466 MHz;
    public static boolean serviceIsBound = false;
    private AntService mAntRadioService = null;
    public AntChannelProvider antChannelProvider;
    public AntChannel antChannelIMUs;
    public ChannelType antChannelIMUs_type;
    public AntMessage antMessage;
    public MessageFromAntType messagetype; //
    public boolean mIsOpen = false;
    public ChannelId channelId_smartphone = new ChannelId(2,2,2, true); //DEFAULT: 2,2,2, true

    byte[] payLoad;

    byte[] payLoad1 = {0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01}; // payload to call unit 1 for the first time
    byte[] payLoad2 = {0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02}; // payload to call unit 2 for the first time
    byte[] payLoad3 = {0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03}; // payload to call unit 3 for the first time

    byte[] payLoad4 = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; // payload to synchronize the three units

    byte[] payLoad5 = {0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; // payload to call unit 1 during acquisition
    byte[] payLoad6 = {0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; // payload to call unit 2 during acquisition
    byte[] payLoad7 = {0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; // payload to call unit 3 during acquisition

    public String string0 = "[00][00][00][00][00][00][00][00]";
    public String string1 = "[01][01][01][01][01][01][01]"; //message from slave 1 for check
    public String string2 = "[02][02][02][02][02][02][02]";//message from slave 2 for check
    public String string3 = "[03][03][03][03][03][03][03]";//message from slave 3 for check

    public int state;
    public int connected1 = 0;
    public int connected2 = 0;
    public int connected3 = 0;

    //STATES
    private static final int INITIALIZATION = 0;
    private static final int CONNECT1 = 1;
    private static final int CONNECT2 = 2;
    private static final int CONNECT3 = 3;
    private static final int SYNCHRONIZATION = 4;
    private static final int CALL1 = 5;
    private static final int CALL2 = 6;
    private static final int CALL3 = 7;
    private static final int START = 8;
    private static final int STOP = 0;

    //LOGIC
    private static final int FALSE = 0;
    private static final int TRUE = 1;

    BroadcastDataMessage broadcastDataMessage;
    String current_default,current,day;
    public boolean timeoff = false;

    public IAntChannelEventHandler eventCallBack = new IAntChannelEventHandler() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceiveMessage(MessageFromAntType messageFromAntType, AntMessageParcel antMessageParcel) {

            //tick 1,2,3
            ImageView iv1=(ImageView) findViewById(R.id.true1);
            ImageView iv2=(ImageView) findViewById(R.id.true2);
            ImageView iv3=(ImageView) findViewById(R.id.true3);

            iv1.setVisibility(View.INVISIBLE);

            iv2.setVisibility(View.INVISIBLE);

            iv3.setVisibility(View.INVISIBLE);

            //path where the root of the txt file is located
            String path=getExternalFilesDir(null).getAbsolutePath();

            switch(messageFromAntType){

                case BROADCAST_DATA:

                    //save time
                    day=LocalDateTime.now().toLocalDate().toString(); //date

                    current_default = LocalDateTime.now().toString(); //add this to save datetime
                    //remove the "." from the time because firebase does NOT accept it
                    //and replace it with a ","
                    current=current_default.replace(".",",");

                    String messageContentString = antMessageParcel.getMessageContentString();

                    //call the firebase class to upload data on firebase
                    WritingDataToFirebase writingDataToFirebase= new WritingDataToFirebase();
                    writingDataToFirebase.mainFirebase(messageContentString+current,day);

                    //call the file class to save data in a txt file
                    //WritingDataToFile writingDataToFile = new WritingDataToFile();
                    //writingDataToFile.mainFile(messageContentString+current, current, day, path);

                    //if the slave responds, the connection is established and a flag is raised
                    //when all 3 are connected go to SYNCHRONIZATION
                    if(connected1==TRUE && connected2==TRUE){

                        Log.e(LOG_TAG, "Rx: " + messageContentString); //hex

                    }else{

                        Log.e(LOG_TAG, "CHECK Rx: " + messageContentString); //hex

                        if(messageContentString.contains(string1)){
                            connected1 = TRUE;
                            Log.e(LOG_TAG,"1 is:" + connected1);
                            state=CONNECT2;
                        }

                        if(messageContentString.contains(string2)){
                            connected2 = TRUE;
                            Log.e(LOG_TAG,"2 is:" + connected2);
                            state=SYNCHRONIZATION;; //state=CONNECT3;
                        }

                        //TODO-- add the following if condition when a third battery is available
                        /*
                        if(messageContentString.contains(string3)){
                            connected3 = TRUE;
                            Log.e(LOG_TAG,"3 is:" + connected3);
                            state=SYNCHRONIZATION;
                        }
                         */
                    }

                    break;

                case CHANNEL_ID:
                    Log.e(LOG_TAG, "CASE CHANNEL ID");
                    break;

                case CHANNEL_EVENT:
                    ChannelEventMessage eventMessage = new ChannelEventMessage(antMessageParcel);
                    switch (eventMessage.getEventCode()) {
                        case RX_SEARCH_TIMEOUT:
                            break;
                        case RX_FAIL:
                            break;
                        case TX:
                            //if the channel has been opened during initialization...
                            if (mIsOpen) {

                                // Setting the data to be broadcast on the next channel period
                                if(state==CONNECT1){
                                    payLoad = payLoad1;
                                }

                                if(state==CONNECT2) {
                                    payLoad = payLoad2;
                                }

                                if(state==CONNECT3) {
                                    payLoad = payLoad3;
                                }

                                if(state==SYNCHRONIZATION)
                                {
                                    payLoad = payLoad4;
                                }

                                if(state==CALL1) {
                                    payLoad = payLoad5;
                                }

                                if(state==CALL2) {
                                    payLoad = payLoad6;
                                }

                                if(state==CALL3) {
                                    payLoad = payLoad7;
                                }

                                //send the message through a specific payload
                                try {
                                    antChannelIMUs.setBroadcastData(payLoad);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                                //CONTINUOUS ACQUISITION
                                //after synchronization, call periodically one after the other

                                if(state==SYNCHRONIZATION)
                                {
                                    state=CALL1;
                                }
                                else if(state == CALL1)
                                {
                                    state = CALL2;
                                }
                                else if(state == CALL2)
                                {
                                    state = CALL1; //state = CALL3;
                                }
                                //TODO-- add the following if condition when a third battery is available
                                /*
                                else if (state == CALL3)
                                {
                                    state = CALL1;
                                }
                                */
                            }
                            else{
                                //Log.e(LOG_TAG, "Ant Service is bound: "+ serviceIsBound);
                            }

                            break;
                        case TRANSFER_RX_FAILED:
                            break;
                        case TRANSFER_TX_COMPLETED:
                            break;
                        case TRANSFER_TX_FAILED:
                            break;
                        case CHANNEL_CLOSED:
                            break;
                        case RX_FAIL_GO_TO_SEARCH:
                            break;
                        case CHANNEL_COLLISION:
                            break;
                        case TRANSFER_TX_START:
                            break;
                        case UNKNOWN:
                            break;
                    }
                    break;
            }
        };

        @Override
        public void onChannelDeath()
        {
            //EMPTY
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //at the start, show the main activity layout (see layout folder on the left)
        //and sets all the widgets
        setContentView(R.layout.main_activity);

        //redirectActivity(ReadingAntSensor.this,PatientData.class);

        //it makes the button clickable calling onClick() method
        Button initialization = findViewById(R.id.initialization);
        initialization.setOnClickListener(this);

        Button connect1 = findViewById(R.id.connect1);
        connect1.setOnClickListener(this);

        Button connect2 = findViewById(R.id.connect2);
        connect2.setOnClickListener(this);

        Button connect3 = findViewById(R.id.connect3);
        connect3.setOnClickListener(this);

        Button synchronization = findViewById(R.id.synchronization);
        synchronization.setOnClickListener(this);

        Button start = findViewById(R.id.start);
        start.setOnClickListener(this);

        Button stop = findViewById(R.id.stop);
        stop.setOnClickListener(this);

        //define some button names
        //tick 1,2,3
        ImageView iv1=(ImageView) findViewById(R.id.true1);
        ImageView iv2=(ImageView) findViewById(R.id.true2);
        ImageView iv3=(ImageView) findViewById(R.id.true3);

        iv1.setVisibility(View.INVISIBLE);
        iv1.setOnClickListener(this);

        iv2.setVisibility(View.INVISIBLE);
        iv2.setOnClickListener(this);

        iv3.setVisibility(View.INVISIBLE);
        iv3.setOnClickListener(this);

        ProgressBar progressBar;
        progressBar=(ProgressBar) findViewById(R.id.progressbar);

        //BINDING TO THE ANT RADIO SERVICE
        //state = INITIALIZATION;
        serviceIsBound = AntService.bindService(this, mAntRadioServiceConnection);
        Log.e(LOG_TAG, "Ant Service is bound: "+ serviceIsBound);
        Log.e(LOG_TAG, "Version name: "+ AntService.getVersionName(this));
    }

    //when click on some buttons, the following cases are shown
    public void onClick(View v) {

        //define some button names
        //tick 1,2,3
        ImageView iv1=(ImageView) findViewById(R.id.true1);
        ImageView iv2=(ImageView) findViewById(R.id.true2);
        ImageView iv3=(ImageView) findViewById(R.id.true3);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);

        //get the name of the button clicked
        switch (v.getId()) {

            case R.id.initialization:

                //INTERFACE INITIALIZATION
                iv1.setVisibility(View.INVISIBLE);

                iv2.setVisibility(View.INVISIBLE);

                iv3.setVisibility(View.INVISIBLE);

                try {
                    antChannelProvider = mAntRadioService.getChannelProvider();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "Ant Channel Provider is " + antChannelProvider);

                //TODO--remove channels available
                //channels available
                int channels=0;
                try {
                    channels=antChannelProvider.getNumChannelsAvailable();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "Ant Channels available " + channels);

                try {
                    antChannelIMUs = antChannelProvider.acquireChannel(this, PredefinedNetwork.PUBLIC);
                } catch (ChannelNotAvailableException | RemoteException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "Ant Channel IMUs: "+ antChannelIMUs);

                try {
                    antChannelIMUs.setChannelEventHandler(eventCallBack);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "Event handler" + eventCallBack);

                try {
                    antChannelIMUs.assign(ChannelType.SHARED_BIDIRECTIONAL_MASTER);//SHARED_BIDIRECTIONAL_MASTER, 48=0x30

                } catch (RemoteException | AntCommandFailedException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "Channel is a SHARED_BIDIRECTIONAL_MASTER");

                try {
                    antChannelIMUs.setChannelId(channelId_smartphone);
                } catch (RemoteException | AntCommandFailedException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "" + channelId_smartphone);

                try {
                    antChannelIMUs.setPeriod(USER_PERIOD_IMUS);
                } catch (RemoteException | AntCommandFailedException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "User period is:" + USER_PERIOD_IMUS);

                try {
                    antChannelIMUs.setRfFrequency(USER_RADIOFREQUENCY);
                } catch (RemoteException | AntCommandFailedException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "User radiofrequency is:" + USER_RADIOFREQUENCY);

                try {
                    antChannelIMUs.setTransmitPower(3);
                } catch (RemoteException | AntCommandFailedException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "Transmit power is 3");

                try {
                    antChannelIMUs.open();
                    mIsOpen = true;
                } catch (RemoteException | AntCommandFailedException e) {
                    e.printStackTrace();
                }
                Log.e(LOG_TAG, "Channel is open");

                state = CONNECT1;

                /*
                When both IMUs are switched off,
                click initialization and then switch on the sensor 1 and then sensor 2.
                 */

                break; //end of case INITIALIZATION

            //if 1 button is pressed,...
            case R.id.connect1:
                Log.e(LOG_TAG, "Connect unit 1");
                state = CONNECT1;
                break;

            //if 2 button is pressed,...
            case R.id.connect2:
                Log.e(LOG_TAG, "Connect unit 2");
                state = CONNECT2;
                break;

            //if 3 button is pressed,...
            case R.id.connect3:
                Log.e(LOG_TAG, "Connect unit 3");
                state = CONNECT3;
                break;

            //if SYNCHRONIZATION button is pressed,...
            case R.id.synchronization:
                Log.e(LOG_TAG, "Start synchronization");
                state = SYNCHRONIZATION;
                break;

            //if START button is pressed,...
            case R.id.start:
                Log.e(LOG_TAG, "Start acquisition");
                state = CALL1;
                break;

            //if STOP button is pressed,...
            case R.id.stop:
                Log.e(LOG_TAG, "Stop button pressed");
                state = STOP;
                GlobalVariables.flag_stoprec=true;
                //close the channel

                try {
                    antChannelIMUs.close();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (AntCommandFailedException e) {
                    e.printStackTrace();
                }
                mIsOpen = false;

                //remove the check tick
                connected1=FALSE;
                iv1.setVisibility(View.INVISIBLE);

                connected2=FALSE;
                iv2.setVisibility(View.INVISIBLE);

                connected3=FALSE;
                iv3.setVisibility(View.INVISIBLE);

                Log.e(LOG_TAG, "Channel is closed");

                break;

            //check box 1
            case R.id.check1:

                //if a message is received on device 1,...
                if(connected1 == TRUE) {
                    iv1.setVisibility(View.VISIBLE);
                    Log.e(LOG_TAG,"CONNECTED1");
                } else {
                    iv1.setVisibility(View.INVISIBLE);
                    Log.e(LOG_TAG,"UNCONNECTED1");
                }
                break;

            //check box 2
            case R.id.check2:

                //if a message is received on device 2,...
                if(connected2 == TRUE) {
                    iv2.setVisibility(View.VISIBLE);
                    Log.e(LOG_TAG,"CONNECTED2");
                } else {
                    iv2.setVisibility(View.INVISIBLE);
                    Log.e(LOG_TAG,"UNCONNECTED2");
                }
                break;

            //check box 3
            case R.id.check3:

                //if a message is received on device 3,...
                if(connected3 == TRUE) {
                    iv3.setVisibility(View.VISIBLE);
                    Log.e(LOG_TAG,"CONNECTED3");
                } else {
                    iv3.setVisibility(View.INVISIBLE);
                    Log.e(LOG_TAG,"UNCONNECTED3");
                }
                break;

            default:
                break;
        }
    }

    private ServiceConnection mAntRadioServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mAntRadioService = new AntService(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //empty
        }
    };

    private static void redirectActivity(Activity activity, Class aClass) {
        //initialize intent
        Intent intent= new Intent (activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Start activity
        activity.startActivity(intent);
    }

}


