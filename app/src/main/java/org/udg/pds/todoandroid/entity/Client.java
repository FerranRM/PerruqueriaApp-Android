package org.udg.pds.todoandroid.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Client {

    public String nomClient;
    public Integer preuTotal;
    public Boolean sexeClient;  // 0/False = Dona, 1/True = Home
    public Integer pentinatClient;
    public Date dataClient;


    public Client(){}

    public Client(Date dataC, Integer pentinatC, Boolean sexeC, String nomC, Integer preuTotal){
        this.nomClient = nomC;
        this.preuTotal = preuTotal;
        this.sexeClient = sexeC;
        this.pentinatClient = pentinatC;
        this.dataClient = dataC;
    }

    public String getNomClient(){
        return nomClient;
    }

    public void canviarNomClient(String nom){
        this.nomClient = nom;
    }

    public Integer getPreuTotal(){
        return preuTotal;
    }

    public void setPreuTotal(Integer preuTotal){
        this.preuTotal = preuTotal;
    }

    public Boolean getSexeClient(){ return sexeClient; }

    public Integer getPentinatClient(){ return pentinatClient; }

    public Date getDataClient(){ return dataClient; }




}
