package A_Logica_Y_Metodos;

import A_Inicio.Azz_Conexion;
import E_Modelos.Personal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO para la tabla 'personal'.
 * Esta es la versión REVERTIDA (simple).
 * Su única función es guardar un nuevo registro de personal.
 */
public class PersonalDAO {

    /**
     * Inserta un nuevo registro de personal en la base de datos.
     * (Lógica desacoplada: no toca la tabla 'usuarios')
     * @param personal El objeto Personal con todos los datos.
     * @return true si el guardado fue exitoso, false si falló.
     */
    public boolean guardarPersonal(Personal personal) {
        Connection con = Azz_Conexion.getConnection();
        if (con == null) {
            System.err.println("Error: No se pudo obtener la conexión a la BD.");
            return false;
        }

        // SQL simple de inserción en la tabla 'personal'
        String sql = "INSERT INTO personal (nombre, apellido_paterno, apellido_materno, edad, sexo, telefono, categoria) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, personal.getNombre());
            pst.setString(2, personal.getApellidoPaterno());
            pst.setString(3, personal.getApellidoMaterno());
            pst.setInt(4, personal.getEdad());
            pst.setString(5, personal.getSexo());
            pst.setString(6, personal.getTelefono());
            pst.setString(7, personal.getCategoria());

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0; // True si se guardó

        } catch (SQLException e) {
            System.err.println("Error al guardar personal: " + e.getMessage());
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

