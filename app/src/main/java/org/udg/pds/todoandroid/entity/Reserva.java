package org.udg.pds.todoandroid.entity;

import java.util.Date;

public class Reserva {

    public Long id;
    public String nomReserva;
    public Date dataReserva;


    public Reserva(){}

    public Reserva(Date dataReserva, String nomReserva){
        this.nomReserva = nomReserva;
        this.dataReserva = dataReserva;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNomReserva(){
        return nomReserva;
    }

    public void canviarNomReserva(String nom){
        this.nomReserva = nom;
    }

    public Date getDataReserva(){ return dataReserva; }

    public void setDataReserva(Date data){ this.dataReserva = data; }



}
