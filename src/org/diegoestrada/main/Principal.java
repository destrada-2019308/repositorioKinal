/*
    Programador: Diego René Estrada Juárez
    Código Técnico: IN5AM
    Carné: 2019308
    Fecha de creación: 28/03/2023
    Fecha de modificación: 
        11/04/2023  | 03/05/2023
        12/04/2023  | 04/05/2023
        16/04/2023  | 06/05/2023
        17/04/2023  | 07/05/2023
        18/04/2023  | 09/05/2023                   
        19/04/2023  | 10/05/2023
        22/04/2023  | 11/05/2023
        23/04/2023
        24/04/2023
        25/04/2023
        28/04/2023
        29/04/2023
        01/05/2023
        02/05/2023
*/
package org.diegoestrada.main;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.diegoestrada.controller.EmpleadoController;
import org.diegoestrada.controller.EmpresaController;
import org.diegoestrada.controller.LoginController;
import org.diegoestrada.controller.MenuPrincipalController;
import org.diegoestrada.controller.PlatoController;
import org.diegoestrada.controller.PresupuestoController;
import org.diegoestrada.controller.ProductoController;
import org.diegoestrada.controller.ProductoHasPlatosController;
import org.diegoestrada.controller.ProgramadorController;
import org.diegoestrada.controller.ServicioController;
import org.diegoestrada.controller.ServiciosHasEmpleadosController;
import org.diegoestrada.controller.ServiciosHasPlatosController;
import org.diegoestrada.controller.TipoEmpleadoController;
import org.diegoestrada.controller.TipoPlatoController;
import org.diegoestrada.controller.UsuarioController;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/diegoestrada/view/";
    //Stage lo usamos como escenarioPrincipal
    //Ya que vamos hacer referencia a un objeto
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal) throws IOException {
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("Tony´s Kinal 2023");
        escenarioPrincipal.getIcons().add(new Image("/org/diegoestrada/image/Menu.png"));
        //Parent root = FXMLLoader.load(getClass().getResource("/org/diegoestrada/view/MenuPrincipalView.fxml"));
        //Scene escena = new Scene(root);
        //escenarioPrincipal.setScene(escena);
        login();
        escenarioPrincipal.show();
        
        /*Rectangle2D centrar = Screen.getPrimary().getVisualBounds();
        escenarioPrincipal.setX((centrar.getWidth() - centrar.getWidth())/2);
        escenarioPrincipal.setY((centrar.getHeight() - centrar.getHeight())/2);*/
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController menu = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml",663,674);
            menu.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController coder = (ProgramadorController)cambiarEscena("ProgramadorView.fxml",1260,500);
            coder.setEscenarioPrincipal(this);
            //escenarioPrincipal.setX(350);
            //escenarioPrincipal.setY(130);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpresa(){
        try{
            EmpresaController vEmpresa = (EmpresaController)cambiarEscena("EmpresaView.fxml",1280,750);
            vEmpresa.setEscenarioPrincipal(this);
            /*Rectangle2D centrar = Screen.getPrimary().getVisualBounds();
            escenarioPrincipal.setX((centrar.getWidth() - centrar.getWidth())/2);
            escenarioPrincipal.setY((centrar.getHeight() - centrar.getHeight())/2);*/
            //escenarioPrincipal.setX(350);
            //escenarioPrincipal.setY(130);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProducto(){
        try{
            ProductoController producto = (ProductoController)cambiarEscena("ProductoView.fxml",1280,750);
            producto.setEscenarioPrincipal(this);
            //escenarioPrincipal.setX(350);
            //escenarioPrincipal.setY(130);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoEmpleado(){
        try{
            TipoEmpleadoController tipoEmpleado = (TipoEmpleadoController)cambiarEscena("TipoEmpleadoView.fxml", 1280,750);
            tipoEmpleado.setEscenarioPrincipal(this);
            //escenarioPrincipal.setX(350);
            //escenarioPrincipal.setY(130);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoPlato(){
        try{
            TipoPlatoController tipoPlato = (TipoPlatoController)cambiarEscena("TipoPlatoView.fxml", 1280, 750);
            tipoPlato.setEscenarioPrincipal(this);
            //escenarioPrincipal.setX(350);
            //escenarioPrincipal.setY(130);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPresupuesto(){
        try{
            PresupuestoController presupuesto = (PresupuestoController)cambiarEscena("PresupuestoView.fxml", 1280, 750);
            presupuesto.setEscenarioPrincipal(this);
            //escenarioPrincipal.setX(350);
            //escenarioPrincipal.setY(130);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    public void ventanaEmpleado(){
        try{
            EmpleadoController empleado = (EmpleadoController)cambiarEscena("EmpleadoView.fxml", 1280, 750);
            empleado.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaServicio(){
        try{
            ServicioController servicio = (ServicioController)cambiarEscena("ServicioView.fxml", 1280, 750);
            servicio.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPlato(){
        try{
            PlatoController plato = (PlatoController)cambiarEscena("PlatoView.fxml",1280,750);
            plato.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void login(){
        try{
            LoginController login = (LoginController)cambiarEscena("LoginView.fxml", 363, 500);
            login.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaUsuario(){
        try{
            UsuarioController usuario = (UsuarioController)cambiarEscena("UsuarioView.fxml", 1280, 750);
            usuario.setEscePrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProductoshasPlatos(){
        try{
            ProductoHasPlatosController productohasplatos = (ProductoHasPlatosController)cambiarEscena("ProductoHasPlatosView.fxml", 1039, 602);
            productohasplatos.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaServiciosHasPlatos(){
        try{
            ServiciosHasPlatosController servicioshasplatos = (ServiciosHasPlatosController)cambiarEscena("ServicioHasPlatosView.fxml", 1039, 602);
            servicioshasplatos.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaServiciosHasEmpleados(){
        try{
            ServiciosHasEmpleadosController servicioshasempleados = (ServiciosHasEmpleadosController)cambiarEscena("ServiciosHasEmpleadosView.fxml",1039,602);
            servicioshasempleados.setEscenarioPrincipal(this);
            escenarioPrincipal.centerOnScreen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    
    //Initializable:
    //Es con lo que trabaja JavaFX, ósea los objetos
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws IOException{
        Initializable result = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA + fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        result = (Initializable)cargadorFXML.getController();
        return result;
    }

}
