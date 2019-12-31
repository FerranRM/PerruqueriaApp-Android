package org.udg.pds.todoandroid.activity;

import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class AdaptadorLlistaClients extends RecyclerView.Adapter<AdaptadorLlistaClients.ViewHolderDades> {

    ArrayList<Client> llistaClients;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolderDades extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextViewDiners;

        public ViewHolderDades(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.nomClient);
            mTextView2 = itemView.findViewById(R.id.horaClient);
            mTextView3 = itemView.findViewById(R.id.dataClient);
            mTextViewDiners = itemView.findViewById(R.id.dinersTotals);
        }
    }

    public AdaptadorLlistaClients(ArrayList<Client> exampleList) {
        llistaClients = exampleList;
    }

    @Override
    public ViewHolderDades onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.llistat_mesos, parent, false);
        ViewHolderDades evh = new ViewHolderDades(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ViewHolderDades holder, int position) {
        Client clientActual = llistaClients.get(position);

        holder.mTextView1.setText(clientActual.getNomClient());

        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy/HH:mm:ss");
        String dia = (String) DateFormat.format("dd",  clientActual.getDataClient());
        String hora = (String) DateFormat.format("HH",  clientActual.getDataClient());
        String minuts = (String) DateFormat.format("mm",  clientActual.getDataClient());

        holder.mTextView2.setText("a las "+hora+":"+minuts);

        holder.mTextView3.setText("Día: "+dia);

        holder.mTextViewDiners.setText(clientActual.getPreuTotal()+" €");
    }

    @Override
    public int getItemCount() {
        return llistaClients.size();
    }
}
