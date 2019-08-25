package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.jetbrains.annotations.Nullable;
import org.udg.pds.todoandroid.R;

public class Alertes extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitat_alertes);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setSelectedItemId(R.id.navegacio_alertes);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navegacio_afegirClient:
                        Intent intent1 = new Intent(Alertes.this, ActivitatClient.class);
                        startActivity(intent1);
                        break;
                    case R.id.navegacio_calendari:
                        Intent intent2 = new Intent(Alertes.this, CalendariDia.class);
                        startActivity(intent2);
                        break;
                    case R.id.navegacio_estadistiques:
                        Intent intent3 = new Intent(Alertes.this, Estadistiques.class);
                        startActivity(intent3);
                        break;
                    case R.id.navegacio_alertes:
                        break;
                    case R.id.navegacio_ajustos:
                        Intent intent5 = new Intent(Alertes.this, Ajustos.class);
                        startActivity(intent5);
                        break;
                }
                return false;
            }
        });
    }

}

