package org.diegoestrada.bean;

public class TipoPlato {

    private int codigoTipoPlato;
    private String descripcionTipo;
    
    public TipoPlato(){
        
    }
    
    public TipoPlato(int codigoTipoPlato, String descripcionTipoPlato){
        this.codigoTipoPlato = codigoTipoPlato;
        this.descripcionTipo = descripcionTipoPlato;
    }

    public int getCodigoTipoPlato() {
        return codigoTipoPlato;
    }

    public void setCodigoTipoPlato(int codigoTipoPlato) {
        this.codigoTipoPlato = codigoTipoPlato;
    }

    public String getDescripcionTipo() {
        return descripcionTipo;
    }

    public void setDescripcionTipo(String descripcionTipo) {
        this.descripcionTipo = descripcionTipo;
    }
   
    @Override
    public String toString(){
        return codigoTipoPlato + " | " + descripcionTipo; 
    }
    
}
