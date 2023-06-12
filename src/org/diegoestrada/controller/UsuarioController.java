package org.diegoestrada.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.diegoestrada.bean.Usuario;
import org.diegoestrada.db.Conexion;
import org.diegoestrada.main.Principal;

public class UsuarioController implements Initializable{
    private Principal escePrincipal;
    private enum operaciones{GUARDAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    
    @FXML private TextField txtCodigoUsuario;
    @FXML private TextField txtNombreUsuario;
    @FXML private TextField txtApellidoUsuario;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtPassword;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    
    @Override
    public void initialize(URL location, ResourceBundle resource){
        
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
                imgEliminar.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                login();
                break;
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case GUARDAR:
                    limpiarControles();;
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Cancelar");
                    imgNuevo.setImage(new Image("/org/diegoestrada/image/agregar-archivo.png"));
                    imgEliminar.setImage(new Image("/org/diegoestrada/image/cancelar.png"));
                    tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void guardar(){
        Usuario registro = new Usuario();
        if(txtApellidoUsuario.getText().isEmpty() || txtApellidoUsuario.getText().isEmpty() || txtUsuario.getText().isEmpty() || txtPassword.getText().isEmpty() || txtCodigoUsuario == null){
            JOptionPane.showMessageDialog(null,"Dejó un dato vacío. \n Porfavor llene todos los campos");
        }else{
            registro.setNombreUsuario(txtNombreUsuario.getText());
            registro.setApellidoUsuario(txtApellidoUsuario.getText());
            registro.setUsuarioLogin(txtUsuario.getText());
            registro.setContrasena(txtPassword.getText());
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarUsuario(?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreUsuario());
                procedimiento.setString(2, registro.getApellidoUsuario());
                procedimiento.setString(3, registro.getUsuarioLogin());
                procedimiento.setString(4, registro.getContrasena());
                procedimiento.execute();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void desactivarControles(){
        txtCodigoUsuario.setEditable(false);
        txtNombreUsuario.setEditable(false);
        txtApellidoUsuario.setEditable(false);
        txtUsuario.setEditable(false);
        txtPassword.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoUsuario.setEditable(false);
        txtNombreUsuario.setEditable(true);
        txtApellidoUsuario.setEditable(true);
        txtUsuario.setEditable(true);
        txtPassword.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoUsuario.clear();
        txtNombreUsuario.clear();
        txtApellidoUsuario.clear();
        txtUsuario.clear();
        txtPassword.clear();
    }
    
    public Principal getEscePrincipal() {
        return escePrincipal;
    }

    public void setEscePrincipal(Principal escePrincipal) {
        this.escePrincipal = escePrincipal;
    }
    
    public void login(){
        escePrincipal.login();
    }
    
}
