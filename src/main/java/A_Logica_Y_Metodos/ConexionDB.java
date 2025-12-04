package A_Logica_Y_Metodos;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class ConexionDB {
    
    // 1. Configuración de la Base de Datos
    // 'sistema_ventas' es el nombre que le pondremos a tu base de datos en Workbench
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_ventas?serverTimezone=UTC";
    private static final String USER = "root";
    
    // 2. IMPORTANTE: Aquí va la contraseña que pusiste en el instalador (ej: 1234)
    private static final String PASSWORD = "1234"; // <--- Pon aquí la que usaste en el instalador

    public static Connection conectar() {
        Connection con = null;
        try {
            // Cargamos el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Intentamos conectar
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            // Si falla, muestra un mensaje
            JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage());
        }
        return con;
    }
}