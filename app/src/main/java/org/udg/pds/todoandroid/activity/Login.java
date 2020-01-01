package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Perruquer;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.rest.TodoApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



// Aquest és el fragment d’accés on l’usuari introdueix el nom d’usuari i la contrasenya i
// a continuació, es truca a un RESTResponder_RF per comprovar l'autenticació
public class Login extends AppCompatActivity {

    TodoApi mTodoService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        Button b = (Button)findViewById(R.id.login_button);
        // Aquest és l’oient que s’utilitzarà quan l’usuari premi el botó “Iniciar sessión”

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText u = (EditText) Login.this.findViewById(R.id.login_username);
                EditText p = (EditText) Login.this.findViewById(R.id.login_password);
                Login.this.checkCredentials(u.getText().toString(), p.getText().toString());
            }
        });

    }


    // Aquest mètode es crida quan es prem el botó "Iniciar sessión" al fragment d'inici de sessió
    public void checkCredentials(String nomPerruquer, String contrasenya) {
        UserLogin ul = new UserLogin();
        ul.nomPerruquer = nomPerruquer;
        ul.contrasenya = contrasenya;
        Call<Perruquer> call = mTodoService.login(ul);
        call.enqueue(new Callback<Perruquer>() {
            @Override
            public void onResponse(Call<Perruquer> call, Response<Perruquer> response) {

                if (response.isSuccessful()) {
                    Login.this.startActivity(new Intent(Login.this, ActivitatClient.class));
                    Login.this.finish();
                } else {
                    Toast toast = Toast.makeText(Login.this, "Error en el inicio de sessión", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Perruquer> call, Throwable t) {
                Toast toast = Toast.makeText(Login.this, "Fallo en el inicio de sessión", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
