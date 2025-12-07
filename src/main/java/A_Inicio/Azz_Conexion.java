package A_Inicio; // <--- Importante: Esto ubica el archivo en tu carpeta A_Inicio

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Azz_Conexion {

    // 1. CONFIGURACIÓN
    private static final String USUARIO = "root";
    
    // OJO: Si al probar te da error de acceso, intenta cambiar "1234" por tu otra contraseña "123456789"
    private static final String PASSWORD = "1234"; 
    
    private static final String BASE_DATOS = "sistema_ventas";
    private static final String PUERTO = "3306";
    
    // URL de conexión segura
    private static final String URL = "jdbc:mysql://localhost:" + PUERTO + "/" + BASE_DATOS 
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    // 2. MÉTODO ESTÁTICO PARA OBTENER LA CONEXIÓN
    // Lo puse "static" para que puedas llamarlo directo desde el Login sin crear objetos (new Azz_Conexion)
    public static Connection getConnection() {
        Connection con = null;
        try {
            // Cargar Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Conectar
            con = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            // System.out.println("¡Conexión exitosa!"); // Puedes descomentar esto para probar
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: \n" + e.getMessage());
        }
        return con;
    }
}