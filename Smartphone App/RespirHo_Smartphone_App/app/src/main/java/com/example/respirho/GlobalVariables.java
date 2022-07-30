package com.example.respirho;

public class GlobalVariables extends GlobalClass {

    //add global variables
    public static boolean flag_download = false;
    //drawer
    public static boolean flag_logout = false;
    public static boolean flag_closeapp = false;
    //help
    public static boolean flag_help_login = false;
    public static boolean flag_help_register = false;
    public static boolean flag_help_patientslist = false;
    public static boolean flag_help_patientdata = false;
    public static boolean flag_help_demodownload = false;
    public static boolean flag_help_support = false;
    //text
    public static String string_idpatient=null;
    public static String string_telephonepatient=null;
    public static String string_infopatient=null;
    //recording info
    public static String string_setinforec =null;

    //delay
    public static boolean flag_delay = false;
    //stop recording
    public static boolean flag_stoprec = false;
    //file exist
    public static boolean flag_fileexist = false;
    //file name
    public static String setCurrentTimeFile = "";
    //connection
    public static boolean flag_connected1 = false;
    public static boolean flag_connected2 = false;
    public static boolean flag_connected3 = false;
    //first access to Patient Data to save the ID Patient info
    public static boolean flag_patientdata = false;

    //NOOOOTTTT USEDDDD
    //flag for dead batteries show only once and save unit
    public static boolean flag_battery = false;
    public static String dead_battery_unit1 ="";
    public static String dead_battery_unit2 ="";
    public static String dead_battery_unit3 ="";














    //

    private static GlobalVariables singleton;

    public static GlobalVariables getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
