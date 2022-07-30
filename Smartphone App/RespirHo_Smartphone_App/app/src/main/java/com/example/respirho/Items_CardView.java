package com.example.respirho;

import android.os.Parcel;
import android.os.Parcelable;

public class Items_CardView implements Parcelable {
    private String ID_patient;
    private String Telephone_patient;
    private String Info_patient;

    public Items_CardView(String id_patient,String telephone_patient,String info_patient){
        ID_patient=id_patient;
        Telephone_patient=telephone_patient;
        Info_patient=info_patient;
    }

    //it should match the content of writeToParcel function
    protected Items_CardView(Parcel in) {
        ID_patient = in.readString();
        Telephone_patient = in.readString();
        Info_patient = in.readString();
    }

    public static final Creator<Items_CardView> CREATOR = new Creator<Items_CardView>() {
        @Override
        public Items_CardView createFromParcel(Parcel in) {
            return new Items_CardView(in);
        }

        @Override
        public Items_CardView[] newArray(int size) {
            return new Items_CardView[size];
        }
    };

    public String getID_patient(){
        return ID_patient;
    }

    public String getTelephone_patient(){
        return Telephone_patient;
    }

    public String getInfo_patient(){
        return Info_patient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID_patient);
        dest.writeString(Telephone_patient);
        dest.writeString(Info_patient);
    }
}
