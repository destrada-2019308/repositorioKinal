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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.diegoestrada.bean.Producto;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class ProductoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    private ObservableList<Producto> listaProducto; 
   
    
    @FXML private TextField txtCodigoProducto;
    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtCantidad;
    @FXML private TableView tblProductos;
    @FXML private TableColumn colCodigoProducto;
    @FXML private TableColumn colNombreProducto;
    @FXML private TableColumn colCantidad;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private Label lbFecha;
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        laFecha();
    }
    
    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        lbFecha.setText("Hoy es "+ahora.getDayOfMonth() + " de " + nombre + " de " + ahora.getYear());
    }
    
    public void cargarDatos(){
        tblProductos.setItems(getProducto());
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("codigoProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("cantidad"));
    }
  
    public ObservableList<Producto> getProducto(){
        ArrayList<Producto> lista = new ArrayList<Producto>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_ListarProductos");
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
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("/org/diegoestrada/image/salvar.png"));
                imgEliminar.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                guardar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/diegoestrada/image/agregar-archivo.png"));
                imgEliminar.setImage(new Image("/org/diegoestrada/image/boton-eliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void seleccionarElemento(){
        if(tblProductos.getSelectionModel().getSelectedItem() != null){
        txtCodigoProducto.setText(String.valueOf(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto()));
        txtNombreProducto.setText(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getNombreProducto());
        txtCantidad.setText(String.valueOf(((Producto)tblProductos.getSelectionModel().getSelectedItem()).getCantidad()));
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemnto");
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/diegoestrada/image/agregar-archivo.png"));
                imgEliminar.setImage(new Image("/org/diegoestrada/image/boton-eliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblProductos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?","Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_EliminarProducto(?)");
                            procedimiento.setInt(1,((Producto)tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto());
                            procedimiento.execute();
                            listaProducto.remove(tblProductos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                        }catch(SQLException e){
                            JOptionPane.showMessageDialog(null,"nop");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else if(respuesta == JOptionPane.NO_OPTION){
                        limpiarControles();
                        cargarDatos();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
        }
    }
    
    public void guardar(){
        Producto registro = new Producto();
        //registro.setCodigoProducto(Integer.parseInt(txtCodigoProducto.getText()));
        if(txtNombreProducto.getText().isEmpty() || txtCantidad.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Debe ingresar algo");
        }else{
            registro.setNombreProducto(txtNombreProducto.getText());
            registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarProducto (?,?)");
                procedimiento.setString(1, registro.getNombreProducto());
                procedimiento.setInt(2, registro.getCantidad());
                procedimiento.execute();
                listaProducto.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblProductos.getSelectionModel().getSelectedItem() != null){  
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/diegoestrada/image/salvar.png"));
                    imgReporte.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                imgEditar.setImage(new Image("/org/diegoestrada/image/editar.png"));
                imgReporte.setImage(new Image("/org/diegoestrada/image/reporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void actualizar(){
            if(txtNombreProducto.getText().isEmpty()|| txtCantidad.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Dejo un dato sin ingresar. \n Porfavor llenar todos los campos");
            }else{
                try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarProducto(?,?,?)");
                Producto registro = (Producto)tblProductos.getSelectionModel().getSelectedItem();
                registro.setNombreProducto(txtNombreProducto.getText());
                registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
                procedimiento.setInt(1, registro.getCodigoProducto());
                procedimiento.setString(2, registro.getNombreProducto());
                procedimiento.setInt(3, registro.getCantidad());
                procedimiento.execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                imgEditar.setImage(new Image("/org/diegoestrada/image/editar.png"));
                imgReporte.setImage(new Image("/org/diegoestrada/image/reporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void desactivarControles(){
        txtCodigoProducto.setEditable(false);
        txtNombreProducto.setEditable(false);
        txtCantidad.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoProducto.setEditable(false);
        txtNombreProducto.setEditable(true);
        txtCantidad.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoProducto.setText("");
        txtNombreProducto.clear();
        txtCantidad.setText("");
    }
    
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal; 
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}
