package org.udg.pds.todoandroid.entity;

public class TallCabells {

    public Long id;
    public Integer preuTall;
    public String descripcioTall;

    public TallCabells(Integer preuP, String descrT){
        this.preuTall = preuP;
        this.descripcioTall = descrT;
    }

    public Long getId(){
        return id;
    }

    public Integer getPreuTall(){
        return preuTall;
    }

    public String getDescripcioTall(){ return descripcioTall; }


}
