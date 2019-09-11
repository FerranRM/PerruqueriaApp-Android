package org.udg.pds.todoandroid.entity;

public class Producte {

    public Long id;
    public Integer preuProducte;
    public String descripcioProducte;

    public Producte(Integer preuP, String descrP){
        this.preuProducte = preuP;
        this.descripcioProducte = descrP;
    }

    public Long getId(){
        return id;
    }

    public Integer getPreuProducte(){
        return preuProducte;
    }

    public String getDescripcioProducte(){ return descripcioProducte; }


}
