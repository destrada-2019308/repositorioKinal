package org.diegoestrada.controller;

import com.mysql.jdbc.MysqlDataTruncation;
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
import org.diegoestrada.bean.Empleado;
import org.diegoestrada.bean.TipoEmpleado;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class EmpleadoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GURDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Empleado> listaEmpleado;
    private ObservableList<TipoEmpleado> listaTipoEmpleado;
    
    /*
    ------------------------------------------------------------------------------------------------------
        Todos los objetos provenienetes de JavaFX
    ------------------------------------------------------------------------------------------------------
    */
    
    @FXML private TextField txtCodigoEmpleado;
    @FXML private TextField txtNombreEmpleado;
    @FXML private TextField txtDireccionEmpleado;
    @FXML private TextField txtGradoCocinero;
    @FXML private TextField txtNumeroEmpleado;
    @FXML private TextField txtApellidosEmpleado;
    @FXML private TextField txtTelefonoEmpleado;
    @FXML private ComboBox cmbCodigoTipoEmpleado;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private Label lbFecha;
    @FXML private TableView tblEmpleados;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colNumeroEmpleado;
    @FXML private TableColumn colNombresEmpleado;
    @FXML private TableColumn colApellidosEmpleado;
    @FXML private TableColumn colDireccionEmpleado;
    @FXML private TableColumn colTelefonoEmpleado;
    @FXML private TableColumn colGradoCocinero;
    @FXML private TableColumn colCodigoTipoEmpleado;
    
    @Override
    public void initialize(URL location, ResourceBundle resource){
        cargarDatos();
        laFecha();
        cmbCodigoTipoEmpleado.setItems(getTipoEmpleado());
        cmbCodigoTipoEmpleado.setDisable(true);
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Método para poner la fecha en un label
    ------------------------------------------------------------------------------------------------------
    */
    
    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        lbFecha.setText("Hoy es "+ahora.getDayOfMonth() + " de " + nombre + " de "+ahora.getYear() );
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Métodos para usae el tableView
    ------------------------------------------------------------------------------------------------------
    */
    
    public void cargarDatos(){
       tblEmpleados.setItems(getEmpleado());
       colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, Integer>("codigoEmpleado"));
       colNumeroEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, Integer>("numeroEmpleado"));
       colNombresEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("nombresEmpleado"));
       colApellidosEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("apellidosEmpleado"));
       colDireccionEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("direccionEmpleado"));
       colTelefonoEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("telefonoContacto"));
       colGradoCocinero.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("gradoCocinero"));
       colCodigoTipoEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, Integer>("codigoTipoEmpleado"));
    }
    
    public void seleccionarElemento(){
        if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
            txtCodigoEmpleado.setText(String.valueOf(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            txtNumeroEmpleado.setText(String.valueOf(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getNumeroEmpleado()));
            txtNombreEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getNombresEmpleado());
            txtApellidosEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getApellidosEmpleado()); 
            txtDireccionEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getDireccionEmpleado());
            txtTelefonoEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getTelefonoContacto());
            txtGradoCocinero.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getGradoCocinero());
            cmbCodigoTipoEmpleado.getSelectionModel().select(buscarTipoEmpleado(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado()));
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
        }
    }
    
    //Método para usar el comboBox
    public TipoEmpleado buscarTipoEmpleado(int codigoTipoEmpleado){
        TipoEmpleado result = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("Call sp_BuscarTipoEmpleado(?)");
            procedimiento.setInt(1, codigoTipoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
               result = new TipoEmpleado(registro.getInt("codigoTipoEmpleado"),
                                        registro.getString("descripcion"));
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
    
    public ObservableList<TipoEmpleado> getTipoEmpleado(){
        ArrayList<TipoEmpleado> lista = new ArrayList<TipoEmpleado>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarTipoEmpleados");
            ResultSet result = procedimiento.executeQuery();
            while(result.next()){
                lista.add(new TipoEmpleado(result.getInt("codigoTipoEmpleado"),
                                        result.getString("descripcion")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTipoEmpleado = FXCollections.observableList(lista);
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos para acciones de controles
    --------------------------0----------------------------------------------------------------------------
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
                    tipoDeOperacion  = operaciones.GURDAR;
                break;
            case GURDAR:
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
            case GURDAR:
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
                if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Empleado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarEmpleado(?)");
                            procedimiento.setInt(1, ((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                            procedimiento.execute();
                            listaEmpleado.remove(tblEmpleados.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblEmpleados.getSelectionModel().clearSelection();
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
                    JOptionPane.showMessageDialog(null, "Porfavor seleccionar un registro");
                }
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                    if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
                        btnNuevo.setDisable(true);
                        btnEliminar.setDisable(true);
                        btnEditar.setText("Guardar");
                        btnReporte.setText("Cancelar");
                        imgEditar.setImage(new Image("/org/diegoestrada/image/salvar.png"));
                        imgReporte.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                        activarControles();
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
        }
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos guardar y actualizar
    ------------------------------------------------------------------------------------------------------
    */
    
    public void guardar(){
        Empleado registro = new Empleado();
        if(txtCodigoEmpleado == null || txtNumeroEmpleado.getText().isEmpty() || txtApellidosEmpleado.getText().isEmpty() || txtNombreEmpleado.getText().isEmpty() || txtDireccionEmpleado.getText().isEmpty() || txtTelefonoEmpleado.getText().isEmpty() ||cmbCodigoTipoEmpleado.getSelectionModel().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos");
        }else{
            registro.setNumeroEmpleado(Integer.parseInt(txtNumeroEmpleado.getText()));
            registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
            registro.setNombresEmpleado(txtNombreEmpleado.getText());
            registro.setDireccionEmpleado(txtDireccionEmpleado.getText());
            registro.setGradoCocinero(txtGradoCocinero.getText());
            registro.setCodigoTipoEmpleado(((TipoEmpleado)cmbCodigoTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado());
            if(txtTelefonoEmpleado.getText().matches("\\d{8}")){
            registro.setTelefonoEmpleado(txtTelefonoEmpleado.getText());
                try{
                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarEmpleado(?,?,?,?,?,?,?)");
                    procedimiento.setInt(1, registro.getNumeroEmpleado());
                    procedimiento.setString(2, registro.getApellidosEmpleado());
                    procedimiento.setString(3, registro.getNombresEmpleado());
                    procedimiento.setString(4, registro.getDireccionEmpleado());
                    procedimiento.setString(5, registro.getTelefonoContacto());
                    procedimiento.setString(6, registro.getGradoCocinero());
                    procedimiento.setInt(7, registro.getCodigoTipoEmpleado());
                    procedimiento.execute();
                    listaEmpleado.add(registro);
                }catch(MysqlDataTruncation error){
                    JOptionPane.showMessageDialog(null,"Verifique el número ", "AVISO", JOptionPane.WARNING_MESSAGE);
                }catch(NumberFormatException error){
                    JOptionPane.showMessageDialog(null,"Valor incorrecto", "AVISO", JOptionPane.WARNING_MESSAGE);                    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(null,"Teléfono Incorrecto", "ERROR", JOptionPane.WARNING_MESSAGE);
            }
        
        }
    
    }
    
    public void actualizar(){ 
        if(txtNumeroEmpleado.getText().isEmpty() || txtApellidosEmpleado.getText().isEmpty() || txtNombreEmpleado.getText().isEmpty() || txtDireccionEmpleado.getText().isEmpty() || txtTelefonoEmpleado.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejó un dato vacío. \n Por favor llene todos los campos");
        }else{
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarEmpleado(?,?,?,?,?,?,?)");
                //Traer los datos a los objetos en este caso a los TextField
                Empleado registro =(Empleado)tblEmpleados.getSelectionModel().getSelectedItem();
                registro.setNumeroEmpleado(Integer.parseInt(txtNumeroEmpleado.getText()));
                registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
                registro.setNombresEmpleado(txtNombreEmpleado.getText());
                registro.setDireccionEmpleado(txtDireccionEmpleado.getText());
                registro.setTelefonoEmpleado(txtTelefonoEmpleado.getText());
                registro.setGradoCocinero(txtGradoCocinero.getText());
                //Paso para editar los campos a traves de los objetos 
                procedimiento.setInt(1, registro.getCodigoEmpleado());
                procedimiento.setInt(2, registro.getNumeroEmpleado());
                procedimiento.setString(3, registro.getApellidosEmpleado());
                procedimiento.setString(4, registro.getNombresEmpleado());
                procedimiento.setString(5, registro.getDireccionEmpleado());
                procedimiento.setString(6, registro.getTelefonoContacto());
                procedimiento.setString(7, registro.getGradoCocinero());
                procedimiento.execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos para los botones: Nuevo, eliminar, editar y reporte
    ------------------------------------------------------------------------------------------------------
    */
    
    public void desactivarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNumeroEmpleado.setEditable(false);
        txtNombreEmpleado.setEditable(false);
        txtApellidosEmpleado.setEditable(false);
        txtDireccionEmpleado.setEditable(false);
        txtTelefonoEmpleado.setEditable(false);
        txtGradoCocinero.setEditable(false);
        cmbCodigoTipoEmpleado.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNumeroEmpleado.setEditable(true);
        txtNombreEmpleado.setEditable(true);
        txtApellidosEmpleado.setEditable(true);
        txtDireccionEmpleado.setEditable(true);
        txtTelefonoEmpleado.setEditable(true);
        txtGradoCocinero.setEditable(true);
        cmbCodigoTipoEmpleado.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoEmpleado.clear();
        txtNumeroEmpleado.clear();
        txtNombreEmpleado.clear();
        txtApellidosEmpleado.clear();
        txtDireccionEmpleado.clear();
        txtTelefonoEmpleado.clear();
        txtGradoCocinero.clear();
        cmbCodigoTipoEmpleado.setValue(null);
    }
    
    /*
    ------------------------------------------------------------------------------------------------------
        Metodos para levantar la vista
    ------------------------------------------------------------------------------------------------------
    */
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaTipoEmpleado(){
        escenarioPrincipal.ventanaTipoEmpleado();
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    } 
}
