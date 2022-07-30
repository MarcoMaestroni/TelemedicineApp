package com.example.respirho;

import android.graphics.ColorSpace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.model.Model;

import java.util.ArrayList;

public class Adapter_PatientsLists extends RecyclerView.Adapter<Adapter_PatientsLists.ViewHolder> {

    private ArrayList<Items_CardView> List; //mExampleList (codingflow video)
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onShowPopUpClick(int position,View itemView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView showPopUp;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.card);
            showPopUp = itemView.findViewById(R.id.showpopup);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            //go to PatientsList to see the implementation
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            showPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            //go to PatientsList to see the implementation
                            listener.onShowPopUpClick(position,itemView);
                        }
                    }
                }
            });
        }
    }

    public Adapter_PatientsLists(ArrayList<Items_CardView> list) {
        List = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_cardview, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Items_CardView currentItem = List.get(position);
        holder.textView.setText(currentItem.getID_patient());

            //TODO - demo popup
            //TODO - demo popup
    }
}
