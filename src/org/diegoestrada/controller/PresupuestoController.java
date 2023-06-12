package org.diegoestrada.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import org.diegoestrada.bean.Presupuesto;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;
import org.diegoestrada.report.GenerarReporte;


public class PresupuestoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Presupuesto> listaPresupuesto;
    private ObservableList<Empresa> listaEmpresa;
    private DatePicker fecha;
    
    /*
    ------------------------------------------------------------------------------------------------------
        Todos los objetos provenienetes de JavaFX
    ------------------------------------------------------------------------------------------------------
    */
    
    @FXML private TextField txtCodigoPresupuesto;
    @FXML private TextField txtCantidadPresupuesto;
    @FXML private GridPane grpFecha;
    @FXML private ComboBox cmbCodigoEmpresa;
    @FXML private TableView tblPresupuestos;
    @FXML private TableColumn colCodigoPresupuesto;
    @FXML private TableColumn colFechaPresupuesto;
    @FXML private TableColumn colCantidadPresupuesto;
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
   
    
    @Override
    public void initialize(URL location, ResourceBundle resource){
        cargarDatos();
        laFecha();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getStylesheets().add("/org/diegoestrada/resource/TonysKinal.css");
        grpFecha.add(fecha, 3, 0);
        cmbCodigoEmpresa.setItems(getEmpresa());
        cmbCodigoEmpresa.setDisable(true);
        fecha.setDisable(true); 
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Método para poner la fecha en un label
    ------------------------------------------------------------------------------------------------------
    */
    
    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        lblFecha.setText("Hoy es "+ahora.getDayOfMonth() + " de " + nombre + " de "+ahora.getYear() );
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Métodos para usae el tableView
    ------------------------------------------------------------------------------------------------------
    */
    
    public void cargarDatos(){
        tblPresupuestos.setItems(getPresupuesto());
        colCodigoPresupuesto.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("codigoPresupuesto"));
        colFechaPresupuesto.setCellValueFactory(new PropertyValueFactory<Empresa, Date>("fechaSolicitud"));
        colCantidadPresupuesto.setCellValueFactory(new PropertyValueFactory<Empresa, Double>("CantidadPresupuesto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("codigoEmpresa"));
    }
    
    public void seleccionarElemento(){
        if(tblPresupuestos.getSelectionModel().getSelectedItem() != null){
        txtCodigoPresupuesto.setText(String.valueOf(((Presupuesto)tblPresupuestos.getSelectionModel().getSelectedItem()).getCodigoPresupuesto()));
        fecha.selectedDateProperty().set(((Presupuesto)tblPresupuestos.getSelectionModel().getSelectedItem()).getFechaSolicitud());
        txtCantidadPresupuesto.setText(String.valueOf(((Presupuesto)tblPresupuestos.getSelectionModel().getSelectedItem()).getCantidadPresupuesto()));
        cmbCodigoEmpresa.getSelectionModel().select(buscarEmpresa(((Presupuesto)tblPresupuestos.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
        }
    }
    
    //Método para usar el cobobox
    
    public Empresa buscarEmpresa(int codigoEmpresa){
        Empresa result = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarEmpresa(?)");
            procedimiento.setInt(1, codigoEmpresa);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
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
    
    /*
    ------------------------------------------------------------------------------------------------------
        Método para listar registros, el de presupuesto y empresa
    ------------------------------------------------------------------------------------------------------
    */
    
    public ObservableList<Presupuesto> getPresupuesto(){
        ArrayList<Presupuesto> lista = new ArrayList<Presupuesto>(); 
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarPresupuestos()");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new Presupuesto(result.getInt("codigoPresupuesto"),
                                        result.getDate("fechaSolicitud"),
                                        result.getDouble("cantidadPresupuesto"),
                                        result.getInt("codigoEmpresa")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPresupuesto = FXCollections.observableArrayList(lista);
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
                if(tblPresupuestos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Presupuesto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_EliminarPresupuesto(?)");
                            procedimiento.setInt(1, ((Presupuesto)tblPresupuestos.getSelectionModel().getSelectedItem()).getCodigoPresupuesto());
                            procedimiento.execute();
                            listaPresupuesto.remove(tblPresupuestos.getSelectionModel().getSelectedIndex());
                            limpiarControles(); 
                            tblPresupuestos.getSelectionModel().clearSelection();
                        }catch(SQLException e){
                            JOptionPane.showMessageDialog(null, "nop");
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
                    if(tblPresupuestos.getSelectionModel().getSelectedItem() != null){
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
            case NINGUNO:
                imprimirReporte();
                break;
        }
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos guardar y actualizar
    ------------------------------------------------------------------------------------------------------
    */
    
    public void guardar(){
        Presupuesto registro = new Presupuesto();
        if(txtCantidadPresupuesto.getText().isEmpty() || fecha.getSelectedDate() == null || cmbCodigoEmpresa.getSelectionModel().isEmpty() || txtCodigoPresupuesto == null){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos");
        }else{
            registro.setFechaSolicitud(fecha.getSelectedDate());
            registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
            if(txtCantidadPresupuesto.getText().matches("-?([0-9]*)?")){
            registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));            
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_AgregarPresupuesto(?,?,?)");
                procedimiento.setDate(1, new java.sql.Date(registro.getFechaSolicitud().getTime()));
                procedimiento.setDouble(2, registro.getCantidadPresupuesto());
                procedimiento.setInt(3, registro.getCodigoEmpresa());
                procedimiento.execute();
                listaPresupuesto.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
            }else{
                JOptionPane.showMessageDialog(null,"Presupuesto Incorrecto", "ERROR", JOptionPane.WARNING_MESSAGE);
            }
        }
    }    
    
    public void actualizar(){
        if(txtCantidadPresupuesto.getText().isEmpty() || fecha.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null, "Dejó un dato vacío \n Porfavor llene todos lo campos");
        }else{
            try{
             //Falta el parámetro de codigo Empresa ya que no se edita ya que es llave foránea
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarPresupuesto(?,?,?)");
            Presupuesto registro = (Presupuesto)tblPresupuestos.getSelectionModel().getSelectedItem();
            registro.setFechaSolicitud(fecha.getSelectedDate());
            registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));
            //registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa()); 
            procedimiento.setInt(1, registro.getCodigoPresupuesto());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaSolicitud().getTime()));
            procedimiento.setDouble(3, registro.getCantidadPresupuesto());
//            procedimiento.setInt(4, registro.getCodigoEmpresa());
            procedimiento.execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void imprimirReporte(){
        if(cmbCodigoEmpresa.getSelectionModel().getSelectedItem() == null){
            JOptionPane.showMessageDialog(null,"Debe elejir un registro para poder crear un reporte :(");
        }else{
            Map parametros = new HashMap();
            int codEmpresa = Integer.valueOf(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
            parametros.put("codEmpresa", codEmpresa);
            parametros.put("imgReporte", GenerarReporte.class.getResource("/org/diegoestrada/image/reporte.png"));
            parametros.put("imgOpacoLogo", GenerarReporte.class.getResource("/org/diegoestrada/image/opacoLogo.png"));
            parametros.put("subEmpleado", GenerarReporte.class.getResource("/org/diegoestrada/report/SubEmpleado.jasper"));
            parametros.put("SUBSERVICIO",GenerarReporte.class.getResource("/org/diegoestrada/report/SubServicio.jasper"));
            parametros.put("SUBPLATO",GenerarReporte.class.getResource("/org/diegoestrada/report/SubPlatoProducto.jasper"));
            parametros.put("SUBREPORT_DIR",GenerarReporte.class.getResource("/org/diegoestrada/report/SubPresupuesto.jasper"));
            
            
            
            GenerarReporte.mostrarReporte("ReporteGeneral.jasper", "Reporte de presupuestos", parametros);
        }   
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos para acciones de controles
    ------------------------------------------------------------------------------------------------------
    */
    
    public void desactivarControles(){
        txtCodigoPresupuesto.setEditable(false);
        txtCantidadPresupuesto.setEditable(false);  
        cmbCodigoEmpresa.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoPresupuesto.setEditable(false);
        txtCantidadPresupuesto.setEditable(true);
        cmbCodigoEmpresa.setDisable(false);
        fecha.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoPresupuesto.clear();
        txtCantidadPresupuesto.clear();
        cmbCodigoEmpresa.setValue(null);
        fecha.setSelectedDate(null);
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
