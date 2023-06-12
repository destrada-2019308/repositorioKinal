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
import org.diegoestrada.bean.TipoPlato;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class TipoPlatoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    private ObservableList<TipoPlato> listaTipoPlato;
    
    @FXML private TextField txtCodigoTipoPlato;
    @FXML private TextField txtDescripcionTipoPlato;
    @FXML private TableView tblTipoPlatos;
    @FXML private TableColumn colCodigoTipoPlato;
    @FXML private TableColumn colDescripcionTipoPlato;
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
    public void initialize(URL location, ResourceBundle resources){
        cargarDatos();
        laFecha();
    }
    
    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        lbFecha.setText("Hoy es "+ahora.getDayOfMonth() + " de " + nombre + " de " + ahora.getYear());
    }
    
    public void cargarDatos(){
        tblTipoPlatos.setItems(getTipoPlato());
        colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<TipoPlato, Integer>("codigoTipoPlato"));
        colDescripcionTipoPlato.setCellValueFactory(new PropertyValueFactory<TipoPlato, Integer>("descripcionTipo"));
    }
    
    public ObservableList<TipoPlato> getTipoPlato(){
        ArrayList<TipoPlato> lista = new ArrayList<TipoPlato>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_ListarTipoPlatos");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new TipoPlato(result.getInt("codigoTipoPlato"),
                            result.getString("descripcionTipo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTipoPlato = FXCollections.observableList(lista);
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
        if(tblTipoPlatos.getSelectionModel().getSelectedItem() != null){
        txtCodigoTipoPlato.setText(String.valueOf(((TipoPlato)tblTipoPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato()));
        txtDescripcionTipoPlato.setText(((TipoPlato)tblTipoPlatos.getSelectionModel().getSelectedItem()).getDescripcionTipo());
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento de la tabla");
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
                if(tblTipoPlatos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Esta seguro de eliminar el registro?", "Eliminar Tipo Plato", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);     
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_EliminarTipoPlato(?)");   
                            procedimiento.setInt(1,((TipoPlato)tblTipoPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
                            procedimiento.execute();
                            listaTipoPlato.remove(tblTipoPlatos.getSelectionModel().getSelectedIndex());
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
                JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            }
    }
}      

    public void guardar(){
        TipoPlato registro = new TipoPlato();
        //registro.setCodigoTipoPlato(Integer.parseInt(txtCodigoTipoPlato.getText()));
        if(txtDescripcionTipoPlato.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos");
        }else{
            registro.setDescripcionTipo(txtDescripcionTipoPlato.getText());
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_AgregarTipoPlato(?)");
                procedimiento.setString(1, registro.getDescripcionTipo());
                procedimiento.execute();
                listaTipoPlato.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTipoPlatos.getSelectionModel().getSelectedItem() != null){  
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
        if(txtDescripcionTipoPlato.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos"); 
        }else{
            try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarTipoPlato(?,?)");
            TipoPlato registro = (TipoPlato)tblTipoPlatos.getSelectionModel().getSelectedItem();
            registro.setDescripcionTipo(txtDescripcionTipoPlato.getText());
            procedimiento.setInt(1, registro.getCodigoTipoPlato());
            procedimiento.setString(2, registro.getDescripcionTipo());
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
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcionTipoPlato.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcionTipoPlato.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoTipoPlato.clear();
        txtDescripcionTipoPlato.setText("");
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
    
    public void ventanaPlato(){
        escenarioPrincipal.ventanaPlato();
    }
    
}
