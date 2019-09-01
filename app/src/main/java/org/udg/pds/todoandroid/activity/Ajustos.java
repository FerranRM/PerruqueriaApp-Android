package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ajustos extends AppCompatActivity {

    TodoApi mTodoService;

    int[] imatges = {R.drawable.noi, R.drawable.noi1, R.drawable.noia, R.drawable.noia1, R.drawable.noia2, R.drawable.noia3};


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
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Ajustos.this, Reserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Ajustos.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Ajustos.this, Calendari.class);
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

    public void modificarImatge(View view) {
        ImageView modImatge = findViewById(R.id.imatgePerfil);
        int n = new Random().nextInt(imatges.length-1);
        modImatge.setImageResource(imatges[n]);

        Toast toast = Toast.makeText(Ajustos.this, "IMAGEN CAMBIADA", Toast.LENGTH_SHORT);
        toast.show();
    }



    public void tancarSessioPerruquer(View view){
        TodoApi todoApi = ((TodoApp) this.getApplication()).getAPI();
        Call<String> call = todoApi.logout();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    Ajustos.this.startActivity(new Intent(Ajustos.this, Login.class));
                    Ajustos.this.finish();
                } else {
                    Toast toast = Toast.makeText(Ajustos.this, "Error cerrando sesión "+response.raw(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(Ajustos.this, "Error cerrando sesión in 2", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }



    public void modificarPerfilPerruquer(View view){
        /*Call<Response> call = mTodoService.logout();
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {

                if (response.isSuccessful()) {
                    Ajustos.this.startActivity(new Intent(Ajustos.this, Login.class));
                    Ajustos.this.finish();
                } else {

                    Toast toast = Toast.makeText(Ajustos.this, "Error cerrando sesión "+response.raw(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast toast = Toast.makeText(Ajustos.this, "Error cerrando sesión in 2", Toast.LENGTH_SHORT);
                toast.show();
            }
        });*/
    }


    public void instagramPerruqueria(View view) {
        Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ferranfrm/?hl=es"));
        startActivity(instagramIntent);
    }


}





