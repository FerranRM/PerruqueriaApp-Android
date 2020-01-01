package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.ParseException;
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

public class Estad_BarresClients extends AppCompatActivity {

    TodoApi mTodoService;

    public ArrayList<Client> llistaClients2;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
                case R.id.navegacio_afegirClient:
                    Intent intent1 = new Intent(Estad_BarresClients.this, ActivitatClient.class);
                    startActivity(intent1);
                    break;
                case R.id.navegacio_reserves:
                    Intent intent2 = new Intent(Estad_BarresClients.this, ActivitatReserva.class);
                    startActivity(intent2);
                    break;
                case R.id.navegacio_estadistiques:
                    Intent intent3 = new Intent(Estad_BarresClients.this, Estadistiques.class);
                    startActivity(intent3);
                    break;
                case R.id.navegacio_calendari:
                    Intent intent4 = new Intent(Estad_BarresClients.this, Calendari.class);
                    startActivity(intent4);
                    break;
                case R.id.navegacio_ajustos:
                    Intent intent5 = new Intent(Estad_BarresClients.this, Ajustos.class);
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

        TextView titol = findViewById(R.id.titolCalendariDia);
        titol.setText("Total clientes último año"); //Afegim el títol de l'estadística


        ferGrafic();	//Omplim la llista amb les dades corresponents
    }


    //Pre: --
    //Post: fa la crida als clients fets durant el darrer any i l'anterior i calcula el total per a cada mes a partir d'aquests.
    //      Després mostra el resultat per pantalla en forma de gràfic.
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
                    tractarDades(response.body(), data2.get(Calendar.YEAR)-1);
                } else {
                    Toast.makeText(Estad_BarresClients.this, "Error cargando datos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(Estad_BarresClients.this, "Fallo cargando datos", Toast.LENGTH_LONG).show();
            }
        });
    }


    //Pre: clients no és buit
    //Post: agafa la col·lecció de clients i els ajunta per mesos comptant el total de clients fets en cada un d'aquests mesos.
    //      Després crea el gràfic a partir d'aquesta llista de dades.
    public void tractarDades(Collection<Client> clients, int anyAnterior) {
        List<DataEntry> data = new ArrayList<>();
        Integer total = 0;
        String mes = null;
        String mesInicial = null;
        for(Client auxClient : clients){
            if (mes==null){ //El primer client (inicialitzem les dues variables)
                mes = obtenirNomMes(auxClient.getDataClient());
                mesInicial = mes;
                total ++;
            }
            else if (!obtenirNomMes(auxClient.getDataClient()).equals(mes)){    //Si el mes del client auxClient és diferent al de anterior
                data.add(new ValueDataEntry(mes+" "+anyAnterior, total));
                mes = obtenirNomMes(auxClient.getDataClient());
                total = 1;
                if (mes.equals(mesInicial)) anyAnterior++;
            }
            else total ++;

        }
        data.add(new ValueDataEntry(mes+" "+anyAnterior, total));   //Afegim el darrer mes calculat

        dibuixarGrafic(data);
    }

    //Pre: data no és buida
    //Post: mostra el gràfic de barres del total de clients fets el darrer any per cada mes a partir de la llista de dades d'entrada.
    public void dibuixarGrafic(List<DataEntry> data) {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.column();

        Column column = cartesian.column(data);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Total clientes mensuales");

        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Mes");
        cartesian.yAxis(0).title("Total dinero");
        cartesian.credits().text("David Tellez");
        cartesian.credits().logoSrc("https://image.flaticon.com/icons/png/512/2303/2303279.png");   //Icona dels crèdits del gràfic
        cartesian.background().fill("#FFFFFF");

        anyChartView.setChart(cartesian);
        anyChartView.setLicenceKey("ferryjack2@gmail.com-49753d4b-a9936015");
    }


    //Pre: data és una data correcta
    //Post: retorna el mes en castellà de la data entrada per paràmetres
    public String obtenirNomMes(Date data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String mes  = (String) DateFormat.format("MM",  data);

        if (mes.equals("01")) return "enero";
        else if (mes.equals("02")) return "febrero";
        else if (mes.equals("03")) return "marzo";
        else if (mes.equals("04")) return "abril";
        else if (mes.equals("05")) return "mayo";
        else if (mes.equals("06")) return "junio";
        else if (mes.equals("07")) return "julio";
        else if (mes.equals("08")) return "agosto";
        else if (mes.equals("09")) return "septiembre";
        else if (mes.equals("10")) return "octubre";
        else if (mes.equals("11")) return "noviembre";
        else return "diciembre";
    }
}

