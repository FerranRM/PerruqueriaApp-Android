package org.udg.pds.todoandroid.activity;

import android.app.TimePickerDialog;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.Calendar.getInstance;

public class Reserva extends AppCompatActivity {

    TodoApi mTodoService;

    private String missatge;
    public ArrayList<org.udg.pds.todoandroid.entity.Reserva> llistaReserves;
    private RecyclerView mRecyclerView;     //RecyclerView de la llista de reserves
    private AdaptadorLlistaDies mAdapter;   //Adaptador de reserves
    private RecyclerView.LayoutManager mLayoutManager;  //Layout manager del recyclerview


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_reserva);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();   //Ens connectem a la BD


        //Creació de la barra de navegació dels 4 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_reserves);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String data = dataActual(); //Convertim la data que estem consultant en un format particular

        crearLlista(data);  //Obtenim les reserves per a la data
        buildRecyclerView();    //Construim la llista de reserves


        TextView dataActual = findViewById(R.id.titolCalendariDia);
        dataActual.setText(data);
    }



    //Crear barra menús
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Reserva.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Reserva.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Reserva.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Reserva.this, Ajustos.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        }
    };


    // Comparador entre dues Reserves (en funció de les seves dates)
    Comparator<org.udg.pds.todoandroid.entity.Reserva> comparator = new Comparator<org.udg.pds.todoandroid.entity.Reserva>() {
        @Override
        public int compare(org.udg.pds.todoandroid.entity.Reserva r1, org.udg.pds.todoandroid.entity.Reserva r2) {
            return r1.getDataReserva().compareTo(r2.getDataReserva());
        }
    };


    //Pre: data té el format correcte (mitjanánt el mètode dataActual)
    //Post: s'omple la llista llistaReserves dels clients per el dia actual amb totes les reserves
    public void crearLlista(String data) {
        llistaReserves = new ArrayList<>();

        Call<List<org.udg.pds.todoandroid.entity.Reserva>> call = mTodoService.getReserves();
        call.enqueue(new Callback<List<org.udg.pds.todoandroid.entity.Reserva>>() {
            @Override
            public void onResponse(Call<List<org.udg.pds.todoandroid.entity.Reserva>> call, Response<List<org.udg.pds.todoandroid.entity.Reserva>> response) {
                if (response.isSuccessful()) {
                    for(org.udg.pds.todoandroid.entity.Reserva auxReserva : response.body()){
                        if (esDataCorresponent(data, auxReserva.getDataReserva())) {
                            llistaReserves.add(auxReserva);
                        }
                    }
                    llistaReserves.sort(comparator);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast toast = Toast.makeText(Reserva.this, "Error obteniendo listado de reservas", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<org.udg.pds.todoandroid.entity.Reserva>> call, Throwable t) {
                Toast toast = Toast.makeText(Reserva.this, "Error 2 obteniendo listado de reservas", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    //Pre: data i dataClient tenen el format correcte
    //Post: retorna cert si ambdues dates són les mateixes, altrament fals.
    public boolean esDataCorresponent(String data, Date dataClient) {
        String dClient = transformarData(dataClient);
        return data.equals(dClient);
    }


    //Pre: --
    //Post: retorna la data en la que es troba el perruquer. Hi han dues possibilitats, que hagi entrat a un dia concret des del calendari
    //      o que hagi entrat directament a aquesta pantalla i, per tant, la data és l'actual en la que s'ha entrat.
    //      Llavors retorna la data en funció d'aquest factor.
    private String dataActual(){

        String textAMostrar = null;
        Intent intent = getIntent();
        if (intent.hasExtra("dadesData")){          //Hem entrat a través de la pantalla del calendari seleccionant un dia que volem consultar
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

            String dia = null;
            if (dataAMostrar[1]<10) dia = "0"+String.valueOf(dataAMostrar[1]);
            else dia = String.valueOf(dataAMostrar[1]);

            textAMostrar = (diaText+"\n"+dia+" de "+mesActual);
        }
        else {     //No hem entrat a través de la pantalla del calendari
            Date currentTime = getInstance().getTime();
            textAMostrar = transformarData(currentTime);
        }

        return textAMostrar;
    }


    //Pre: --
    //Post: retorna la data en la que es troba el perruquer. Hi han dues possibilitats, que hagi entrat a un dia concret des del calendari
    //      o que hagi entrat directament a aquesta pantalla i, per tant, la data és l'actual en la que s'ha entrat.
    //      Llavors retorna la data en funció d'aquest factor.
    private Date dataActual(String hora){

        String[] horaMinuts = hora.split(":");
        Intent intent = getIntent();
        if (intent.hasExtra("dadesData")){          //Hem entrat a través de la pantalla del calendari seleccionant un dia que volem consultar
            int[] dataAMostrar = intent.getIntArrayExtra("dadesData");
            return new GregorianCalendar(dataAMostrar[3], dataAMostrar[0], dataAMostrar[1],Integer.parseInt(horaMinuts[0]), Integer.parseInt(horaMinuts[1])).getTime();
        }
        else {                           //No hem entrat a través de la pantalla del calendari
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaMinuts[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(horaMinuts[1]));

            return cal.getTime();
        }
    }




    public String transformarData(Date data){
        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy");

        String diaText = (String) DateFormat.format("EEEE",  data);
        if (diaText.equals("lunes") || diaText.equals("Mon")) diaText = "Lunes";
        else if (diaText.equals("martes") || diaText.equals("Tue")) diaText = "Martes";
        else if (diaText.equals("miércoles") || diaText.equals("Wed")) diaText = "Miércoles";
        else if (diaText.equals("jueves") || diaText.equals("Thu")) diaText = "Jueves";
        else if (diaText.equals("viernes") || diaText.equals("Fri")) diaText = "Viernes";
        else if (diaText.equals("sábado") || diaText.equals("Sat")) diaText = "Sábado";
        else diaText = "Domingo";

        String diaActual  = (String) DateFormat.format("dd",  data);
        String mesActual  = (String) DateFormat.format("MM",  data);
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

        return (diaText+"\n"+diaActual+ " de "+mesActual);
    }


    //Botó afegir client
    public void onClickAfegirClient(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Reserva.this);

        LayoutInflater inflater = Reserva.this.getLayoutInflater();
        View infoClient = inflater.inflate(R.layout.afegir_client, null);

        EditText eT_nomClient = infoClient.findViewById(R.id.afegir_client_nom);
        Button b_horaClient = infoClient.findViewById(R.id.afegir_client_hora_button);


        mBuilder.setView(infoClient)
            .setPositiveButton("AÑADIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String nomClient = eT_nomClient.getText().toString();
                    String horaClient = b_horaClient.getText().toString();

                    Date data = dataActual(horaClient);     //Obtenim la data en la que s'està fent la reserva i li afegim la hora que s'ha entrat

                    org.udg.pds.todoandroid.entity.Reserva novaReserva = new org.udg.pds.todoandroid.entity.Reserva(data, nomClient);     //Assignem valors a la Reserva

                    missatge = "Reserva añadida!";
                    afegirReserva(novaReserva);  //Afegim la nova reserva
                }
            })

            .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    //Pre: novaReserva és correcte
    //Post: s'afegeix la reserva a la BD i a la llista de Reserves que es veu per pantalla, actualitzant-la després.
    //      Altrament, si hi ha un error es mostra per pantalla.
    void afegirReserva(org.udg.pds.todoandroid.entity.Reserva novaReserva) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Reserva.this);
        Call<IdObject> call = mTodoService.addReseva(novaReserva);
        call.enqueue(new Callback<IdObject>() {
            @Override
            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                if (response.isSuccessful()) {
                    novaReserva.setId(response.body().id);
                    llistaReserves.add(novaReserva);
                    llistaReserves.sort(comparator);

                    mAdapter.notifyDataSetChanged();    //Actualitzem canvis

                    Toast toast = Toast.makeText(Reserva.this, missatge, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(Reserva.this, "Error al añadir la reserva", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<IdObject> call, Throwable t) {
                Toast toast = Toast.makeText(Reserva.this, "Error 2 al añadir la reserva", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    // CREACIÓ BOTÓ RELLOTGE PER TRIAR HORA
    public void onClickHoraClient(View infoClient) {
        Button dateButton = (Button) infoClient.findViewById(R.id.afegir_client_hora_button);
        // Show the date selection dialog when the "Set" button is pressed
        // Es mostra un rellotje per pantalla a l'hora de fer click al botó 'Escoger hora'
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog horaPickerDialog = new TimePickerDialog(Reserva.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute<10 && hourOfDay>9) dateButton.setText(hourOfDay + ":0" + minute);   //Possem un 0 davant per als minuts (Si és < 10)
                        else if (minute<10 && hourOfDay<10) dateButton.setText("0"+hourOfDay + ":0" + minute);
                        else if (minute>9 && hourOfDay<10) dateButton.setText("0"+hourOfDay + ":" + minute);
                        else  dateButton.setText(hourOfDay + ":" + minute);
                    }
                },8,0,true);
                horaPickerDialog.show();
            }
        });
    }

    //Pre: posicio existeix a llistaReserves
    //Post: s'elimina la reserva de la BD i, per tant, del llistat actual que es mostra per pantalla.
    public void removeItem(int posicio) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Reserva.this);

        org.udg.pds.todoandroid.entity.Reserva reservaEliminar = llistaReserves.get(posicio);

        Call<ResponseBody> call = mTodoService.deleteReserva(reservaEliminar.getId().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    llistaReserves.remove(posicio);
                    llistaReserves.sort(comparator);

                    mAdapter.notifyDataSetChanged();    //Actualitzem canvis

                    if (!missatge.equals("Modificación echa!")) {
                        Toast toast = Toast.makeText(Reserva.this, missatge, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(Reserva.this, "Error al eliminar la reserva", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast toast = Toast.makeText(Reserva.this, "Error 2: "+t.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    //MODIFICAR UNA RESERVA (HORA O NOM)
    public void changeItem(int position) {
        final String[] nom = {llistaReserves.get(position).getNomReserva()};

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(llistaReserves.get(position).getDataReserva());
        String horaR1 = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Reserva.this);

        LayoutInflater inflater = Reserva.this.getLayoutInflater();
        View infoClient = inflater.inflate(R.layout.afegir_client, null);

        EditText eT_nomClient = infoClient.findViewById(R.id.afegir_client_nom);
        Button b_horaClient = infoClient.findViewById(R.id.afegir_client_hora_button);

        eT_nomClient.setText(nom[0]);
        b_horaClient.setText(horaR1);

        mBuilder.setView(infoClient)
                .setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String nomClient = eT_nomClient.getText().toString();
                        String horaClient = b_horaClient.getText().toString();

                        Date pData = dataActual(horaClient);     //Obtenim la data en la que s'està fent la reserva i li afegim la hora que s'ha entrat

                        org.udg.pds.todoandroid.entity.Reserva novaReserva = new org.udg.pds.todoandroid.entity.Reserva(pData, nomClient);     //Assignem valors a la Reserva

                        removeItem(position);   //Primer eliminem l'antic
                        afegirReserva(novaReserva);     //I després afegim el nou
                    }
                })

                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.rVclients);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new AdaptadorLlistaDies(llistaReserves);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdaptadorLlistaDies.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                missatge = "Modificación echa!";
                changeItem(position);
            }

            @Override
            public void onDeleteClick(int position) {
                missatge = "Reserva eliminada!";
                removeItem(position);
            }
        });
    }






}





