package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Client;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Llistat_ClientsFets extends AppCompatActivity {

    TodoApi mTodoService;

    private String[] llistatMesos = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};


    public ArrayList<Client> llistaClients;
    private RecyclerView mRecyclerView;     //RecyclerView de la llista de clients fets
    private AdaptadorLlistaClients mAdapter;   //Adaptador de clients fets
    private RecyclerView.LayoutManager mLayoutManager;  //Layout manager del recyclerview
    private Integer totalDiners = 0;    //Enter on guardem el total dels diners fets
    
    
    //Crear barra menús
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Llistat_ClientsFets.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Llistat_ClientsFets.this, ActivitatReserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Llistat_ClientsFets.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Llistat_ClientsFets.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Llistat_ClientsFets.this, Ajustos.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_llistat_clients_fets);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();   //Ens connectem a la BD


        //Creació de la barra de navegació dels 4 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_estadistiques);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Obtenim el mes que ha triat el perruquer a visualitzar
        int mesAVisualitzar = getIntent().getIntExtra("MES_TRIAT",0);

        //Creem el títol amb el mes triat
        TextView dataActual = findViewById(R.id.titolCalendariDia);
        dataActual.setText("LISTADO CLIENTES\n"+llistatMesos[mesAVisualitzar]);
       
        crearLlista(mesAVisualitzar);
        buildRecyclerView();    //Construim la llista de reserves
    }



    //Pre: mesAVisualitzar és un mes amb el format correcte
    //Post: s'omple la llista llistaClients dels clients fets entre les dues dates d'entrada (dataIn, dataFi)
    public void crearLlista(int mesAVisualitzar) {
        
        //Obtenim les dues dates del mes (Primer i darrer dia del mes)
        Calendar data2 = GregorianCalendar.getInstance();
        Date data1 = new GregorianCalendar(data2.get(Calendar.YEAR), mesAVisualitzar, 01).getTime();

        data2.setTime(data1);
        Date data2F = new GregorianCalendar(data2.get(Calendar.YEAR), mesAVisualitzar, data2.getActualMaximum(Calendar.DAY_OF_MONTH)+1).getTime();


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");    //Creem el format amb el que volem consultar les dates al servidor
        String sData1 = format.format(data1);
        String sData2 = format.format(data2F);
        
        
        
        llistaClients = new ArrayList<>();

        Call<List<Client>> call = mTodoService.listAllClients(sData1,sData2);   //Obtenim els clients entre les dues dates
        call.enqueue(new Callback<List<Client>>() {
            @Override 
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful()) {
                    for(Client auxClient : response.body()){
                        llistaClients.add(auxClient);
                        totalDiners += auxClient.getPreuTotal();
                    }
                    mAdapter.notifyDataSetChanged();    //Actualitzem el llistat

                    TextView textTotalDiners = findViewById(R.id.totalDiners);
                    textTotalDiners.setText(" Total beneficios: "+ totalDiners + "€ ");     //Mostrem per pantalla el total de diners fets
                } else {
                    Toast toast = Toast.makeText(Llistat_ClientsFets.this, "Error obteniendo listado de clientes", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(Llistat_ClientsFets.this, "Error 2 obteniendo listado de clientes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.rVclients);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new AdaptadorLlistaClients(llistaClients);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }





}
