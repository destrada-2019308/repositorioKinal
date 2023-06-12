package org.diegoestrada.bean;

public class ProductohasPlatos {
    private int Productos_codigoProducto;
    private int codigoPlato;
    private int codigoProducto;

    public ProductohasPlatos() {
    }

    public ProductohasPlatos(int Productos_codigoProducto, int codigoPlato, int codigoProducto) {
        this.Productos_codigoProducto = Productos_codigoProducto;
        this.codigoPlato = codigoPlato;
        this.codigoProducto = codigoProducto;
    }

    public int getProductos_codigoProducto() {
        return Productos_codigoProducto;
    }

    public void setProductos_codigoProducto(int Productos_codigoProducto) {
        this.Productos_codigoProducto = Productos_codigoProducto;
    }

    public int getCodigoPlato() {
        return codigoPlato;
    }

    public void setCodigoPlato(int codigoPlato) {
        this.codigoPlato = codigoPlato;
    }

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }
    
}
