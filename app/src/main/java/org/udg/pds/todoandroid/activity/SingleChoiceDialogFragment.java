package org.udg.pds.todoandroid.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;


public class SingleChoiceDialogFragment extends DialogFragment {

    int posicio = 0; //posició seleccionada per defecte
    private String[] llistatMesos = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};


    public interface SingleChoiceListener {
        void onPositiveButtonClicked(int posicio);
        void onNegativeButtonClicked();
    }

    SingleChoiceListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SingleChoiceListener) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + " SingleChoiceListener s'ha d'implementar");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("ESCOGE MES A CONSULTAR")
                .setSingleChoiceItems(llistatMesos, posicio, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        posicio = i;
                    }
                })
                .setPositiveButton("Consultar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onPositiveButtonClicked(posicio);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onNegativeButtonClicked();
                    }
                });

        return builder.create();
    }
}
