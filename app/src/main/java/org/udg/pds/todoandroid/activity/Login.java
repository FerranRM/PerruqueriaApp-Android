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

/**
 * Created with IntelliJ IDEA.
 * Perruquer: imartin
 * Date: 28/03/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */

// This is the Login fragment where the user enters the username and password and
// then a RESTResponder_RF is called to check the authentication
public class Login extends AppCompatActivity {

    TodoApi mTodoService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        Button b = (Button)findViewById(R.id.login_button);
        // This is teh listener that will be used when the user presses the "Login" button
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*Intent entrarApp = new Intent(Login.this, ActivitatClient.class);
                startActivity(entrarApp);*/

                EditText u = (EditText) Login.this.findViewById(R.id.login_username);
                EditText p = (EditText) Login.this.findViewById(R.id.login_password);
                Login.this.checkCredentials(u.getText().toString(), p.getText().toString());
            }
        });

    }
    // This method is called when the "Login" button is pressed in the Login fragment
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

                    Toast toast = Toast.makeText(Login.this, "Error en el login "+response.raw(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Perruquer> call, Throwable t) {
                Toast toast = Toast.makeText(Login.this, "Error logging in 2", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
