package com.example.respirho;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_PatientData extends RecyclerView.Adapter<Adapter_PatientData.ViewHolder> {

    private ArrayList<Item_StorageFiles> List; //mExampleList (codingflow video)
    private OnItemClickListener mListener;

    public interface OnItemClickListener {

        void onDownloadFileClick(int position);
        void onDeleteFileClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_item_storage_files;
        public ImageView download_item_storage_files;
        public ImageView delete_item_storage_files;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            text_item_storage_files = itemView.findViewById(R.id.text_item_storage_files);
            download_item_storage_files = itemView.findViewById(R.id.download_item_storage_files);
            delete_item_storage_files=itemView.findViewById(R.id.delete_item_storage_files);

            download_item_storage_files.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            //go to PatientData to see the implementation
                            listener.onDownloadFileClick(position);
                        }
                    }
                }
            });

            delete_item_storage_files.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            //go to PatientData to see the implementation
                            listener.onDeleteFileClick(position);
                        }
                    }
                }
            });
        }
    }

    public Adapter_PatientData(ArrayList<Item_StorageFiles> list) {
        List = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_storage_files, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item_StorageFiles currentItem = List.get(position);
        holder.text_item_storage_files.setText(currentItem.getFilename());
    }
}
