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
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.diegoestrada.bean.Empleado;
import org.diegoestrada.bean.Servicio;
import org.diegoestrada.bean.ServicioshasEmpleados;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class ServiciosHasEmpleadosController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ServicioshasEmpleados> listaServiciohaservicio;
    private ObservableList<Servicio> listaServicio;
    private ObservableList<Empleado> listaEmpleado;
    private DatePicker fecha;
    
    @FXML private TextField txtServicioscodigoservicios;
    @FXML private TextField txtLugarEvento;
    @FXML private ComboBox cmbCodigoServicio;
    @FXML private ComboBox cmbCodigoEmpleado;
    @FXML private JFXTimePicker jpHora;
    @FXML private GridPane grpFecha;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private TableView tblSerivcioshasEmpleados;
    @FXML private TableColumn colServicioscodigoservicios;
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colFechaEvento;
    @FXML private TableColumn colHoraEvento;
    @FXML private TableColumn colLugarEvento;
    @FXML private Label lblFecha;
    
    @Override
    public void initialize(URL location, ResourceBundle resource){
        cargarDatos();
        laFecha();
        cmbCodigoEmpleado.setItems(getEmpleado());
        cmbCodigoEmpleado.setDisable(true);
        cmbCodigoServicio.setItems(getServicio());
        cmbCodigoServicio.setDisable(true);
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getStylesheets().add("/org/diegoestrada/resource/TonysKinal.css");
        grpFecha.add(fecha, 3, 1);
        fecha.setDisable(true);
    }

    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        lblFecha.setText("Hoy es "+ahora.getDayOfMonth() + " de " + nombre + " de " + ahora.getYear());
    }
    
    public void cargarDatos(){
        tblSerivcioshasEmpleados.setItems(getServicioshasEmpleados());
        colServicioscodigoservicios.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("Servicios_codigoServicio"));
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoServicio"));
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoEmpleado"));
        colFechaEvento.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("fechaEvento"));
        colHoraEvento.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("horaEvento"));
        colLugarEvento.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("lugarEvento"));
    }
    
    public void seleccionarElemento(){
        if(tblSerivcioshasEmpleados.getSelectionModel().getSelectedItem() != null){
            txtServicioscodigoservicios.setText(String.valueOf(((ServicioshasEmpleados)tblSerivcioshasEmpleados.getSelectionModel().getSelectedItem()).getServicios_codigoServicio()));
            cmbCodigoServicio.getSelectionModel().select(buscarServicio(((ServicioshasEmpleados)tblSerivcioshasEmpleados.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            cmbCodigoEmpleado.getSelectionModel().select(buscarEmpleado(((ServicioshasEmpleados)tblSerivcioshasEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            fecha.selectedDateProperty().set(((ServicioshasEmpleados)tblSerivcioshasEmpleados.getSelectionModel().getSelectedItem()).getFechaEvento());
            jpHora.setValue(LocalTime.parse(((ServicioshasEmpleados)tblSerivcioshasEmpleados.getSelectionModel().getSelectedItem()).getHoraEvento()));
            txtLugarEvento.setText(((ServicioshasEmpleados)tblSerivcioshasEmpleados.getSelectionModel().getSelectedItem()).getLugarEvento());
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
        }
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
    
    public Empleado buscarEmpleado(int codigoEmpleado){
        Empleado result = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarEmpleado(?)");
            procedimiento.setInt(1, codigoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                result = new Empleado(registro.getInt("codigoEmpleado"),
                                    registro.getInt("numeroEmpleado"),
                                    registro.getString("apellidosEmpleado"),
                                    registro.getString("nombresEmpleado"),
                                    registro.getString("direccionEmpleado"),
                                    registro.getString("telefonoContacto"),
                                    registro.getString("gradoCocinero"),
                                    registro.getInt("codigoTipoEmpleado"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    public ObservableList<ServicioshasEmpleados> getServicioshasEmpleados(){
        ArrayList<ServicioshasEmpleados> lista = new ArrayList<ServicioshasEmpleados>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarServicios_has_Empleados()");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new ServicioshasEmpleados(result.getInt("Servicios_codigoServicio"),
                                                result.getInt("codigoServicio"),
                                                result.getInt("codigoEmpleado"),
                                                result.getDate("fechaEvento"),
                                                result.getString("horaEvento"),
                                                result.getString("lugarEvento")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaServiciohaservicio = FXCollections.observableArrayList(lista);
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
    
    public ObservableList<Empleado> getEmpleado(){
        ArrayList<Empleado> lista = new ArrayList<Empleado>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarEmpleados()");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new Empleado(result.getInt("codigoEmpleado"),
                                    result.getInt("numeroEmpleado"),
                                    result.getString("apellidosEmpleado"),
                                    result.getString("nombresEmpleado"),
                                    result.getString("direccionEmpleado"),
                                    result.getString("telefonoContacto"),
                                    result.getString("gradoCocinero"),
                                    result.getInt("codigoTipoEmpleado")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEmpleado = FXCollections.observableArrayList(lista);
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
        ServicioshasEmpleados registro = new ServicioshasEmpleados();
        if(txtServicioscodigoservicios.getText().isEmpty() || cmbCodigoEmpleado.getSelectionModel().isEmpty() || cmbCodigoServicio.getSelectionModel().isEmpty() || fecha.getSelectedDate() == null || jpHora.getValue() == null || txtLugarEvento.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos");
        }else{
                registro.setCodigoEmpleado(((Empleado)cmbCodigoEmpleado.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                registro.setCodigoServicio(((Servicio)cmbCodigoServicio.getSelectionModel().getSelectedItem()).getCodigoServicio());
                registro.setFechaEvento(fecha.getSelectedDate());
                registro.setHoraEvento(String.valueOf(jpHora.getValue()));
                registro.setLugarEvento(txtLugarEvento.getText());
            if(txtServicioscodigoservicios.getText().matches("-?([0-9]*)?")){  
                registro.setServicios_codigoServicio(Integer.parseInt(txtServicioscodigoservicios.getText()));
                try{
                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarServicios_has_Empleados(?,?,?,?,?,?)");
                    procedimiento.setInt(1, registro.getServicios_codigoServicio());
                    procedimiento.setInt(2, registro.getCodigoServicio());
                    procedimiento.setInt(3, registro.getCodigoEmpleado());
                    procedimiento.setDate(4, new java.sql.Date(registro.getFechaEvento().getTime()));
                    procedimiento.setString(5, registro.getHoraEvento());
                    procedimiento.setString(6, registro.getLugarEvento());
                    procedimiento.execute();
                    listaServiciohaservicio.add(registro);
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
        txtServicioscodigoservicios.clear();
        cmbCodigoServicio.setValue(null);
        cmbCodigoEmpleado.setValue(null);
        fecha.setSelectedDate(null);
        jpHora.setValue(null);
        txtLugarEvento.clear();
    }
    
    public void activarControles(){
        txtServicioscodigoservicios.setEditable(true);
        cmbCodigoServicio.setDisable(false);
        cmbCodigoEmpleado.setDisable(false);
        fecha.setDisable(false);
        jpHora.setDisable(false);
        txtLugarEvento.setEditable(true);
    }
    
    public void desactivarControles(){
        txtServicioscodigoservicios.setEditable(false);
        cmbCodigoServicio.setDisable(true);
        cmbCodigoEmpleado.setDisable(true);
        fecha.setDisable(true);
        jpHora.setDisable(true);
        txtLugarEvento.setEditable(false);
        
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
