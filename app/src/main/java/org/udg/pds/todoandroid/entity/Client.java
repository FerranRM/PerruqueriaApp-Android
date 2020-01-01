package org.udg.pds.todoandroid.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Client {

    public Long id;
    public String nomClient;
    public Integer preuTotal;
    public Boolean sexeClient;  // 0/False = Dona, 1/True = Home
    public Date dataClient;


    public Client(){}

    public Client(Date dataC, Boolean sexeC, String nomC, Integer preuTotal){
        this.nomClient = nomC;
        this.preuTotal = preuTotal;
        this.sexeClient = sexeC;
        this.dataClient = dataC;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public Date getDataClient(){ return dataClient; }




}
