package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onResume() {
        super.onResume();

        TodoApi todoApi = ((TodoApp) this.getApplication()).getAPI();

        Call<String> call = todoApi.check();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {  //Ja estem logegats
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, ActivitatClient.class));
                    SplashScreen.this.finish();
                } else {    //No estem logegats
                    SplashScreen.this.startActivity(new Intent(SplashScreen.this, Login.class));
                    SplashScreen.this.finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(SplashScreen.this, "Error al verificar el estado de inicio de sesión", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


    }
}
