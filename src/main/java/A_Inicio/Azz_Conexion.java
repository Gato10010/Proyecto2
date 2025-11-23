package A_Inicio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Azz_Conexion {

    // Ajusta la URL, usuario y contraseña a tu base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/proyecto2";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "123456789"; // ¡Cambia esto por tu contraseña!

    public static Connection getConnection() {
        Connection con = null;
        try {
            // Class.forName("com.mysql.cj.jdbc.Driver"); // No es necesario con JDBC 4.0+
            con = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            // System.out.println("¡Conexión exitosa a la base de datos!"); // Descomenta para probar
        } catch (SQLException e) { // Solo capturamos SQLException
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return con;
    }
}
