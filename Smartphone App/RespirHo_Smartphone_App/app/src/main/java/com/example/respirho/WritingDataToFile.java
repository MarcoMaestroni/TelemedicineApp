package com.example.respirho;

//save data in a file in internal storage and if the user confirm the storage then copy the file in external storage

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class WritingDataToFile extends DemoDownload {

    public String message, current, day, extPath;
    public String current_title,current_title_set;
    public File folder,fileInt;
    public FileWriter writer;

    public void mainFile(String message, String current, String day, String intPath, String extPath) {

        //first time access, set the start time of recording for the title
        if(!GlobalVariables.flag_fileexist){
            GlobalVariables.setCurrentTimeFile=current;
        }

        File folderInt=new File(intPath);

        if(!folderInt.exists()){
            folderInt.mkdir();
        }

        fileInt=new File(folderInt,GlobalVariables.string_idpatient +" - " + GlobalVariables.setCurrentTimeFile + ".txt");

        //first time access, set the first line
        if(!GlobalVariables.flag_fileexist){
            try {
                writer = new FileWriter(fileInt,true);
                writer.append("ID Patient: " + GlobalVariables.string_idpatient + "\n");
                writer.flush();
                writer.append("Info recording: " + GlobalVariables.string_setinforec + "\n");
                writer.flush();
                writer.append("Real time postures and extra info recording:" + "\n");
                writer.flush();
                writer.append("Recording started at: " + GlobalVariables.setCurrentTimeFile + "\n\n");
                writer.flush();
                writer.append("START:"+ "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            GlobalVariables.flag_fileexist=true;
        }

        try {
            writer = new FileWriter(fileInt,true);
            writer.append(message + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO- usually it does NOT works, check timing of the flags
        //close the file to prevent additional writing
        if(flag_stoprec){

            try {
                writer = new FileWriter(fileInt,true);
                writer.append("\n\n"+ "Recording finished at: " +current);
                writer.close();
                flag_stoprec=false;
                GlobalVariables.flag_fileexist=false; //demo
                Log.e("demo","File saved internally"); //ON ANDROID STUDIO ON THE RIGHT PANEL: DeviceFileExplorer-data-data-com.example.respirho-files
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void moveFileToExt(File fileInt,String day,String extPath) throws IOException {

        //ext storage
        File folderExt=createDirectory("respirho","Patients", GlobalVariables.string_idpatient, extPath);
        File fileExt=new File(folderExt,GlobalVariables.string_idpatient +" - " + GlobalVariables.setCurrentTimeFile + ".txt");
        //TODO - try to save the file in Documents or Downloads - still to implement
        //File fileExt=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),GlobalVariables.string_idpatient +" - " + GlobalVariables.setCurrentTimeFile + ".txt");

        FileChannel source=null;
        FileChannel destination=null;

        try {
            source = new FileInputStream(fileInt).getChannel();
            destination = new FileOutputStream(fileExt).getChannel();
            destination.transferFrom(source, 0, source.size());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }

        Log.e("demo","File moved to:"+fileExt.getAbsolutePath());
    }

    public void deleteFileInt(File fileInt){
        fileInt.delete();
        //Log.e("demo","fileInt deleted");
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
