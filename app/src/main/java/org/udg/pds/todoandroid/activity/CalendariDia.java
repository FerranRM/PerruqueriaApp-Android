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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendariDia extends AppCompatActivity {

    private ArrayList<Client> llistaClients;
    private RecyclerView mRecyclerView;
    private AdaptadorLlistaDies mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_calendari_dia);


        //Creació de la barra de navegació dels 4 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_calendari);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        createExampleList();
        buildRecyclerView();
        //setButtons();



        TextView dataActual = findViewById(R.id.titolCalendariDia);

        dataActual.setText(dataActual());
    }




    //Crear barra menús
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(CalendariDia.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_calendari:
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(CalendariDia.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_alertes:
                    Intent intent4 = new Intent(CalendariDia.this, Alertes.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(CalendariDia.this, Ajustos.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        }
    };


    public void createExampleList() {
        llistaClients = new ArrayList<>();
        llistaClients.add(new Client("David Tellez Lorenzo","09:30"));
        llistaClients.add(new Client("Ferran Rodríguez Martínez","10:00"));
        llistaClients.add(new Client("Daniel Ros Rodríguez","10:00"));
        llistaClients.add(new Client("Manuel Mallol Herman","10:00"));
        llistaClients.add(new Client("Isabela Quesada Pullares Lopez","10:00"));
    }



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
        Intent intent = new Intent(CalendariDia.this, Calendari.class);
        startActivity(intent);
    }


    //Botó afegir client
    public void onClickAfegirClient(View v) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CalendariDia.this);

        LayoutInflater inflater = CalendariDia.this.getLayoutInflater();
        View infoClient = inflater.inflate(R.layout.afegir_client, null);

        EditText eT_nomClient = infoClient.findViewById(R.id.afegir_client_nom);
        EditText eT_horaClient = infoClient.findViewById(R.id.afegir_client_hora);

        mBuilder.setView(infoClient)
            .setPositiveButton("AÑADIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String nomClient = eT_nomClient.getText().toString();
                    String horaClient = eT_horaClient.getText().toString();
                    Client auxClient = new Client(nomClient,horaClient);
                    llistaClients.add(auxClient);
                }
            })

            .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void removeItem(int position) {
        llistaClients.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text) {
        llistaClients.get(position).canviarNomClient(text);
        mAdapter.notifyItemChanged(position);
    }


    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.rVclients);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new AdaptadorLlistaDies(llistaClients);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdaptadorLlistaDies.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeItem(position, "Clicked");
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }




    /*public void setButtons() {
        buttonInsert = findViewById(R.id.button_insert);
        buttonRemove = findViewById(R.id.button_remove);
        editTextInsert = findViewById(R.id.edittext_insert);
        editTextRemove = findViewById(R.id.edittext_remove);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextInsert.getText().toString());
                insertItem(position);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(position);
            }
        });
    }*/


}





