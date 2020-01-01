package org.udg.pds.todoandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Perruquer;
import org.udg.pds.todoandroid.entity.Producte;
import org.udg.pds.todoandroid.entity.ServeiPrestat;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
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
                    Intent intent2 = new Intent(Ajustos.this, ActivitatReserva.class);
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

        mTodoService = ((TodoApp)this.getApplication()).getAPI();   //Ens connectem a la BD


        //Creació de la barra de navegació dels 5 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_ajustos);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        assginarNomPerruquer();

    }


    //Pre: --
    //Post: assignem el nom de l'usuari per mostrar-ho per pantalla
    public void assginarNomPerruquer() {
        TextView nomPerruquer = findViewById(R.id.nomPerruquer);

        Call<Perruquer> call = mTodoService.getIdPerruquer();
        call.enqueue(new Callback<Perruquer>() {
            @Override
            public void onResponse(Call<Perruquer> call, Response<Perruquer> response) {
                if (response.isSuccessful()) {
                    nomPerruquer.setText(response.body().getNomUsuari());
                } else {
                    Toast toast = Toast.makeText(Ajustos.this, "Error accediendo al ID de usuario "+response.raw(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<Perruquer> call, Throwable t) {
                Toast toast = Toast.makeText(Ajustos.this, "Error accediendo al ID de usuario ", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    //Pre: --
    //Post: modifica la imatge de perfil aleatoriament entre unes imatges ja predefinides
    public void modificarImatge(View view) {
        ImageView modImatge = findViewById(R.id.imatgePerfil);
        int n = new Random().nextInt(imatges.length-1);
        modImatge.setImageResource(imatges[n]);
    }


    //Pre: --
    //Post: afegeix un nou producte, després d'introduir el preu i descripció que li volem donar.
    public void afegirProducte(View view) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Ajustos.this);

        LayoutInflater inflater = Ajustos.this.getLayoutInflater();
        View infoClient = inflater.inflate(R.layout.dades_producte, null);

        EditText preuProducte = infoClient.findViewById(R.id.afegir_producte_preu);
        EditText descProducte = infoClient.findViewById(R.id.afegir_producte_nom);


        mBuilder.setView(infoClient)
                .setPositiveButton("AÑADIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Producte nouProducte = new Producte(Integer.parseInt(preuProducte.getText().toString()), descProducte.getText().toString());

                        Call<IdObject> call = mTodoService.addProducte(nouProducte);
                        call.enqueue(new Callback<IdObject>() {
                            @Override
                            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                                if (response.isSuccessful()) {
                                    nouProducte.setId(response.body().id);

                                    Toast toast = Toast.makeText(Ajustos.this, "Producto añadido!", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(Ajustos.this, "Error al añadir el producto", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<IdObject> call, Throwable t) {
                                Toast toast = Toast.makeText(Ajustos.this, "Fallo al añadir el producto", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                })

                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancel·lar no fa res
                    }
                });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }



    //Pre: hi ha productes a eliminar
    //Post: ens mostra el llistat de productes que tenim actualment i, després d'escollir un o varis, els elimina de la base de dades.
    public void eliminarProducte(View view) {
        List<Producte> llistaProductes = new ArrayList<>();

        Call<List<Producte>> call = mTodoService.getProductes();
        call.enqueue(new Callback<List<Producte>>() {
            @Override
            public void onResponse(Call<List<Producte>> call, Response<List<Producte>> response) {
                if (response.isSuccessful()) {
                    for(Producte auxProducte : response.body()){
                        llistaProductes.add(auxProducte);
                    }
                    llistarProductesEliminar(llistaProductes);
                } else {
                    Toast toast = Toast.makeText(Ajustos.this, "Error obteniendo listado de productos", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Producte>> call, Throwable t) {
                Toast toast = Toast.makeText(Ajustos.this, "Fallo obteniendo listado de productos", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    //Pre: --
    //Post: afegeix un nou servei, després d'introduir el preu i nom que li volem donar.
    public void afegirServei(View view) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Ajustos.this);

        LayoutInflater inflater = Ajustos.this.getLayoutInflater();
        View infoClient = inflater.inflate(R.layout.dades_producte, null);

        EditText preuServei = infoClient.findViewById(R.id.afegir_producte_preu);
        EditText descServei = infoClient.findViewById(R.id.afegir_producte_nom);


        mBuilder.setView(infoClient)
                .setPositiveButton("AÑADIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ServeiPrestat nouServei = new ServeiPrestat(Integer.parseInt(preuServei.getText().toString()), descServei.getText().toString());

                        Call<IdObject> call = mTodoService.addServeiPrestat(nouServei);
                        call.enqueue(new Callback<IdObject>() {
                            @Override
                            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                                if (response.isSuccessful()) {
                                    nouServei.setId(response.body().id);

                                    Toast toast = Toast.makeText(Ajustos.this, "Servicio añadido!", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(Ajustos.this, "Error al añadir el servicio", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<IdObject> call, Throwable t) {
                                Toast toast = Toast.makeText(Ajustos.this, "Fallo al añadir el servicio", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                })

                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancel·lar no fa res
                    }
                });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    //Pre: ha de tenir la sessió oberta
    //Post: es tanca la sessió actual i es redirigeix a la pantalla de login.
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


    //Pre: --
    //Post: redirigeix a l'Instagram de la perruqueria
    public void instagramPerruqueria(View view) {
        Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ferranfrm/?hl=es"));
        startActivity(instagramIntent);
    }


    //Pre: llistaProductes no és buid
    //Post: es mostra un llistat de tots els productes actuals. Cada producte es pot checkquejar/seleccionar per després eliminar-lo.
    public void llistarProductesEliminar(List<Producte> llistaProductes) {
        ArrayList<Integer> mUserItems = new ArrayList<>();
        boolean[] checkedItems = new boolean[llistaProductes.size()];

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Ajustos.this);
        mBuilder.setTitle("Que productos deseas eliminar?");

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


        //Botó per eliminar tots els clickeables triats
        mBuilder.setPositiveButton("Eliminar seleccionados", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                final Boolean[] eliminatOk = {true};
                for (int i = 0; i < mUserItems.size(); i++) {

                    int finalI = i;
                    Call<ResponseBody> call = mTodoService.deleteProducte(llistaProductes.get(mUserItems.get(i)).getId().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                llistaProductes.remove(finalI);
                            } else {
                                eliminatOk[0] = false;
                                Toast toast = Toast.makeText(Ajustos.this, "Error al eliminar el producto", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            eliminatOk[0] =false;
                            Toast toast = Toast.makeText(Ajustos.this, "Fallo: "+t.getMessage(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });


                }

                if (eliminatOk[0]) {
                    Toast toast = Toast.makeText(Ajustos.this, "Productos eliminados correctamente!", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        //Tornem a la pantalla d'ajustos sense eliminar cap producte
        mBuilder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                mUserItems.clear();
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }


    //Pre: hi ha serveis a eliminar
    //Post: ens mostra el llistat de serveis que tenim actualment i, després d'escollir un o varis, els elimina de la base de dades.
    public void eliminarServei(View view) {
        List<ServeiPrestat> llistaServeis = new ArrayList<>();

        Call<List<ServeiPrestat>> call = mTodoService.getServeisPrestats();
        call.enqueue(new Callback<List<ServeiPrestat>>() {
            @Override
            public void onResponse(Call<List<ServeiPrestat>> call, Response<List<ServeiPrestat>> response) {
                if (response.isSuccessful()) {
                    for(ServeiPrestat auxServei : response.body()){
                        llistaServeis.add(auxServei);
                    }
                    llistarServeisEliminar(llistaServeis);
                } else {
                    Toast toast = Toast.makeText(Ajustos.this, "Error obteniendo listado de servicios prestados", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<ServeiPrestat>> call, Throwable t) {
                Toast toast = Toast.makeText(Ajustos.this, "Fallo obteniendo listado de servicios prestados", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    //Pre: llistaServeis no és buida.
    //Post: es mostra un llistat de tots els serveis actuals. Cada servei es pot checkquejar/seleccionar per després eliminar-lo.
    public void llistarServeisEliminar(List<ServeiPrestat> llistaServeis) {
        ArrayList<Integer> mUserItems = new ArrayList<>();
        boolean[] checkedItems = new boolean[llistaServeis.size()];

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Ajustos.this);
        mBuilder.setTitle("Que servicios deseas eliminar?");

        String[] arr = new String[llistaServeis.size()];
        for(int i=0 ; i< llistaServeis.size();i++){
            arr[i] = llistaServeis.get(i).getDescripcioServei();
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


        //Botó per eliminar tots els clickeables triats
        mBuilder.setPositiveButton("Eliminar seleccionados", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                final Boolean[] eliminatOk = {true};
                for (int i = 0; i < mUserItems.size(); i++) {

                    int finalI = i;
                    Call<ResponseBody> call = mTodoService.deleteServeiPrestat(llistaServeis.get(mUserItems.get(i)).getId().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                llistaServeis.remove(finalI);
                            } else {
                                eliminatOk[0] = false;
                                Toast toast = Toast.makeText(Ajustos.this, "Error al eliminar el servicio prestado", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            eliminatOk[0] =false;
                            Toast toast = Toast.makeText(Ajustos.this, "Fallo: "+t.getMessage(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });


                }

                if (eliminatOk[0]) {
                    Toast toast = Toast.makeText(Ajustos.this, "Servicio prestados eliminados correctamente!", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        //Tornem a la pantalla d'ajustos sense eliminar cap servei prestat
        mBuilder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                mUserItems.clear();
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}
