package org.udg.pds.todoandroid.entity;

public class ServeiPrestat {

    public Long id;
    public Integer preuServei;
    public String descripcioServei;

    public ServeiPrestat(Integer preuServei, String descripcioS){
        this.preuServei = preuServei;
        this.descripcioServei = descripcioS;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Integer getPreuServei(){
        return preuServei;
    }

    public String getDescripcioServei(){ return descripcioServei; }


}
