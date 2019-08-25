package org.udg.pds.todoandroid.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Calendari extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_calendari);


        //Creació de la barra de navgeació dels 4 menús
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_calendari);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        CalendarView calendari = findViewById(R.id.calendarView);
        calendari.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent intentCanvi = new Intent(Calendari.this, CalendariDia.class);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                int[] dataAMostrar = new int[]{month, dayOfMonth, dayOfWeek, year};
                intentCanvi.putExtra("dadesData",dataAMostrar);
                startActivity(intentCanvi);

                /*Fragment fCalendari = new clientsDia();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();*/
            }
        });

        TextView dataActual = findViewById(R.id.textData);
        dataActual.setText(dataActual());
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
                case R.id.navegacio_calendari:
                    Intent intent2 = new Intent(Calendari.this, CalendariDia.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Calendari.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_alertes:
                    Intent intent4 = new Intent(Calendari.this, Alertes.class);
                    startActivity(intent4);
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

}





