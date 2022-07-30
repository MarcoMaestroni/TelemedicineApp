package com.example.respirho;

import android.os.Parcel;
import android.os.Parcelable;

public class Item_StorageFiles implements Parcelable {

    private String Filename;

    public Item_StorageFiles(String filename){
        Filename =filename;
    }

    //it should match the content of writeToParcel function
    protected Item_StorageFiles(Parcel in) {
        Filename = in.readString();
    }

    public static final Creator<Item_StorageFiles> CREATOR = new Creator<Item_StorageFiles>() {
        @Override
        public Item_StorageFiles createFromParcel(Parcel in) {
            return new Item_StorageFiles(in);
        }

        @Override
        public Item_StorageFiles[] newArray(int size) {
            return new Item_StorageFiles[size];
        }
    };

    public String getFilename(){
        return Filename;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Filename);
    }
}
