package org.udg.pds.todoandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Ajustos extends AppCompatActivity {

    ArrayList<String> llistaClients;
    RecyclerView recycler;
    private AdaptadorLlistaDies adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_ajustos);


        //Creació de la barra de navegació dels 5 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_ajustos);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }



    //Crear barra menús
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Ajustos.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent2 = new Intent(Ajustos.this, CalendariDia.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Ajustos.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_alertes:
                    Intent intent4 = new Intent(Ajustos.this, Alertes.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    break;
            }
            return false;
        }
    };



    private String dataActual(){

        String textAMostrar = null;
        Intent intent = getIntent();
        if (intent.hasExtra("dadesData")){                            //Hem entrat a través de la pantalla del calendari seleccionant un dia que volem consultar
            int[] dataAMostrar = intent.getIntArrayExtra("dadesData");

            String diaText = null;
            if (dataAMostrar[2]==2) diaText = "Lunes";
            else if (dataAMostrar[2]==3) diaText = "Martes";
            else if (dataAMostrar[2]==4) diaText = "Miércoles";
            else if (dataAMostrar[2]==5) diaText = "Jueves";
            else if (dataAMostrar[2]==6) diaText = "Viernes";
            else if (dataAMostrar[2]==7) diaText = "Sábado";
            else diaText = "Domingo";

            String mesActual = null;
            if (dataAMostrar[0]==0) mesActual = "ENERO";
            else if (dataAMostrar[0]==1) mesActual = "FEBRERO";
            else if (dataAMostrar[0]==2) mesActual = "MARZO";
            else if (dataAMostrar[0]==3) mesActual = "ABRIL";
            else if (dataAMostrar[0]==4) mesActual = "MAYO";
            else if (dataAMostrar[0]==5) mesActual = "JUNIO";
            else if (dataAMostrar[0]==6) mesActual = "JULIO";
            else if (dataAMostrar[0]==7) mesActual = "AGOSTO";
            else if (dataAMostrar[0]==8) mesActual = "SETIEMBRE";
            else if (dataAMostrar[0]==9) mesActual = "OCTUBRE";
            else if (dataAMostrar[0]==10) mesActual = "NOVIEMBRE";
            else mesActual = "DICIEMBRE";

            textAMostrar = (diaText+"\n"+dataAMostrar[1]+" de "+mesActual);
        }
        else {                           //No hem entrat a través de la pantalla del calendari
            SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy");
            Date currentTime = Calendar.getInstance().getTime();

            String diaText = (String) DateFormat.format("EEEE",  currentTime);
            if (diaText.equals("lunes")) diaText = "Lunes";
            else if (diaText.equals("martes")) diaText = "Martes";
            else if (diaText.equals("miercoles")) diaText = "Miércoles";
            else if (diaText.equals("jueves")) diaText = "Jueves";
            else if (diaText.equals("viernes")) diaText = "Viernes";
            else if (diaText.equals("sabado")) diaText = "Sábado";
            else diaText = "Domingo";

            String diaActual  = (String) DateFormat.format("dd",  currentTime);
            String mesActual  = (String) DateFormat.format("MM",  currentTime);
            if (mesActual.compareTo("01")==0) mesActual = "ENERO";
            else if (mesActual.compareTo("02")==0) mesActual = "FEBRERO";
            else if (mesActual.compareTo("03")==0) mesActual = "MARZO";
            else if (mesActual.compareTo("04")==0) mesActual = "ABRIL";
            else if (mesActual.compareTo("05")==0) mesActual = "MAYO";
            else if (mesActual.compareTo("06")==0) mesActual = "JUNIO";
            else if (mesActual.compareTo("07")==0) mesActual = "JULIO";
            else if (mesActual.compareTo("08")==0) mesActual = "AGOSTO";
            else if (mesActual.compareTo("09")==0) mesActual = "SETIEMBRE";
            else if (mesActual.compareTo("10")==0) mesActual = "OCTUBRE";
            else if (mesActual.compareTo("11")==0) mesActual = "NOVIEMBRE";
            else mesActual = "DICIEMBRE";

            textAMostrar = (diaText+"\n"+diaActual+ " de "+mesActual);
        }

        return textAMostrar;
    }


    //Botó canvi a pantalla calendari
    public void onClickCalendari(View v) {
        Intent intent = new Intent(Ajustos.this, Calendari.class);
        startActivity(intent);
    }


    //Botó afegir client
    public void onClickAfegirClient(View v) {
        /*Toast missatge = Toast.makeText(getApplicationContext(), "GRRRR", Toast.LENGTH_SHORT);
        missatge.setGravity(Gravity.CENTER, 0, 0);
        missatge.show();*/

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Ajustos.this);

        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mBuilder.setView(inflater.inflate(R.layout.afegir_client, null))
            .setPositiveButton("AÑADIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Toast missatge = Toast.makeText(getApplicationContext(), "AÑADIDO", Toast.LENGTH_SHORT);
                    missatge.setGravity(Gravity.CENTER, 0, 0);
                    missatge.show();
                }
            })

            .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

}





