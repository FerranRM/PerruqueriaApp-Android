package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.entity.Reserva;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Calendari extends AppCompatActivity {

    TodoApi mTodoService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_calendari);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();   //Ens connectem a la BD


        //Creació de la barra de navgeació dels 4 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_calendari);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        CalendarView calendari = findViewById(R.id.calendarView);
        calendari.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent intentCanvi = new Intent(Calendari.this, ActivitatReserva.class);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                int[] dataAMostrar = new int[]{month, dayOfMonth, dayOfWeek, year};
                intentCanvi.putExtra("dadesData",dataAMostrar);
                startActivity(intentCanvi);
            }
        });

        TextView dataActual = findViewById(R.id.textData);
        dataActual.setText(dataActual());

        totalReservesActuals();
    }




    //Crear barra menús
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Calendari.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Calendari.this, Reserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Calendari.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Calendari.this, Ajustos.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        }
    };



    private String dataActual(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date currentTime = Calendar.getInstance().getTime();
        String diaActual  = (String) DateFormat.format("dd",  currentTime);
        String mesActual  = (String) DateFormat.format("MM",  currentTime);
        if (mesActual.compareTo("01")==0) mesActual = "ENERO";
        else if (mesActual.compareTo("02")==0) mesActual = "FEBRERO";
        else if (mesActual.compareTo("03")==0) mesActual = "MARZO";
        else if (mesActual.compareTo("04")==0) mesActual = "ABRIL";
        else if (mesActual.compareTo("05")==0) mesActual = "MAYO";
        else if (mesActual.compareTo("06")==0) mesActual = "JUNIO";
        else if (mesActual.compareTo("07")==0) mesActual = "JULIO";
        else if (mesActual.compareTo("08")==0) mesActual = "AGOSTO";
        else if (mesActual.compareTo("09")==0) mesActual = "SETIEMBRE";
        else if (mesActual.compareTo("10")==0) mesActual = "OCTUBRE";
        else if (mesActual.compareTo("11")==0) mesActual = "NOVIEMBRE";
        else mesActual = "DICIEMBRE";

        return (diaActual+" de "+mesActual);
    }


    //Pre: --
    //Post: mostra per pantalla el total de reserves que té el perruquer per al dia actual
    public void totalReservesActuals() {

        Calendar data2 = GregorianCalendar.getInstance();
        Date data1 = new GregorianCalendar(data2.get(Calendar.YEAR), data2.get(Calendar.MONTH), data2.get(Calendar.DAY_OF_MONTH)).getTime();
        Date data2F = new GregorianCalendar(data2.get(Calendar.YEAR), data2.get(Calendar.MONTH), data2.get(Calendar.DAY_OF_MONTH)+1).getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");    //Creem el format amb el que volem consultar les dates al servidor
        String sData1 = format.format(data1);
        String sData2 = format.format(data2F);

        Call<List<Reserva>> call = mTodoService.listAllReserves(sData1,sData2);
        call.enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {
                if (response.isSuccessful()) {

                    TextView totalReserves = findViewById(R.id.totalReserves);
                    totalReserves.setText("Total reservas para hoy: " + response.body().size());

                } else {
                    Toast toast = Toast.makeText(Calendari.this, "Error obteniendo listado de reservas", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast toast = Toast.makeText(Calendari.this, "Error 2 obteniendo listado de reservas", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

}





