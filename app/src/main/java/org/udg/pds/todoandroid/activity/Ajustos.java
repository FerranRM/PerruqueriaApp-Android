package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Perruquer;
import org.udg.pds.todoandroid.entity.Producte;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ajustos extends AppCompatActivity {

    TodoApi mTodoService;


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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_ajustos);


        //Creació de la barra de navegació dels 5 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_ajustos);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }






    public void tancarSessioPerruquer(View view){
        Call<OkHttpClient> call = mTodoService.logout();
        call.enqueue(new Callback<OkHttpClient>() {
            @Override
            public void onResponse(Call<OkHttpClient> call, Response<OkHttpClient> response) {

                if (response.isSuccessful()) {
                    Ajustos.this.startActivity(new Intent(Ajustos.this, Login.class));
                    Ajustos.this.finish();
                } else {

                    Toast toast = Toast.makeText(Ajustos.this, "Error cerrando sesión "+response.raw(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<OkHttpClient> call, Throwable t) {
                Toast toast = Toast.makeText(Ajustos.this, "Error cerrando sesión in 2", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }





}





