package A_Logica_Y_Metodos;

import E_Modelos.Usuario;
import A_Inicio.Azz_Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para la tabla 'usuarios'.
 * Esta es la versión REVERTIDA:
 * 1. validarUsuario: Para el Login.
 * 2. crearUsuario: Para el formulario de Registro.
 */
public class UsuarioDAO {

    /**
     * Valida al usuario y devuelve un objeto Usuario si el login es exitoso.
     * Coincide con la BBDD donde 'usuarios' tiene su propia 'categoria'.
     */
    public Usuario validarUsuario(String usuario, String contrasena) {
        
        Connection con = Azz_Conexion.getConnection();
        if (con == null) {
            System.err.println("Error: No se pudo obtener la conexión a la BD.");
            return null;
        }
        
        // Consulta simple (sin JOIN) a la tabla usuarios
        String sql = "SELECT Id_usuario, Usuario, categoria FROM usuarios " +
                     "WHERE Usuario = ? AND Contraseña = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, usuario);
            pst.setString(2, contrasena);
            
            try (ResultSet rs = pst.executeQuery()) {
                
                if (rs.next()) {
                    // Usuario encontrado, crear y devolver el objeto
                    return new Usuario(
                        rs.getInt("Id_usuario"),
                        rs.getString("Usuario"),
                        rs.getString("categoria")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        
        return null; // Login fallido
    }

    /**
     * Crea una nueva entrada en la tabla 'usuarios'.
     * Usado por el formulario de registro (D_SubUsuario_Controller).
     */
    public boolean crearUsuario(String usuario, String contrasena, String categoria) {
        Connection con = Azz_Conexion.getConnection();
        if (con == null) {
            System.err.println("Error: No se pudo obtener la conexión a la BD.");
            return false;
        }

        String sql = "INSERT INTO usuarios (Usuario, Contraseña, categoria) VALUES (?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, usuario);
            pst.setString(2, contrasena);
            pst.setString(3, categoria);

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            // Error común: El 'Usuario' ya existe (violación de UNIQUE)
            System.err.println("Error al crear usuario (quizás ya existe): " + e.getMessage());
            return false;
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}

