package com.example.respirho;

//translating python code to java to get data from the quaternions

//txt file can be changed to csv file and they can be read without problems

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadingData extends AppCompatActivity {

    public final String LOG_TAG = MainActivity.class.getSimpleName();

    Context context;
    String fileName;
    List<String[]> data = new ArrayList<>();

    public ReadingData(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public List<String[]> readingData() throws IOException {

        //DEFAULT CODE
        InputStream is = context.getAssets().open(fileName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        String [] Rxline = new String[0];

        String DevID_default = "";            //token[1]
        String DevID_split = "";

        String B_default = "";                //token[2]
        String B_split = "";

        String C_default = "";               //token[3]
        String C_split = "";

        String nthvalue_default = "";        //token[4]
        String nthvalue_split = "";

        String one_default = "";            //token[5]
        String one_split = "";

        String two_default = "";             //token[6]
        String two_split = "";

        String three_default = "";             //token[7]
        String three_split = "";

        String four_default = "";             //token[8]
        String four_split = "";

        String [] data_fix = new String[0];
        String [] data_1 = new String[0];
        String [] data_2 = new String[0];
        String [] data_3 = new String[0];

        String [] data_1_dec = new String[0];
        String [] data_2_dec = new String[0];
        String [] data_3_dec = new String[0];

        double max_value=-99.0; //initialization

        try {
            while ((line = br.readLine()) != null) {
                //split the lines with : and ,
                String[] token = line.split("[:,]");
                //get the first column, to check whether it's Tx or Rx and choose Rx only
                if(token[0].equals("Rx")){
                    //remove brackets and fix the data as in python

                    //Rx
                    Rxline=token;
                    //Log.e(LOG_TAG, "demo is:"+ token[0]); //Rx

                    //DevID
                    DevID_default=Rxline[1];
                    DevID_split=DevID_default.replace("(", "").replace(")", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ DevID_split); //0 OR 1

                    //B
                    B_default=Rxline[2];
                    B_split=B_default.replace("[", "").replace("]", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ B_split); //01

                    //C
                    C_default=Rxline[3];
                    C_split=C_default.replace("[", "").replace("]", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ C_split); //00

                    //nthvalue
                    nthvalue_default=Rxline[4];
                    nthvalue_split=nthvalue_default.replace("[", "").replace("]", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ nthvalue_split); //01

                    //one
                    one_default=Rxline[5];
                    one_split=one_default.replace("[", "").replace("]", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ one_split); //01

                    //two
                    two_default=Rxline[6];
                    two_split=two_default.replace("[", "").replace("]", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ two_split); //01

                    //three
                    three_default=Rxline[7];
                    three_split=three_default.replace("[", "").replace("]", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ three_split); //01

                    //four
                    four_default=Rxline[8];
                    four_split=four_default.replace("[", "").replace("]", "").replace(" ","");
                    //Log.e(LOG_TAG, "demo is:"+ four_split); //01

                    //Log.e(LOG_TAG, "demo is:"+ token[0] + " " +DevID_split + " "+ B_split + " "+C_split+ " "+nthvalue_split+ " "+one_split+ " "+two_split+ " "+three_split+ " "+four_split);//Rx  0   01  00  00  00  aa  00  00
                    data_fix= new String[]{token[0],DevID_split,B_split,C_split,nthvalue_split,one_split,two_split,three_split,four_split};
                    Log.e(LOG_TAG, "demo is:"+ Arrays.toString(data_fix)); //[Rx, 0, 01, 00, 00, 00, aa, 00, 00]
                    
                    //find the thoracic,abdominal and reference data
                    //thoracic data
                    if(data_fix[1]=="01"){
                        data_1 = new String[]{nthvalue_split,one_split,two_split,three_split,four_split};
                        Log.e(LOG_TAG, "demo is:"+ Arrays.toString(data_1)); //
                    }

                    //abdominal data
                    if(data_fix[1]=="02"){
                        data_2 = new String[]{nthvalue_split,one_split,two_split,three_split,four_split};
                        Log.e(LOG_TAG, "demo is:"+ Arrays.toString(data_2)); //
                    }

                    //reference data
                    if(data_fix[1]=="03"){
                        data_3 = new String[]{nthvalue_split,one_split,two_split,three_split,four_split};
                        Log.e(LOG_TAG, "demo is:"+ Arrays.toString(data_3)); //
                    }

                    //take the maximum value
                    if(data_3.length > data_1.length && data_3.length > data_2.length){
                        max_value=Math.floor(data_3.length / 256);
                    }
                    else if (data_1.length > data_2.length && data_1.length > data_3.length){
                        max_value=Math.floor(data_1.length / 256);
                    }
                    else{
                        max_value=Math.floor(data_2.length / 256);
                    }

                    // conversion from hexadecimal to decimal
                    data_1_dec=data_1;
                    //nthvalue from hex to dec conversion
                    data_1_dec[0]= String.valueOf(Integer.parseInt(data_1[0],16));
                    data_2_dec[0]= String.valueOf(Integer.parseInt(data_2[0],16));
                    data_3_dec[0]= String.valueOf(Integer.parseInt(data_3[0],16));

                    //one from hex to dec conversion
                    data_1_dec[1]= String.valueOf(Integer.parseInt(data_1[1],16));
                    data_2_dec[1]= String.valueOf(Integer.parseInt(data_2[1],16));
                    data_3_dec[1]= String.valueOf(Integer.parseInt(data_3[1],16));

                    //two from hex to dec conversion
                    data_1_dec[2]= String.valueOf(Integer.parseInt(data_1[2],16));
                    data_2_dec[2]= String.valueOf(Integer.parseInt(data_2[2],16));
                    data_3_dec[2]= String.valueOf(Integer.parseInt(data_3[2],16));

                    //three from hex to dec conversion
                    data_1_dec[3]= String.valueOf(Integer.parseInt(data_1[3],16));
                    data_2_dec[3]= String.valueOf(Integer.parseInt(data_2[3],16));
                    data_3_dec[3]= String.valueOf(Integer.parseInt(data_3[3],16));

                    //four from hex to dec conversion
                    data_1_dec[4]= String.valueOf(Integer.parseInt(data_1[4],16));
                    data_2_dec[4]= String.valueOf(Integer.parseInt(data_2[4],16));
                    data_3_dec[4]= String.valueOf(Integer.parseInt(data_3[4],16));

                    //add an empty row when there is a "jump" in communication, when the nth value is not received










                    //String demo=Arrays.toString(Rxline);
                    //Log.e(LOG_TAG, ""+ demo); //OK, E/MainActivity: [Rx, ( 0) ,  [01],  [00],  [00],  [00],  [00],  [00],  [00],  [00]]
                    data.add(Rxline);
                }
            }

        } catch (IOException e) {

        }
        //Log.e(LOG_TAG, ""+ Arrays.toString(line_splitbycomma));


        /*
        //OPENCSV
        // Create an object of filereader
        // class with CSV file as a parameter.

        try {
            // create csvReader object passing
            // file reader as a parameter
            FileReader filereader = new FileReader("src/main/assets/demo.csv");
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextLine;

            // we are going to read data line by line
            while ((nextLine = csvReader.readNext()) != null) {
                for (String cell : nextLine) {
                    //rows.add(cell);
                    Log.e(LOG_TAG, ""+ cell);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
         */
        

        return data;
    }
}
