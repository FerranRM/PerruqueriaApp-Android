package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Client;
import org.udg.pds.todoandroid.entity.Perruquer;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estad_PastisTotalDiners extends AppCompatActivity {

    TodoApi mTodoService;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Estad_PastisTotalDiners.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Estad_PastisTotalDiners.this, ActivitatReserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Estad_PastisTotalDiners.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Estad_PastisTotalDiners.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Estad_PastisTotalDiners.this, Ajustos.class);
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

        mTodoService = ((TodoApp)this.getApplication()).getAPI();   //Ens connectem a la BD

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navegacio_estadistiques);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ferGrafic();
    }


    //Pre: --
    //Post: fa la crida dels perruquers que hi ha a la base de dades i mostra el total que ha fet cada un el darrer any
    public void ferGrafic(){

        Call<List<Perruquer>> callPerruquers = mTodoService.listAllPerruquers();
        callPerruquers.enqueue(new Callback<List<Perruquer>>() {
            @Override
            public void onResponse(Call<List<Perruquer>> call, Response<List<Perruquer>> response) {
                if (response.isSuccessful()) {
                    tractarDades(response.body());
                } else {
                    Toast.makeText(Estad_PastisTotalDiners.this, "Error cargando lista peluqueros", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Perruquer>> call, Throwable t) {
                Toast.makeText(Estad_PastisTotalDiners.this, "Fallo cargando lista peluqueros", Toast.LENGTH_LONG).show();
            }
        });

    }



    //Pre: llistat perruquers no és buit
    //Post: agafa el llistat de perruquers i calcula el total de diners fets per cada un.
    //      Després mostra el gràfic de pastís amb els resultats.
    private void tractarDades(List<Perruquer> perruquers) {

        List<String> llistatNomPerruquers = new ArrayList<>();  //Llistat on guardarem tots els noms dels perruquers
        List<Integer> llistatTotalVendes = new ArrayList<>();   //Llistat on guardarem tots els totals de diners fets per cada perruquer

        TextView titol = findViewById(R.id.titolCalendariDia);

        for(Perruquer auxPerruquer : perruquers){
            Calendar data2 = GregorianCalendar.getInstance();

            int anyInicial = data2.get(Calendar.YEAR)-1;
            int mesInicial = data2.get(Calendar.MONTH)+1;

            if (mesInicial>11) {    //Si estem al Desembre només haurem d'agafar dades del Gener al Desembre del mateix any
                anyInicial++;
                mesInicial = 00;
            }

            Date dataInicial = new GregorianCalendar(anyInicial, mesInicial, 01).getTime();
            Date dataFinal = data2.getTime();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");    //Creem el format amb el que volem consultar les dates al servidor
            String sData1 = format.format(dataInicial);
            String sData2 = format.format(dataFinal);

            //Afegim el títol de l'estadística
            titol.setText("Total dinero de cada peluquero\n01/"+(mesInicial+1)+"/"+anyInicial +" - "+ data2.get(Calendar.DAY_OF_MONTH)+"/"+(data2.get(Calendar.MONTH)+1)+"/"+data2.get(Calendar.YEAR));

            Call<List<Client>> call = mTodoService.listAllClients(sData1,sData2, auxPerruquer.getId());
            call.enqueue(new Callback<List<Client>>() {
                @Override
                public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                    if (response.isSuccessful()) {
                        llistatNomPerruquers.add(auxPerruquer.getNomUsuari());

                        int numTotalVendes = 0;
                        for(Client auxClient : response.body()){
                            numTotalVendes += auxClient.getPreuTotal();
                        }
                        llistatTotalVendes.add(numTotalVendes);



                        dibuixarGrafic(llistatNomPerruquers, llistatTotalVendes);
                    } else {
                        Toast.makeText(Estad_PastisTotalDiners.this, "Error cargando lista clientes", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Client>> call, Throwable t) {
                    Toast.makeText(Estad_PastisTotalDiners.this, "Fallo cargando lista clientes", Toast.LENGTH_LONG).show();
                }
            });
        }


    }



    //Pre: ambdues llistes no són buides
    //Post: mostra el gràfic pastís del total de diners fets per cada perruquer.
    public void dibuixarGrafic(List<String> llistatNomPerruquers, List<Integer> llistatTotalVendes) {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);

        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        for(int i = 0; i<llistatNomPerruquers.size(); i++){
            data.add(new ValueDataEntry(llistatNomPerruquers.get(i),llistatTotalVendes.get(i)));
        }

        pie.data(data);

        pie.title("Total dinero último año");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Peluquero")
                .padding(0d, 0d, 10d, 0d);

        pie.tooltip()
                .format("{%Value} € ");

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        pie.credits().text("David Tellez");
        pie.credits().logoSrc("https://image.flaticon.com/icons/png/512/2303/2303279.png");     //Icona dels crèdits del gràfic
        pie.background().fill("#FFFFFF");

        anyChartView.setChart(pie);
        anyChartView.setLicenceKey("ferryjack2@gmail.com-49753d4b-a9936015");
    }
}

