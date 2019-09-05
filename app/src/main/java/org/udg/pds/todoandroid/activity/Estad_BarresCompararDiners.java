package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Client;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estad_BarresCompararDiners extends AppCompatActivity {

    TodoApi mTodoService;
    public ArrayList<Client> llistaClients2;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Estad_BarresCompararDiners.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Estad_BarresCompararDiners.this, Reserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Estad_BarresCompararDiners.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Estad_BarresCompararDiners.this, Ajustos.class);
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
        titol.setText("COMPARAR TOTAL DINERO AÑO PASADO Y ACTUAL");

        ferGrafic();	//Omplim la llista amb les dades corresponents
    }


    //Pre: Les dues dates d'entrada són correctes
    //Post: fa la crida als clients que es troben entre les dues dates entrades per paràmetre i mostra els gràfics a partir d'aquests
    public void ferGrafic(){
        llistaClients2 = new ArrayList<>();

        Calendar data2 = GregorianCalendar.getInstance();
        Date data1 = new GregorianCalendar(data2.get(Calendar.YEAR)-1, data2.get(Calendar.MONTH), 01).getTime();
        Date data2F = data2.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");    //Creem el format amb el que volem consultar les dates al servidor
        String sData1 = format.format(data1);
        String sData2 = format.format(data2F);

        Call<List<Client>> call = mTodoService.listAllClients(sData1,sData2);
        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful()) {
                    tractarDades(response.body());
                } else {
                    Toast.makeText(Estad_BarresCompararDiners.this, "Error cargando datos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(Estad_BarresCompararDiners.this, "Error 2 cargando datos", Toast.LENGTH_LONG).show();
            }
        });
    }


    //Pre: clients no és buid
    //Post: agafa la col·lecció de clients i els ajunta per mesos comptant el total de diners fets en cada un d'aquests mesos.
    //      Després crea el gràfic a partir d'aquesta llista de dades.
    public void tractarDades(Collection<Client> clients) {
        //HAY QUE ASEGURARSE QUE HAY DATOS DE LOS ULTIMOS 12 MESES EN LOS DOS!!!
        //QUE PASA SI UN MES ESTA EN UN AÑO Y EN OTRO NO?

        List<DataEntry> dataPasado = new ArrayList<>();
        List<DataEntry> dataActual = new ArrayList<>();
        Integer total = 0;
        String mes = null;
        String mesInicial = null;
        Boolean nouAny = false;
        for(Client auxClient : clients){
            if (mes==null){
                mes = obtenirNomMes(auxClient.getDataClient());
                mesInicial = mes;
                total += auxClient.getPreuTotal();
            }
            else if (!obtenirNomMes(auxClient.getDataClient()).equals(mes)){    //Si el mes del client auxClient és diferent al de anterior
                if (nouAny)  //Fem estadístiques dels últims 13 mesos
                    dataActual.add(new ValueDataEntry(mes, total));
                else
                    dataPasado.add(new ValueDataEntry(mes, total));
                mes = obtenirNomMes(auxClient.getDataClient());
                total = auxClient.getPreuTotal();
                if (mes.equals(mesInicial)) nouAny = true;
            }
            else total += auxClient.getPreuTotal();
        }

        dataActual.add(new ValueDataEntry(mes, total));   //Afegim el darrer mes calculat

        dibuixarGrafic(dataPasado,dataActual);
    }

    //Pre: data no és buida
    //Post: mostra el gràfic de barres a partir de la llista data d'entrada.
    public void dibuixarGrafic(List<DataEntry> anyPassat, List<DataEntry> anyActual) {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.column();

        Column column1 = cartesian.column(anyPassat);
        column1.name("Año pasado")
                .color("HotPink");
        column1.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value} €");

        Column column2 = cartesian.column(anyActual);
        column2.name("Año actual");
        column2.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value} €");


        cartesian.animation(true);
        cartesian.barsPadding(0.5);

        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("€{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.SINGLE);  // SINGLE = Cada valro de la columna per separat quan fem click a sobre

        cartesian.xAxis(0).title("Mes");
        cartesian.yAxis(0).title("Total dinero");

        anyChartView.setChart(cartesian);
    }

    public String obtenirNomMes(Date data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String mes  = (String) DateFormat.format("MM",  data);

        if (mes.equals("01")) return "ENERO";
        else if (mes.equals("02")) return "FEBRERO";
        else if (mes.equals("03")) return "MARZO";
        else if (mes.equals("04")) return "ABRIL";
        else if (mes.equals("05")) return "MAYO";
        else if (mes.equals("06")) return "JUNIO";
        else if (mes.equals("07")) return "JULIO";
        else if (mes.equals("08")) return "AGOSTO";
        else if (mes.equals("09")) return "SETIEMBRE";
        else if (mes.equals("10")) return "OCTUBRE";
        else if (mes.equals("11")) return "NOVIEMBRE";
        else return "DICIEMBRE";
    }
}

