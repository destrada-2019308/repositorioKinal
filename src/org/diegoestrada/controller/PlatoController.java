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
import org.diegoestrada.bean.TipoPlato;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class PlatoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, GUARDAR, ACTUALIZAR, ELIMINAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Plato> listaPlato;
    private ObservableList<TipoPlato> listaTipoPlato;
    
    
    @FXML private TextField txtCodigoPlato;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtNombrePlato;
    @FXML private TextField txtDescripcionPlato;
    @FXML private TextField txtPrecioPlato;
    @FXML private ComboBox  cmbCodigoTipoPlato;
    @FXML private TableView tblPlatos;
    @FXML private TableColumn colCodigoPlato;
    @FXML private TableColumn colCantidad;
    @FXML private TableColumn colNombrePlato;
    @FXML private TableColumn colDescripcionPlato;
    @FXML private TableColumn colPrecioPlato;
    @FXML private TableColumn colCodigoTipoPlato;
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
        cmbCodigoTipoPlato.setItems(getTipoPlato());
        cmbCodigoTipoPlato.setDisable(true);
    }
    
    public void laFecha(){
        LocalDate ahora = LocalDate.now();
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es","ES"));
        lblFecha.setText("Hoy es " + ahora.getDayOfMonth() + " de " + nombre + " de " + ahora.getYear());
    }
    
    public void cargarDatos(){
        tblPlatos.setItems(getPlato());
        colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("codigoPlato"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("cantidad"));
        colNombrePlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("nombrePlato"));
        colDescripcionPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("descripcionPlato"));
        colPrecioPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("precioPlato"));
        colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("codigoTipoPlato"));
    }
    
    public void seleccionarElemento(){
        if(tblPlatos.getSelectionModel().getSelectedItem() != null){
            txtCodigoPlato.setText(String.valueOf(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            txtCantidad.setText(String.valueOf(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCantidad()));
            txtNombrePlato.setText(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getNombrePlato());
            txtDescripcionPlato.setText(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getDescripcionPlato());
            txtPrecioPlato.setText(String.valueOf(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getPrecioPlato()));
            cmbCodigoTipoPlato.getSelectionModel().select(buscarTipoPlato(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato()));
        }else{
            JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
        }
    }
    
    public TipoPlato buscarTipoPlato(int codigoTipoPlato){
        TipoPlato result = null;
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarTipoPlato(?)");
                procedimiento.setInt(1,codigoTipoPlato);
                ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    result = new TipoPlato(registro.getInt("codigoTipoPlato"),
                                    registro.getString("descripcionTipo"));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return result;
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
                if(tblPlatos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar Plato", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarPlato(?)");
                            procedimiento.setInt(1, ((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato());
                            procedimiento.execute();
                            listaPlato.remove(tblPlatos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                        }catch(SQLException e){
                            JOptionPane.showMessageDialog(null,"nop");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else if(respuesta == JOptionPane.NO_OPTION){
                        limpiarControles();;
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
                    if(tblPlatos.getSelectionModel().getSelectedItem() != null){
                       activarControles();
                       btnNuevo.setDisable(true);
                       btnEliminar.setDisable(true);
                       btnEditar.setText("Actualizar");
                       btnReporte.setText("Cancelar");
                       imgEditar.setImage(new Image("/org/diegoestrada/image/salvar.png"));
                       imgReporte.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                       tipoDeOperacion = operaciones.ACTUALIZAR;
                    }else{
                        JOptionPane.showMessageDialog(null,"Debe seleccionar un registro para editarlo");
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
    
    public void guardar(){
        Plato registro = new Plato();
        if(txtCodigoPlato == null || txtCantidad.getText().isEmpty() || txtNombrePlato.getText().isEmpty() || txtDescripcionPlato.getText().isEmpty() || txtPrecioPlato.getText().isEmpty() || cmbCodigoTipoPlato.getSelectionModel().isEmpty()){
            JOptionPane.showMessageDialog(null, "Dejo un dato vacío \n Porfavor llene todos los campos");
        }else{
                registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
                registro.setNombrePlato(txtNombrePlato.getText());
                registro.setDescripcionPlato(txtDescripcionPlato.getText());
                registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
                registro.setCodigoTipoPlato(((TipoPlato)cmbCodigoTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarPlato(?,?,?,?,?)");
                procedimiento.setInt(1, registro.getCantidad());
                procedimiento.setString(2, registro.getNombrePlato());
                procedimiento.setString(3, registro.getDescripcionPlato());
                procedimiento.setDouble(4, registro.getPrecioPlato());
                procedimiento.setInt(5, registro.getCodigoTipoPlato());
                procedimiento.execute();
                listaPlato.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void actualizar(){
        if(txtCantidad.getText().isEmpty() || txtNombrePlato.getText().isEmpty() || txtDescripcionPlato.getText().isEmpty() || txtPrecioPlato.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Dejo un dato sin ingresar. \n Porfavor llenar todos los campos");
        }else{
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarPlato(?,?,?,?,?)");
                Plato registro = (Plato)tblPlatos.getSelectionModel().getSelectedItem();
                registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
                registro.setNombrePlato(txtNombrePlato.getText());
                registro.setDescripcionPlato(txtDescripcionPlato.getText());
                registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
                procedimiento.setInt(1, registro.getCodigoTipoPlato());
                procedimiento.setInt(2, registro.getCantidad());
                procedimiento.setString(3, registro.getNombrePlato());
                procedimiento.setString(4, registro.getDescripcionPlato());
                procedimiento.setDouble(5, registro.getPrecioPlato());
                procedimiento.execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    
    public void limpiarControles(){
        txtCodigoPlato.clear();
        txtCantidad.clear();
        txtNombrePlato.clear();
        txtDescripcionPlato.clear();
        txtPrecioPlato.clear();
        cmbCodigoTipoPlato.setValue(null);
    }
    
    public void desactivarControles(){
        txtCodigoPlato.setEditable(false);
        txtCantidad.setEditable(false);
        txtNombrePlato.setEditable(false);
        txtDescripcionPlato.setEditable(false);
        txtPrecioPlato.setEditable(false);
        cmbCodigoTipoPlato.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoPlato.setEditable(false);
        txtCantidad.setEditable(true);
        txtNombrePlato.setEditable(true);
        txtDescripcionPlato.setEditable(true);
        txtPrecioPlato.setEditable(true);
        cmbCodigoTipoPlato.setDisable(false);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaTipoPlato(){
        escenarioPrincipal.ventanaTipoPlato();
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
}
