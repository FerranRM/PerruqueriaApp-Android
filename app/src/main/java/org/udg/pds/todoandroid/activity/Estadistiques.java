package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Perruquer;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estadistiques extends AppCompatActivity {

    TodoApi mTodoService;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Estadistiques.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Estadistiques.this, Reserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Estadistiques.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Estadistiques.this, Ajustos.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_estadistiques);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_estadistiques);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ImageButton bGuardar = findViewById(R.id.imgButtonGuardar);


    }

    //Canvi a pantalla estadística total vendes
    public void estdTotalDiners(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresDiners.class);
        startActivity(intent);
    }

    //Canvi a pantalla estadística total vendes
    public void estdTotalVendes(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_TotalVendes.class);
        startActivity(intent);
    }

    //Canvi a pantalla estadística total vendes
    public void estdTotaClients(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresClients.class);
        startActivity(intent);
    }

    //Canvi a pantalla estadística total vendes
    public void estdCompararSexes(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresCompararSexes.class);
        startActivity(intent);
    }

    //Canvi a pantalla estadística total vendes
    public void estdCompararDiners(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresCompararDiners.class);
        startActivity(intent);
    }

    //Canvi a pantalla estadística total vendes
    public void estdPastisTotalDiners(View v){
        Call<Perruquer> call = mTodoService.getIdPerruquer();
        call.enqueue(new Callback<Perruquer>() {
            @Override
            public void onResponse(Call<Perruquer> call, Response<Perruquer> response) {

                if (response.isSuccessful()) {
                    if (response.body().getNomPerruquer().equals("fer")) {
                        Intent intent = new Intent(Estadistiques.this, Estad_PastisTotalDiners.class);
                        startActivity(intent);
                    }
                    else{
                        Toast toast = Toast.makeText(Estadistiques.this, "Acceso único para DAVID TELLEZ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(Estadistiques.this, "Error accediendo al ID de usuario "+response.raw(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Perruquer> call, Throwable t) {
                Toast toast = Toast.makeText(Estadistiques.this, "Error accediendo al ID de usuario ", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


}

