package org.diegoestrada.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.diegoestrada.bean.Plato;
import org.diegoestrada.bean.Producto;
import org.diegoestrada.bean.ProductohasPlatos;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class ProductoHasPlatosController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, NINGUNO};
    private  operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ProductohasPlatos> listaProductohasPlatos;
    private ObservableList<Plato> listaPlato;
    private ObservableList<Producto> listaProducto;
    
    @FXML private TextField txtProductosCodigoProducto;
    @FXML private ComboBox cmbCodigoPlato;
    @FXML private ComboBox cmbCodigoProducto;
    @FXML private TableView tblProductoshasProductos;
    @FXML private TableColumn colProductoshasProductos;
    @FXML private TableColumn colCodigoPlato;
    @FXML private TableColumn colCodigoProducto;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private Label lblFecha;
    
    @Override
    public void initialize(URL location, ResourceBundle resource){
        cargarDatos();
        laFecha();
        cmbCodigoPlato.setItems(getPlato());
        cmbCodigoPlato.setDisable(true);
        cmbCodigoProducto.setItems(getProducto());
        cmbCodigoProducto.setDisable(true);
    }
    
    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        lblFecha.setText("Hoy es "+ahora.getDayOfMonth() + " de " + nombre + " de " + ahora.getYear());
    }
    
    public void cargarDatos(){
       tblProductoshasProductos.setItems(getProductohasPlatos());
       colProductoshasProductos.setCellValueFactory(new PropertyValueFactory<Producto,Integer>("Productos_codigoProducto"));
       colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Producto,Integer>("codigoPlato"));
       colCodigoProducto.setCellValueFactory(new PropertyValueFactory<Producto,Integer>("codigoProducto"));
    }
    
    public void seleccionarElemento(){
        if(tblProductoshasProductos.getSelectionModel().getSelectedItem() != null){
            txtProductosCodigoProducto.setText(String.valueOf(((ProductohasPlatos)tblProductoshasProductos.getSelectionModel().getSelectedItem()).getProductos_codigoProducto()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlato(((ProductohasPlatos)tblProductoshasProductos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            cmbCodigoProducto.getSelectionModel().select(buscarProducto(((ProductohasPlatos)tblProductoshasProductos.getSelectionModel().getSelectedItem()).getCodigoProducto()));
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
        }
    }

    public Plato buscarPlato(int codigoPlato){
        Plato result = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarPlato(?)");
            procedimiento.setInt(1, codigoPlato);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                result = new Plato(registro.getInt("codigoPlato"),
                                registro.getInt("cantidad"),
                                registro.getString("nombrePlato"),
                                registro.getString("descripcionPlato"),
                                registro.getDouble("precioPlato"),
                                registro.getInt("codigoTipoPlato"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    public Producto buscarProducto(int codigoProducto){
        Producto result = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarProducto(?)");
            procedimiento.setInt(1, codigoProducto);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                result = new Producto(registro.getInt("codigoProducto"),
                                registro.getString("nombreProducto"),
                                registro.getInt("cantidad"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    public ObservableList<ProductohasPlatos> getProductohasPlatos(){
        ArrayList<ProductohasPlatos> lista = new ArrayList<ProductohasPlatos>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarProductos_has_Platos");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new ProductohasPlatos(result.getInt("Productos_codigoProducto"),
                                            result.getInt("codigoPlato"),
                                            result.getInt("codigoProducto")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaProductohasPlatos = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Plato> getPlato(){
        ArrayList<Plato> lista = new ArrayList<Plato>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarPlatos()");
            ResultSet result = procedimiento.executeQuery();
                while(result.next()){
                    lista.add(new Plato(result.getInt("codigoPlato"),
                                    result.getInt("cantidad"),
                                    result.getString("nombrePlato"),
                                    result.getString("descripcionPlato"),
                                    result.getDouble("precioPlato"),
                                    result.getInt("codigoTipoPlato")));
                }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPlato = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Producto> getProducto(){
        ArrayList<Producto> lista = new ArrayList<Producto>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_ListarProductos()");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new Producto(result.getInt("codigoProducto"),
                            result.getString("nombreProducto"),
                            result.getInt("cantidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaProducto = FXCollections.observableList(lista);
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                imgNuevo.setImage(new Image("/org/diegoestrada/image/salvar.png"));
                imgEliminar.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                guardar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Cancelar");
                imgNuevo.setImage(new Image("/org/diegoestrada/image/agregar-archivo.png"));
                imgEliminar.setImage(new Image("/org/diegoestrada/image/boton-eliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Cancelar");
                imgNuevo.setImage(new Image("/org/diegoestrada/image/agregar-archivo.png"));
                imgEliminar.setImage(new Image("/org/diegoestrada/image/boton-eliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void guardar(){
        ProductohasPlatos registro = new ProductohasPlatos();
        if(txtProductosCodigoProducto.getText().isEmpty() || cmbCodigoPlato.getSelectionModel().isEmpty() || cmbCodigoProducto.getSelectionModel().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos");
        }else{
                registro.setCodigoPlato(((Plato)cmbCodigoPlato.getSelectionModel().getSelectedItem()).getCodigoPlato());
                registro.setCodigoProducto(((Producto)cmbCodigoProducto.getSelectionModel().getSelectedItem()).getCodigoProducto());
            if(txtProductosCodigoProducto.getText().matches("-?([0-9]*)?")){
                registro.setProductos_codigoProducto(Integer.parseInt(txtProductosCodigoProducto.getText()));
                try{
                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarProductos_has_Platos(?,?,?)");
                    procedimiento.setInt(1, registro.getProductos_codigoProducto());
                    procedimiento.setInt(2, registro.getCodigoPlato());
                    procedimiento.setInt(3, registro.getCodigoProducto());
                    procedimiento.execute();
                    listaProductohasPlatos.add(registro);
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "El código se repite, porfavor ponga uno no existente");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                    JOptionPane.showMessageDialog(null,"Código Incorrecto", "ERROR", JOptionPane.WARNING_MESSAGE);
            }
        }    
    }
    
    public void limpiarControles(){
        txtProductosCodigoProducto.clear();
        cmbCodigoPlato.setValue(null);
        cmbCodigoProducto.setValue(null);
    }
    
    public void activarControles(){
        txtProductosCodigoProducto.setEditable(true);
        cmbCodigoPlato.setDisable(false);
        cmbCodigoProducto.setDisable(false);
    }
    
    public void desactivarControles(){
        txtProductosCodigoProducto.setEditable(false);
        cmbCodigoPlato.setDisable(true);
        cmbCodigoProducto.setDisable(true);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
}
