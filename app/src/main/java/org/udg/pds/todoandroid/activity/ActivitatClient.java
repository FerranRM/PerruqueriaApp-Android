package org.udg.pds.todoandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
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
import org.udg.pds.todoandroid.entity.TallCabells;
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

    private Button botoCabells;
    private Button botoProductes;
    private TextView textTallCabells;
    private TextView textProductes;
    private List<TallCabells> llistaTalls = new ArrayList<>();
    private List<Producte> llistaProductes = new ArrayList<>();
    private boolean[] checkedItems;
    private ArrayList<Integer> mUserItems = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(ActivitatClient.this, Reserva.class);
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

        llistatTallCabells();   //Métode encarregat de llistar els tipus de tall de cabells que pot fer-se el client
        llistatProductes();     //Métode encarregat de llistar els productes que pot comprar el client
    }


    //Pre: --
    //Post: crea un String amb els valors entrats a l'hora d'afegir un client.
    //      Cada valor del client (Sexe, tipus de pentinat, preu total, nom, data i productes) està separat per un "/" .
    //      En cas de que hi hagui alguna dada malament es mostra una alerta per pantalla amb el valor erroni.
    //      Altrament es guarda el client a la BD mitjaçant el mètode guardarClientBD.
    public void guardarClient(View view) {
        String clientFet = comprovarSexe();
        if (clientFet=="!") mostrarAlerta("Tipo de sexo no seleccionado");
        else {
            Integer auxPreu = null;
            String aux = comprovarTall();
            if (aux.equals("1/")) auxPreu = 7;
            else if (aux.equals("2/")) auxPreu = 15;
            else auxPreu = 30;

            if (aux == "!") mostrarAlerta("Tipo de corte no seleccionado");
            else{
                clientFet += aux;

                EditText nomClient = (EditText) findViewById(R.id.nomClient);
                aux = nomClient.getText().toString();
                if (aux.isEmpty()) mostrarAlerta("Nombre cliente no introducido");
                else {
                    clientFet += aux + "/";
                    auxPreu += productesPreu();
                    aux = productes();
                    clientFet += aux+"/"+auxPreu.toString();

                    TextView preuTotal = findViewById(R.id.textTotal);
                    preuTotal.setText("Total: " +auxPreu+ "€");      //Mostrem per pantalla el preu total final

                    guardarClientBD(clientFet);
                }
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

    //Pre: --
    //Post: Retorna un string amb el número identificatiu del tipus de tall seleccionat, si no hi ha cap seleccionat retorna !.
    public String comprovarTall(){
        return "1/";
    }

    //Pre: --
    //Post: retorna un string de llista de tots els productes que estàn seleccionats separats per una coma entre ells.
    //      En cas de que no hi hagui cap seleccionat retorna un string buit: "".
    public String productes(){
        String llistatProd="";
        for (int i = 0; i<mUserItems.size(); i++){
            llistatProd = llistatProd + llistaProductes.get(mUserItems.get(i)).getDescripcioProducte();
            if (i< mUserItems.size()-1) llistatProd += ",";
        }
        return llistatProd;
    }

    //Pre: --
    //Post: retorna un string de llista de tots els productes que estàn seleccionats separats per una coma entre ells.
    //      En cas de que no hi hagui cap seleccionat retorna un string buit: "".
    public Integer productesPreu(){
        Integer preuTotal = 0;
        for (int i = 0; i<mUserItems.size(); i++){
            preuTotal = preuTotal + llistaProductes.get(mUserItems.get(i)).getPreuProducte();
        }
        return preuTotal;
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
    //Post: s'encarrega del llistat de tipus de tall de cabells que pot fer-se el client
    public void llistatTallCabells() {
        botoCabells = findViewById(R.id.ButtonTallCabells);
        textTallCabells = (TextView) findViewById(R.id.tvTallCabells);

        obtenirDadesTallCabells();
    }

    //Pre: --
    //Post: s'encarrega del llistat de productes a triar que pot haver comprat el client
    public void llistatProductes() {
        botoProductes = findViewById(R.id.ButtonProductes);
        textProductes = (TextView) findViewById(R.id.tvItemSelected);

        obtenirDadesProductes();
    }

    public void obtenirDadesTallCabells() {
        Call<List<TallCabells>> call = mTodoService.getTallsCabells();
        call.enqueue(new Callback<List<TallCabells>>() {
            @Override
            public void onResponse(Call<List<TallCabells>> call, Response<List<TallCabells>> response) {
                if (response.isSuccessful()) {
                    for(TallCabells auxTallCabells : response.body()){
                        llistaTalls.add(auxTallCabells);
                    }
                    botoTallCabells();
                } else {
                    Toast toast = Toast.makeText(ActivitatClient.this, "Error obteniendo listado de productos", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<TallCabells>> call, Throwable t) {
                Toast toast = Toast.makeText(ActivitatClient.this, "Error 2 obteniendo listado de productos", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void botoTallCabells() {
        //TIENE QUE HACERSE
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
                Toast toast = Toast.makeText(ActivitatClient.this, "Error 2 obteniendo listado de productos", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    public void butonsBLA() {
        checkedItems = new boolean[llistaProductes.size()];

        botoProductes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatClient.this);
                mBuilder.setTitle("Productos comprados por el cliente");

                String[] arr = new String[llistaProductes.size()];
                for(int i=0 ; i< llistaProductes.size();i++){
                    arr[i] = llistaProductes.get(i).getDescripcioProducte();
                }


                mBuilder.setMultiChoiceItems(arr, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mUserItems.add(position);
                        }else{
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);

                //Botó acceptar amb els clickeables triats
                mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + llistaProductes.get(mUserItems.get(i)).getDescripcioProducte();
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        textProductes.setText(item);
                    }
                });

                //Botó seleccionar tots els productes directament
                mBuilder.setNegativeButton("Seleccionar todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        mUserItems.clear();
                        for (int i = 0; i < llistaProductes.size(); i++) {
                            checkedItems[i] = true;
                            mUserItems.add(i);
                            item = item + llistaProductes.get(i).getDescripcioProducte();
                            if (i != llistaProductes.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        textProductes.setText(item);
                    }
                });

                //Botó borrar tots els productes seleccionats
                mBuilder.setNeutralButton("Borrar todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mUserItems.clear();
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            textProductes.setText("Ningún producto seleccionado");
                        }
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
        c1.pentinatClient = Integer.valueOf(cadenaVariables[1]);
        c1.nomClient = cadenaVariables[2];
        c1.preuTotal = Integer.valueOf(cadenaVariables[4]);
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
                Toast toast = Toast.makeText(ActivitatClient.this, "Error 2: "+t.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    //Pre: id del Client actual ja guardat prèviament a la BD
    //Post: s'afegeixen els productes seleccionats prèviament, si és que n'hi ha, a la BD amb el client actual.
    public void guardarProductes(Long id) {
        List<Long> llistaIds = new ArrayList<>();
        for(int i=0; i<checkedItems.length; i++){   //Obtenim la llista dels id dels productes seleccionats
            if (checkedItems[i]) {
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
                    Toast toast = Toast.makeText(ActivitatClient.this, "Error 2 al añadir productos", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
