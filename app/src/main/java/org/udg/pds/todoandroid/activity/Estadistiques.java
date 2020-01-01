package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Perruquer;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estadistiques extends AppCompatActivity implements SingleChoiceDialogFragment.SingleChoiceListener {

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
                    Intent intent2 = new Intent(Estadistiques.this, ActivitatReserva.class);
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

        mTodoService = ((TodoApp)this.getApplication()).getAPI();   //Ens connectem a la BD

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_estadistiques);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    //Canvi a PANTALLA llistat clients (Nom client i quant diner s'ha deixat). Abans de canviar surt un fragment per triar el mes que es vol consultar
    public void llistatClientsFets(View v){
        DialogFragment singleChoiceDialog = new SingleChoiceDialogFragment();
        singleChoiceDialog.setCancelable(false);
        singleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");
    }

    @Override
    public void onPositiveButtonClicked(int posicio) {
        Intent intent = new Intent(Estadistiques.this, Llistat_ClientsFets.class);
        intent.putExtra("MES_TRIAT",posicio);
        startActivity(intent);
    }

    @Override
    public void onNegativeButtonClicked() {
        //Botó cancel·lar no fa res
    }



    //Canvi a PANTALLA ESTADÍSTICA total guanys el darrer any
    public void estdTotalDiners(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresDiners.class);
        startActivity(intent);
    }


    //Canvi a PANTALLA ESTADÍSTICA número total de clients el darrer any
    public void estdTotalClients(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresClients.class);
        startActivity(intent);
    }


    //Canvi a PANTALLA ESTADÍSTICA comparació de beneficis entre els dos géneres
    public void estdCompararSexes(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresCompararSexes.class);
        startActivity(intent);
    }


    //Canvi a PANTALLA ESTADÍSTICA comparació diners any actual i anterior
    public void estdCompararDiners(View v){
        Intent intent = new Intent(Estadistiques.this, Estad_BarresCompararDiners.class);
        startActivity(intent);
    }


    //Canvi a PANTALLA ESTADÍSTICA total diners fets per cada perruquer
    //Aquesta estadística només pot ser consultada per el cap de la perruqueria, altrament es mostrarà un missatge per pantalla.
    public void estdPastisTotalDiners(View v){
        Call<Perruquer> call = mTodoService.getIdPerruquer();
        call.enqueue(new Callback<Perruquer>() {
            @Override
            public void onResponse(Call<Perruquer> call, Response<Perruquer> response) {

                if (response.isSuccessful()) {
                    if (response.body().getNomPerruquer().equals("d")) {
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

