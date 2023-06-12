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
import org.diegoestrada.bean.Servicio;
import org.diegoestrada.bean.ServicioshasPlatos;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class ServiciosHasPlatosController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ServicioshasPlatos> listaServicioshasPlatos;
    private ObservableList<Plato> listaPlato;
    private ObservableList<Servicio> listaServicio;
    
    @FXML private TextField txtServicioscodigoServicio; 
    @FXML private ComboBox cmbCodigoPlato;
    @FXML private ComboBox cmbCodigoServicio;
    @FXML private TableView tblServicioscodigoServicio;
    @FXML private TableColumn colServicioscodigoServicio;
    @FXML private TableColumn colCodigoPlato;
    @FXML private TableColumn colCodigoServicio;
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
        cmbCodigoServicio.setItems(getServicio());
        cmbCodigoServicio.setDisable(true);
    }
    
    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        lblFecha.setText("Hoy es "+ahora.getDayOfMonth() + " de " + nombre + " de " + ahora.getYear());
    }
    
    public void cargarDatos(){
        tblServicioscodigoServicio.setItems(getServicioshasPlatos());
        colServicioscodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("Servicios_codigoServicio"));
        colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoPlato"));
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoServicio"));
    }
    
        public void seleccionarElemento(){
        if(tblServicioscodigoServicio.getSelectionModel().getSelectedItem() != null){
            txtServicioscodigoServicio.setText(String.valueOf(((ServicioshasPlatos)tblServicioscodigoServicio.getSelectionModel().getSelectedItem()).getServicios_codigoServicio()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlato(((ServicioshasPlatos)tblServicioscodigoServicio.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            cmbCodigoServicio.getSelectionModel().select((buscarServicio(((ServicioshasPlatos)tblServicioscodigoServicio.getSelectionModel().getSelectedItem()).getCodigoServicio())));
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
    
    public Servicio buscarServicio(int codigoServicio){
        Servicio result = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarServicio(?)");
            procedimiento.setInt(1, codigoServicio);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                result = new Servicio(registro.getInt("codigoServicio"),
                                    registro.getDate("fechaServicio"),
                                    registro.getString("tipoServicio"),
                                    registro.getString("horaServicio"),
                                    registro.getString("lugarServicio"),
                                    registro.getString("telefonoContacto"),
                                    registro.getInt("codigoEmpresa"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    public ObservableList<ServicioshasPlatos> getServicioshasPlatos(){
        ArrayList<ServicioshasPlatos> lista = new ArrayList<ServicioshasPlatos>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarServicios_has_Platos()");
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                lista.add(new ServicioshasPlatos(registro.getInt("Servicios_codigoServicio"),
                                            registro.getInt("codigoPlato"),
                                            registro.getInt("codigoServicio")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaServicioshasPlatos = FXCollections.observableArrayList(lista);
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
    
    public ObservableList<Servicio> getServicio(){
        ArrayList<Servicio> lista = new ArrayList<Servicio>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarServicios()");
            ResultSet result = procedimiento.executeQuery();
            while (result.next()) {
                lista.add(new Servicio(result.getInt("codigoServicio"),
                                    result.getDate("fechaServicio"),
                                    result.getString("tipoServicio"),
                                    result.getString("horaServicio"),
                                    result.getString("lugarServicio"),
                                    result.getString("telefonoContacto"),
                                    result.getInt("codigoEmpresa")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
            return listaServicio = FXCollections.observableArrayList(lista);
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
        ServicioshasPlatos registro = new ServicioshasPlatos();
        if(txtServicioscodigoServicio.getText().isEmpty() || cmbCodigoPlato.getSelectionModel().isEmpty() || cmbCodigoServicio.getSelectionModel().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos");
        }else{
                registro.setCodigoPlato(((Plato)cmbCodigoPlato.getSelectionModel().getSelectedItem()).getCodigoPlato());
                registro.setCodigoServicio(((Servicio)cmbCodigoServicio.getSelectionModel().getSelectedItem()).getCodigoServicio());
            if(txtServicioscodigoServicio.getText().matches("-?([0-9]*)?")){  
                registro.setServicios_codigoServicio(Integer.parseInt(txtServicioscodigoServicio.getText()));
                try{
                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarServicios_has_Platos(?,?,?)");
                    procedimiento.setInt(1, registro.getServicios_codigoServicio());
                    procedimiento.setInt(2, registro.getCodigoPlato());
                    procedimiento.setInt(3, registro.getCodigoServicio());
                    procedimiento.execute();
                    listaServicioshasPlatos.add(registro);
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
        txtServicioscodigoServicio.clear();
        cmbCodigoPlato.setValue(null);
        cmbCodigoServicio.setValue(null);
    }
    
    public void desactivarControles(){
        txtServicioscodigoServicio.setEditable(false);
        cmbCodigoPlato.setDisable(true);
        cmbCodigoServicio.setDisable(true);
    }
    
    public void activarControles(){
        txtServicioscodigoServicio.setEditable(true);
        cmbCodigoPlato.setDisable(false);
        cmbCodigoServicio.setDisable(false);    
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
