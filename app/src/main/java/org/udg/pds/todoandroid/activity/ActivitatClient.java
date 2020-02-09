package org.udg.pds.todoandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Client;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Producte;
import org.udg.pds.todoandroid.entity.ServeiPrestat;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitatClient extends AppCompatActivity {

    TodoApi mTodoService;

    private Button botoServeis;
    private Button botoProductes;

    private TextView textServeisPrestats;
    private TextView textProductes;

    private TextView tvPreuTotal;
    private Integer preuTotal;

    private List<ServeiPrestat> llistaServeis = new ArrayList<>();
    private List<Producte> llistaProductes = new ArrayList<>();

    private boolean[] checkedServeis;
    private boolean[] checkedProductes;


    private ArrayList<Integer> mUserServeis = new ArrayList<>();
    private ArrayList<Integer> mUserProductes = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(ActivitatClient.this, ActivitatReserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(ActivitatClient.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(ActivitatClient.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(ActivitatClient.this, Ajustos.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitat_client);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();   //Ens connectem a la BD

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_afegirClient);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tvPreuTotal = findViewById(R.id.textTotal);
        preuTotal = 0;

        llistatServeis();   // Métode encarregat de llistar tots els serveis que se li poden fer a un client
        llistatProductes();     // Métode encarregat de llistar els productes que pot comprar el client
    }


    //Pre: --
    //Post: crea un String amb els valors entrats a l'hora d'afegir un client.
    //      Cada valor del client (Sexe, tipus de pentinat, nom, data i productes) està separat per un "/" .
    //      El preu total és l'únic que ja s'ha calculat prèviament.
    //      En cas de que hi hagui alguna dada malament es mostra una alerta per pantalla amb el valor erroni.
    //      Altrament es guarda el client a la BD mitjaçant el mètode guardarClientBD.
    public void guardarClient(View view) {
        String clientFet = comprovarSexe();
        if (clientFet=="!") mostrarAlerta("Por favor, introduce el género del cliente");
        else {
            EditText nomClient = (EditText) findViewById(R.id.nomClient);
            String aux = nomClient.getText().toString();
            if (aux.isEmpty()) mostrarAlerta("Por favor, introduce el nombre del cliente");
            else {
                clientFet += aux + "/";

                if (preuTotal!=0) guardarClientBD(clientFet);
                else mostrarAlerta("Por favor, selecciona un servicio y/o producto");
            }
        }
    }

    //Pre: --
    //Post: Retorna un string amb el número identificatiu del tipus de sexe seleccionat, si no hi ha cap seleccionat retorna '!'.
    //      Altrament, si és home 'True', si és dona 'False'.
    public String comprovarSexe(){
        RadioButton rBHome = (RadioButton) findViewById(R.id.radioButtonH);
        RadioButton rBDona = (RadioButton) findViewById(R.id.radioButtonM);
        if (rBHome.isChecked()) return "True/";
        else if (rBDona.isChecked()) return "False/";
        else return "!";
    }


    //Pre: nomAlerta ha de ser un missatge d'alerta identificatiu de l'error.
    //Post: mostra per pantalla l'alerta amb el titol: ERROR i el missatge entrat per paràmetre.
    public void mostrarAlerta(String nomAlerta){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ActivitatClient.this);
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

    //Pre: --
    //Post: s'encarrega del llistat de serveis que se li poden fer al client
    public void llistatServeis() {
        botoServeis = findViewById(R.id.ButtonServeisPrestats);
        textServeisPrestats = (TextView) findViewById(R.id.tvServeisPrestats);

        obtenirDadesServeis();
    }

    //Pre: --
    //Post: s'encarrega del llistat de productes a triar que pot haver comprat el client
    public void llistatProductes() {
        botoProductes = findViewById(R.id.ButtonProductes);
        textProductes = (TextView) findViewById(R.id.tvItemSelected);

        obtenirDadesProductes();
    }

    public void obtenirDadesServeis() {
        Call<List<ServeiPrestat>> call = mTodoService.getServeisPrestats();
        call.enqueue(new Callback<List<ServeiPrestat>>() {
            @Override
            public void onResponse(Call<List<ServeiPrestat>> call, Response<List<ServeiPrestat>> response) {
                if (response.isSuccessful()) {
                    for(ServeiPrestat auxServeis : response.body()){
                        llistaServeis.add(auxServeis);
                    }
                    finestraServeisPrestats();
                } else {
                    Toast toast = Toast.makeText(ActivitatClient.this, "Error obteniendo listado de productos", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<ServeiPrestat>> call, Throwable t) {
                Toast toast = Toast.makeText(ActivitatClient.this, "Fallo obteniendo listado de productos", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    public void finestraServeisPrestats() {
        checkedServeis = new boolean[llistaServeis.size()];

        botoServeis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatClient.this);
                mBuilder.setTitle("Servicios prestados al cliente");

                String[] arrServeis = new String[llistaServeis.size()];
                for(int i=0 ; i< llistaServeis.size();i++){
                    arrServeis[i] = llistaServeis.get(i).getDescripcioServei();
                }


                mBuilder.setMultiChoiceItems(arrServeis, checkedServeis, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mUserServeis.add(position);

                            preuTotal += llistaServeis.get(position).getPreuServei();
                            tvPreuTotal.setText("Total: " +preuTotal+ "€");      //Mostrem per pantalla el preu total final
                        }else{
                            mUserServeis.remove((Integer.valueOf(position)));

                            preuTotal -= llistaServeis.get(position).getPreuServei();
                            tvPreuTotal.setText("Total: " +preuTotal+ "€");      //Mostrem per pantalla el preu total final
                        }
                    }
                });

                mBuilder.setCancelable(false);

                //Botó acceptar amb els clickeables triats
                mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserServeis.size(); i++) {
                            item = item + llistaServeis.get(mUserServeis.get(i)).getDescripcioServei();
                            if (i != mUserServeis.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        textServeisPrestats.setText(item);
                    }
                });

                //Botó borrar tots els serveis seleccionats
                mBuilder.setNeutralButton("Borrar todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mUserServeis.clear();
                        for (int i = 0; i < checkedServeis.length; i++) {
                            if (checkedServeis[i])
                                preuTotal -= llistaServeis.get(i).getPreuServei();

                            checkedServeis[i] = false;
                            textServeisPrestats.setText("Ningún servicio seleccionado");
                        }

                        tvPreuTotal.setText("Total: " +preuTotal+ "€");      //Mostrem per pantalla el preu total final
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }


    public void obtenirDadesProductes() {
        Call<List<Producte>> call = mTodoService.getProductes();
        call.enqueue(new Callback<List<Producte>>() {
            @Override
            public void onResponse(Call<List<Producte>> call, Response<List<Producte>> response) {
                if (response.isSuccessful()) {
                    for(Producte auxProducte : response.body()){
                        llistaProductes.add(auxProducte);
                    }
                    butonsBLA();
                } else {
                    Toast toast = Toast.makeText(ActivitatClient.this, "Error obteniendo listado de productos", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Producte>> call, Throwable t) {
                Toast toast = Toast.makeText(ActivitatClient.this, "Fallo obteniendo listado de productos", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    public void butonsBLA() {
        checkedProductes = new boolean[llistaProductes.size()];

        botoProductes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatClient.this);
                mBuilder.setTitle("Productos comprados por el cliente");

                String[] arr = new String[llistaProductes.size()];
                for(int i=0 ; i< llistaProductes.size();i++){
                    arr[i] = llistaProductes.get(i).getDescripcioProducte();
                }


                mBuilder.setMultiChoiceItems(arr, checkedProductes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mUserProductes.add(position);

                            preuTotal += llistaProductes.get(position).getPreuProducte();
                            tvPreuTotal.setText("Total: " +preuTotal+ "€");      //Mostrem per pantalla el preu total final
                        }else{
                            mUserProductes.remove((Integer.valueOf(position)));

                            preuTotal -= llistaProductes.get(position).getPreuProducte();
                            tvPreuTotal.setText("Total: " +preuTotal+ "€");      //Mostrem per pantalla el preu total final
                        }
                    }
                });

                mBuilder.setCancelable(false);

                //Botó acceptar amb els clickeables triats
                mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserProductes.size(); i++) {
                            item = item + llistaProductes.get(mUserProductes.get(i)).getDescripcioProducte();
                            if (i != mUserProductes.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        if (!item.isEmpty()) textProductes.setText(item);
                    }
                });

                //Botó borrar tots els productes seleccionats
                mBuilder.setNeutralButton("Borrar todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mUserProductes.clear();
                        for (int i = 0; i < checkedProductes.length; i++) {
                            if (checkedProductes[i])
                                preuTotal -= llistaProductes.get(i).getPreuProducte();

                            checkedProductes[i] = false;
                            textProductes.setText("Ningún producto seleccionado");
                        }

                        tvPreuTotal.setText("Total: " +preuTotal+ "€");      //Mostrem per pantalla el preu total final
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }



    // Pre: string cadena conté un string amb 5 variables separades per '/' (ja s'ha comprovat previament que és vàlid)
    // Post: guarda el client entrat per cadena a la base de dades
    public void guardarClientBD(String cadena) {
        Client c1 = new Client();

        String[] cadenaVariables = cadena.split("/");

        c1.sexeClient = Boolean.valueOf(cadenaVariables[0]);
        c1.nomClient = cadenaVariables[1];
        c1.preuTotal = preuTotal;
        c1.dataClient = new Date();


        Call<IdObject> call = mTodoService.addClient(c1);
        call.enqueue(new Callback<IdObject>() {
            @Override
            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                if (response.isSuccessful()) {
                    //Un cop es guardi el Client, guardarem els productes d'aquest, si és que en té
                    guardarProductes(response.body().id);
                } else {
                    Toast toast = Toast.makeText(ActivitatClient.this, "Error al añadir cliente", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<IdObject> call, Throwable t) {
                Toast toast = Toast.makeText(ActivitatClient.this, "Fallo: "+t.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    //Pre: id del Client actual ja guardat prèviament a la BD
    //Post: s'afegeixen els productes seleccionats prèviament, si és que n'hi ha, a la BD amb el client actual.
    public void guardarProductes(Long id) {
        List<Long> llistaIds = new ArrayList<>();
        for(int i=0; i<checkedProductes.length; i++){   //Obtenim la llista dels id dels productes seleccionats
            if (checkedProductes[i]) {
                Long aux = Long.valueOf(llistaProductes.get(i).getId());
                llistaIds.add(aux);
            }
        }

        if (llistaIds.isEmpty()){   //Si no s'ha seleccionat cap producte llavors ja podem mostrar el missatge de que el client s'ha introduït a la BD correctament
            Toast toast = Toast.makeText(ActivitatClient.this, "Cliente añadido", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {  //Pujem els productes seleccionats per al client actual
            Call<ResponseBody> call = mTodoService.addProductes(llistaIds, id.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast toast = Toast.makeText(ActivitatClient.this, "Cliente añadido", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(ActivitatClient.this, "Error al añadir productos", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast toast = Toast.makeText(ActivitatClient.this, "Fallo al añadir productos", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
