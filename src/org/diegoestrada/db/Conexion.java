package org.diegoestrada.db;

import java.sql.Connection;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;
import java.sql.DriverManager;

public class Conexion {
    private Connection conexion;
    private static Conexion instancia;
    
    
    private Conexion(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBTonysKinal2023?useSSL=false", "root", "admin");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static Conexion getInstance(){
        if(instancia == null)
        instancia = new Conexion();
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    
}
