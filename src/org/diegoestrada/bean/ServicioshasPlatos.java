package org.diegoestrada.bean;

public class ServicioshasPlatos {
    private int Servicios_codigoServicio;
    private int codigoPlato;
    private int codigoServicio;

    public ServicioshasPlatos() {
    }

    public ServicioshasPlatos(int Servicios_codigoServicio, int codigoPlato, int codigoServicio) {
        this.Servicios_codigoServicio = Servicios_codigoServicio;
        this.codigoPlato = codigoPlato;
        this.codigoServicio = codigoServicio;
    }

    public int getServicios_codigoServicio() {
        return Servicios_codigoServicio;
    }

    public void setServicios_codigoServicio(int Servicios_codigoServicio) {
        this.Servicios_codigoServicio = Servicios_codigoServicio;
    }

    public int getCodigoPlato() {
        return codigoPlato;
    }

    public void setCodigoPlato(int codigoPlato) {
        this.codigoPlato = codigoPlato;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }
    
}
