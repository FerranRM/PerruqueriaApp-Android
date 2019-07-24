package org.udg.pds.todoandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;

import java.util.ArrayList;
import java.util.List;

public class ActivitatClient extends AppCompatActivity {
    private TextView mTextMessage;

    private ImageButton mOrder;
    private TextView mItemSelected;
    private String[] listItems;
    private Integer[] llistaPreusProductes = {3,3,3,3,3,3,3};
    private boolean[] checkedItems;
    private ArrayList<Integer> mUserItems = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    break;
                case R.id.navegacio_calendari:
                    Intent intent2 = new Intent(ActivitatClient.this, CalendariDia.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(ActivitatClient.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_alertes:
                    Intent intent4 = new Intent(ActivitatClient.this, Alertes.class);
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

        mTextMessage = findViewById(R.id.message);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_afegirClient);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //Métode encarregat del llistat de productes a comprar per el client
        llistatProductes();


        //Declaració del botó per afegir el client
        ImageButton bGuardar = findViewById(R.id.imgButtonGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String clientFet = comprovarSexe();
                if (clientFet=="!") mostrarAlerta("Tipo de sexo no seleccionado");
                else {
                    String aux = comprovarTall();
                    if (aux == "!") mostrarAlerta("Tipo de corte no seleccionado");
                    else{
                        clientFet += aux;

                        EditText nomClient = (EditText) findViewById(R.id.nomClient);
                        aux = nomClient.getText().toString();
                        if (aux.isEmpty()) mostrarAlerta("Nombre cliente no introducido");
                        else {
                            clientFet += aux + "/";
                            Integer auxPreus = productesPreu();
                            aux = productes();
                            clientFet += aux+"/"+auxPreus.toString();

                            TextView preuTotal = findViewById(R.id.textTotal);
                            preuTotal.setText("Total: "+auxPreus);

                            // Toast missatge = Toast.makeText(getApplicationContext(),"CLIENTE GUARDADO",Toast.LENGTH_SHORT);
                            Toast missatge = Toast.makeText(getApplicationContext(), clientFet, Toast.LENGTH_SHORT);
                            missatge.setGravity(Gravity.CENTER, 0, 0);
                            missatge.show();
                        }
                    }
                }
            }
        });
    }

    //Pre: --
    //Post: Retorna un string amb el número identificatiu del tipus de sexe seleccionat, si no hi ha cap seleccionat retorna !.
    String comprovarSexe(){
        RadioButton rBHome = (RadioButton) findViewById(R.id.radioButtonH);
        RadioButton rBDona = (RadioButton) findViewById(R.id.radioButtonM);
        if (rBHome.isChecked()) return "Home/";
        else if (rBDona.isChecked()) return "Dona/";
        else return "!";
    }

    //Pre: --
    //Post: Retorna un string amb el número identificatiu del tipus de tall seleccionat, si no hi ha cap seleccionat retorna !.
    String comprovarTall(){
        RadioButton rBTall1 = (RadioButton) findViewById(R.id.rBCorte1);
        RadioButton rBTall2 = (RadioButton) findViewById(R.id.rBCorte2);
        RadioButton rBTall3 = (RadioButton) findViewById(R.id.rBCorte3);
        if (rBTall1.isChecked()) return "1/";
        else if (rBTall2.isChecked()) return "2/";
        else if (rBTall3.isChecked()) return "3/";
        else return "!";
    }

    //Pre: --
    //Post: retorna un string de llista de tots els productes que estàn seleccionats separats per una coma entre ells.
    //      En cas de que no hi hagui cap seleccionat retorna un string buit: "".
    String productes(){
        String llistaProductes="";
        for (int i = 0; i<mUserItems.size(); i++){
            llistaProductes = llistaProductes + listItems[mUserItems.get(i)];
            if (i< mUserItems.size()-1) llistaProductes += ",";
        }
        return llistaProductes;
    }

    //Pre: --
    //Post: retorna un string de llista de tots els productes que estàn seleccionats separats per una coma entre ells.
    //      En cas de que no hi hagui cap seleccionat retorna un string buit: "".
    Integer productesPreu(){
        Integer preuTotal = 0;
        for (int i = 0; i<mUserItems.size(); i++){
            preuTotal = preuTotal + llistaPreusProductes[i];
        }
        return preuTotal;
    }

    //Pre: nomAlerta ha de ser un missatge d'alerta identificatiu de l'error.
    //Post: mostra per pantalla l'alerta amb el titol: ERROR i el missatge entrat per paràmetre.
    void mostrarAlerta(String nomAlerta){
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
    //Post: s'encarrega del llistat de productes a triar que pot haver comprat el client
    void llistatProductes() {
        mOrder = (ImageButton) findViewById(R.id.ButtonProductes);
        mItemSelected = (TextView) findViewById(R.id.tvItemSelected);

        listItems = getResources().getStringArray(R.array.productes_comprar);
        checkedItems = new boolean[listItems.length];

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ActivitatClient.this);
                mBuilder.setTitle("Productos comprados por el cliente");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                            item = item + listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        mItemSelected.setText(item);
                    }
                });

                //Botó seleccionar tots els productes directament
                mBuilder.setNegativeButton("Seleccionar todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        mUserItems.clear();
                        for (int i = 0; i < listItems.length; i++) {
                            checkedItems[i] = true;
                            mUserItems.add(i);
                            item = item + listItems[i];
                            if (i != listItems.length - 1) {
                                item = item + ", ";
                            }
                        }
                        mItemSelected.setText(item);
                    }
                });

                //Botó borrar tots els productes seleccionats
                mBuilder.setNeutralButton("Borrar todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mUserItems.clear();
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mItemSelected.setText("Ningún producto seleccionado");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

}
