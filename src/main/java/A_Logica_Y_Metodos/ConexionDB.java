package A_Logica_Y_Metodos;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class ConexionDB {

    // CAMBIO IMPORTANTE: La base de datos se llama 'sistema_ventas'
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_ventas?serverTimezone=UTC";
    
    // Usuario dueño del servidor MySQL
    private static final String USER = "root";
    
    // Tu contraseña de MySQL
    private static final String PASSWORD = "1234"; 

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error Conexión BD: " + e.getMessage());
        }
        return conexion;
    }
}