package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Producte;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estad_TotalVendes extends AppCompatActivity {

    TodoApi mTodoService;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Estad_TotalVendes.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Estad_TotalVendes.this, ActivitatReserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Estad_TotalVendes.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Estad_TotalVendes.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Estad_TotalVendes.this, Ajustos.class);
                    startActivity(intent5);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_estad_total_vendes);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_estadistiques);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        TextView titol = findViewById(R.id.titolCalendariDia);
        titol.setText("Total productos vendidos");

        ferGrafic();
    }


    //Pre: --
    //Post: fa la crida als clients que es troben entre les dues dates entrades per paràmetre i mostra els gràfics a partir d'aquests
    public void ferGrafic(){
        Call<List<Producte>> call = mTodoService.getProductes();

        call.enqueue(new Callback<List<Producte>>() {
            @Override
            public void onResponse(Call<List<Producte>> call, Response<List<Producte>> response) {
                if (response.isSuccessful()) {
                    tractarDades(response.body());
                } else {
                    Toast.makeText(Estad_TotalVendes.this, "Error cargando datos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producte>> call, Throwable t) {
                Toast.makeText(Estad_TotalVendes.this, "Fallo cargando datos", Toast.LENGTH_LONG).show();
            }
        });
    }


    //Pre: clients no és buid
    //Post: agafa la col·lecció de clients i els ajunta per mesos comptant el total de diners fets en cada un d'aquests mesos.
    //      Després crea el gràfic a partir d'aquesta llista de dades.
    public void tractarDades(Collection<Producte> productes) {
        List<String> nomProductes = new ArrayList<>();
        List<Integer> totalProductes = new ArrayList<>();

        for(Producte auxProducte : productes){
            if (nomProductes.contains(auxProducte.getDescripcioProducte())){
                Integer valorAnterior = totalProductes.indexOf(auxProducte.getDescripcioProducte()) + 1;
                totalProductes.set(totalProductes.indexOf(auxProducte.getDescripcioProducte()), valorAnterior);
            }
            else {
                nomProductes.add(auxProducte.getDescripcioProducte());
                totalProductes.add(auxProducte.getPreuProducte());
            }

        }
        dibuixarGrafic(nomProductes, totalProductes);
    }

    //Pre: ambdues llistes no són buides
    //Post: mostra el gràfic pastís del total de cada producte venut i també el tant per cent corresponent entre tots els productes venuts.
    public void dibuixarGrafic(List<String> nomProductes, List<Integer> totalProductes) {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);

        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(Estad_TotalVendes.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> data = new ArrayList<>();
        for(int i = 0; i<nomProductes.size(); i++){
            data.add(new ValueDataEntry(nomProductes.get(i),totalProductes.get(i)));
        }

        pie.data(data);

        pie.title("Total dinero último año");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("PRODUCTOS")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        pie.credits("D.Tellez Barber");

        pie.credits().text("David Tellez");
        pie.credits().logoSrc("https://image.flaticon.com/icons/png/512/2303/2303279.png");
        pie.background().fill("#FFFFFF");

        anyChartView.setChart(pie);
        anyChartView.setLicenceKey("ferryjack2@gmail.com-49753d4b-a9936015");
    }
}

