package org.diegoestrada.controller;

import com.jfoenix.controls.JFXTimePicker;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.diegoestrada.bean.Empresa;
import org.diegoestrada.bean.Servicio;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class ServicioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, ACTUALIZAR, GUARDAR, ELIMINAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Servicio> listaServicio;
    private ObservableList<Empresa> listaEmpresa;
    private DatePicker fecha;
    
    /*
    ------------------------------------------------------------------------------------------------------
        Todos los objetos provenienetes de JavaFX
    ------------------------------------------------------------------------------------------------------
    */
    
    @FXML private TextField txtCodigoServicio;
    @FXML private TextField txtTipoServicio;
    @FXML private JFXTimePicker jpTime;
    @FXML private TextField txtLugarServicio;
    @FXML private TextField txtTelefonoContacto;
    @FXML private ComboBox cmbCodigoEmpresa;
    @FXML private TableView tblServicios;
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colFechaServicio;
    @FXML private TableColumn colTipoServicio;
    @FXML private TableColumn colHoraServicio;
    @FXML private TableColumn colLugarServicio;
    @FXML private TableColumn colTelefonoContacto;
    @FXML private TableColumn colCodigoEmpresa;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private Label lblFecha;
    @FXML private GridPane grpFecha;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        cargarDatos();
        laFecha();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getStylesheets().add("/org/diegoestrada/resource/TonysKinal.css");
        grpFecha.add(fecha, 3, 0);
        fecha.setDisable(true);
        cmbCodigoEmpresa.setItems(getEmpresa());
        cmbCodigoEmpresa.setDisable(true);
    }
     
    public void laFecha(){
        LocalDate now = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        lblFecha.setText("Hoy es "+now.getDayOfMonth() + " de " + nombre + " de "+now.getYear());
    }
    
    public void cargarDatos(){
        tblServicios.setItems(getServicio());
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("codigoServicio"));
        colFechaServicio.setCellValueFactory(new PropertyValueFactory<Empresa, Date>("fechaServicio"));
        colTipoServicio.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("tipoServicio"));
        colHoraServicio.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("horaServicio"));
        colLugarServicio.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("lugarServicio"));
        colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("telefonoContacto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("codigoEmpresa"));
    }
    
    public void seleccionarElemento(){
        if(tblServicios.getSelectionModel().getSelectedItem() != null){
            txtCodigoServicio.setText(String.valueOf(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            fecha.selectedDateProperty().set(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getFechaServicio());
            txtTipoServicio.setText(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getTipoServicio());
            jpTime.setValue(LocalTime.parse(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getHoraServicio()));
            txtLugarServicio.setText(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getLugarServicio());
            txtTelefonoContacto.setText(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getTelefonoContacto());
            cmbCodigoEmpresa.getSelectionModel().select(buscarEmpresa(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
        }
    }
    
    public Empresa buscarEmpresa(int codigoEmpresa){
        Empresa result = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarEmpresa(?)");
            procedimiento.setInt(1, codigoEmpresa);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                result = new Empresa(registro.getInt("codigoEmpresa"),
                                    registro.getString("nombreEmpresa"),
                                    registro.getString("direccion"),
                                    registro.getString("telefono"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
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
    
    public ObservableList<Empresa> getEmpresa(){
        ArrayList<Empresa> lista = new ArrayList<Empresa>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_ListarEmpresas");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new Empresa(result.getInt("codigoEmpresa"),
                            result.getString("nombreEmpresa"),
                            result.getString("direccion"),
                            result.getString("telefono")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEmpresa = FXCollections.observableList(lista);
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos para los botones: Nuevo, eliminar, editar y reporte
    ------------------------------------------------------------------------------------------------------
    */
    
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
                if(tblServicios.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Servicio", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarServicio(?)");
                            procedimiento.setInt(1, ((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio());
                            procedimiento.execute();
                            listaServicio.remove(tblServicios.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblServicios.getSelectionModel().clearSelection();
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
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblServicios.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/diegoestrada/image/salvar.png"));
                    imgReporte.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento ");
                }
                break;
            case ACTUALIZAR:
                    guardar();
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
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                    limpiarControles();;
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
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos guardar y actualizar
    ------------------------------------------------------------------------------------------------------
    */
    
    public void guardar(){
        Servicio registro = new Servicio();
        if(txtCodigoServicio == null || fecha.getSelectedDate() == null || txtTipoServicio.getText().isEmpty() || jpTime.getValue() == null || txtLugarServicio.getText().isEmpty() || txtTelefonoContacto.getText().isEmpty() || cmbCodigoEmpresa.getSelectionModel().isEmpty()){
            JOptionPane.showMessageDialog(null,"Dejó un dato vacío. \n Porfavor llene todos los campos");
        }else{
            registro.setFechaServicio(fecha.getSelectedDate());
            registro.setTipoServicio(txtTipoServicio.getText());
            registro.setHoraServicio(String.valueOf(jpTime.getValue()));
            registro.setLugarServicio(txtLugarServicio.getText());
            registro.setTelefonoContacto(txtTelefonoContacto.getText());
            registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarServicio(?,?,?,?,?,?)");
                procedimiento.setDate(1, new java.sql.Date(registro.getFechaServicio().getTime()));
                procedimiento.setString(2, registro.getTipoServicio());
                procedimiento.setString(3, registro.getHoraServicio());
                procedimiento.setString(4, registro.getLugarServicio());
                procedimiento.setString(5, registro.getTelefonoContacto());
                procedimiento.setInt(6, registro.getCodigoEmpresa());
                procedimiento.execute();
                listaServicio.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void actualizar(){
        if(txtCodigoServicio == null || fecha.getSelectedDate() == null || txtTipoServicio.getText().isEmpty() || jpTime.getValue() == null || txtLugarServicio.getText().isEmpty() || txtTelefonoContacto.getText().isEmpty() || cmbCodigoEmpresa.getSelectionModel().isEmpty()){
            JOptionPane.showMessageDialog(null,"Dejó un dato vacío. \n Porfavor llene todos los campos");
        }else{
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarServicio(?,?,?,?,?,?)");
                Servicio registro = (Servicio)tblServicios.getSelectionModel().getSelectedItem();
                registro.setFechaServicio(fecha.getSelectedDate());
                registro.setTipoServicio(txtTipoServicio.getText());
                registro.setHoraServicio(String.valueOf(jpTime.getValue()));
                registro.setLugarServicio(txtLugarServicio.getText());
                registro.setTelefonoContacto(txtTelefonoContacto.getText());
                procedimiento.setInt(1, registro.getCodigoServicio());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaServicio().getTime()));
                procedimiento.setString(3, registro.getTipoServicio());
                procedimiento.setString(4, registro.getHoraServicio());
                procedimiento.setString(5, registro.getLugarServicio());
                procedimiento.setString(6, registro.getTelefonoContacto());
                procedimiento.execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos para acciones de controles
    ------------------------------------------------------------------------------------------------------
    */
    
    public void limpiarControles(){
        txtCodigoServicio.clear();
        fecha.setSelectedDate(null);
        txtTipoServicio.clear();
        jpTime.setValue(null);
        txtLugarServicio.clear();
        txtTelefonoContacto.clear();
        cmbCodigoEmpresa.setValue(null);
    }
    
    public void desactivarControles(){
        txtCodigoServicio.setEditable(false);
        fecha.setDisable(true);
        txtTipoServicio.setEditable(false);
        jpTime.setEditable(false);
        txtLugarServicio.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        cmbCodigoEmpresa.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoServicio.setEditable(false);
        fecha.setDisable(false);
        txtTipoServicio.setEditable(true);
        jpTime.setEditable(true);
        txtLugarServicio.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        cmbCodigoEmpresa.setDisable(false);
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos para levantar la vista
    ------------------------------------------------------------------------------------------------------
    */
    
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal;
        
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaEmpresa(){
        escenarioPrincipal.ventanaEmpresa();
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
}
