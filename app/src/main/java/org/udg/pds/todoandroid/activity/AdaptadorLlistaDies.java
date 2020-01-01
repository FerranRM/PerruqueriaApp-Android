package org.udg.pds.todoandroid.activity;

import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Reserva;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class AdaptadorLlistaDies extends RecyclerView.Adapter<AdaptadorLlistaDies.ViewHolderDades> {

    ArrayList<Reserva> llistaReserves;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolderDades extends RecyclerView.ViewHolder {
        public ImageView mImageView;    // Recuadre on hi han les altres dades
        public TextView mTextView1;     // Nom del client
        public TextView mTextView2;     // Hora a la que s'ha atés al client
        public ImageView mDeleteImage;  // Imatge d'esborrar

        public ViewHolderDades(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.nomClient);
            mTextView2 = itemView.findViewById(R.id.horaClient);
            mDeleteImage = itemView.findViewById(R.id.imatgeBorrar);

            //Modificar reserva
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            //Eliminar reserva
            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public AdaptadorLlistaDies(ArrayList<Reserva> exampleList) {
        llistaReserves = exampleList;
    }


    @Override
    public ViewHolderDades onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.llistat_clients_dia, parent, false);
        ViewHolderDades evh = new ViewHolderDades(v, mListener);
        return evh;
    }


    @Override
    public void onBindViewHolder(ViewHolderDades holder, int position) {
        Reserva reservaActual = llistaReserves.get(position);

        holder.mTextView1.setText(reservaActual.getNomReserva());

        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy/HH:mm:ss");
        String hora = (String) DateFormat.format("HH",  reservaActual.getDataReserva());
        String minuts = (String) DateFormat.format("mm",  reservaActual.getDataReserva());

        holder.mTextView2.setText(hora+":"+minuts);
    }

    @Override
    public int getItemCount() {
        return llistaReserves.size();
    }
}
