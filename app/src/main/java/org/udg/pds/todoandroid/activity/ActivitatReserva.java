package org.udg.pds.todoandroid.activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Reserva;
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

public class ActivitatReserva extends AppCompatActivity {

    TodoApi mTodoService;

    private Date dataReserva;   //Data de la qual es mostraràn les reserves

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

        crearLlista();      //Obtenim les reserves per a la data
        buildRecyclerView();    //Construim la llista de reserves


        TextView dataActual = findViewById(R.id.titolCalendariDia);
        dataActual.setText(data);



        /*ServeiMissatgeriaFirebase serveiMissatgeriaFirebase = null;
        serveiMissatgeriaFirebase.mostrarNotificacio("JJJJ", "Nueva reserva");*/
    }



    //Crear barra menús
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(ActivitatReserva.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(ActivitatReserva.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(ActivitatReserva.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(ActivitatReserva.this, Ajustos.class);
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


    //Pre: --
    //Post: s'omple la llista llistaReserves dels clients per el dia actual amb totes les reserves
    public void crearLlista() {
        llistaReserves = new ArrayList<>();


        Calendar data2 = GregorianCalendar.getInstance();
        data2.setTime(dataReserva);

        Date data2F = new GregorianCalendar(data2.get(Calendar.YEAR), data2.get(Calendar.MONTH), data2.get(Calendar.DAY_OF_MONTH)+1).getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");    //Creem el format amb el que volem consultar les dates al servidor
        String sData1 = format.format(dataReserva);
        String sData2 = format.format(data2F);

        Call<List<Reserva>> call = mTodoService.listAllReserves(sData1,sData2);
        call.enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful()) {
                    for(Reserva auxReserva : response.body()){
                        llistaReserves.add(auxReserva);
                    }
                    llistaReserves.sort(comparator);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast toast = Toast.makeText(ActivitatReserva.this, "Error obteniendo listado de reservas", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast toast = Toast.makeText(ActivitatReserva.this, "Fallo obteniendo listado de reservas", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
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
            dataReserva = new GregorianCalendar(dataAMostrar[3], dataAMostrar[0], dataAMostrar[1]).getTime();
            getIntent().removeExtra("dadesData");
        }
        else {
            Calendar data2 = GregorianCalendar.getInstance();
            dataReserva = new GregorianCalendar(data2.get(Calendar.YEAR), data2.get(Calendar.MONTH), data2.get(Calendar.DAY_OF_MONTH)).getTime();
        }

        textAMostrar = transformarData(dataReserva);

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
            cal.setTime(dataReserva);
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
        if (mesActual.compareTo("01")==0) mesActual = "enero";
        else if (mesActual.compareTo("02")==0) mesActual = "febrero";
        else if (mesActual.compareTo("03")==0) mesActual = "marzo";
        else if (mesActual.compareTo("04")==0) mesActual = "abril";
        else if (mesActual.compareTo("05")==0) mesActual = "mayo";
        else if (mesActual.compareTo("06")==0) mesActual = "junio";
        else if (mesActual.compareTo("07")==0) mesActual = "julio";
        else if (mesActual.compareTo("08")==0) mesActual = "agosto";
        else if (mesActual.compareTo("09")==0) mesActual = "setiembre";
        else if (mesActual.compareTo("10")==0) mesActual = "octubre";
        else if (mesActual.compareTo("11")==0) mesActual = "noviembre";
        else mesActual = "diciembre";

        return (diaText+"\n"+diaActual+ " de "+mesActual);
    }


    //Botó afegir client
    public void onClickAfegirClient(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatReserva.this);

        LayoutInflater inflater = ActivitatReserva.this.getLayoutInflater();
        View infoClient = inflater.inflate(R.layout.afegir_client, null);

        EditText eT_nomClient = infoClient.findViewById(R.id.afegir_client_nom);
        Button b_horaClient = infoClient.findViewById(R.id.afegir_client_hora_button);


        mBuilder.setView(infoClient)
            .setPositiveButton("AÑADIR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String nomClient = eT_nomClient.getText().toString();
                    String horaClient = b_horaClient.getText().toString();

                    if (horaClient.equals("Escoger hora")) {
                        mostrarAlerta("Por favor, introduce la hora de la reserva");
                    }
                    else if (nomClient.isEmpty()) {
                        mostrarAlerta("Por favor, introduce el nombre de la reserva");
                    }
                    else {
                        Date data = dataActual(horaClient);     //Obtenim la data en la que s'està fent la reserva i li afegim la hora que s'ha entrat

                        org.udg.pds.todoandroid.entity.Reserva novaReserva = new org.udg.pds.todoandroid.entity.Reserva(data, nomClient);     //Assignem valors a la ActivitatReserva

                        missatge = "Reserva añadida!";
                        afegirReserva(novaReserva);  //Afegim la nova reserva
                    }
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatReserva.this);
        Call<IdObject> call = mTodoService.addReseva(novaReserva);
        call.enqueue(new Callback<IdObject>() {
            @Override
            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                if (response.isSuccessful()) {
                    novaReserva.setId(response.body().id);
                    llistaReserves.add(novaReserva);
                    llistaReserves.sort(comparator);




                    /*ServeiMissatgeriaFirebase serveiMissatgeriaFirebase = null;

                    serveiMissatgeriaFirebase.mostrarNotificacio("JJJJ", "Nueva reserva");*/





                    mAdapter.notifyDataSetChanged();    //Actualitzem canvis

                    Toast toast = Toast.makeText(ActivitatReserva.this, missatge, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(ActivitatReserva.this, "Error al añadir la reserva", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<IdObject> call, Throwable t) {
                Toast toast = Toast.makeText(ActivitatReserva.this, "Fallo al añadir la reserva", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    // CREACIÓ BOTÓ RELLOTGE PER TRIAR HORA
    public void onClickHoraClient(View infoClient) {
        Button dateButton = (Button) infoClient.findViewById(R.id.afegir_client_hora_button);

        TimePickerDialog horaPickerDialog = new TimePickerDialog(ActivitatReserva.this, new TimePickerDialog.OnTimeSetListener() {
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

    //Pre: posicio existeix a llistaReserves
    //Post: s'elimina la reserva de la BD i, per tant, del llistat actual que es mostra per pantalla.
    public void removeItem(int posicio) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatReserva.this);

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
                        Toast toast = Toast.makeText(ActivitatReserva.this, missatge, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(ActivitatReserva.this, "Error al eliminar la reserva", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast toast = Toast.makeText(ActivitatReserva.this, "Fallo: "+t.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }


    //MODIFICAR UNA RESERVA (HORA O NOM)
    public void changeItem(int position) {
        final String[] nom = {llistaReserves.get(position).getNomReserva()};

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(llistaReserves.get(position).getDataReserva());
        String horaR1 = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatReserva.this);

        LayoutInflater inflater = ActivitatReserva.this.getLayoutInflater();
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

                        org.udg.pds.todoandroid.entity.Reserva novaReserva = new org.udg.pds.todoandroid.entity.Reserva(pData, nomClient);     //Assignem valors a la ActivitatReserva

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


    //Pre: nomAlerta ha de ser un missatge d'alerta identificatiu de l'error.
    //Post: mostra per pantalla l'alerta amb el titol: ERROR i el missatge entrat per paràmetre.
    public void mostrarAlerta(String nomAlerta){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ActivitatReserva.this);
        alerta.setMessage(nomAlerta);
        alerta.setCancelable(true);
        alerta.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = alerta.create();
        alert11.setTitle("ERROR");
        alert11.show();
    }




}





